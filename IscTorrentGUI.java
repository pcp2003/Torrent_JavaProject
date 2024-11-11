import javax.swing.*;
import java.awt.*;

public class IscTorrentGUI extends JFrame {

    private JTextField searchField;
    private JButton searchButton;
    private JList<String> resultsList;
    private JButton downloadButton;
    private JButton connectButton;

    public IscTorrentGUI() {
        setTitle("IscTorrent");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel de pesquisa
        JPanel searchPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Texto a procurar:");
        searchField = new JTextField(15);
        searchButton = new JButton("Procurar");
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Lista de resultados
        resultsList = new JList<>(new DefaultListModel<>());
        JScrollPane resultsScrollPane = new JScrollPane(resultsList);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        downloadButton = new JButton("Descarregar");
        connectButton = new JButton("Ligar a Nó");
        buttonPanel.add(downloadButton);
        buttonPanel.add(connectButton);

        // Adiciona os componentes à janela principal
        add(searchPanel, BorderLayout.NORTH);
        add(resultsScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(IscTorrentGUI::new);
    }
}
