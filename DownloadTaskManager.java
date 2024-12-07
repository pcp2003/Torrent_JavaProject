import java.util.*;
import java.util.concurrent.*;

public class DownloadTaskManager {

    public class DownloadTaskManagerRequesterThread extends Thread {
        @Override
        public void run() {

            int agentIndex = 0;

            while (true){

                FileBlockRequestMessage blockRequest = getNextBlockRequest();

                if (blockRequest == null) {
                    break;
                }

                NodeAgent nodeAgent = nodeAgentList.get(agentIndex);

                System.out.println("DownloadTaskmanagerThread sending: " + blockRequest);
                nodeAgent.sendObject(blockRequest);

                agentIndex = (agentIndex + 1) % nodeAgentList.size();

                waitForResponse(blockRequest);
            }

            writeOnDisc();
        }
    }

    private static final int BLOCK_SIZE = 10240;
    private List<FileBlockRequestMessage> fileBlockRequestList = new ArrayList<>();
    private List<FileBlockAnswerMessage> fileBlockAnswers = new ArrayList<>();
    private List<NodeAgent> nodeAgentList;
    private DownloadTaskManagerRequesterThread requesterThread;

    private String filePath;

    public DownloadTaskManager(int hashValue, long fileLength, List<NodeAgent> nodeAgentList, String filePath) {
        this.nodeAgentList = nodeAgentList;
        this.filePath = filePath;

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

        requesterThread = new DownloadTaskManagerRequesterThread();
    }

    public void startDownload () {
        requesterThread.start();
    }

    public synchronized FileBlockRequestMessage getNextBlockRequest () {

        if (!fileBlockRequestList.isEmpty()) {

            return fileBlockRequestList.removeFirst();
        }
        return null;
    }

    // Aguarda por uma resposta correspondente
    private synchronized void waitForResponse(FileBlockRequestMessage request)  {

        while (true) {

            // Verifica se a answer recebida corresponde a request enviada!
            for ( FileBlockAnswerMessage answer : fileBlockAnswers) {

                if (answer.getOffset() == request.getOffset() && answer.getHash() == request.getHash()) {

                    System.out.println("Bloco baixado com sucesso: " + answer);
                    return;
                }

            }

            try {
                wait(); // Aguarda por uma nova resposta
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //  Adicionar respostas recebidas
    public synchronized void addFileBlockAnswer(FileBlockAnswerMessage answer) {
        fileBlockAnswers.add(answer);
        notifyAll(); // Notifica todas as threads aguardando
    }

    public void writeOnDisc () {

        System.out.println("Escrevendo no disco!");
        FileBlockUtils.writeMessagesToFile(fileBlockAnswers, filePath);
    }

}
