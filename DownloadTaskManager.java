import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class DownloadTaskManager {

    private static final int BLOCK_SIZE = 10240;

    private final List<FileBlockRequestMessage> fileBlockRequestMessageList = new ArrayList<FileBlockRequestMessage>();

    DownloadTaskManager(File file) throws NoSuchAlgorithmException {

        String fileHash = hashValue(file);
        int nFullBlocks = (int) (file.length() / BLOCK_SIZE);
        for (int i = 0; i < nFullBlocks; i++) {
            fileBlockRequestMessageList.add(new FileBlockRequestMessage(fileHash, (long) i * BLOCK_SIZE, BLOCK_SIZE));
        }
        long lastOffset = (long) nFullBlocks * BLOCK_SIZE;
        long rest = file.length() - lastOffset;
        if (rest > 0) fileBlockRequestMessageList.add(new FileBlockRequestMessage(fileHash, lastOffset, rest));


    }

    public List<FileBlockRequestMessage> getFileBlockRequestMessageList() {
        return fileBlockRequestMessageList;
    }

    public String hashValue(File file) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // Read the file and update the digest
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Convert the byte array to a hexadecimal string
        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }



}
