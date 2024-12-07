import java.io.*;
import java.util.List;

public class FileBlockUtils {

    public static FileBlockAnswerMessage readFileBlock(String filePath, long offset, long length) {
        File file = new File(filePath);
        int fileHash = filePath.hashCode();

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

    public static void writeMessagesToFile(List<FileBlockAnswerMessage> messages, String outputPath) throws IOException {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("Lista de mensagens inválida.");
        }

        messages.sort((m1, m2) -> Long.compare(m1.getOffset(), m2.getOffset()));
        int fileHash = messages.get(0).getHash();
        String fileName = "file_" + fileHash;

        File outputFile = new File(outputPath, fileName);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            for (FileBlockAnswerMessage message : messages) {
                fos.write(message.getData());
            }
        }
        System.out.println("Ficheiro escrito em: " + outputFile.getAbsolutePath());
    }
}

