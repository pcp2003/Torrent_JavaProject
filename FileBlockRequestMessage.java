import java.util.Arrays;

public class FileBlockRequestMessage {

    private int fileHash;
    private long offset;
    private long length;

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
