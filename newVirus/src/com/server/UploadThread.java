package com.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UploadThread extends Thread{
	
	private Baby baby;
	private String file;
	private String path;
	
	public UploadThread(Baby baby,String file,String path) {
		// TODO Auto-generated constructor stub
		this.baby = baby;
		this.file = file;
		this.path = path;
	}
	
	public void run() {
		try {
			//打开本地文件流
			BufferedInputStream inputFile = 
					new BufferedInputStream(new FileInputStream(file));
			//监听随机端口
			
			ServerSocket downloadServerSocket = new ServerSocket(0);
			
			//发送端口
			
			String port = "" + downloadServerSocket.getLocalPort();
			//System.out.println(port);
			//out.write(downloadServerSocket.getLocalPort()+"\r\n");
			baby.getWriter().write(port+"\r\n");
	
			baby.getWriter().flush();
			Socket downloadSocket = downloadServerSocket.accept();
			//监听接口并下载
			
			System.out.println("正在上传"  + file + "...,上传成功将会通知");
			BufferedOutputStream outputToClients = 
					new BufferedOutputStream(downloadSocket.getOutputStream());
			
			int buf;
			int len = 0;
			while((buf = inputFile.read()) != -1) {
				len ++;
				outputToClients.write(buf);
			}
			System.out.println("文件一共:"+ len +"字节");
			outputToClients.flush();
			outputToClients.close();
			inputFile.close();
			System.err.println("上传完成，文件已上传到:"  + path );
			downloadSocket.close();
			downloadServerSocket.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("上传出现问题，可能时你的路径输入有误");
			e.printStackTrace();
			
			
		}
		
	}

}