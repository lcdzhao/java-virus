package com.hider;

import java.io.IOException;

public class BabyHider{

	
	
	/**
	 * 
	 * @param startName
	 * @param path
	 */
	//�޸�ע���
	public static void makeItAutoStartUp(String startName,String path) {
		String regKey = "HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";
		try {
			Runtime.getRuntime().exec("reg add " + regKey + " /v " + startName
					+ " /t reg_sz /d " + path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
