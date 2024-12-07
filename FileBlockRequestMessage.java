import java.io.Serializable;
import java.util.Arrays;

public class FileBlockRequestMessage implements Serializable {

    private final int fileHash;
    private final long offset;
    private final long length;

    FileBlockRequestMessage(int fileHash, long offset, long length) {

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

    public int getHash() {
        return fileHash;
    }

    public long getOffset() {
        return offset;
    }
}
