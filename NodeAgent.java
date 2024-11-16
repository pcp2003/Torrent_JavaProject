import java.io.*;
import java.net.Socket;

public class NodeAgent extends Thread {
    private int agentId;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;

    public NodeAgent(int agentId, Socket socket) throws IOException {
        this.agentId = agentId;
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

//        System.out.println("Socket: " + socket);
        System.out.println(this);

        try {

            NewConnectionRequest request = (NewConnectionRequest) in.readObject();
            System.out.println("Request received from client: " + request);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String toString() {
        return "NodeAgent{" +
                "agentId=" + agentId +
                ", socket=" + socket +
                '}';
    }

}
