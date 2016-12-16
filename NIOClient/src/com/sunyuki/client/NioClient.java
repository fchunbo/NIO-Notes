package com.sunyuki.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 
 * @author 21463
 *
 */
public class NioClient {
	private Selector selector;
	
	public SelectionKey init(String ip,int port) throws IOException{
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);
		
		channel.connect(new InetSocketAddress(ip, port));
		
		this.selector = Selector.open();
		return channel.register(selector, SelectionKey.OP_CONNECT);
	}
	
	public void listen() throws IOException{
		while(true){
			this.selector.select();
			Iterator<SelectionKey> iterator = this.selector.keys().iterator();
			while(iterator.hasNext()){
				SelectionKey key = iterator.next();
				//iterator.remove();
				process(key);
			}
		}
	}

	private void process(SelectionKey key) throws IOException {
		if(key.isConnectable()){
			SocketChannel channel = (SocketChannel) key.channel();
			//如果正在链接，则完成链接
			if(channel.isConnectionPending()){
				channel.finishConnect();
			}
			channel.configureBlocking(false);
			System.out.println("[客户端消息：]服务器链接成功！");
			
			channel.register(selector, SelectionKey.OP_READ);
		}else if(key.isReadable()){
			readAndSend(key);
		}
	}

	private void readAndSend(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		channel.configureBlocking(false);
		
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		channel.read(buffer);
		
		buffer.flip();
		
		System.out.println("[客户端]收到服务器消息 ：" + new String(buffer.array()));
		
		System.out.println("[客户端]请输入消息：");
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String msg = scanner.nextLine();
		
		buffer.clear();
		buffer.put(msg.getBytes());
		
		buffer.flip();
		
		channel.write(buffer);
	}
}
