import javax.management.InstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NetworkManager{
    private List<Node> nodesInNetwork = new ArrayList<Node>();

    public void requestConnection(Node sender, NewConnectionRequest req) throws InstanceNotFoundException {
        for(Node node : nodesInNetwork) {
            if(node.getAddr().equals(req.getAddr()) && node.getPort() == req.getPort()){
                sender.connect(node);
                node.connect(sender);
            }
        }
        throw new InstanceNotFoundException();
    }

    public void addNode(Node node){
        nodesInNetwork.add(node);
    }

}
