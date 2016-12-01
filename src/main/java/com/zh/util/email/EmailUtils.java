package com.zh.util.email;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class EmailUtils {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// 这个类主要是设置邮件
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost("smtp.163.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("hellovx@163.com");
		mailInfo.setPassword("463900");// 您的邮箱密码
		mailInfo.setFromAddress("hellovx@163.com");
		mailInfo.setToAddress("hellovx@163.com");
		mailInfo.setSubject("test");
		mailInfo.setContent("<html><a>hellovx我是测试</a></html>");
		String[] file = { "D:\\logs\\20160816最新节目编排单.xlsx" };
		mailInfo.setAttachFileNames(file);
		try {
			boolean b = false;
			// b = sendTextMail(mailInfo);
			// b = sendHtmlMail(mailInfo);
			b = sendEmailwithAttach(mailInfo);
			System.out.println(b);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	
	
	public static boolean sendEmail(int type,String title,String content,String [] file)throws Exception{
		// 这个类主要是设置邮件
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost("smtp.163.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("hellovx@163.com");
		mailInfo.setPassword("463900");// 您的邮箱密码
		mailInfo.setFromAddress("hellovx@163.com");
		mailInfo.setToAddress("hellovx@163.com");
		mailInfo.setSubject(title);
		mailInfo.setContent(content);
		mailInfo.setAttachFileNames(file);
		boolean result=false; 
		if(type==1){
			result=sendTextMail(mailInfo);
		}else if(type==2){
			result=sendHtmlMail(mailInfo);
		}else if(type==3){
			result=sendEmailwithAttach(mailInfo);
		}
		

		return result;

	}

	/**
	 * 
	 * @Title: sendTextMail
	 * @Description: 以文本格式发送邮件
	 * @author zhanghao
	 * @date 2016年7月21日 下午4:20:41
	 * @param mailInfo
	 *            待发送的邮件的信息
	 * @return
	 * @throws Exception
	 *             boolean 返回类型
	 * @throws
	 */
	public static boolean sendTextMail(MailSenderInfo mailInfo)
			throws Exception {
		// 判断是否需要身份认证
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		if (mailInfo.isValidate()) {
			// 如果需要身份认证，则创建一个密码验证器
			authenticator = new MyAuthenticator(mailInfo.getUserName(),
					mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);

		// 根据session创建一个邮件消息
		Message mailMessage = new MimeMessage(sendMailSession);
		// 创建邮件发送者地址
		Address from = new InternetAddress(mailInfo.getFromAddress());
		// 设置邮件消息的发送者
		mailMessage.setFrom(from);
		// 创建邮件的接收者地址，并设置到邮件消息中
		Address to = new InternetAddress(mailInfo.getToAddress());
		mailMessage.setRecipient(Message.RecipientType.TO, to);
		// 设置邮件消息的主题
		mailMessage.setSubject(mailInfo.getSubject());
		// 设置邮件消息发送的时间
		mailMessage.setSentDate(new Date());
		// 设置邮件消息的主要内容
		String mailContent = mailInfo.getContent();
		mailMessage.setText(mailContent);
		// 发送邮件
		Transport.send(mailMessage);
		return true;
	}

	/**
	 * 
	 * @Title: sendHtmlMail
	 * @Description: 以HTML格式发送邮件
	 * @author zhanghao
	 * @date 2016年7月21日 下午4:15:03
	 * @param mailInfo
	 *            待发送的邮件信息
	 * @return
	 * @throws Exception
	 *             boolean 返回类型
	 * @throws
	 */
	public static boolean sendHtmlMail(MailSenderInfo mailInfo)
			throws Exception {
		// 判断是否需要身份认证
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		// 如果需要身份认证，则创建一个密码验证器
		if (mailInfo.isValidate()) {
			authenticator = new MyAuthenticator(mailInfo.getUserName(),
					mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);

		// 根据session创建一个邮件消息
		Message mailMessage = new MimeMessage(sendMailSession);
		// 创建邮件发送者地址
		Address from = new InternetAddress(mailInfo.getFromAddress());
		// 设置邮件消息的发送者
		mailMessage.setFrom(from);
		// 创建邮件的接收者地址，并设置到邮件消息中
		Address to = new InternetAddress(mailInfo.getToAddress());
		// Message.RecipientType.TO属性表示接收者的类型为TO
		mailMessage.setRecipient(Message.RecipientType.TO, to);
		// 设置邮件消息的主题
		mailMessage.setSubject(mailInfo.getSubject());
		// 设置邮件消息发送的时间
		mailMessage.setSentDate(new Date());
		// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
		Multipart mainPart = new MimeMultipart();
		// 创建一个包含HTML内容的MimeBodyPart
		BodyPart html = new MimeBodyPart();
		// 设置HTML内容
		html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
		mainPart.addBodyPart(html);
		// 将MiniMultipart对象设置为邮件内容
		mailMessage.setContent(mainPart);
		// 发送邮件
		Transport.send(mailMessage);
		return true;
	}

	/**
	 * 
	 * @Title: sendEmailwithAttach
	 * @Description: 发送邮件 html 带附件
	 * @author zhanghao
	 * @date 2016年7月21日 下午4:44:33
	 * @param subject
	 *            邮件主题
	 * @param sendHtml
	 *            邮件内容
	 * @param receiveUser
	 *            收件人地址
	 * @param attachment
	 *            附件
	 * @return boolean 返回类型
	 * @throws
	 */
	public static boolean sendEmailwithAttach(MailSenderInfo mailInfo) {
		try {
			// 判断是否需要身份认证
			MyAuthenticator authenticator = null;
			Properties pro = mailInfo.getProperties();
			// 如果需要身份认证，则创建一个密码验证器
			if (mailInfo.isValidate()) {
				authenticator = new MyAuthenticator(mailInfo.getUserName(),
						mailInfo.getPassword());
			}
			// 根据邮件会话属性和密码验证器构造一个发送邮件的session
			Session sendMailSession = Session.getDefaultInstance(pro,
					authenticator);

			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address to = new InternetAddress(mailInfo.getToAddress());
			// Message.RecipientType.TO属性表示接收者的类型为TO
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());

			// 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
			Multipart multipart = new MimeMultipart();

			// 添加邮件正文
			BodyPart contentPart = new MimeBodyPart();
			contentPart.setContent(mailInfo.getContent(),
					"text/html;charset=UTF-8");
			multipart.addBodyPart(contentPart);

			// 添加附件的内容
			if (mailInfo.getAttachFileNames() != null) {
				for (int i = 0; i < mailInfo.getAttachFileNames().length; i++) {
					BodyPart attachmentBodyPart = new MimeBodyPart();
					DataSource source = new FileDataSource(
							mailInfo.getAttachFileNames()[i]);
					attachmentBodyPart.setDataHandler(new DataHandler(source));

					// 网上流传的解决文件名乱码的方法，其实用MimeUtility.encodeWord就可以很方便的搞定
					// 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
					// sun.misc.BASE64Encoder enc = new
					// sun.misc.BASE64Encoder();
					// messageBodyPart.setFileName("=?GBK?B?" +
					// enc.encode(attachment.getName().getBytes()) + "?=");

					// MimeUtility.encodeWord可以避免文件名乱码
					File file = new File(mailInfo.getAttachFileNames()[i]);
					attachmentBodyPart.setFileName(MimeUtility.encodeWord(file
							.getName()));
					multipart.addBodyPart(attachmentBodyPart);
				}
			}

			// 将multipart对象放到message中
			mailMessage.setContent(multipart);
			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
