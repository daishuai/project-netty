package com.daishuai.io.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

@Slf4j
public class NioServer {

    private ServerSocketChannel serverSocketChannel;

    public NioServer(int port) {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
        } catch (IOException e) {
            log.error("创建Nio服务端出错: {}", e.getMessage(), e);
        }
    }

    public void start() {
        try {
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    if (!selectionKey.isValid()) {
                        continue;
                    }
                    if (selectionKey.isAcceptable()) {
                        this.acceptConnection(selectionKey, selector);
                    }
                    if (selectionKey.isReadable()) {
                        this.process(selectionKey);
                    }
                }
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptConnection(SelectionKey selectionKey, Selector selector) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        try {
            SocketChannel accept = serverSocketChannel.accept();
            accept.configureBlocking(false);
            accept.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(SelectionKey selectionKey) {
        String message = "";
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            int length = socketChannel.read(byteBuffer);
            if (length < 0) {
                socketChannel.close();
                selectionKey.channel();
            } else if (length == 0) {
                message = "";
            } else {
                message = new String(byteBuffer.array(), 0, length);
            }
            log.info("收到客户端的消息: {}", message);
            this.response(selectionKey, "Hello Client");
        } catch (IOException e) {
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
