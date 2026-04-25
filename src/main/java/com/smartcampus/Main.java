package com.smartcampus;



import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import java.net.URI;

public class Main {
    public static final String BASE_URI = "http://localhost:8080/";

    public static HttpServer startServer() {
        // Note: Make sure com.smartcampus.config.SmartCampus matches your actual config file name!
        final ResourceConfig rc = ResourceConfig.forApplication(new com.smartcampus.config.SmartCampus());
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) {
        try {
            final HttpServer server = startServer();
            // FIX: Hardcode the print statement to include the /api/v1/ path so it's clickable
            System.out.println("Smart Campus API running at: http://localhost:8080/api/v1/");
            System.out.println("Press Enter to shutdown...");
            System.in.read();
            server.shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}