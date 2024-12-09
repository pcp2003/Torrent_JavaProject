import java.io.File;
import java.util.*;

public class DownloadTaskManager extends Thread {
    public class DownloadTaskManagerRequesterThread extends Thread {
        @Override
        public void run() {
            while (true){
                try {
                    FileBlockRequestMessage blockRequest = getNextBlockRequest();
                    if (blockRequest == null) break;
                    NodeAgent nodeAgent = getAvailableNodeAgent();
                    nodeAgent.sendObject(blockRequest);
                    System.out.println("Sending " + blockRequest + " to " + nodeAgent);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private final String fileName;
    private final String filePath;

    private final List<FileBlockRequestMessage> fileBlockRequestList;
    private final List<FileBlockAnswerMessage> fileBlockAnswers = new ArrayList<>();
    private final List<NodeAgent> availableNodeAgentList;
    private final Map<NodeAgent, Integer> nodeAgentAnswersCount = new HashMap<>();

    public DownloadTaskManager(int hashValue, long fileLength, String filePath, String fileName, List<NodeAgent> nodeAgentList) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.availableNodeAgentList = nodeAgentList;

        this.fileBlockRequestList = FileUtils.createFileBlockRequestList(hashValue, fileLength);
    }

    @Override
    public void run() {

        long initial = System.currentTimeMillis();
        for (NodeAgent _ : availableNodeAgentList) {
            DownloadTaskManagerRequesterThread requesterThread = new DownloadTaskManagerRequesterThread();
            requesterThread.start();
        }

        //esperar todas as answers chegarem
        try {
            wait(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        reconstructFile();
        System.out.println("It took " + (System.currentTimeMillis() - initial) + "ms to finish download");
        for(NodeAgent nodeAgent : nodeAgentAnswersCount.keySet().stream().toList()){
            System.out.println(nodeAgent + " sent " + nodeAgentAnswersCount.get(nodeAgent) + " file blocks");
        }
    }

    public synchronized FileBlockRequestMessage getNextBlockRequest () {
        if (fileBlockRequestList.isEmpty()) return null;

        FileBlockRequestMessage fileBlockRequestMessage = fileBlockRequestList.removeFirst();
        notifyAll();
        return fileBlockRequestMessage;

    }

    // Aguarda por uma resposta correspondente
    private synchronized NodeAgent getAvailableNodeAgent() throws InterruptedException {
        while(availableNodeAgentList.isEmpty()) wait();
        NodeAgent nodeAgent = availableNodeAgentList.removeFirst();
        notifyAll();
        return nodeAgent;
    }

    //  Adicionar respostas recebidas
    public synchronized void addFileBlockAnswer(FileBlockAnswerMessage answer, NodeAgent nodeAgent) {
        fileBlockAnswers.add(answer);
        availableNodeAgentList.add(nodeAgent);
        nodeAgentAnswersCount.put(nodeAgent, nodeAgentAnswersCount.getOrDefault(nodeAgent, 0) + 1);
        System.out.println();
        notifyAll();
    }

    public synchronized void reconstructFile() {
        System.out.println("File saved in " + filePath + File.separator + fileName);

        FileUtils.createFile(fileBlockAnswers, filePath, fileName);
    }

}
