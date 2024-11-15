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

    void connectToServer( int serverPort, int clientPort) throws IOException {
        InetAddress endereco = InetAddress.getByName( null);
        System.out.println("Endereco:" + endereco);
        socket = new Socket(endereco, serverPort);
        System.out.println("Socket:" + socket);
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())),
                true);

        System.out.println("Conectado com sucesso ao servidor!");
        System.out.println( "Enviando o porto: " + clientPort + " para o servidor" );
        out.println(clientPort);

    }

    public void searchForMusic () {

    }



}
