package com.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DownloadThread extends Thread{
	
	private Baby baby;
	private String file;
	private String path;
	
	public DownloadThread(Baby baby,String file,String path) {
		// TODO Auto-generated constructor stub
		this.baby = baby;
		this.file = file;
		this.path = path;
	}
	
	public void run() {
		try {
			//打开本地文件流
			BufferedOutputStream outToFile = 
					new BufferedOutputStream(new FileOutputStream(path));
			//监听随机端口
			
			ServerSocket downloadServerSocket = new ServerSocket(0);
			
			//发送端口
			
			String port = "" + downloadServerSocket.getLocalPort();
			System.out.println(port);
			//out.write(downloadServerSocket.getLocalPort()+"\r\n");
			baby.getWriter().write(port+"\r\n");
	
			baby.getWriter().flush();
			Socket downloadSocket = downloadServerSocket.accept();
			//监听接口并下载
			
			System.out.println("正在下载"  + file + "...,下载成功将会通知");
			BufferedInputStream inputFromClients = 
					new BufferedInputStream(downloadSocket.getInputStream());
			
			int buf;
			int len = 0;
			while((buf = inputFromClients.read()) != -1) {
				len ++;
				outToFile.write(buf);
			}
			System.out.println("文件一共:"+ len +"字节");
			outToFile.flush();
			outToFile.close();
			System.err.println("下载完成，文件在:"  + path );
			downloadSocket.close();
			downloadServerSocket.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("下载出现问题，可能时你的本地路径输入有误");
			e.printStackTrace();
			
			
		}
		
	}

}
