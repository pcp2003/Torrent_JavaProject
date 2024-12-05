import java.util.*;
import java.util.concurrent.*;

public class DownloadTaskManager {

    private static final int BLOCK_SIZE = 10240;

    private final LinkedList<FileBlockRequestMessage> fileBlockRequestList = new LinkedList<>();
    private final List<FileBlockAnswerMessage> fileBlockAnswers = new ArrayList<>();
    private final List<NodeAgent> nodeAgentList;
    private final ExecutorService threadPool;

    public DownloadTaskManager(int hashValue, long fileLength, List<NodeAgent> nodeAgentList, int maxThreads) {
        this.nodeAgentList = nodeAgentList;
        this.threadPool = Executors.newFixedThreadPool(maxThreads);

        // Divide o arquivo em blocos
        int nFullBlocks = (int) (fileLength / BLOCK_SIZE);
        for (int i = 0; i < nFullBlocks; i++) {
            fileBlockRequestList.add(new FileBlockRequestMessage(hashValue, (long) i * BLOCK_SIZE, BLOCK_SIZE));
        }
        long lastOffset = (long) nFullBlocks * BLOCK_SIZE;
        long rest = fileLength - lastOffset;
        if (rest > 0) {
            fileBlockRequestList.add(new FileBlockRequestMessage(hashValue, lastOffset, rest));
        }
    }

    // Método que inicia o download
    public void startDownload() {

        for (NodeAgent nodeAgent : nodeAgentList) {
            threadPool.execute(() -> {
                while (!fileBlockRequestList.isEmpty()) {
                    FileBlockRequestMessage blockRequest;
                    synchronized (fileBlockRequestList) {
                        blockRequest = fileBlockRequestList.removeFirst();
                    }
                    if (blockRequest != null) {
                        processBlockRequest(blockRequest, nodeAgent);
                    }
                }
            });
        }
        threadPool.shutdown(); // Finaliza o pool após todas as tarefas
    }

    // Processa uma requisição de bloco
    private void processBlockRequest(FileBlockRequestMessage blockRequest, NodeAgent nodeAgent) {
        try {

            nodeAgent.sendObject(blockRequest);

            // Espera pela resposta correspondente
            FileBlockAnswerMessage response = waitForResponse(blockRequest);



        } catch (Exception e) {
            System.err.println("Erro ao processar bloco: " + blockRequest.getOffset());
            e.printStackTrace();
        }
    }

    // Aguarda por uma resposta correspondente
    private synchronized FileBlockAnswerMessage waitForResponse(FileBlockRequestMessage request)  {

        while (true) {

            for ( FileBlockAnswerMessage answer : fileBlockAnswers) {

                if (answer.getOffset() == request.getOffset() && answer.getHash() == request.getHash()) {
                    System.out.println("Bloco baixado com sucesso: " + answer.getOffset());
                }

            }

            try {
                wait(); // Aguarda por uma nova resposta
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Método para adicionar respostas recebidas
    public synchronized void addFileBlockAnswer(FileBlockAnswerMessage answer) {
        fileBlockAnswers.add(answer);
        notifyAll(); // Notifica todas as threads aguardando
    }
}
