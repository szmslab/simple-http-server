package com.szmslab.simplehttpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HttpServer {

    private final ExecutorService pool = Executors.newCachedThreadPool();

    private final int port;

    private final RequestHandler requestHandler;

    public HttpServer(int port) {
        this.port = port;
        this.requestHandler = new DefaultRequestHandler();
    }

    public HttpServer(int port, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
    }

    public void start() {
        System.out.println(String.format("=== simple-http-server(port:%d) [start] ===", port));
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                runThread(server.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        if (!pool.isShutdown()) {
            System.out.println(String.format("=== simple-http-server(port:%d) [shutdown] ===", port));
            pool.shutdown();
            try {
                if (!pool.awaitTermination(1, TimeUnit.MINUTES)) {
                    pool.shutdownNow();
                }
            } catch (InterruptedException e) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }
            System.out.println(String.format("=== simple-http-server(port:%d) [terminate] ===", port));
        }
    }

    private void runThread(Socket acceptedSocket) {
        pool.execute(() -> {
            String id = "[" + acceptedSocket.hashCode() + "]";
            System.out.println("Request is accepted. " + id);

            try (Socket socket = acceptedSocket;
                 InputStream inputStream = socket.getInputStream();
                 OutputStream outputStream = socket.getOutputStream()
            ) {
                HttpRequest request = HttpRequest.parse(inputStream);
                HttpResponse response = requestHandler.handleRequest(request);
                response.writeTo(outputStream);

                StringBuilder sb = new StringBuilder();
                sb.append(">>>>>>>>>>>>>>>> " + id + System.lineSeparator());
                sb.append(request == null ? "" : request);
                sb.append("<<<<<<<<<<<<<<<< " + id + System.lineSeparator());
                System.out.println(sb);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
