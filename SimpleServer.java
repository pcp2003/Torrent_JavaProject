import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class SimpleServer {

    private int PORTO;


    public SimpleServer(int port) {
        PORTO = port;
    }


    public void startServing() throws IOException {
        ServerSocket ss = new ServerSocket(PORTO);
        try {
            while(true){
                Socket socket = ss.accept();
                new DealWithClient(socket).start();
            }
        } finally {
            ss.close();
        }
    }

    public int getPort(){
        return PORTO;
    }

}
