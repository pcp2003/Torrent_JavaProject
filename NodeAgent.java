import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class NodeAgent extends Thread {
    private Node node;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;

    public NodeAgent(Node node, Socket socket) {
        this.node = node;
        this.socket = socket;
        doConnections();
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

    public void sendFilesList(List<String> files) {

        String[] filesList = files.toArray(new String[0]);

        try {
            out.writeObject(filesList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Faz o pedido da lista dos ficheiros para os nós ligados
    // Para garantir o funcionamento na função searchMusic temos que garantir que esta função só acabe quando a lista receivedList for alterada.
    public void requestFilesList(){
        try {
            int beforeRequest = node.getReceivedFilesList().size();
            System.out.println("Enviando REQUEST_FILES_LIST para o servidor: " + socket);
            out.writeObject("REQUEST_FILES_LIST");


        } catch (IOException e) {
            System.err.println("Erro ao enviar REQUEST_FILES_LIST: " + e.getMessage());
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
                    case String requestType when requestType.equals("REQUEST_FILES_LIST") -> {
                        System.out.println("Solicitação de lista de arquivos recebida.");
                        node.updateFilesList();
                        sendFilesList(node.getFiles());
                    }

                    case String [] filesList -> {

                        node.appendFilesToReceivedFiles(filesList);
                        node.decreaseWaitNodes();

                    }
                    case NewConnectionRequest request -> {
                        System.out.println("Request received from client: " + request);
                    }
                    default -> System.out.println("Tipo desconhecido: " + obj);
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            System.err.println("Conexão encerrada ou erro: " + e.getMessage());
            e.printStackTrace();
        }
    }



    @Override
    public String toString() {
        return "NodeAgent{" + "node=" + node + ", socket=" + socket + '}';
    }


}
