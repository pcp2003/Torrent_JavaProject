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

            String[] files = node.getFileList();

            for (String file : files) {
                if (file.equals(toFind))
                    break;
            }
        }
    }
}
