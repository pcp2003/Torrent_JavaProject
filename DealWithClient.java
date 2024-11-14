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

        while (true) {
            String str = in.readLine();
            if (str.equals("FIM"))
                break;
            System.out.println("Eco:" + str);
            out.println(str);
        }
    }
}
