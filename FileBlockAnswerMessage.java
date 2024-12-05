import java.util.Arrays;

public class FileBlockAnswerMessage {

    private final int fileHash;
    private final long offset;
    private final long length;
    private final byte [] data;

    FileBlockAnswerMessage(int fileHash, long offset, long length, byte [] data) {

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
                ", data=" + Arrays.toString(data) +
                '}';
    }

    public int getHash() {
        return fileHash;
    }

    public long getOffset() {
        return offset;
    }

}
