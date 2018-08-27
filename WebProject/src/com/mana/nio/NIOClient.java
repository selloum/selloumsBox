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
	
	//�������ݻ�����
	private static ByteBuffer sendBuffer =ByteBuffer.allocate(1024);
	
	//�������ݻ�����
	private static ByteBuffer receiveBuffer=ByteBuffer.allocate(1024);
	
	//�������˵�ַ
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
			 * �ͻ�����������˷�������������
			 */
			SocketChannel socketChannel=SocketChannel.open();
			socketChannel.configureBlocking(false);
			selector=Selector.open();
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
			socketChannel.connect(SERVER);
			/*
			 * ��ѯ�����ͻ�����ע���¼��ķ���
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
			 * ���ӽ����¼����ѳɹ����ӵ�������
			 */
			client=(SocketChannel)selectionKey.channel();
			if(client.isConnectionPending()) {
				client.finishConnect();
				System.out.println("connect success!");
				sendBuffer.clear();
				sendBuffer.put((new Date()+"connect!").getBytes());
				sendBuffer.flip();
				client.write(sendBuffer);//������Ϣ����������
				/*
				 * �����߳�һֱ�����ͻ������룬����Ϣ����������������
				 * ��Ϊ�����������ģ����Ե����̼߳���
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
								 * δע��WRITE�¼�����Ϊ�󲿷�ʱ��channel���ǿ���д��
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
			//ע����¼�
			client.register(selector, SelectionKey.OP_READ);
		}else if (selectionKey.isReadable()) {
			/*
			 * �ж��¼�����
			 * �дӷ������˷��͹�������Ϣ����ȡ�������Ļ�Ϻ󣬼���ע����¼�
			 * �����������˷�����Ϣ
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
