import java.io.*;
import java.net.Socket;
import java.util.List;

public class NodeAgent extends Thread {
    private Node node;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;

    public NodeAgent(Node node, Socket socket)  {
        this.node = node;
        this.socket = socket;
        doConnections();
    }

    @Override
    public void run() {

        serve();

    }

    // Envia pedidos de cone
    public void sendConnectionRequest( NewConnectionRequest request) {

        try {
            out.writeObject(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendFilesList (List<String> files ){

        String[] filesList = files.toArray(new String[0]);

        try {
            out.writeObject(filesList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // Realiza as conexões dos canais dos sockets
    void doConnections() {

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    // Funções realizadas pelo servidor
    private void serve() {

        System.out.println(this);

        try {

            Object obj = in.readObject();

            switch (obj) {

                case String [] files -> {

                }
                case NewConnectionRequest request -> {
                    System.out.println("Request received from client: " + request);
                }
                default -> System.out.println("Tipo desconhecido.");
            }


        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String toString() {
        return "NodeAgent{" +
                "node=" + node +
                ", socket=" + socket +
                '}';
    }


}
