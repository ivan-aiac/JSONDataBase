package client;

import com.beust.jcommander.JCommander;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        Arguments arguments = new Arguments();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);
        String address = "127.0.0.1";
        int port = 23456;
        try (Socket socket = new Socket(address, port);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
            System.out.println("Client started!");
            String json = arguments.toJson();
            output.writeUTF(json);
            System.out.printf("Sent: %s%n", json);
            String res = input.readUTF();
            System.out.printf("Received: %s%n", res);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
