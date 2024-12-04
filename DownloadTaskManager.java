import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class DownloadTaskManager {

    private static final int BLOCK_SIZE = 10240;

    private final List<FileBlockRequestMessage> fileBlockRequestMessageList = new ArrayList<FileBlockRequestMessage>();

    DownloadTaskManager(int hashValue, Long fileLenght) throws NoSuchAlgorithmException {

        int nFullBlocks = (int) (fileLenght / BLOCK_SIZE);
        for (int i = 0; i < nFullBlocks; i++) {
            fileBlockRequestMessageList.add(new FileBlockRequestMessage(hashValue, (long) i * BLOCK_SIZE, BLOCK_SIZE));
        }
        long lastOffset = (long) nFullBlocks * BLOCK_SIZE;
        long rest = fileLenght - lastOffset;
        if (rest > 0) fileBlockRequestMessageList.add(new FileBlockRequestMessage(hashValue, lastOffset, rest));


    }

    public List<FileBlockRequestMessage> getFileBlockRequestMessageList() {
        return fileBlockRequestMessageList;
    }



}
