package com.daishuai;

import com.daishuai.io.bio.BioServer;
import com.daishuai.io.nio.NioServer;

public class MainDemo {

    public static void main(String[] args) {
        NioServer nioServer = new NioServer(59999);
        nioServer.start();
    }
}
