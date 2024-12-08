import java.io.Serializable;
import java.util.Arrays;

public class FileBlockAnswerMessage implements Serializable {

    private final int fileHash;
    private final long offset;
    private final int length;
    private final byte [] data;

    FileBlockAnswerMessage(int fileHash, long offset, int length, byte [] data) {

        this.fileHash = fileHash;
        this.offset = offset;
        this.length = length;
        this.data = data;
    }

    public long getLength() {
        return length;
    }

    public byte [] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "FileBlockAnswerMessage{" +
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
