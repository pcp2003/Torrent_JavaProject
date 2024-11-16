import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class SimpleServer {

    private int port;

    public SimpleServer(int port) {
        this.port = port;
    }

    public void startServing() throws IOException {

        ServerSocket ss = new ServerSocket(port);
        try {
            while (true) {

                Socket normalSocket = ss.accept();
                new NodeAgent(normalSocket).start();


            }
        } finally {
            ss.close();
        }
    }

}
