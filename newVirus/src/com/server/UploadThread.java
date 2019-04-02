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
			//�򿪱����ļ���
			BufferedInputStream inputFile = 
					new BufferedInputStream(new FileInputStream(file));
			//��������˿�
			
			ServerSocket downloadServerSocket = new ServerSocket(0);
			
			//���Ͷ˿�
			
			String port = "" + downloadServerSocket.getLocalPort();
			//System.out.println(port);
			//out.write(downloadServerSocket.getLocalPort()+"\r\n");
			baby.getWriter().write(port+"\r\n");
	
			baby.getWriter().flush();
			Socket downloadSocket = downloadServerSocket.accept();
			//�����ӿڲ�����
			
			System.out.println("�����ϴ�"  + file + "...,�ϴ��ɹ�����֪ͨ");
			BufferedOutputStream outputToClients = 
					new BufferedOutputStream(downloadSocket.getOutputStream());
			
			int buf;
			int len = 0;
			while((buf = inputFile.read()) != -1) {
				len ++;
				outputToClients.write(buf);
			}
			System.out.println("�ļ�һ��:"+ len +"�ֽ�");
			outputToClients.flush();
			outputToClients.close();
			inputFile.close();
			System.err.println("�ϴ���ɣ��ļ����ϴ���:"  + path );
			downloadSocket.close();
			downloadServerSocket.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("�ϴ��������⣬����ʱ���·����������");
			e.printStackTrace();
			
			
		}
		
	}

}