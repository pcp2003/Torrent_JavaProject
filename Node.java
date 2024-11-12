import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class Node extends Thread{
    public class NewConnectionRequest {
        Node sender;
        LocalDateTime time;
        NewConnectionRequest(Node s, LocalDateTime t){
            sender = s;
            time = t;
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
    @Override
    public void run () {
        try {
            System.out.println("Node: " + knownedNodes.get(knownedNodes.size()-1).toString());
        } catch ( InterruptedException e ) {}
    }
}
