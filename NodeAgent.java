import java.io.*;
import java.net.Socket;

public class NodeAgent extends Thread {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;

    public NodeAgent(Socket socket) throws IOException {
        this.socket = socket;
        doConnections(socket);
    }

    @Override
    public void run() {
        try {
            serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendConnectionRequest( NewConnectionRequest request) throws IOException {

        out.writeObject(request);
    }

    void doConnections(Socket socket) throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

    }

    private void serve() throws IOException {

        System.out.println("Serving ...");

        System.out.println("Socket: " + socket);

        try {

            NewConnectionRequest request = (NewConnectionRequest) in.readObject();
            System.out.println("Request received throught socket: " + request);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
}
