import java.util.*;

public class DownloadTaskManager{

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

    public void sendFileBlocksToAgents () {

        while(!fileBlockRequestMessageList.isEmpty()) {
            for(NodeAgent nodeAgent: nodeAgentList){
                FileBlockRequestMessage nextFileBlock = getNextFileBlockRequestMessage();
                if(nextFileBlock != null){
                    nodeAgent.sendObject(nextFileBlock);
                }else{
                    break;
                }
            }
        }
    }

    private FileBlockRequestMessage getNextFileBlockRequestMessage() {

        if(fileBlockRequestMessageList.isEmpty()) return null;

        return fileBlockRequestMessageList.removeFirst();
    }

    public void startDownload() {

    }

    public List<FileBlockRequestMessage> getFileBlockRequestMessageList() {
        return fileBlockRequestMessageList;
    }



}
