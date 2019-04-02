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
			//�򿪱����ļ���
			BufferedOutputStream outToFile = 
					new BufferedOutputStream(new FileOutputStream(path));
			//��������˿�
			
			ServerSocket downloadServerSocket = new ServerSocket(0);
			
			//���Ͷ˿�
			
			String port = "" + downloadServerSocket.getLocalPort();
			System.out.println(port);
			//out.write(downloadServerSocket.getLocalPort()+"\r\n");
			baby.getWriter().write(port+"\r\n");
	
			baby.getWriter().flush();
			Socket downloadSocket = downloadServerSocket.accept();
			//�����ӿڲ�����
			
			System.out.println("��������"  + file + "...,���سɹ�����֪ͨ");
			BufferedInputStream inputFromClients = 
					new BufferedInputStream(downloadSocket.getInputStream());
			
			int buf;
			int len = 0;
			while((buf = inputFromClients.read()) != -1) {
				len ++;
				outToFile.write(buf);
			}
			System.out.println("�ļ�һ��:"+ len +"�ֽ�");
			outToFile.flush();
			outToFile.close();
			System.err.println("������ɣ��ļ���:"  + path );
			downloadSocket.close();
			downloadServerSocket.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("���س������⣬����ʱ��ı���·����������");
			e.printStackTrace();
			
			
		}
		
	}

}
