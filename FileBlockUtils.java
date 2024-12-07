import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class FileBlockUtils {

    private static String musicPathByHash (String pathToFolder, int musicHash){

        for (File file : Objects.requireNonNull(new File(pathToFolder).listFiles())) {
            if (file.isFile() && file.getName().endsWith("mp3")) {
                if (musicHash == FileBlockUtils.hashValue(file)){
                    return (pathToFolder + File.separator + file.getName());
                }
            }
        }

        return null;
    }

    public static FileBlockAnswerMessage readFileBlock(String pathToFolder, FileBlockRequestMessage fileBlockRequestMessage) {

        File file = new File(Objects.requireNonNull(musicPathByHash(pathToFolder, fileBlockRequestMessage.getHash())));
        System.out.println(file);

        int offset = fileBlockRequestMessage.getOffset();
        int length = fileBlockRequestMessage.getLength();

        int fileHash = hashValue(file);

        if (offset < 0 || offset >= file.length()) {
            throw new IllegalArgumentException("Offset inválido");
        }
        if (length <= 0 || offset + length > file.length()) {
            throw new IllegalArgumentException("Lenght inválida");
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

    public static void writeMessagesToFile(List<FileBlockAnswerMessage> messages, String outputPath, String fileName) {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("Lista de mensagens inválida.");
        }

        messages.sort(Comparator.comparingLong(FileBlockAnswerMessage::getOffset));
        int fileHash = messages.getFirst().getHash();

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

