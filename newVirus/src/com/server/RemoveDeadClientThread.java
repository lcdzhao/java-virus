package com.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class RemoveDeadClientThread extends Thread {

	private ArrayList<Baby> babys;
	private ArrayList<Baby> removeQueue = new ArrayList<Baby>();

	public RemoveDeadClientThread(ArrayList<Baby> babys) {
		// TODO Auto-generated constructor stub
		this.babys = babys;
	}

	// 每60秒检查并移除一次断开连接的客户端
	public void run() {
		while (true) {
			RemoveDead();
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 移除连接断开的客户端
	public void RemoveDead() {
		if (babys.size() != 0) {
			for (Baby baby : babys) {
				if (!isAlive(baby))
					try {
						baby.getSocket().close();
						baby.getReader().close();
						baby.getWriter().close();
						removeQueue.add(baby);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.err.println("移除断开连接客户端时发生的小错误");
						;
					}
				
			}
			for (Baby baby : removeQueue) {
				babys.remove(baby);				
			}
			
			removeQueue.clear();
		}
	}

	private boolean isAlive(Baby baby) {
		if (!baby.isUsing()) {
			BufferedReader babyReader = baby.getReader();
			BufferedWriter babyWriter = baby.getWriter();
			try {
				babyWriter.write("server: are you alive\r\n");
				babyWriter.flush();
				if (babyReader.readLine().equals("client: i,m alive"))
					return true;
				else
					return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return false;
			}
		}
		return true;
	}
}
