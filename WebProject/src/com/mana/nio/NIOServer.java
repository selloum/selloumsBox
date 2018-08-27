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
	
	//����Buffer
	private Charset charset=Charset.forName("utf-8");
	
	//�������ݻ�����
	private static ByteBuffer receiveBuffer=ByteBuffer.allocate(1024);
	
	//�������ݻ�����
	private static ByteBuffer sendBuffer=ByteBuffer.allocate(1024);

	//ӳ��ͻ���Channel
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
		 * �����������ˣ�����Ϊ���������󶨶˿ڣ�ע��accept�¼�
		 * ACCEPT�¼�����������յ��ͻ�����������ʱ���������¼�
		 */
		ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
		//����Ϊ������
		serverSocketChannel.configureBlocking(false);
		ServerSocket serverSocket=serverSocketChannel.socket();
		//�󶨶˿�
		serverSocket.bind(new InetSocketAddress(port));
		selector=Selector.open();
		//ע��accep�¼�
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("server start on port: "+port);
	}
	
	//����������ѯ������select������һֱ��ֱ��������¼�������ʱ
	private void listen() {
		while(true) {
			try {
				selector.select();//����ֵΪ���δ������¼���
				Set<SelectionKey> selectionKeys=selector.selectedKeys();
				for(SelectionKey key:selectionKeys) {
					handle(key);
				}
				selectionKeys.clear();//�����������¼�
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

	//����ͬ�¼�
	private void handle(SelectionKey selectionKey) throws IOException{
		ServerSocketChannel serverSocketChannel=null;
		SocketChannel client=null;
		String receiveText=null;
		int count =0;
		if(selectionKey.isAcceptable()) {
			
			/**
			 * �ͻ������������¼�
			 * serversocketΪ�ÿͻ��˽���socket���ӣ�����socketע��ΪRead�¼��������ͻ�������
			 * READ�¼������ͻ��˷������ݣ����ѱ�����˿����߳���ȷ��ȡʱ���������¼�
			 */
			serverSocketChannel=(ServerSocketChannel)selectionKey.channel();
			client=serverSocketChannel.accept();
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ);
			
		}else if(selectionKey.isReadable()) {
			/*
			 * READ�¼����յ��ͻ��˷������ݣ���ȡ���ݺ����ע������ͻ���
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

	//�ѵ�ǰ�ͻ�����Ϣ�����͵������ͻ���
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
					//�����ͨ��
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
