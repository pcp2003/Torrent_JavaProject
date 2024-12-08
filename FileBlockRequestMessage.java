import java.io.Serializable;
import java.util.Arrays;

public class FileBlockRequestMessage implements Serializable {

    private final int fileHash;
    private final long offset;
    private final int length;

    FileBlockRequestMessage(int fileHash, long offset, int length) {

        this.fileHash = fileHash;
        this.offset = offset;
        this.length = length;
    }

    public int getLength() {
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

    public int getHash() {
        return fileHash;
    }

    public long getOffset() {
        return offset;
    }
}
