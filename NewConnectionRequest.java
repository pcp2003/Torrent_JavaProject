public class NewConnectionRequest {
    private String addr;
    private int port;

    NewConnectionRequest(String addr, int port) {
        this.addr = addr;
        this.port = port;
    }

    public String getAddr() {return addr;}
    public int getPort() {return port;}
}
