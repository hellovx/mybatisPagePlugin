<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
	String path = request.getContextPath();
	request.setAttribute("path", path);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript">
function nextPage(currentPage){
	//alert(currentPage);
	window.location.href="${path}/user/userlist.do?pageSize=2&currentPage="+currentPage;
}
</script>
</head>
<body>

	<table border="1" width="80%" align="center">
		<tr>
			<td align="center">序号：</td>
			<td align="center">id</td>
			<td align="center">姓名:</td>
			<td align="center">年龄：</td>
			<td align="center">地址：</td>
		</tr>
		<c:forEach items="${page.list}" var="user" varStatus="var">
			<tr>
				<td align="center">${var.index+1}:</td>
				<td align="center">${user.id}</td>
				<td align="center">${user.name}</td>
				<td align="center">${user.age}</td>
				<td align="center">${user.address}</td>
			</tr>
		</c:forEach>
	</table>

	<center>
		<tr align="center" width="10">
			共${pageBean.allRow }条记录 共${pageBean.totalPage }页
			当前第${pageBean.currentPage}页
			<br />
			<c:if test="${pageBean.currentPage == 1}">第一页 上一页</c:if>
			<c:if test="${pageBean.currentPage != 1}">
				<a href="javascript:void(0)" onclick="nextPage(1);">第一页</a>
				<a href="javascript:void(0)" onclick="nextPage(${pageBean.currentPage-1});">上一页</a>
			</c:if>
			<c:if test="${pageBean.currentPage != pageBean.totalPage}">
				<a href="javascript:void(0)" onclick="nextPage(${pageBean.currentPage+1});">下一页</a>
				<a href="javascript:void(0)" onclick="nextPage(${pageBean.totalPage});">最后一页</a>
			</c:if>
			<c:if test="${pageBean.currentPage == pageBean.totalPage}">	
下一页  最后一页
</c:if>

		</tr>
	</center>

</body>
</html>