import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Node {

    private final InetAddress address;
    private final int port;
    public NewConnectionRequest request;
    private String pathToFolder;
    private final List<NodeAgent> nodeAgentList;
    private List<FileSearchResult> musicSearchResult = new ArrayList<>();
    private List<NodeAgentTask<FileBlockRequestMessage>> fileBlockRequestMessages = new ArrayList<>();
    private List<NodeAgentTask<FileBlockAnswerMessage>> fileBlockAnswerMessages = new ArrayList<>();
    private IscTorrentGUI gui;
    private ExecutorService threadPool;
    private Map<Integer, DownloadTaskManager> downloadTaskManagerMap = new HashMap<>();
    private final int THREADPOOL_NR_OF_THREADS = 5;

    public Node(IscTorrentGUI gui, int port, String folderName) throws UnknownHostException {
        this.gui = gui;
        this.address = InetAddress.getByName("localhost");
        this.port = port;
        this.pathToFolder = ("folders" + File.separator + folderName);
        this.request = new NewConnectionRequest(port);
        this.nodeAgentList = new ArrayList<>();
        startServing();
        this.threadPool = Executors.newFixedThreadPool(THREADPOOL_NR_OF_THREADS);
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
                e.printStackTrace();
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
    public List<File> getFilesList() {
        List<File> files = new ArrayList<>();
        for (File file : Objects.requireNonNull(new File(pathToFolder).listFiles())) {
            if (file.isFile() && file.getName().endsWith("mp3")) {
                files.add(file);
            }
        }
        return files;
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
            if (file.getName().toLowerCase().contains(word.toLowerCase())) {
                results.add(new FileSearchResult(wordSearchMessage, file, address, port));
            }
        }
        return results;
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

        DownloadTaskManager newDownloadTaskManager = new DownloadTaskManager(result.getHash(), result.getFileSize(), pathToFolder, result.getFileName(), canDownload);
        downloadTaskManagerMap.put(result.getHash(), newDownloadTaskManager);
        newDownloadTaskManager.start(); // (1) 2, 3

    }


    public synchronized void receiveFileRequest(FileBlockRequestMessage fileBlockRequestMessage, NodeAgent nodeAgent) {
        System.out.println( "Received request " + fileBlockRequestMessage + " in node " + port);
        fileBlockRequestMessages.add(new NodeAgentTask<>(fileBlockRequestMessage, nodeAgent));
        System.out.println( "IsEmpty? " + fileBlockRequestMessages.isEmpty());
        notifyAll();

    }

    public synchronized void receiveAnswer (FileBlockAnswerMessage fileBlockAnswerMessage, NodeAgent nodeAgent ) { // (1), 2, 3
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

    // TODO: A threpool deveria usar apenas uma thread por DownloadTaskManagerRequest
    // Função para processar os requests recebidos e fazer enviar respostas
    public void processRequestsAndMakeAnswers() { // 1, (2), (3)

        threadPool.submit(() -> {
            while(true){
                try {
                    NodeAgentTask<FileBlockRequestMessage> request = getFileBlockRequestMessage();
                    System.out.println("Processing: " + request);

                    FileBlockAnswerMessage answer = FileBlockUtils.readFileBlock(pathToFolder, request.getTask());
                    System.out.println("Sending answer " + answer);

                    request.getNodeAgent().sendObject(answer);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public String toString() {
        return "Node{" +
                "port=" + port +
                '}';
    }


}
