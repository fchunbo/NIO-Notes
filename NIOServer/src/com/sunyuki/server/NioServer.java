package com.sunyuki.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * NioServer
 * @author 21463
 *
 */
public class NioServer {
	/**
	 * 创建selector，是NIO中的核心
	 */
	private Selector selector;
	
	/**
	 * 初始化selector
	 * @param port
	 * @return
	 * @throws IOException
	 */
	public SelectionKey init(int port) throws IOException{
		//创建服务器通道
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		//将通道配置为非阻塞的，通道和selector一起使用必须是非阻塞的
		serverSocketChannel.configureBlocking(false);
		
		//通道绑定端口
		ServerSocket socket = serverSocketChannel.socket();
		socket.bind(new InetSocketAddress(port));
		
		//创建selector
		this.selector = Selector.open();
		
		//注册侦听时间
		SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		return selectionKey;
	}
	
	/**
	 * 侦听
	 * @throws IOException 
	 */
	public void listen() throws IOException{
		System.out.println("服务器启动成功！");
		while(true){
			//选择已就绪的通道
			this.selector.select();
			
			Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
			while(iterator.hasNext()){
				//获取就绪通道注册的事件
				SelectionKey key = iterator.next();
				iterator.remove();
				process(key);
			}
		}
	}

	private void process(SelectionKey key) throws IOException {
		if(key.isAcceptable()){
			ServerSocketChannel server = (ServerSocketChannel) key.channel();
			//获取与客户端的链接通道
			SocketChannel channel = server.accept();
			channel.configureBlocking(false);
			
			//创建数据缓冲区，使用这种方式，不需要调用flip方法，否则channel不能将buffer中的数据发送出去
			ByteBuffer buffer = ByteBuffer.wrap("欢迎来到回射服务器".getBytes());

			//使用这种方式时，先开辟空的缓冲区，然后添加数据，这时需要调用flip方法
//			ByteBuffer buffer = ByteBuffer.allocate(100);
//			buffer.put("欢迎来到回射服务器".getBytes());
//			buffer.flip();
			
			//向客户端发送消息
			channel.write(buffer);
			channel.register(selector, SelectionKey.OP_READ);
		}else if(key.isReadable()){
			readAndWriteMsg(key);
		}
		
		
	}

	private void readAndWriteMsg(SelectionKey key) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		SocketChannel channel = (SocketChannel) key.channel();
		channel.configureBlocking(false);
		
		channel.read(buffer);
		
		System.out.println("服务端收到消息：" + new String(buffer.array()));
		
		buffer.flip();
		
		channel.write(buffer);
		
		//设置为非阻塞
//		channel.configureBlocking(false);
//		
//		channel.register(selector, SelectionKey.OP_READ);
	} 
	
}
