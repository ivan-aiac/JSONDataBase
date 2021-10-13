package server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static volatile boolean running;

    public static void main(String[] args) {
        String address = "127.0.0.1";
        int port = 23456;
        running = true;
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try (ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(address))) {
            System.out.println("Server started!");
            while (running) {
                Socket socket = server.accept();
                executor.submit(() -> {
                    try (DataInputStream input = new DataInputStream(socket.getInputStream());
                         DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
                        String response = handleRequest(input.readUTF());
                        System.out.printf("Sent: %s%n", response);
                        output.writeUTF(response);
                        socket.close();
                        if (!running) {
                            server.close();
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        executor.shutdown();
    }

    private static String handleRequest(String requestString) {
        System.out.printf("Received: %s%n", requestString);
        Request request = Request.fromJsonObject(JsonParser.parseString(requestString).getAsJsonObject());
        switch (request.getType().toUpperCase(Locale.ROOT)) {
            case "SET":
                return setRecord(request.getKeyPath(), request.getJsonValue());
            case "GET":
                return getRecord(request.getKeyPath());
            case "DELETE":
                return deleteRecord(request.getKeyPath());
            case "EXIT":
                return exit();
            default:
                return Response.jsonErrorResponse("Not and option");
        }
    }

    private static String exit() {
        running = false;
        return Response.jsonOkResponse();
    }

    private static String setRecord(List<String> key, JsonElement value) {
        return DataBase.setRecord(key, value);
    }

    private static String getRecord(List<String> key) {
        return DataBase.getRecord(key);
    }

    private static String deleteRecord(List<String> key) {
        return DataBase.deleteRecord(key);
    }

}
