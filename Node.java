import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Node {

    private List<Integer> knownedNodesPorts = new ArrayList<Integer>();
    private final int PORTO;
    private List<String> files;
    private String folderName;
    public SimpleServer server;
    public SimpleClient client;

    public Node(int PORTO, String folderName) {
        this.PORTO = PORTO;
        this.folderName = folderName;
        for (File file : new File("folders").listFiles()) {
            if (file.isFile() && file.getName().endsWith("mp3")) {
                files.add(file.getName());
            }
        }
        this.server = new SimpleServer(PORTO);
        this.client = new SimpleClient();

        // Iniciar o servidor em uma thread separada
        new Thread(() -> {
            try {
                server.startServing();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void connectClient(int portServer) {

        if (!isConnected(portServer)) {
            // Adiciono a porta a lista de portas para evitar iniciar a conexão novamente
            knownedNodesPorts.add(portServer);
            try {
                client.connectToServer(portServer, PORTO);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Already connected!");
        }

    }

    private boolean isConnected(int port) {
        for (int p : knownedNodesPorts) {
            if (p == port) {
                return true;
            }
        }
        return false;
    }

    public List<String> getFiles() {
        return files;
    }

    public int getPort() {
        return PORTO;
    }

    @Override
    public String toString() {
        return "localhost" + ':' + PORTO;

    }
}
