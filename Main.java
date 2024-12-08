import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) throws UnknownHostException {
        IscTorrentGUI visualizadorA = new IscTorrentGUI(Integer.parseInt(args[0]));
        visualizadorA.setVisible(true);

    }
}
