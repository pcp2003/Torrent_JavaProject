import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args){
        IscTorrentGUI visualizador = new IscTorrentGUI(Integer.parseInt(args[0]));
        visualizador.setVisible(true);

    }
}
