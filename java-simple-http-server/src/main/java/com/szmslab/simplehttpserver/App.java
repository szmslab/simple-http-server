package com.szmslab.simplehttpserver;


public class App {

    public static void main(String[] args) {
        HttpServer server = new HttpServer(80);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
        }));

        server.start();
    }

}
