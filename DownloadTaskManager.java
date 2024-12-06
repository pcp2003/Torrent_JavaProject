import java.util.*;
import java.util.concurrent.*;

public class DownloadTaskManager {

    private static final int BLOCK_SIZE = 10240;

    private final List<FileBlockRequestMessage> fileBlockRequestList = new LinkedList<>();
    private final List<FileBlockAnswerMessage> fileBlockAnswers = new ArrayList<>();
    private final List<NodeAgent> nodeAgentList;

    public DownloadTaskManager(int hashValue, long fileLength, List<NodeAgent> nodeAgentList) {
        this.nodeAgentList = nodeAgentList;

        // Divide o arquivo em blocos
        int nFullBlocks = (int) (fileLength / BLOCK_SIZE);
        for (int i = 0; i < nFullBlocks; i++) {
            fileBlockRequestList.add(new FileBlockRequestMessage(hashValue, (long) i * BLOCK_SIZE, BLOCK_SIZE));
        }
        long lastOffset = (long) nFullBlocks * BLOCK_SIZE;
        long rest = fileLength - lastOffset;
        if (rest > 0) {
            fileBlockRequestList.add(new FileBlockRequestMessage(hashValue, lastOffset, rest));
        }
    }

    //  Inicia o download
    // Todo falta esperar pela resposta para enviar a proxima request para o mesmo no
    public void startDownload() {

        int agentIndex = 0;

        while (true) {

            FileBlockRequestMessage blockRequest = getNextBlockRequest();

            if (blockRequest == null) {
                break;
            }

            NodeAgent nodeAgent = nodeAgentList.get(agentIndex);

            nodeAgent.sendObject(blockRequest);

            agentIndex = (agentIndex + 1) % nodeAgentList.size(); // Alterna para o próximo agente (com rotação circular)

        }
    }

    public synchronized FileBlockRequestMessage getNextBlockRequest () {

        if (!fileBlockRequestList.isEmpty()) {
            FileBlockRequestMessage request = fileBlockRequestList.remove(0);
            System.out.println("Sending request: " + request );
            return request;
        }
        return null;
    }

    // Aguarda por uma resposta correspondente
    private synchronized void waitForResponse(FileBlockRequestMessage request)  {

        while (true) {

            // Verifica se a answer recebida corresponde a request enviada!
            for ( FileBlockAnswerMessage answer : fileBlockAnswers) {

                if (answer.getOffset() == request.getOffset() && answer.getHash() == request.getHash()) {

                    System.out.println("Bloco baixado com sucesso: " + answer);
                    return;
                }

            }

            try {
                wait(); // Aguarda por uma nova resposta
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //  Adicionar respostas recebidas

    public synchronized void addFileBlockAnswer(FileBlockAnswerMessage answer) {
        fileBlockAnswers.add(answer);
        notifyAll(); // Notifica todas as threads aguardando
    }
}
