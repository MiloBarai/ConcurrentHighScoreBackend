package com.test.concurrent.high.score.backend;

import com.test.concurrent.high.score.backend.server.Server;

import java.io.IOException;

public class Application {
    public static void main(String ... args) throws IOException {
        int defaultPort = 8080;
        if(args.length > 0){
            defaultPort = Integer.parseInt(args[0]);
        }
        Server s = new Server(defaultPort);
        s.start();
    }
}
