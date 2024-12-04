import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Node {

    private final InetAddress address;
    private final int port;
    public NewConnectionRequest request;
    private String pathToFolder;
    private final List<NodeAgent> nodeAgentList;
    private List<FileSearchResult> musicSearchResult = new ArrayList<>();
    public volatile int waitNodesLists = 0;
    private IscTorrentGUI gui;

    public Node(IscTorrentGUI gui, InetAddress address, int port, String folderName) {
        this.gui = gui;
        this.address = address;
        this.port = port;
        this.pathToFolder = ("folders" + File.separator + folderName);
        this.request = new NewConnectionRequest(port);
        this.nodeAgentList = new ArrayList<>();
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
                nodeAgentList.add(nodeAgent);
                nodeAgent.sendConnectionRequest(request);

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

    // Função para associar o número de aparições de uma musica x na lista como cada musica
    // Resumindo: Numeros de nós que possuem uma musica x
    public Map<String, Integer> countFileOccurrences(List<String> receivedFiles) {

        Map<String, Integer> fileCounts = new HashMap<>();

        synchronized (receivedFiles) {
            for (String file : receivedFiles) {
                // Incrementa o contador ou inicializa como 1
                fileCounts.put(file, fileCounts.getOrDefault(file, 0) + 1);
            }
        }

        return fileCounts;
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

//    // To append filesReceived
//    public void appendFilesToReceivedFiles(String[] filesList) {
//        lock.lock();  // Bloqueia a operação enquanto modifica a lista
//        try {
//            receivedFiles.addAll(Arrays.asList(filesList));  // Modifica a lista de forma segura
//        } finally {
//            lock.unlock();  // Desbloqueia para permitir o acesso de outros threads
//        }
//    }



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

//    // Lista de files recebidos através da utilização de "Procurar"
//    public List<String> getReceivedFilesList() {
//        lock.lock();
//        try {
//            return new ArrayList<>(receivedFiles);  // Retorna uma cópia da lista para evitar problemas de concorrência
//        } finally {
//            lock.unlock();
//        }
//    }


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
}
