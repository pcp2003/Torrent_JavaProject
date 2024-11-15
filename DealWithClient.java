import java.io.*;
import java.net.Socket;

public class DealWithClient extends Thread {

    private BufferedReader in;
    private PrintWriter out;

    public DealWithClient(Socket socket) throws IOException {
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

    void doConnections(Socket socket) throws IOException {
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())),
                true);
    }

    private void serve() throws IOException {

        System.out.println("Serving...");

        // Recebe a porta do cliente para conexão reversa
        String clientPortStr = in.readLine();
        int clientPort = Integer.parseInt(clientPortStr.trim());

        System.out.println( "Mensagem recebida: " + clientPort );

        // Conecta de volta ao cliente que se conectou a este servidor
//        serverNode.connectClient(clientPort);


    }
}
