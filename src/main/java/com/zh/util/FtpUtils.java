package com.zh.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TimeZone;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

public class FtpUtils {
	private FTPClient ftpClient;
	private String strIp;
	private int intPort;
	private String user;
	private String password;

	private static Logger logger = Logger.getLogger(FtpUtils.class.getName());

	/* *
	 * Ftp构造函数
	 */
	public FtpUtils(String strIp, int intPort, String user, String Password) {
		this.strIp = strIp;
		this.intPort = intPort;
		this.user = user;
		this.password = Password;
		this.ftpClient = new FTPClient();
	}

	/**
	 * @return 判断是否登入成功
	 * */
	public boolean ftpLogin() {
		boolean isLogin = false;
		FTPClientConfig ftpClientConfig = new FTPClientConfig();
		ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
		this.ftpClient.setControlEncoding("GBK");
		this.ftpClient.configure(ftpClientConfig);
		try {
			if (this.intPort > 0) {
				this.ftpClient.connect(this.strIp, this.intPort);
			} else {
				this.ftpClient.connect(this.strIp);
			}
			// FTP服务器连接回答
			int reply = this.ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				this.ftpClient.disconnect();
				logger.error("登录FTP服务失败！");
				return isLogin;
			}
			this.ftpClient.login(this.user, this.password);
			// 设置传输协议
			this.ftpClient.enterLocalPassiveMode();
			this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			logger.info("恭喜" + this.user + "成功登陆FTP服务器");
			isLogin = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(this.user + "登录FTP服务失败！" + e.getMessage());
		}
		this.ftpClient.setBufferSize(1024 * 2);
		this.ftpClient.setDataTimeout(30 * 1000);
		return isLogin;
	}

	/**
	 * @退出关闭服务器链接
	 * */
	public void ftpLogOut() {
		if (null != this.ftpClient && this.ftpClient.isConnected()) {
			try {
				boolean reuslt = this.ftpClient.logout();// 退出FTP服务器
				if (reuslt) {
					logger.info("成功退出服务器");
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.warn("退出FTP服务器异常！" + e.getMessage());
			} finally {
				try {
					this.ftpClient.disconnect();// 关闭FTP服务器的连接
				} catch (IOException e) {
					e.printStackTrace();
					logger.warn("关闭FTP服务器的连接异常！");
				}
			}
		}
	}

	/***
	 * 上传Ftp文件
	 * 
	 * @param localFile
	 *            当地文件  D:\BaiduYunDownload\jdk-8u91-windows-x64.exe
	 * @param romotUpLoadePath上传服务器路径    
	 *            \jdk-windows-x64.exe   \add\jdk-windows-x64.exe
	 * */
	public boolean uploadFile(File localFile, String remoteWorkingPath) {
		BufferedInputStream inStream = null;
		boolean isOK = false;
		try {
			// 设置被动模式
			ftpClient.enterLocalActiveMode();
			//System.out.println(this.ftpClient.printWorkingDirectory());
			// 改变工作路径
			String remotePath = remoteWorkingPath.substring(0,
					remoteWorkingPath.lastIndexOf(File.separator));
			String fileName = remoteWorkingPath.substring(remoteWorkingPath
					.lastIndexOf(File.separator) + 1);
			if (!changeDirectoryPath(remotePath)) {
				// FIXME:创建目录失败是否继续往下写文件？
				return false;
			}
			//System.out.println(this.ftpClient.printWorkingDirectory());
			// this.ftpClient.changeWorkingDirectory(romotUpLoadePath);// 改变工作路径
			inStream = new BufferedInputStream(new FileInputStream(localFile));
			//上传ftp路径
			String uploadAdd=remotePath+File.separator+fileName;
			logger.info(localFile.getName() + "开始上传.....");
			System.out.println("Upload To:"+uploadAdd);
			isOK = this.ftpClient.storeFile(uploadAdd, inStream);
			if (isOK) {
				logger.info(localFile.getName() + "上传成功");
				return isOK;
			} else {
				logger.info(localFile.getName() + "上传失败");
				return isOK;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error(localFile + "未找到");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return isOK;
	}

	/***
	 * @上传文件夹
	 * @param localDirectory
	 *            当地文件夹  D:\Program Files (x86)\Dict
	 * @param remoteDirectoryPath
	 *            Ftp 服务器路径   \date\ftp
	 * */
	public boolean uploadDirectory(String localDirectory,
			String remoteDirectoryPath) {
		File src = new File(localDirectory);
		try {
			remoteDirectoryPath = remoteDirectoryPath +File.separator+ src.getName() + File.separator;
			this.ftpClient.makeDirectory(remoteDirectoryPath);
			// ftpClient.listDirectories();
		} catch (IOException e) {
			e.printStackTrace();
			logger.info(remoteDirectoryPath + "目录创建失败");
		}
		File[] allFile = src.listFiles();
		for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
			if (!allFile[currentFile].isDirectory()) {
				String srcName = allFile[currentFile].getPath().toString();
				uploadFile(new File(srcName), remoteDirectoryPath+srcName.substring(srcName.lastIndexOf(File.separator)+1));
			}
		}
		for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
			if (allFile[currentFile].isDirectory()) {
				// 递归
				uploadDirectory(allFile[currentFile].getPath().toString(),
						remoteDirectoryPath);
			}
		}
		return true;
	}

	// FtpClient的Set 和 Get 函数
	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}

	/**
	 * 切换{@link FTPClient}工作路径,如果路径不存在则递归创建
	 * 
	 * @param remoteDirectoryPath
	 * @return
	 * @throws IOException
	 */
	public boolean changeDirectoryPath(String remoteDirectoryPath)
			throws IOException {

		if (remoteDirectoryPath.equalsIgnoreCase("/")
				|| ftpClient.changeWorkingDirectory(new String(
						remoteDirectoryPath))) {
			return true;
		}
		ftpClient.makeDirectory(new String(remoteDirectoryPath.getBytes(),
				"GBK"));
		ftpClient.changeWorkingDirectory(new String(remoteDirectoryPath
				.getBytes(), "GBK"));
		//
		// int startPathIndex = 0;
		// if (remoteDirectoryPath.startsWith("/")) {
		// startPathIndex = 1;
		// }
		// int endPathIndex = remoteDirectoryPath.indexOf("/", startPathIndex);
		// // 递归创建目录
		// while (true) {
		// String subDirectory = remoteDirectoryPath.substring(startPathIndex,
		// endPathIndex);
		// if (!ftpClient.changeWorkingDirectory(subDirectory)) {
		// if (ftpClient.makeDirectory(subDirectory)) {
		// ftpClient.changeWorkingDirectory(subDirectory);
		// } else {
		// logger.info(remoteDirectoryPath + "目录创建失败！");
		// return false;
		// }
		// }
		// //目录层次加一
		// startPathIndex = endPathIndex + 1;
		// endPathIndex = remoteDirectoryPath.indexOf("/", startPathIndex);
		// // 检查所有目录是否创建完毕
		// if (endPathIndex <= startPathIndex) {
		// break;
		// }
		// }
		return true;
	}

	public static void main(String[] args) throws IOException {
		// FtpUtils ftp = new FtpUtils("172.28.254.108", 21, "ctvitftp",
		// "ctvitftp");
		FtpUtils ftp = new FtpUtils("10.6.0.197", 21, "ctvitftp", "ctvitftp");
		ftp.ftpLogin();//登录
		// 上传文件夹
	    ftp.uploadDirectory("D:"+File.separator+"Program Files (x86)"+File.separator+"Dict", File.separator+"date"+File.separator+"ftp");
		// 上传文件
		ftp.uploadFile(new File("D:"+File.separator+"BaiduYunDownload"+File.separator+"jdk-8u91-windows-x64.exe"),
				File.separator+"jdk-windows-x64.exe");
		ftp.ftpLogOut();
	}
}