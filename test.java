import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

public class test {
    public static void main(String[] args) throws UnknownHostException {
        File fileA = new File("C:/Users/guipe/Documents/Iscte/3º Ano/PCD/PCD_project/folders/dl2/hiLorena.mp3");
        File fileB = new File("folders/dl3/hiLorena.mp3");
        System.out.println(new FileSearchResult(new WordSearchMessage(""), fileA, InetAddress.getByName("localhost"), 20));
        System.out.println(new FileSearchResult(new WordSearchMessage(""), fileB, InetAddress.getByName("localhost"), 20));
        }
    }
