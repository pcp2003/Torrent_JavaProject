import javax.management.InstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NetworkManager{
    private List<Node> nodesInNetwork = new ArrayList<Node>();

    public void requestConnection(Node sender, NewConnectionRequest req) throws InstanceNotFoundException {
        if(sender.getAddr().equals(req.getAddr()) && sender.getPort() == req.getPort()) throw new IllegalArgumentException("Cant connect to self");
        for(Node node : nodesInNetwork) {
            if(node.getAddr().equals(req.getAddr()) && node.getPort() == req.getPort()){
                sender.connect(node);
                node.connect(sender);
                return;
            }
        }
        throw new InstanceNotFoundException();
    }

    public void addNode(Node node){
        nodesInNetwork.add(node);
    }

}
