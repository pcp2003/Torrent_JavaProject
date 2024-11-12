import java.io.File;
import java.util.*;

public class DownloadTaskManager {
    private static final int BLOCK_SIZE = 10240;
    
    public List<FileBlockRequestMessage> fileBlockRequestMessageList = new ArrayList<FileBlockRequestMessage>();
    DownloadTaskManager(File file){
        // String hash = file.toHASH  //nao sei fazer isto
        for(long offset = 0; offset < file.length(); offset++){

        }

    }

}
