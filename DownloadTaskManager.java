import java.util.*;

public class DownloadTaskManager{

    public class DownloadTaskThread extends Thread{
        @Override
        public void run(){
            boolean listFinished = false;
            while(!listFinished){
                for(NodeAgent nodeAgent: nodeAgentList){

                    FileBlockRequestMessage nextFileBlock = getNextFileBlockRequestMessage();
                    if(nextFileBlock != null){listFinished = true; break;};
                    nodeAgent.sendRequestFileBlock();
                }
            }
        }
    }

    private static final int BLOCK_SIZE = 10240;

    private final List<FileBlockRequestMessage> fileBlockRequestMessageList = new ArrayList<FileBlockRequestMessage>();

    private List<NodeAgent> nodeAgentList;

    DownloadTaskManager(int hashValue, Long fileLenght, List<NodeAgent> nodeAgentList){
        this.nodeAgentList = nodeAgentList;
        int nFullBlocks = (int) (fileLenght / BLOCK_SIZE);
        for (int i = 0; i < nFullBlocks; i++) {
            fileBlockRequestMessageList.add(new FileBlockRequestMessage(hashValue, (long) i * BLOCK_SIZE, BLOCK_SIZE));
        }
        long lastOffset = (long) nFullBlocks * BLOCK_SIZE;
        long rest = fileLenght - lastOffset;
        if (rest > 0) fileBlockRequestMessageList.add(new FileBlockRequestMessage(hashValue, lastOffset, rest));


    }

    private synchronized FileBlockRequestMessage getFileBlockRequestMessage() {
        FileBlockRequestMessage fbrm = fileBlockRequestMessageList.getFirst();
        fileBlockRequestMessageList.remove(fbrm);
        notifyAll();
        return fbrm;
    }

    public void startDownload() {

    }

    public List<FileBlockRequestMessage> getFileBlockRequestMessageList() {
        return fileBlockRequestMessageList;
    }



}
