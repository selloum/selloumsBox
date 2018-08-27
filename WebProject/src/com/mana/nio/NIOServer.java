package com.mana.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;



public class NIOServer {

	private int port=8888;
	
	//解码Buffer
	private Charset charset=Charset.forName("utf-8");
	
	//接收数据缓存区
	private static ByteBuffer receiveBuffer=ByteBuffer.allocate(1024);
	
	//发送数据缓存区
	private static ByteBuffer sendBuffer=ByteBuffer.allocate(1024);

	//映射客户端Channel
	private Map<String, SocketChannel> clientsMap=new HashMap<String,SocketChannel>();
	
	private static Selector selector;
	
	public NIOServer(int port) {
		this.port=port;
		try {
			init();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() throws IOException{
		/**
		 * 启动服务器端，配置为非阻塞，绑定端口，注册accept事件
		 * ACCEPT事件：当服务端收到客户端连接请求时，触发该事件
		 */
		ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
		//配置为非阻塞
		serverSocketChannel.configureBlocking(false);
		ServerSocket serverSocket=serverSocketChannel.socket();
		//绑定端口
		serverSocket.bind(new InetSocketAddress(port));
		selector=Selector.open();
		//注册accep事件
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("server start on port: "+port);
	}
	
	//服务器端轮询监听，select方法会一直阻直到有相关事件发生或超时
	private void listen() {
		while(true) {
			try {
				selector.select();//返回值为本次触发的事件数
				Set<SelectionKey> selectionKeys=selector.selectedKeys();
				for(SelectionKey key:selectionKeys) {
					handle(key);
				}
				selectionKeys.clear();//清除处理过的事件
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

	//处理不同事件
	private void handle(SelectionKey selectionKey) throws IOException{
		ServerSocketChannel serverSocketChannel=null;
		SocketChannel client=null;
		String receiveText=null;
		int count =0;
		if(selectionKey.isAcceptable()) {
			
			/**
			 * 客户端请求连接事件
			 * serversocket为该客户端建立socket连接，将此socket注册为Read事件，监听客户端输入
			 * READ事件：当客户端发来数据，并已被服务端控制线程正确读取时，触发该事件
			 */
			serverSocketChannel=(ServerSocketChannel)selectionKey.channel();
			client=serverSocketChannel.accept();
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ);
			
		}else if(selectionKey.isReadable()) {
			/*
			 * READ事件：收到客户端发送数据，读取数据后继续注册监听客户端
			 */
			client=(SocketChannel)selectionKey.channel();
			receiveBuffer.clear();
			count=client.read(receiveBuffer);
			if(count>0) {
				receiveBuffer.flip();
				receiveText=String.valueOf(charset.decode(receiveBuffer).array());
				System.out.println(client.toString()+":"+receiveText);
				dispatch(client,receiveText);
				client=(SocketChannel)selectionKey.channel();
			}else if (count==-1) {
				selectionKey.cancel();
				client.close();
			}
		}
		
	}

	//把当前客户端信息，推送到其他客户端
	private void dispatch(SocketChannel client, String receiveText) throws IOException{
		Socket socket=client.socket();
		String name="["+socket.getInetAddress().toString().substring(1)+":"+Integer.toHexString(client.hashCode())+"]";
		if(!clientsMap.isEmpty()) {
			for(Map.Entry<String, SocketChannel> entry:clientsMap.entrySet()) {
				SocketChannel temp=entry.getValue();
				if(!client.equals(temp)) {
					sendBuffer.clear();
					sendBuffer.put((name+":"+receiveText).getBytes());
					sendBuffer.flip();
					//输出到通道
					temp.write(sendBuffer);
				}
			}
		}
		clientsMap.put(name, client);
		
	}
	
//	public static void main(String[] args)throws IOException{
//		NIOServer server=new NIOServer(7777);
//		server.listen();
//	}
	
}
