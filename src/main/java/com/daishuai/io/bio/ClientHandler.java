package com.daishuai.io.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ClientHandler {

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }


    public void handle() {
        log.info("新的客户端接入");
        new Thread(this::process).start();
    }

    private void process() {
        try {
            InputStream inputStream = socket.getInputStream();
            for (;;) {
                byte[] datas = new byte[1024];
                int length;
                while ((length = inputStream.read(datas)) != -1) {
                    String message = new String(datas, StandardCharsets.UTF_8);
                    log.info("收到来自客户端的消息: {}", message);
                    socket.getOutputStream().write(datas);
                }
            }
        } catch (IOException e) {
            log.error("处理业务异常: {}", e.getMessage(), e);
        }
    }
}
