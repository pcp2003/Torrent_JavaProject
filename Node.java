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

    // Função para um cliente se conectar a um servidor de um nó que não seja servidor de si próprio.
    void connectClient(String addr, int serverPort, NewConnectionRequest request) {

        if ( !(serverPort == port) ) {
            try {

                InetAddress endereco = InetAddress.getByName(addr);
                Socket clientSocket = new Socket(endereco, serverPort);
                NodeAgent nodeAgent = new NodeAgent(this, clientSocket); // Cria o agent reponsavel pelo socket do cliente
                nodeAgent.start();
                nodeAgent.sendConnectionRequest(request);
                nodeAgentList.add(nodeAgent);

            } catch (IOException e) {
                System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
                e.printStackTrace();
            }
        }else {
            System.out.println("Não é possivel conectar-se ao seu próprio server!");
        }

    }

    // Função para incializar um servidor de um nó.
    public void startServing() {

        // Precisa ser incializado em uma thread separada porque .accept() fica a espera.

        new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(port)) {
                while (true) {
                    try {
                        Socket normalSocket = ss.accept();
                        NodeAgent nodeAgent = new NodeAgent(this, normalSocket); // Cria o agent responsavel pelo socket do servidor
                        nodeAgent.start();
                        nodeAgentList.add(nodeAgent);

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

    // Função para buscar as music que possuam no nome a palavra introduzida no "Procurar"
    public List<String> searchMusic(String wordToSearch) {
        System.out.println("Iniciando busca por: " + wordToSearch);

        for (NodeAgent nodeAgent : nodeAgentList) {
            System.out.println("Enviando solicitação para agente: " + nodeAgent);
            nodeAgent.requestFilesList();
        }

        // Simulação de resultados
        List<String> test = new ArrayList<>();
        test.add(wordToSearch);
        test.add("Resultado 1");
        test.add("Resultado 2");

        return test;
    }

    // Função para criar/atualizar a lista de filmes.
    // Deve ser usando antes de realizar uma procura para garantir que filmes filme que possam ser adicionados enquanto o programa acontece estejam incluidos.
    public void updateFilesList () {

        for (File file : new File(pathToFolder).listFiles()) {
            if (file.isFile() && file.getName().endsWith("mp3")) {
                files.add(file.getName());
            }
        }
    }

    @Override
    public String toString() {
        return "Node{" +
                "port=" + port +
                '}';
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
