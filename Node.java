import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Node {

    private List<DealWithClient> dealWithClientList = new ArrayList<DealWithClient>();
    private final int PORTO;
    private List<String> files;
    private String folderName;
    public SimpleServer server;
    public SimpleClient client;
    public NewConnectionRequest request;

    public Node(int PORTO, String folderName) {
        this.PORTO = PORTO;
        this.folderName = folderName;
        this.request = new NewConnectionRequest(PORTO);
        for (File file : new File("folders" + File.separator + folderName).listFiles()) {
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


    public void connectClient(int serverPort, NewConnectionRequest request) {

        try {
            client.connectToServer(serverPort, request);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<String> getFiles() {
        return files;
    }

    public int getPort() {
        return PORTO;
    }

    public NewConnectionRequest getConnectionRequest() {
        return request;
    }

    @Override
    public String toString() {
        return "localhost" + ':' + PORTO;

    }
}
