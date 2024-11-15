import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class SimpleClient {

    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;


    // Client Socket = CS

    void connectToServer( int serverPort, NewConnectionRequest request) throws IOException {

        // Alterar para ser recebi como argumento no GUI
        InetAddress endereco = InetAddress.getByName( "localhost");

        socket = new Socket(endereco, serverPort);

        System.out.println("Socket:" + socket);

        DealWithClient dealWithClient = new DealWithClient(socket);

        dealWithClient.start();

        dealWithClient.sendConnectionRequest(request);

        System.out.println("Conectado com sucesso ao servidor!");

    }

    public void searchForMusic () {

    }



}
