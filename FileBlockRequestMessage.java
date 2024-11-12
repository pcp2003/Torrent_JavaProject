public class FileBlockRequestMessage{
    private String fileHash;
    private long offset;
    private long length;

    FileBlockRequestMessage(String fileHash, long offset, long length){
        this.fileHash = fileHash;
        this.offset = offset;
        this.length = length;
    }


}
