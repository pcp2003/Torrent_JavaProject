public class NodeAgentTask<T> {

    private final T task;
    private final NodeAgent nodeAgent;

    NodeAgentTask(T task, NodeAgent nodeAgent) {
        this.task = task;
        this.nodeAgent = nodeAgent;
    }

    public T getTask() {
        return task;
    }

    public NodeAgent getNodeAgent() {
        return nodeAgent;
    }

    @Override
    public String toString() {
        return "NodeAgentTask{" +
                "task=" + task +
                ", nodeAgent=" + nodeAgent +
                '}';
    }
}
