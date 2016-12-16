package com.sunyuki.server;

import java.io.IOException;

public class Application {
	public static void main(String[] args) throws IOException {
		NioServer nioServer = new NioServer();
		nioServer.init(8090);
		nioServer.listen();
	}
}
