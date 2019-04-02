package com.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

public class Baby {
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	public boolean isUsing = false;
	
	public boolean isUsing() {
		return isUsing;
	}

	public void setUsing(boolean isUsing) {
		this.isUsing = isUsing;
	}

	public Baby(Socket socket,BufferedReader reader,BufferedWriter writer) {
		this.socket = socket;
		this.reader = reader;
		this.writer = writer;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public BufferedReader getReader() {
		return reader;
	}

	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}

	public BufferedWriter getWriter() {
		return writer;
	}

	public void setWriter(BufferedWriter writer) {
		this.writer = writer;
	}
	

}
