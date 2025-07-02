package com.hit.server;

public class ServerDriver {
    public static void main(String[] args) {
        Server server = new Server(34567);
        new Thread(server).start();

        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.stop();
        }));
    }
}
