import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;

public class IscTorrentGUI extends JFrame {

    private JTextField searchField;
    private JButton searchButton;
    private JList<String> resultsList;
    private JButton downloadButton;
    private JButton connectButton;
    private Node node;
    private Map<Integer, List<FileSearchResult>> searchHashMap = new HashMap<>();

    public IscTorrentGUI(int id) throws UnknownHostException {
        this.node = new Node(this, InetAddress.getLocalHost(), 8080 + id, "dl" + id);
        setTitle("IscTorrent " +  "localhost" + ":" + node.getPort());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Texto a procurar:");
        searchField = new JTextField(15);
        searchButton = new JButton("Procurar");
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        resultsList = new JList<>(new DefaultListModel<>());
        JScrollPane resultsScrollPane = new JScrollPane(resultsList);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        downloadButton = new JButton("Descarregar");

        searchButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                String wordToSearch = searchField.getText();

                node.searchMusic(wordToSearch);

            }
        });

        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Integer selectedHash = searchHashMap.keySet().stream().toList().get(resultsList.getSelectedIndex());
                String message = "A fazer download de " + searchHashMap.get(selectedHash).getFirst().getFileName() + " através de " +  searchHashMap.get(selectedHash).size() + " nós";
                for(FileSearchResult fileSearchResult : searchHashMap.get(selectedHash)){
                    message+=" [" + fileSearchResult.getAddress() + "/" + fileSearchResult.getPort() + "] ";
                }
                System.out.println(message);
                startDownload();
            }
        });

        connectButton = new JButton("Ligar a Nó");

        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openConnectionDialog();
            }
        });

        buttonPanel.add(downloadButton);
        buttonPanel.add(connectButton);

        add(searchPanel, BorderLayout.NORTH);
        add(resultsScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);

        setVisible(true);
    }

    private void startDownload() {

        
    }



    private void openConnectionDialog() {
        JDialog connectionDialog = new JDialog(this, "Conectar a Nó", true);
        connectionDialog.setSize(500, 120);
        connectionDialog.setLayout(new FlowLayout());

        JLabel addressLabel = new JLabel("Endereço:");
        JTextField addressField = new JTextField("localhost", 15);
        JLabel portLabel = new JLabel("Porta:");
        JTextField portField = new JTextField("8081", 5);
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancelar");

        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                String addr = addressField.getText();
                int port = Integer.parseInt(portField.getText());

                node.connectClient( addr, port, node.getConnectionRequest());

                connectionDialog.dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectionDialog.dispose();
            }
        });


        connectionDialog.add(addressLabel);
        connectionDialog.add(addressField);
        connectionDialog.add(portLabel);
        connectionDialog.add(portField);
        connectionDialog.add(cancelButton);
        connectionDialog.add(okButton);
        connectionDialog.setLocationRelativeTo(this);
        connectionDialog.setVisible(true);
    }

    public void updateMusicResultList(List<FileSearchResult> musicSearchResult) {
        // Atualizando o modelo da lista
        DefaultListModel<String> model = new DefaultListModel<>();
        searchHashMap = FileSearchResult.hashMap(musicSearchResult);
        Set<Integer> hashes = searchHashMap.keySet();
        System.out.println(hashes);
        for (Integer hash : hashes) {
            model.addElement(searchHashMap.get(hash).getFirst().getFileName() + " [" + hash + "] " + "(" + searchHashMap.get(hash).size() + ")");
        }
        resultsList.setModel(model); // Definindo o novo modelo no JList

//        DefaultListModel<String> model = new DefaultListModel<>();
//        Map<Integer, List<FileSearchResult>> results = FileSearchResult.hashMap(musicSearchResult);
//        List<Integer> hashes = results.keySet().stream().toList();
//        for (Integer hash : hashes) {
//            model.addElement(results.get(hash).getFirst().getFileName() + "(" + hash + ")" + "(" + results.get(hash).size() + ")");
//        }
//        resultsList.setModel(model); // Definindo o novo modelo no JList
    }
}
