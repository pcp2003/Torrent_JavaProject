import java.util.List;

public class FileSearcher extends Thread {

    String toFind;
    List<Node> knownedNodes;

    FileSearcher(String f, List<Node> knownedNodes) {
        this.knownedNodes = knownedNodes;
        toFind = f;
    }

    @Override
    public void run() {

        for (Node node : knownedNodes) {

            List<String> files = node.getFiles();

            for (String file : files) {
                if (file.equals(toFind))
                    break;
            }
        }
    }
}
