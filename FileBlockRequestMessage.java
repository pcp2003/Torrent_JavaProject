import java.util.Arrays;

public class FileBlockRequestMessage {

    private String fileHash;
    private long offset;
    private long length;

    FileBlockRequestMessage(String fileHash, long offset, long length) {

        this.fileHash = fileHash;
        this.offset = offset;
        this.length = length;
    }

    public long getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "FileBlockRequestMessage{" +
                "fileHash=" + fileHash +
                ", offset=" + offset +
                ", length=" + length +
                '}';
    }

}
