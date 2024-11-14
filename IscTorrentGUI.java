import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IscTorrentGUI extends JFrame {

    private JTextField searchField;
    private JButton searchButton;
    private JList<String> resultsList;
    private JButton downloadButton;
    private JButton connectButton;
    private Node node;

    public IscTorrentGUI(Node node) {
        this.node = node;
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

        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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

                // O endereço não importa por causa do InetAdress,
                // mas posteriormente vale a pena implementar a parte do adress para impedir o erro

                int port = Integer.parseInt(portField.getText());

                node.startClient(port);

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

}
