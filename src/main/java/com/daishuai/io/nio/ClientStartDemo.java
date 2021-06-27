package com.daishuai.io.nio;

public class ClientStartDemo {
    public static void main(String[] args) {
        NioClient nioClient = new NioClient(59999);
        nioClient.start();
    }
}
