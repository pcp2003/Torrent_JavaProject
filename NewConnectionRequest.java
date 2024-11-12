public class NewConnectionRequest {

    private String addr;
    private int port;

    NewConnectionRequest(String addr, int port) {
        this.addr = addr;
        this.port = port;
    }

    public String getAddr() {return addr;}
    public int getPort() {return port;}

    @Override
    public boolean equals(Object o) {

        if (o instanceof Node) {
            return addr.equals(((Node) o).getAddr()) && port == ((Node) o).getPort();
        }

        if (o instanceof NewConnectionRequest) {
            return addr.equals(((NewConnectionRequest) o).getAddr()) && port == ((NewConnectionRequest) o).getPort();
        }

        return false;
    }
}
