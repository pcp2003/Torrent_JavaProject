public class Main {

    public static void main(String[] args) {

        int port = Integer.parseInt(args[0]);

        String folderName = args[1];

//        int port = 8081;
//
//        String folderName = "dl1";

        Node a = new Node(port, folderName);

        IscTorrentGUI visualizadorA = new IscTorrentGUI(a);
        visualizadorA.setVisible(true);

    }
}
