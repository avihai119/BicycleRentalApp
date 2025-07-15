package com.hit.server;

public class ServerDriver {
    public static void main(String[] args) {
        Server server = new Server(34567);

        Thread serverThread = new Thread(server);
        serverThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.stop();

            try {
                serverThread.join(5000);
            }

            catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for server to stop");
                Thread.currentThread().interrupt();
            }
        }));

        System.out.println("Server started on port 34567. Press Ctrl+C to stop.");
    }
}