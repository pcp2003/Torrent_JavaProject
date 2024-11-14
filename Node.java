import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Node {

    private List<Node> knownedNodes = new ArrayList<Node>();
    private int port;
    private List<String> files;
    private String folderName;
    public SimpleServer server;
    public SimpleClient client;

    public Node(int port, String folderName)  {
        this.port = port;
        this.folderName = folderName;
        for (File file : new File("folders").listFiles()) {
            if (file.isFile() && file.getName().endsWith("mp3")) {
                files.add(file.getName());
            }
        }
        this.server = new SimpleServer(port);
        this.client = new SimpleClient();

        try {
            server.startServing();
        } catch (IOException e) {

        }

    }

    public void startClient ( int port ) {
        client.runClient(port);
    }

    public void connect(Node node) {
        knownedNodes.add(node);
    }

    public List<String> getFiles() {
        return files;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "localhost" + ':' + port;

    }
}
