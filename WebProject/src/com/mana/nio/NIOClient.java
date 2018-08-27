package com.mana.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;

import javax.print.CancelablePrintJob;


public class NIOClient {
	
	//发送数据缓冲区
	private static ByteBuffer sendBuffer =ByteBuffer.allocate(1024);
	
	//接收数据缓冲区
	private static ByteBuffer receiveBuffer=ByteBuffer.allocate(1024);
	
	//服务器端地址
	private InetSocketAddress SERVER;
	
	private static Selector  selector;
	
	private static SocketChannel client;
	
	private static String receiveText;
	
	private static String sendText;
	
	private static int count=0;
	
	public NIOClient(int port) {
		SERVER=new InetSocketAddress("localhost", port);
		init();
	}

	private void init() {
		try {
			/*
			 * 客户端向服务器端发起建立连接请求
			 */
			SocketChannel socketChannel=SocketChannel.open();
			socketChannel.configureBlocking(false);
			selector=Selector.open();
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
			socketChannel.connect(SERVER);
			/*
			 * 轮询监听客户端上注册事件的发生
			 */
			while(true) {
				selector.select();
				Set<SelectionKey> keySet=selector.selectedKeys();
				for(final SelectionKey key:keySet) {
					handle(key);
				}
				;
				keySet.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void handle(SelectionKey selectionKey) throws IOException {
		if(selectionKey.isConnectable()) {
			/*
			 * 连接建立事件，已成功连接到服务器
			 */
			client=(SocketChannel)selectionKey.channel();
			if(client.isConnectionPending()) {
				client.finishConnect();
				System.out.println("connect success!");
				sendBuffer.clear();
				sendBuffer.put((new Date()+"connect!").getBytes());
				sendBuffer.flip();
				client.write(sendBuffer);//发送信息至服务器端
				/*
				 * 启动线程一直监听客户端输入，有信息输入则发往服务器端
				 * 因为输入是阻塞的，所以单独线程监听
				 */
				
				new Thread() {
					@Override
					public void run() {
						while(true) {
							try {
								
								sendBuffer.clear();
								Scanner scanner=new Scanner(System.in);
								sendText=scanner.nextLine();
								System.out.println(sendText);
								/*
								 * 未注册WRITE事件，因为大部分时间channel都是可以写的
								 */
								sendBuffer.put(sendText.getBytes("utf-8"));
								sendBuffer.flip();
								client.write(sendBuffer);
							} catch (IOException e) {
								e.printStackTrace();
								break;
							}
						}
					};
				}.start();
			}
			//注册读事件
			client.register(selector, SelectionKey.OP_READ);
		}else if (selectionKey.isReadable()) {
			/*
			 * 有读事件触发
			 * 有从服务器端发送过来的信息，读取输出到屏幕上后，继续注册读事件
			 * 监听服务器端发送信息
			 */
			client=(SocketChannel)selectionKey.channel();
			receiveBuffer.clear();
			count=client.read(receiveBuffer);
			if(count>0) {
				receiveText=new String(receiveBuffer.array(), 0, count);
				System.out.println(receiveText);
				client=(SocketChannel)selectionKey.channel();
				client.register(selector, SelectionKey.OP_READ);
			}
		}
		
	}
	
	public static void main(String[] args) throws IOException{
		new NIOClient(7777);
	}
	
}	
