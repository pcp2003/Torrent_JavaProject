import java.io.File;
import java.util.*;

public class DownloadTaskManager extends Thread {
    public class DownloadTaskManagerRequesterThread extends Thread {
        @Override
        public void run() {
            while (true){

                FileBlockRequestMessage blockRequest = getNextBlockRequest();
                if (blockRequest == null) break;
                NodeAgent nodeAgent = getAvailableNodeAgent();
                nodeAgent.sendObject(blockRequest);
                System.out.println("Sending " + blockRequest + " to " + nodeAgent);


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
        List<DownloadTaskManagerRequesterThread> threads = new ArrayList<>();

        try {
            for (NodeAgent _ : availableNodeAgentList) {
                DownloadTaskManagerRequesterThread requesterThread = new DownloadTaskManagerRequesterThread();
                threads.add(requesterThread);
                requesterThread.start();
            }
            for (DownloadTaskManagerRequesterThread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            System.out.println("Uma das threads do DownloadTaskManager foi interrompida...");
        }

    // Esperar todas as threads acabarem de enviar os pedidos
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
    private synchronized NodeAgent getAvailableNodeAgent() {
        while(availableNodeAgentList.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        NodeAgent nodeAgent = availableNodeAgentList.removeFirst();
        notifyAll();
        return nodeAgent;
    }

    //  Adicionar respostas recebidas
    public synchronized void addFileBlockAnswer(FileBlockAnswerMessage answer, NodeAgent nodeAgent) {
        fileBlockAnswers.add(answer);
        availableNodeAgentList.add(nodeAgent);
        nodeAgentAnswersCount.put(nodeAgent, nodeAgentAnswersCount.getOrDefault(nodeAgent, 0) + 1);
        notifyAll();
    }

    public void reconstructFile() {
        System.out.println("File saved in " + filePath + File.separator + fileName);

        FileUtils.createFile(fileBlockAnswers, filePath, fileName);
    }

}
