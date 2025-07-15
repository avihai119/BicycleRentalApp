package com.hit.server;

public class ServerDriver {
    public static void main(String[] args) {
        // Create the server instance
        Server server = new Server(34567);

        // Start the server on a separate thread
        Thread serverThread = new Thread(server);
        serverThread.start();

        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.stop();
            try {
                serverThread.join(5000); // Wait up to 5 seconds for server to stop
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for server to stop");
                Thread.currentThread().interrupt();
            }
        }));

        System.out.println("Server started on port 34567. Press Ctrl+C to stop.");
    }
}