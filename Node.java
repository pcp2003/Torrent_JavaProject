import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Node {

    private final int port;
    public NewConnectionRequest request;
    private String pathToFolder;
    private final List<String> files;
    private final List<NodeAgent> nodeAgentList;

    public Node(int port, String folderName) {

        this.port = port;
        this.pathToFolder = ("folders" + File.separator + folderName);
        this.request = new NewConnectionRequest(port);
        this.files = new ArrayList<>();
        this.nodeAgentList = new ArrayList<>();
        updateFilesList();
        startServing();
    }

    void connectToServer(String addr, int serverPort, NewConnectionRequest request) {

        if ( !(serverPort == port) ) {
            try {

                InetAddress endereco = InetAddress.getByName(addr);
                Socket clientSocket = new Socket(endereco, serverPort);
                NodeAgent nodeAgent = new NodeAgent(port, clientSocket);
                nodeAgent.start();
                nodeAgent.sendConnectionRequest(request);

            } catch (IOException e) {
                System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
                e.printStackTrace();
            }
        }else {
            System.out.println("Não é possivel conectar-se ao seu próprio server!");
        }

    }

    public void startServing() {
        new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(port)) {
                while (true) {
                    try {
                        Socket normalSocket = ss.accept();
                        new NodeAgent(port, normalSocket).start();
                    } catch (IOException e) {
                        System.err.println("Erro ao aceitar conexão: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro ao criar ServerSocket: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
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

}
