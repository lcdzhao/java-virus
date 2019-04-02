package com.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.CharBuffer;

public class GetSystemFiles {

	public GetSystemFiles() {
		// TODO Auto-generated constructor stub
	}
	
	

	/**
	 * 获得path下的目录
	 * 
	 * @param path
	 * @return
	 */
	public static String[] getFiles(String path) {
		File dir = new File(path);
		if ((!dir.isDirectory()) && (!dir.exists())) {
			return null;
		}
		String[] files = dir.list();
		if(files != null) {
		
		String[] filesPath = new String[files.length];
		for (int i = 0; i < filesPath.length; i++)
			filesPath[i] = path +"/"+ files[i];
		return filesPath;
		}
		return null;
	}

	/**
	 * 将path目录下的文件名发给服务器
	 * 
	 * @param path
	 * @param out
	 */
	public static void sendFilesName(String dir, BufferedWriter out) {
		
		String[] files = getFiles(dir);
		for (int i = 0; i < files.length; i++) {
			try {
				out.write(files[i] + "\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param path
	 * @param out
	 * @return
	 */
	public static boolean download(BufferedInputStream input, BufferedOutputStream out) {
		try {
			

			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = input.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			out.flush();
			return true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param path
	 * @param input
	 * @return
	 */
	public static void upload(BufferedOutputStream fileOut, BufferedInputStream input) {
		try {
			
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = input.read(buf)) != -1) {
				fileOut.write(buf, 0, len);

			}
			fileOut.flush();
			fileOut.close();
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}

	}

}
