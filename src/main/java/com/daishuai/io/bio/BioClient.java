package com.daishuai.io.bio;

import java.io.IOException;
import java.net.Socket;

public class BioClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 50000);
        new Thread(() -> {
            while (true) {
                String message = "Hello World";
                try {
                    socket.getOutputStream().write(message.getBytes());
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
