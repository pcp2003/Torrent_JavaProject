import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;

public class IscTorrentGUI extends JFrame {

    private final JTextField searchField;
    private final JList<String> resultsList;
    private final Node node;
    private Map<Integer, List<FileSearchResult>> searchHashMap = new HashMap<>();

    public IscTorrentGUI(int id) {
        
        this.node = new Node(this,8080 + id, "dl" + id);
        setTitle("IscTorrent " +  "localhost" + ":" + node.getPort());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Texto a procurar:");
        searchField = new JTextField(15);
        JButton searchButton = new JButton("Procurar");
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        resultsList = new JList<>(new DefaultListModel<>());
        JScrollPane resultsScrollPane = new JScrollPane(resultsList);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton downloadButton = new JButton("Descarregar");

        // Ação a realizar pressionar searchButton
        searchButton.addActionListener(_ -> {

            String wordToSearch = searchField.getText();

            node.searchMusic(wordToSearch);

        });

        // Ação a realizar pressionar downloadButton
        downloadButton.addActionListener(_ -> {

            if (!resultsList.isSelectionEmpty()) {
                for(int i: resultsList.getSelectedIndices()){
                    int selectedHash = searchHashMap.keySet().stream().toList().get(i);
                    node.download(searchHashMap.get(selectedHash));
                }

            }

        });

        JButton connectButton = new JButton("Ligar a Nó");

        connectButton.addActionListener(_ -> openConnectionDialog());

        buttonPanel.add(downloadButton);
        buttonPanel.add(connectButton);

        add(searchPanel, BorderLayout.NORTH);
        add(resultsScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);

        setVisible(true);
    }

    // Função para realizar o display das informações finais num OptionPane.
    public void displayDownloadInfo (int selectedHash, long time, Map<NodeAgent, Integer> nodeAgentAnswersCount) {

        StringBuilder message = new StringBuilder("Descarga completa.\n");

        for (FileSearchResult fileSearchResult : searchHashMap.get(selectedHash)) {

            NodeAgent matchingNode = nodeAgentAnswersCount.keySet().stream()
                    .filter(nodeAgent -> nodeAgent.getClientPort() == fileSearchResult.getPort())
                    .findFirst()
                    .orElse(null);

            int answersCount = nodeAgentAnswersCount.get(matchingNode);

            message.append("Fornecedor [endereco=")
                    .append(fileSearchResult.getAddress())
                    .append(", porta=")
                    .append(fileSearchResult.getPort())
                    .append("]:")
                    .append(answersCount)
                    .append("\n");
        }

        message.append("Tempo decorrido: ").append(time).append("ms");

        JOptionPane.showMessageDialog(
                this,
                message.toString(),
                "Download Info",
                JOptionPane.INFORMATION_MESSAGE
        );

    }

    // Função para abrir a connectionPane para adicionar as informações para se conectar a outro nó.
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

        okButton.addActionListener(_ -> {

            InetAddress addr;

            try {
                addr = InetAddress.getByName(addressField.getText());
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
            int port = Integer.parseInt(portField.getText());

            node.connectClient( addr, port, node.getConnectionRequest());

            connectionDialog.dispose();
        });

        cancelButton.addActionListener(_ -> connectionDialog.dispose());


        connectionDialog.add(addressLabel);
        connectionDialog.add(addressField);
        connectionDialog.add(portLabel);
        connectionDialog.add(portField);
        connectionDialog.add(cancelButton);
        connectionDialog.add(okButton);
        connectionDialog.setLocationRelativeTo(this);
        connectionDialog.setVisible(true);
    }

    // Função para atualizar a resultList chamada dentro do nó.
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
    }
}
