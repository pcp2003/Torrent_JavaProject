import java.util.*;
import java.util.concurrent.*;

public class DownloadTaskManager extends Thread {

    public class DownloadTaskManagerRequesterThread extends Thread {
        @Override
        public void run() {
            while (true){
                try {
                    FileBlockRequestMessage blockRequest = getNextBlockRequest();
                    if (blockRequest == null) {
                        break;
                    }
                    NodeAgent nodeAgent = getAvailableNodeAgent();
                    System.out.println("DownloadTaskmanagerThread sending: " + blockRequest);
                    nodeAgent.sendObject(blockRequest);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

    private static final int BLOCK_SIZE = 10240;
    private final String fileName;
    private List<FileBlockRequestMessage> fileBlockRequestList = new ArrayList<>();
    private List<FileBlockAnswerMessage> fileBlockAnswers = new ArrayList<>();
    private List<NodeAgent> availableNodeAgentList;
    private List<DownloadTaskManagerRequesterThread> requesterThreadList = new ArrayList<>();

    private String filePath;

    public DownloadTaskManager(int hashValue, long fileLength, List<NodeAgent> nodeAgentList, String filePath, String fileName) {
        this.availableNodeAgentList = nodeAgentList;
        this.filePath = filePath;
        this.fileName = fileName;
        // Divide o arquivo em blocos
        int nFullBlocks = (int) (fileLength / BLOCK_SIZE);
        for (int i = 0; i < nFullBlocks; i++) {
            fileBlockRequestList.add(new FileBlockRequestMessage(hashValue, i * BLOCK_SIZE, BLOCK_SIZE));
        }
        int lastOffset = nFullBlocks * BLOCK_SIZE;
        int rest = (int)(fileLength - lastOffset);
        if (rest > 0) {
            fileBlockRequestList.add(new FileBlockRequestMessage(hashValue, lastOffset, rest));
        }

        for(int i = 0; i<availableNodeAgentList.size(); i++) {
            requesterThreadList.add(new DownloadTaskManagerRequesterThread());
        }
    }

    @Override
    public void run() {
        for(DownloadTaskManagerRequesterThread requesterThread : requesterThreadList) {
            requesterThread.start();
            try {
                requesterThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        writeOnDisc();
    }

    public synchronized FileBlockRequestMessage getNextBlockRequest () {

        if (!fileBlockRequestList.isEmpty()) {

            return fileBlockRequestList.removeFirst();
        }
        return null;
    }

    // Aguarda por uma resposta correspondente
    private synchronized NodeAgent getAvailableNodeAgent() throws InterruptedException {
        while(availableNodeAgentList.isEmpty()) {wait();}
        NodeAgent nextNodeAgent = availableNodeAgentList.removeFirst();
        notifyAll();
        return nextNodeAgent;
    }

    //  Adicionar respostas recebidas
    public synchronized void addFileBlockAnswer(FileBlockAnswerMessage answer, NodeAgent nodeAgent) {
        fileBlockAnswers.add(answer);
        availableNodeAgentList.add(nodeAgent);
        notifyAll(); // Notifica todas as threads aguardando
    }

    public void writeOnDisc () {

        System.out.println("Escrevendo no disco!");
        FileBlockUtils.writeMessagesToFile(fileBlockAnswers, filePath, fileName);
    }

}
