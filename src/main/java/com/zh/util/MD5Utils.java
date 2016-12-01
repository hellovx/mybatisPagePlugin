package com.zh.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

	public static String getMD5(String str) {
		// （信息-摘要算法）
		// Message-Digest Algorithm 5
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString(); // 返回md5 32位小写字符串
	}

	/*
	 * String test="96e79218965eb72c92a549dd5a330112";
	 * System.out.println(test.toUpperCase());//小写转大写 test=test.toUpperCase();
	 * System.out.println(test.toLowerCase()); //大写转小写
	 */
	public static void main(String[] args) {
		System.out.println(MD5Utils.getMD5("111111"));
		// 96e79218965eb72c92a549dd5a330112
	}

}
