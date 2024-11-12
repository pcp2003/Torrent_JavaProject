import java.io.File;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node{
    public class NewConnectionRequest {
        Node sender;
        LocalDateTime time;
        NewConnectionRequest(Node s, LocalDateTime t){
            sender = s;
            time = t;
        }
    }
    public class FileSearcher extends Thread {
        String toFind;
        FileSearcher(String f){
            toFind = f;
        }
        @Override
        public void run(){
            try{
                for(Node node: knownedNodes){
                    String[] files = node.getFileList();
                    for(String file : files){
                        if(file.equals(toFind)) break;
                    }
                }

            }catch(InterruptedException e){
                throw e;
            }
        }
    }

    private List<Node> knownedNodes = new ArrayList<Node>() ;
    Node(List<Node> nodes){
        if(nodes.isEmpty()) throw new IllegalArgumentException("Node list is empty");
        for(Node node: nodes) {
            node.connect(new NewConnectionRequest(this, LocalDateTime.now()));
            knownedNodes.add(node);
        }
    }

    public void connect(NewConnectionRequest req){
        knownedNodes.add(req.sender);
    }

    private String[] getFileList() {
        return new File("/files").list();
    }
}
