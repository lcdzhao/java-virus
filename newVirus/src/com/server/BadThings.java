package com.server;

public class BadThings {
	
	
	
	public static void main(String[] args) {
		ReciveBabysThread getBabyThread = new ReciveBabysThread(10086);
		getBabyThread.start();
		RemoveDeadClientThread removeDeadClientThread = 
				new RemoveDeadClientThread(getBabyThread.getBabys());
		removeDeadClientThread.start();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ConnectToClientsThread connectToClientsThread = 
				new ConnectToClientsThread(getBabyThread.getBabys());
		connectToClientsThread.start();
		
	}
	
	

}
