import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class FileUtils {
    public static final int BLOCK_SIZE = 10240;

    //Recebe o hashValue do ficheiro e o tamanho do mesmo, e cria um lista de FileBlockRequestMessage
    public static List<FileBlockRequestMessage> createFileBlockRequestList(int hashValue, long fileLength) {
        List<FileBlockRequestMessage> result = new ArrayList<>();
        int nFullBlocks = (int) (fileLength / BLOCK_SIZE);
        for (int i = 0; i < nFullBlocks; i++) {
            result.add(new FileBlockRequestMessage(hashValue, (long) i * BLOCK_SIZE, BLOCK_SIZE));
        }
        int lastOffset = nFullBlocks * BLOCK_SIZE;
        int rest = (int)(fileLength - lastOffset);
        if (rest > 0) result.add(new FileBlockRequestMessage(hashValue, lastOffset, rest));

        return result;
    }

    //Recebe um hash de um ficheiro e itera todos os ficheiros da pasta para devolver o ficheiro que tem o mesmo hash
    private static String filePathByHash (String pathToFolder, int fileHash){

        for (File file : Objects.requireNonNull(new File(pathToFolder).listFiles())) {
            if (file.isFile() && file.getName().endsWith("mp3")) {
                if (fileHash == FileUtils.hashValue(file)){
                    return (pathToFolder + File.separator + file.getName());
                }
            }
        }

        return null;
    }

    //Recebe um FileBlockRequestMessage e devolve um FileBlockAnswerMessage que correponde ao pedido
    public static FileBlockAnswerMessage readFileBlock(String pathToFolder, FileBlockRequestMessage fileBlockRequestMessage) {

        File file = new File(Objects.requireNonNull(filePathByHash(pathToFolder, fileBlockRequestMessage.getHash())));

        long offset = fileBlockRequestMessage.getOffset();
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new FileBlockAnswerMessage(fileHash, offset, length, data);
    }

    //Cria um ficheiro a partir de uma lista de FileBlockAnswerMessage
    public static void createFile(List<FileBlockAnswerMessage> messages, String outputPath, String fileName) {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("Lista de mensagens inválida.");
        }

        messages.sort(Comparator.comparingLong(FileBlockAnswerMessage::getOffset));

        File outputFile = new File(outputPath, fileName);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            for (FileBlockAnswerMessage message : messages) {
                fos.write(message.getData());
            }
        }catch (IOException e) {
            System.out.println("Erro ao ler o ficheiro" + e.getMessage());
            throw new RuntimeException(e);
        }
        System.out.println("Ficheiro escrito em: " + outputFile.getAbsolutePath());
    }

    //Lê o ficheiro e devolve o hashValue do mesmo, usando a classe MessageDigest
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

    //Devolve a lista de ficheiros de uma pasta
    public static List<File> getFilesList( String pathToFolder) {
        List<File> files = new ArrayList<>();
        for (File file : Objects.requireNonNull(new File(pathToFolder).listFiles())) {
            if (file.isFile() && file.getName().endsWith("mp3")) {
                files.add(file);
            }
        }
        return files;
    }

    //Procura os ficheiros de uma pasta que contenham uma certa string e cria uma lista de FileSearchResult com a informação dos mesmos
    public static List<FileSearchResult> getFilesByWord(InetAddress address, int port, String pathToFolder, WordSearchMessage wordSearchMessage) {
        String word = wordSearchMessage.getWord();
        List<File> files = FileUtils.getFilesList(pathToFolder);
        List<FileSearchResult> results = new ArrayList<>();
        for (File file : files) {
            if (file.getName().toLowerCase().contains(word.toLowerCase())) {
                results.add(new FileSearchResult(wordSearchMessage, file, address, port));
            }
        }
        return results;
    }
}

