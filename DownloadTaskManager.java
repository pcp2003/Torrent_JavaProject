import java.io.File;
import java.util.*;

public class DownloadTaskManager extends Thread {

    //Thread que pede a cada NodeAgent para enviar FileBlockRequestMessage
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

    private final int hashValue;
    private final String fileName;
    private final String filePath;
    private final CountDownLatch countDownLatch;
    private final IscTorrentGUI gui;

    private final List<FileBlockRequestMessage> fileBlockRequestList;
    private final List<FileBlockAnswerMessage> fileBlockAnswers = new ArrayList<>();
    private final List<NodeAgent> availableNodeAgentList;
    private final Map<NodeAgent, Integer> nodeAgentAnswersCount = new HashMap<>();

    public DownloadTaskManager(int hashValue, long fileLength, String filePath, String fileName, List<NodeAgent> nodeAgentList, IscTorrentGUI gui) {
        this.hashValue = hashValue;
        this.gui = gui;
        this.filePath = filePath;
        this.fileName = fileName;
        this.availableNodeAgentList = nodeAgentList;
        this.fileBlockRequestList = FileUtils.createFileBlockRequestList(hashValue, fileLength);
        this.countDownLatch = new CountDownLatch(fileBlockRequestList.size());
    }

    @Override
    public void run() {

        long initial = System.currentTimeMillis();
        for (NodeAgent _ : availableNodeAgentList) {
            DownloadTaskManagerRequesterThread requesterThread = new DownloadTaskManagerRequesterThread();
            requesterThread.start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        reconstructFile();

        gui.displayDownloadInfo(hashValue, (System.currentTimeMillis() - initial), nodeAgentAnswersCount);

    }

    //Devolve e retira o primeiro FileBlockRequestMessage da lista fileBlockRequestList
    //Usado pela DownloadTaskManagerRequesterThread
    public synchronized FileBlockRequestMessage getNextBlockRequest () {
        if (fileBlockRequestList.isEmpty()) return null;

        FileBlockRequestMessage fileBlockRequestMessage = fileBlockRequestList.removeFirst();
        notifyAll();
        return fileBlockRequestMessage;

    }

    //Devolve e retira o primeiro NodeAgent da lista availableNodeAgentList
    //Usado pela DownloadTaskManagerRequesterThread
    private synchronized NodeAgent getAvailableNodeAgent() throws InterruptedException {
        while(availableNodeAgentList.isEmpty()) wait();
        NodeAgent nodeAgent = availableNodeAgentList.removeFirst();
        notifyAll();
        return nodeAgent;
    }

    //Recebe um FileBlockAnswerMessage e o NodeAgent que o enviou
    //Adiciona o FileBlockAnswerMessage à lista fileBlockAnswers, diminui o countDownLatch, coloca o NodeAgent na lista availableNodeAgentList
    //Contabiliza o FileBlockAnswerMessage recebido pelo NodeAgent no HashMap nodeAgentAnswersCount
    public synchronized void addFileBlockAnswer(FileBlockAnswerMessage answer, NodeAgent nodeAgent) {
        fileBlockAnswers.add(answer);
        countDownLatch.countDown();
        availableNodeAgentList.add(nodeAgent);
        nodeAgentAnswersCount.put(nodeAgent, nodeAgentAnswersCount.getOrDefault(nodeAgent, 0) + 1);
        notifyAll();
    }

    //Chama a função createFile() do FileUtils
    public synchronized void reconstructFile() {
        FileUtils.createFile(fileBlockAnswers, filePath, fileName);
    }

}
