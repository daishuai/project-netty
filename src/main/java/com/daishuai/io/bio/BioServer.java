package com.daishuai.io.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class BioServer {

    private ServerSocket serverSocket;

    public BioServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            log.error("创建Socket服务端出错: {}", e.getMessage(), e);
        }
    }

    /**
     * 启动Socket服务端
     */
    public void start() {
        new Thread(this::doStart).start();
    }

    /**
     * 服务端业务处理
     */
    private void doStart() {
        for (;;) {
            try {
                log.info("等待客户端的接入");
                Socket accept = serverSocket.accept();
                new ClientHandler(accept).handle();
            } catch (IOException e) {
                log.error("服务端处理异常: {}", e.getMessage(), e);
            }
        }
    }
}
