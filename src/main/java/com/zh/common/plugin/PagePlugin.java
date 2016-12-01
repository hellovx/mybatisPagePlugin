package com.zh.common.plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import com.zh.entity.PageBean;
import com.zh.util.ReflectHelper;
import com.zh.util.StringUtil;

@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class PagePlugin implements Interceptor {
	private static String dialect = ""; // 数据库类型
	private static String pageSqlId = ""; // mapper.xml中需要拦截的ID(正则匹配)

	public Object intercept(Invocation invocation) throws Throwable {
		// TODO Auto-generated method stub
		System.out.println("这是插件Interceptor intercept");
		// 拦截逻辑操作
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
		MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
		String selectId = mappedStatement.getId();
		if (selectId.matches(pageSqlId)) {// 符合条件的mapper名称
			BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
			Object parameterObject = boundSql.getParameterObject();// 分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
			if (parameterObject == null) {
				throw new NullPointerException("parameterObject尚未实例化！");
			} else {
				Connection connection = (Connection) invocation.getArgs()[0];
				String sql = boundSql.getSql();
				// String countSql = "select count(0) from (" + sql+ ") as
				// tmp_count"; //记录统计
				String countSql = "select count(0) from (" + sql + ")  tmp_count";
				PreparedStatement countStmt = connection.prepareStatement(countSql);
				BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql,
						boundSql.getParameterMappings(), parameterObject);
				setParameters(countStmt, mappedStatement, countBS, parameterObject);
				ResultSet rs = countStmt.executeQuery();
				int count = 0;// 结果集总数
				if (rs.next()) {
					count = rs.getInt(1);
				}
				rs.close();
				countStmt.close();
				PageBean page = null;
				if (parameterObject instanceof PageBean) {
					page = (PageBean) parameterObject;
					//总记录数
					page.setAllRow(count);
					//计算当前页
					page.setCurrentPage(page.countCurrentPage(page.getCurrentPage()));
					//总页数
					page.setTotalPage(page.countTotalPage(page.getPageSize(), page.getAllRow()));
					//当前记录起始索引
					page.setCurrentResult(page.countOffset(page.getPageSize(), page.getCurrentPage()));
				}else{
					throw new NoSuchFieldException(parameterObject.getClass().getName()+"不存在 page 属性！");
				}
				String pageSql = generatePageSql(sql,page);
				ReflectHelper.setValueByFieldName(boundSql, "sql", pageSql); //将分页sql语句反射回BoundSql.
			}
		}
		return invocation.proceed();
	}

	public Object plugin(Object target) {
		// TODO Auto-generated method stub
		return Plugin.wrap(target, this);
	}

	// 获取mybatis配置的参数
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub
		dialect = getParameter(properties.getProperty("dialect"));
		pageSqlId = getParameter(properties.getProperty("pageSqlId"));
	}

	/**
	 * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.DefaultParameterHandler
	 * 
	 * @param ps
	 * @param mappedStatement
	 * @param boundSql
	 * @param parameterObject
	 * @throws SQLException
	 */
	private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql,
			Object parameterObject) throws SQLException {
		ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (parameterMappings != null) {
			Configuration configuration = mappedStatement.getConfiguration();
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
			for (int i = 0; i < parameterMappings.size(); i++) {
				ParameterMapping parameterMapping = parameterMappings.get(i);
				if (parameterMapping.getMode() != ParameterMode.OUT) {
					Object value;
					String propertyName = parameterMapping.getProperty();
					PropertyTokenizer prop = new PropertyTokenizer(propertyName);
					if (parameterObject == null) {
						value = null;
					} else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
						value = parameterObject;
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						value = boundSql.getAdditionalParameter(propertyName);
					} else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX)
							&& boundSql.hasAdditionalParameter(prop.getName())) {
						value = boundSql.getAdditionalParameter(prop.getName());
						if (value != null) {
							value = configuration.newMetaObject(value)
									.getValue(propertyName.substring(prop.getName().length()));
						}
					} else {
						value = metaObject == null ? null : metaObject.getValue(propertyName);
					}
					TypeHandler typeHandler = parameterMapping.getTypeHandler();
					if (typeHandler == null) {
						throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName
								+ " of statement " + mappedStatement.getId());
					}
					typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
				}
			}
		}
	}

	/**
	 * 根据数据库方言，生成特定的分页sql,mysql oracle mssql
	 * @param sql
	 * @param page
	 * @return
	 */
	private String generatePageSql(String sql,PageBean page){
		if(page!=null && !StringUtil.isEmpty(dialect)){
			StringBuffer pageSql = new StringBuffer();
			if("mysql".equals(dialect)){
				pageSql.append(sql);
				pageSql.append(" limit "+page.getCurrentResult()+","+page.getPageSize());
			}else if("oracle".equals(dialect)){
				pageSql.append("select * from (select tmp_tb.*,ROWNUM row_id from (");
				pageSql.append(sql);
				//pageSql.append(") as tmp_tb where ROWNUM<=");
				pageSql.append(") tmp_tb where ROWNUM<=");
				pageSql.append(page.getCurrentResult()+page.getPageSize());
				pageSql.append(") where row_id>");
				pageSql.append(page.getCurrentResult());
			}
			return pageSql.toString();
		}else{
			return sql;
		}
	} 
	  
	public String getParameter(String val) {
		String result = null;
		if (!StringUtil.isEmpty(val)) {
			result = val;
		}
		return result;
	}

}
