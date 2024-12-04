import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class NodeAgent extends Thread {
    private Node node;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;

    private int clientPort;

    public NodeAgent(Node node, Socket socket) {
        this.node = node;
        this.socket = socket;
        clientPort = socket.getPort();
        doConnections();
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {

        serve();

    }

    // Envia pedidos de cone
    public void sendConnectionRequest(NewConnectionRequest request) {

        try {
            out.writeObject(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendFileSearchResult(List<FileSearchResult> results) {
        try {
            out.writeObject(results);
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
                    case WordSearchMessage wordSearchMessage ->{
                        System.out.println("Solicitação de lista de musicas com " + wordSearchMessage);
                        sendFileSearchResult(node.getMusicByWord(wordSearchMessage));
                    }
                    case List<?> fileSearchResult -> {
                        if(fileSearchResult.stream().allMatch(x -> x instanceof FileSearchResult)) {
                            node.receiveMusicSearchResult((List<FileSearchResult>) fileSearchResult);
                        }
                    }
                    case NewConnectionRequest request -> {
                        System.out.println("Request received from client: " + request);
                        clientPort = request.getPort();
                    }
                    default -> System.out.println("Tipo desconhecido: " + obj);
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            System.err.println("Conexão encerrada ou erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getClientPort() {
        return clientPort;
    }

    @Override
    public String toString() {
        return "NodeAgent{" + "node=" + node + ", socket=" + socket + '}';
    }


}
