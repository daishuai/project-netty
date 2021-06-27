package com.daishuai.io.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

@Slf4j
public class NioClient {


    private SocketChannel socketChannel;

    public NioClient(int port) {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress("localhost", 59999));

            while (true) {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    if (!selectionKey.isValid()) {
                        continue;
                    }
                    if (selectionKey.isConnectable()) {
                        if (socketChannel.finishConnect()) {
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            this.sendMessage("Hello NioServer");
                        }
                    }
                    if (selectionKey.isReadable()) {
                        this.process(selectionKey, "Receive Message");
                    }
                }
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(message.getBytes().length);
        byteBuffer.put(message.getBytes());
        byteBuffer.flip();
        try {
            socketChannel.write(byteBuffer);
            if (!byteBuffer.hasRemaining()) {
                log.info("发送消息成功");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(SelectionKey selectionKey, String message) {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            int length = socketChannel.read(byteBuffer);
            log.info("收到服务端的消息: {}", new String(byteBuffer.array(), 0, length));
            Thread.sleep(2000);
            this.response(selectionKey, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void response(SelectionKey selectionKey, String message) {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(message.getBytes().length);
        byteBuffer.put(message.getBytes());
        byteBuffer.flip();
        try {
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
