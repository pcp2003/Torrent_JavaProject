import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.List;

public class FileBlockUtils {

    public static FileBlockAnswerMessage readFileBlock(String filePath, long offset, long length) {

        File file = new File(filePath);
        System.out.println(file);

        int fileHash = hashValue(file);

        if (offset < 0 || offset >= file.length()) {
            throw new IllegalArgumentException("Offset inválido");
        }
        if (length <= 0 || offset + length > file.length()) {
            length = file.length() - offset;
        }

        byte[] data = new byte[(int) length];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.skip(offset);
            int bytesRead = fis.read(data);
            if (bytesRead != length) {
                throw new IOException("Erro ao ler o ficheiro");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new FileBlockAnswerMessage(fileHash, offset, length, data);
    }

    public static void writeMessagesToFile(List<FileBlockAnswerMessage> messages, String outputPath) {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("Lista de mensagens inválida.");
        }

        messages.sort(Comparator.comparingLong(FileBlockAnswerMessage::getOffset));
        int fileHash = messages.getFirst().getHash();
        String fileName = "file_" + fileHash;

        File outputFile = new File(outputPath, fileName);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            for (FileBlockAnswerMessage message : messages) {
                fos.write(message.getData());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Ficheiro escrito em: " + outputFile.getAbsolutePath());
    }

    public static int hashValue(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] fileContent = Files.readAllBytes(file.toPath());
            byte[] fileDigested = md.digest(fileContent);
            return new BigInteger(1, fileDigested).intValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

