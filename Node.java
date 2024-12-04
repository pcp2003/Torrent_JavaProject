import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Node {

    public class FileBlockResquestMessageHandler extends Thread {

        @Override
        public void run() {

            while (true) {

                NodeAgentTask<FileBlockRequestMessage> fileBlockResquestMessage = getFileBlockResquestMessageFromList();

            }
        }
    }

    public class NodeAgentTask<T>{
        private T task;
        private NodeAgent nodeAgent;
        NodeAgentTask(T task, NodeAgent nodeAgent){
            this.task = task;
            this.nodeAgent = nodeAgent;
        }

        public T getTask() {
            return task;
        }

        public NodeAgent getNodeAgent() {
            return nodeAgent;
        }
    }

    private final InetAddress address;
    private final int port;
    public NewConnectionRequest request;
    private String pathToFolder;
    private final List<NodeAgent> nodeAgentList;
    private List<FileSearchResult> musicSearchResult = new ArrayList<>();
    private List<NodeAgentTask<FileBlockRequestMessage>> fileBlockRequestMessages = new ArrayList<>();
    private IscTorrentGUI gui;


    public Node(IscTorrentGUI gui,int port, String folderName) throws UnknownHostException {
        this.gui = gui;
        this.address = InetAddress.getByName("localhost");
        this.port = port;
        this.pathToFolder = ("folders" + File.separator + folderName);
        this.request = new NewConnectionRequest(port);
        this.nodeAgentList = new ArrayList<>();
        startServing();
    }

    // Função para um cliente se conectar a um servidor de um nó que não seja servidor de si próprio.
    void connectClient(InetAddress addr, int serverPort, NewConnectionRequest request) {

        if ( !(serverPort == port) ) {
            try {
                Socket clientSocket = new Socket(addr, serverPort);
                NodeAgent nodeAgent = new NodeAgent(this, clientSocket); // Cria o agent reponsavel pelo socket do cliente
                nodeAgent.start();
                nodeAgentList.add(nodeAgent);
                nodeAgent.sendObject(request);

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
    public void searchMusic(String wordToSearch) {
        musicSearchResult.clear();
        // Solicitar listas de arquivos de todos os nós conectados
        for (NodeAgent nodeAgent : nodeAgentList) {
            nodeAgent.searchMusicByWord(new WordSearchMessage(wordToSearch));
        }
    }

    public synchronized void receiveMusicSearchResult(List<FileSearchResult> fileSearchResult) {
        musicSearchResult.addAll(fileSearchResult);
        gui.updateMusicResultList(musicSearchResult);
        notifyAll();
    }



    // Função para criar/atualizar a lista de filmes.
    // Deve ser usando antes de realizar uma procura para garantir que filmes filme que possam ser adicionados enquanto o programa acontece estejam incluidos.
    public List<File> getFilesList () {
        List<File> files = new ArrayList<>();
        for (File file : Objects.requireNonNull(new File(pathToFolder).listFiles())) {
            if (file.isFile() && file.getName().endsWith("mp3")) {
                files.add(file);
            }
        }
        return files;
    }

    @Override
    public String toString() {
        return "Node{" +
                "port=" + port +
                '}';
    }

    public int getPort() {
        return port;
    }

    public NewConnectionRequest getConnectionRequest() {
        return request;
    }

    public List<FileSearchResult> getMusicByWord(WordSearchMessage wordSearchMessage) {
        String word = wordSearchMessage.getWord();
        List<File> files = getFilesList();
        List<FileSearchResult> results = new ArrayList<>();
        for (File file : files) {
            if(file.getName().toLowerCase().contains(word.toLowerCase())){
                results.add(new FileSearchResult(wordSearchMessage, file, address, port));
            }
        }
        return results;
    }

    public void requestDownload(List<FileSearchResult> fileSearchResults){
        FileSearchResult result = fileSearchResults.getFirst();
        List<NodeAgent> canDownload = new ArrayList<>();
        for (NodeAgent nodeAgent : nodeAgentList) {
            for(FileSearchResult fileSearchResult : fileSearchResults) {
                if (nodeAgent.getClientPort() == fileSearchResult.getPort()) {
                    canDownload.add(nodeAgent);
                }
            }
        }

        DownloadTaskManager dtm = new DownloadTaskManager(result.getHash(), result.getFileSize(), canDownload);
        dtm.startDownload();
    }

    public synchronized void receiveFileRequest (FileBlockRequestMessage fileBlockRequestMessage, NodeAgent nodeAgent) {
        fileBlockRequestMessages.add(new NodeAgentTask<>(fileBlockRequestMessage, nodeAgent));
        notifyAll();

    }

    public synchronized NodeAgentTask<FileBlockRequestMessage> getFileBlockResquestMessageFromList() {

        while(fileBlockRequestMessages.isEmpty()) {
            try {
                wait();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        NodeAgentTask<FileBlockRequestMessage> fileBlockRequestMessage = fileBlockRequestMessages.removeFirst();
        notifyAll();
        return fileBlockRequestMessage;

    }


}
