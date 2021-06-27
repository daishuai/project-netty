package com.daishuai.io.nio;

public class NioDemo {

    public static void main(String[] args) throws InterruptedException {
        NioServer nioServer = new NioServer(59999);
        new Thread(nioServer::start).start();
    }
}
