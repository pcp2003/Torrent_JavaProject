import java.io.File;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSearchResult implements Serializable {
    WordSearchMessage wordSearchMessage;
    int hash;
    Long fileSize;
    String fileName;
    InetAddress address;
    int port;

    FileSearchResult(WordSearchMessage wordSearchMessage, File file, InetAddress adress, int port) {
        this.wordSearchMessage = wordSearchMessage;
        hash = FileUtils.hashValue(file);
        fileSize = file.length();
        fileName = file.getName();
        this.address = adress;
        this.port = port;
    }

    public int getHash() {
        return hash;
    }

    public String getFileName() {
        return fileName;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public Long getFileSize() {
        return fileSize;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileSearchResult) {
            return this.hash == ((FileSearchResult) obj).getHash();
        }
        return false;
    }

    @Override
    public String toString() {
        return "FileSearchResult{" +
                "wordSearchMessage=" + wordSearchMessage +
                ", hash='" + hash + '\'' +
                ", fileSize=" + fileSize +
                ", fileName='" + fileName + '\'' +
                ", adress=" + address +
                ", port=" + port +
                '}';
    }

    //Recebe uma lista de FileSearchResult e cria um HashMap, onde a chave é o hash do ficheiro e o value é uma lista de todos os FileSearchResult desse ficheiro
    //Este HashMap ajuda a agrupar todos os FileSearchResult recebidos de um determinado ficheiro, facilitando a contagem de Nodes que contêm o ficheiro.
    public static Map<Integer, List<FileSearchResult>> hashMap(List<FileSearchResult> fileSearchResults) {
        Map<Integer, List<FileSearchResult>> result = new HashMap<>();
        for(FileSearchResult fileSearchResult : fileSearchResults) {
            List<FileSearchResult> valueList = result.getOrDefault(fileSearchResult.getHash(), new ArrayList<>());
            valueList.add(fileSearchResult);
            result.put(fileSearchResult.getHash(), valueList);
        }
        return result;
    }
}
