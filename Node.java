import java.io.File;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.FileFilter;

public class Node {

    public class NewConnectionRequest {

        Node sender;
        LocalDateTime time;

        NewConnectionRequest(Node s, LocalDateTime t) {
            sender = s;
            time = t;
        }
    }

    private List<Node> knownedNodes = new ArrayList<Node>();

    private String addr; 

    private int port; 

    public Node( String addr, int port ) {

        this.addr = addr;
        this.port = port;
    }

    public void connect(NewConnectionRequest req) {
        knownedNodes.add(req.sender);
    }

    public String[] getFileList() {

        File[] files = (new File( "files")).listFiles(new FileFilter() {
            public boolean accept(File f) {     
                 return f.isFile() && f.getName().endsWith("mp3");
            }
       });

       return Arrays.stream(files).map(File :: getPath).toArray(String[] :: new);
    }
}
