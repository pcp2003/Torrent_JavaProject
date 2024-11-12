import javax.management.InstanceNotFoundException;
import java.io.File;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.FileFilter;

public class Node {
    private List<Node> knownedNodes = new ArrayList<Node>();
    private NetworkManager manager;
    private String addr;
    private int port;
    private List<String> files;

    public Node(String addr, int port, NetworkManager manager) {
        this.addr = addr;
        this.port = port;
        this.manager = manager;
        manager.addNode(this);
        for (File file : new File( "files").listFiles()) {
            if (file.isFile() && file.getName().endsWith("mp3")) {
                files.add(file.getName());
            }
        }
    }

    public void askToConnect(String addr, int port) throws InstanceNotFoundException {
            manager.requestConnection(this, new NewConnectionRequest(addr,port));
    }

    public void connect(Node node){
        knownedNodes.add(node);
    }

    public List<String> getFiles() {
       return files;
    }

    public void addFile(String file) {
        files.add(file);
    }

    public String getAddr() {
        return addr;
    }

    public int getPort() {
        return port;
    }
}
