import java.io.File;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class DownloadTaskManager {

    private static final int BLOCK_SIZE = 10240;
    
    public List<FileBlockRequestMessage> fileBlockRequestMessageList = new ArrayList<FileBlockRequestMessage>();

    DownloadTaskManager(File file) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        for(long offset = 0; offset < file.length(); offset++){


        }

    }

}
