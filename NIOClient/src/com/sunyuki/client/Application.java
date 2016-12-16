package com.sunyuki.client;

import java.io.IOException;

public class Application {
	public static void main(String[] args) throws IOException {
		NioClient client = new NioClient();
		client.init("127.0.0.1", 8090);
		client.listen();
	}
}
