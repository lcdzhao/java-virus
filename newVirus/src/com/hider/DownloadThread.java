package com.hider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DownloadThread extends Thread{
	
	private BufferedInputStream input;
	private int downloadPort;
	
	
	public DownloadThread(BufferedInputStream input,int downloadPort) {
		// TODO Auto-generated constructor stub
		this.input = input;
		this.downloadPort = downloadPort;
	}
	
	
	public void run() {
		
		try {
			
			System.out.println(downloadPort);
			Socket downloadSocket = new Socket(ConnectToZhao.host, downloadPort);
			//System.out.println("连接成功");
			BufferedOutputStream out = new BufferedOutputStream(downloadSocket.getOutputStream());
			GetSystemFiles.download(input,out);
			downloadSocket.close();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
