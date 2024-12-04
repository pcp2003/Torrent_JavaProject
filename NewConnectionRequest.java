import java.io.Serializable;

public class NewConnectionRequest implements Serializable {

    private int port;

    public NewConnectionRequest(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "NewConnectionRequest{" +
                "port=" + port +
                '}';
    }

    public int getPort() {
        return port;
    }

}
