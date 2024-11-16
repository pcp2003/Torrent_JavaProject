import java.io.File;
import java.io.IOException;
import java.util.*;

public class Node {

    private final int port;
    public SimpleServer server;
    public SimpleClient client;
    public NewConnectionRequest request;
    private String pathToFolder;
    private List<String> files = new ArrayList<String>();
    private List<NodeAgent> nodeAgentList = new ArrayList<NodeAgent>();

    public Node(int port, String folderName) {
        this.port = port;
        this.pathToFolder = ("folders" + File.separator + folderName);
        this.request = new NewConnectionRequest(port);
        updateFilesList();
        this.server = new SimpleServer(port);
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

    public void connectClient(int serverPort, NewConnectionRequest request) {

        try {
            client.connectToServer(serverPort, request);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void updateFilesList () {

        for (File file : new File(pathToFolder).listFiles()) {
            if (file.isFile() && file.getName().endsWith("mp3")) {
                files.add(file.getName());
            }
        }
    }

    public List<String> getFiles() {
        return files;
    }

    public int getPort() {
        return port;
    }

    public NewConnectionRequest getConnectionRequest() {
        return request;
    }

    @Override
    public String toString() {
        return "localhost" + ':' + port;

    }
}
