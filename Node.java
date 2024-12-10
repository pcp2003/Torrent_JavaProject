import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Node {

    private final InetAddress address;
    private final int port;

    private final NewConnectionRequest request;
    private final String pathToFolder;
    private final IscTorrentGUI gui;
    private final ExecutorService threadPool;
    private final List<NodeAgent> nodeAgentList = new ArrayList<>();
    private final List<FileSearchResult> musicSearchResult = new ArrayList<>();
    private final List<NodeAgentTask<FileBlockRequestMessage>> fileBlockRequestMessages = new ArrayList<>();
    private final Map<Integer, DownloadTaskManager> downloadTaskManagerMap = new HashMap<>();

    public Node(IscTorrentGUI gui, int port, String folderName) {
        this.gui = gui;
        try {
            this.address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        this.port = port;
        this.pathToFolder = ("folders" + File.separator + folderName);
        this.request = new NewConnectionRequest(port);
        startServing();
        this.threadPool = Executors.newFixedThreadPool(5);
        processRequestsAndMakeAnswers();
    }

    // Função para um cliente se conectar a um servidor de um nó que não seja servidor de si próprio.

    void connectClient(InetAddress addr, int serverPort, NewConnectionRequest request) {

        if (!(serverPort == port)) {
            try {
                Socket clientSocket = new Socket(addr, serverPort);
                NodeAgent nodeAgent = new NodeAgent(this, clientSocket); // Cria o agent reponsavel pelo socket do cliente
                nodeAgent.start();
                nodeAgentList.add(nodeAgent);
                nodeAgent.sendObject(request);

            } catch (IOException e) {
                System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
            }
        } else {
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
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro ao criar ServerSocket: " + e.getMessage());
            }
        }).start();
    }

    // Função para buscar as music que possuam no nome a palavra introduzida no "Procurar"
    public void searchMusic(String wordToSearch) {

        musicSearchResult.clear();
        for (NodeAgent nodeAgent : nodeAgentList) {
            nodeAgent.searchMusicByWord(new WordSearchMessage(wordToSearch));
        }
    }

    public synchronized void receiveMusicSearchResult(List<FileSearchResult> fileSearchResult) {
        musicSearchResult.addAll(fileSearchResult);
        gui.updateMusicResultList(musicSearchResult);
        notifyAll();
    }

    public void download(List<FileSearchResult> fileSearchResults) {

        FileSearchResult result = fileSearchResults.getFirst();
        List<NodeAgent> canDownload = new ArrayList<>();
        for (NodeAgent nodeAgent : nodeAgentList) {
            for (FileSearchResult fileSearchResult : fileSearchResults) {
                if (nodeAgent.getClientPort() == fileSearchResult.getPort()) {
                    canDownload.add(nodeAgent);
                }
            }
        }

        DownloadTaskManager newDownloadTaskManager = new DownloadTaskManager(result.getHash(), result.getFileSize(), pathToFolder, result.getFileName(), canDownload, gui);
        downloadTaskManagerMap.put(result.getHash(), newDownloadTaskManager);
        newDownloadTaskManager.start(); // (1) 2, 3

    }


    public synchronized void receiveFileRequest(FileBlockRequestMessage fileBlockRequestMessage, NodeAgent nodeAgent) {
        System.out.println("Received request " + fileBlockRequestMessage + " in node " + port);
        fileBlockRequestMessages.add(new NodeAgentTask<>(fileBlockRequestMessage, nodeAgent));
        notifyAll();

    }

    public synchronized void receiveAnswer(FileBlockAnswerMessage fileBlockAnswerMessage, NodeAgent nodeAgent) { // (1), 2, 3
        downloadTaskManagerMap.get(fileBlockAnswerMessage.getHash()).addFileBlockAnswer(fileBlockAnswerMessage, nodeAgent);
    }


    public synchronized NodeAgentTask<FileBlockRequestMessage> getFileBlockRequestMessage() { // 1, (2), (3)

        while (fileBlockRequestMessages.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        NodeAgentTask<FileBlockRequestMessage> fileBlockRequestMessage = fileBlockRequestMessages.removeFirst();
        notifyAll();
        return fileBlockRequestMessage;

    }

    // Função para processar os requests recebidos e fazer enviar respostas

    public void processRequestsAndMakeAnswers() { // 1, (2), (3)

        threadPool.submit(() -> {

            while (true) {

                NodeAgentTask<FileBlockRequestMessage> request = getFileBlockRequestMessage();
                System.out.println("Processing: " + request);

                FileBlockAnswerMessage answer = FileUtils.readFileBlock(pathToFolder, request.getTask());
                System.out.println("Sending answer " + answer);

                request.getNodeAgent().sendObject(answer);

            }
        });
    }

    public int getPort() {
        return port;
    }

    public NewConnectionRequest getConnectionRequest() {
        return request;
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getPathToFolder() {
        return pathToFolder;
    }

    @Override
    public String toString() {
        return "Node{" +
                "port=" + port +
                '}';
    }


}
