import java.io.*;
import java.net.Socket;
import java.util.List;

public class NodeAgent extends Thread {

    private final Node node;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final Socket socket;
    private int clientPort;

    public NodeAgent(Node node, Socket socket) {
        this.node = node;
        this.socket = socket;
        clientPort = socket.getPort();
        doConnections();
    }

    @Override
    public void run() {

        serve();

    }

    public synchronized <T> void sendObject(T object) {
        try {
            out.writeObject(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Faz o pedido da lista dos ficheiros para os nós ligados
    // Para garantir o funcionamento na função searchMusic temos que garantir que esta função só acabe quando a lista receivedList for alterada.
    public void searchMusicByWord(WordSearchMessage message) {
        try {
            System.out.println("Enviando pedido de musicas com " + message + " para o servidor: " + socket);
            out.writeObject(message);


        } catch (IOException e) {
            System.err.println("Erro no pedido de musicas com " + message + ": " + e.getMessage());
        }
    }


    // Realiza as conexões dos canais dos sockets
    void doConnections() {

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    // Funções realizadas pelo servidor
    private void serve() {
        System.out.println("Agente iniciado e ouvindo no socket: " + socket);
        try {

            while (true) {

                Object obj = in.readObject();
                switch (obj) {
                    case WordSearchMessage wordSearchMessage -> {
                        System.out.println("Solicitação de lista de musicas com " + wordSearchMessage);
                        sendObject(FileUtils.getMusicsByWord(node.getAddress(), node.getPort(), node.getPathToFolder(), wordSearchMessage));
                    }
                    case NewConnectionRequest request -> clientPort = request.getPort();

                    case FileBlockRequestMessage fileBlockRequest -> {

                        System.out.println(this.node.getPort() + " sending: " + fileBlockRequest);
                        node.receiveFileRequest(fileBlockRequest, this);

                    }
                    case FileBlockAnswerMessage FileBlockAnswerMessage -> node.receiveAnswer(FileBlockAnswerMessage, this);

                    case List<?> fileSearchResults -> {

                        if (fileSearchResults.stream().allMatch(x -> x instanceof FileSearchResult)) {
                            @SuppressWarnings("unchecked") // Supressão localizada
                            List<FileSearchResult> results = (List<FileSearchResult>) fileSearchResults;
                            node.receiveMusicSearchResult(results);
                        } else {
                            System.err.println("Lista contém elementos incompatíveis.");
                        }
                    }
                    default -> System.out.println("Tipo desconhecido: " + obj);
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            System.err.println("Conexão encerrada ou erro: " + e.getMessage());
        }
    }

    public int getClientPort() {
        return clientPort;
    }

    @Override
    public String toString() {
        return "NodeAgent{" + "myPort=" + node.getPort() + ", clientPort=" + clientPort + '}';
    }


    public Node getNode() {
        return node;
    }
}
