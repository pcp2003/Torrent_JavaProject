import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class SimpleClient {

    private Socket clientSocket;

    void connectToServer( int serverPort, NewConnectionRequest request) throws IOException {

        InetAddress endereco = InetAddress.getByName( "localhost");

        clientSocket = new Socket(endereco, serverPort);

        System.out.println("Client Socket:" + clientSocket);

        NodeAgent nodeAgent = new NodeAgent(clientSocket);

        nodeAgent.start();

        nodeAgent.sendConnectionRequest(request);

    }

    public void searchForMusic () {

    }



}
