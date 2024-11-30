import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;

public class test {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        File fileA = new File("C:/Users/guipe/Documents/Iscte/3º Ano/PCD/PCD_project/folders/dl2/hiLorena.mp3");
        File fileB = new File("folders/dl3/hiLorena.mp3");

        DownloadTaskManager dtmA = new DownloadTaskManager(fileA);
        DownloadTaskManager dtmB = new DownloadTaskManager(fileB);
        for(FileBlockRequestMessage rq : dtmA.getFileBlockRequestMessageList()){
            System.out.println(rq);
        }
        for(FileBlockRequestMessage rq : dtmB.getFileBlockRequestMessageList()){
            System.out.println(rq);
        }
    }
}
