package net.uzlezz.crypman.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ResourceBundle;

public class ServerClient {
    private static ServerClient instance;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private ServerClient() {
        connect();
    }

    public static synchronized ServerClient getInstance() {
        if (instance == null) {
            try {
                instance = new ServerClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    private void connect() {
        var bundle = ResourceBundle.getBundle("server");
        var address = bundle.getString("SERVER_ADDRESS");
        var port = Integer.parseInt(bundle.getString("SERVER_PORT"));

        try {
            socket = new Socket(address, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            instance = null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Response sendRequest(Request request) {
        try {
            System.out.println("Sending request to endpoint " + request.getPath());
            out.writeObject(request);
            out.flush();

            return readResponse();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Response readResponse() {
        try {
            return (Response)in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
