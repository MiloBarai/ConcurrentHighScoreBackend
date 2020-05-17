package com.test.concurrent.high.score.backend.server;

import com.sun.net.httpserver.HttpServer;
import com.test.concurrent.high.score.backend.controller.ServerControllerImpl;
import com.test.concurrent.high.score.backend.model.ScoreHandlerImpl;
import com.test.concurrent.high.score.backend.model.UserSessionHandlerImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Server for starting and receiving http calls
 * Creates a server on the provided port on localhost.
 * Uses {@code newFixedThreadPool} as executor for having
 * threads to pick between when a call is received.
 */
public class Server {
    private int serverPort;
    private String serverStringAddress;
    private HttpServer httpServer;

    public Server(int serverPort) {
        this.serverPort = serverPort;
        serverStringAddress = "localhost:" + serverPort;

    }

    /**
     * Starts the server. if port is taken a IOException will be thrown
     * @throws IOException
     */
    public void start() throws IOException {
        System.out.println("Starting up server on: " + serverStringAddress);

        httpServer = HttpServer.create(new InetSocketAddress("localhost", serverPort), 0);
        httpServer.setExecutor(Executors.newFixedThreadPool(10));

        /*Something like the function bellow would had been great to have,
        unfortunately createContext does not seem to support dynamic addresses*/
        //httpServer.createContext("/*/login", exchange -> {});

        httpServer.createContext("/", new HttpRequestHandler(
                        new ServerControllerImpl(
                                UserSessionHandlerImpl.getInstance(),
                                ScoreHandlerImpl.getInstance()
                        )));

        httpServer.start();
        System.out.println("Server Successfully started on: " + serverStringAddress);
    }

    /**
     * Stops the HTTP server.
     */
    public void stop(){
        httpServer.stop(0);
    }

}
