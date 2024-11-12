import javax.management.InstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NetworkManager{
    private List<Node> nodesInNetwork = new ArrayList<Node>();

    public void requestConnection(Node sender, NewConnectionRequest req) throws InstanceNotFoundException {

        if(sender.equals(req) ) throw new IllegalArgumentException("Cant connect to self");
        for(Node node : nodesInNetwork) {
            if(node.equals(req)){
                sender.connect(node);
                node.connect(sender);
                System.out.println(sender + " connected to " + node );
                return;
            }
        }
        throw new InstanceNotFoundException();
    }

    public void addNode(Node node){
        nodesInNetwork.add(node);
    }

}
