import java.io.*;
import java.net.*;

public class NetworkHandler {
    private Socket socket;
    private ServerSocket serverSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isServer;
    private CrashCrave game;

    public NetworkHandler(CrashCrave game, boolean isServer, String hostIP, int port) throws IOException {
        this.game = game;
        this.isServer = isServer;

        if (isServer) {
            serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
        } else {
            socket = new Socket(hostIP, port);
        }

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        new Thread(this::listenForMessages).start();
    }

    public void sendMessage(String message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForMessages() {
        try {
            while (true) {
                String message = (String) in.readObject();
                game.handleReceivedMessage(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        if (socket != null) socket.close();
        if (serverSocket != null) serverSocket.close();
    }
}
