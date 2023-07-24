import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Node {
    private int id;
    private int timestamp;
    private List<Integer> replies;
    private List<Integer> deferred;

    public Node(int id, int timestamp) {
        this.id = id;
        this.timestamp = timestamp;
        this.replies = new ArrayList<>();
        this.deferred = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public List<Integer> getReplies() {
        return replies;
    }

    public List<Integer> getDeferred() {
        return deferred;
    }

    public void addReply(int nodeId) {
        replies.add(nodeId);
    }

    public void addDeferred(int nodeId) {
        deferred.add(nodeId);
    }

    public boolean canEnterCS(List<Node> nodes) {
        for (Node node : nodes) {
            if (node.getId() != id && !replies.contains(node.getId())) {
                return false;
            }
        }
        return true;
    }
}

class RicartAgrawalaDMEAlgorithm {
    private List<Node> nodes;

    public RicartAgrawalaDMEAlgorithm() {
        nodes = new ArrayList<>();
    }

    public void readFromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            int nodeId = Integer.parseInt(parts[0]);
            int timestamp = Integer.parseInt(parts[1]);
            nodes.add(new Node(nodeId, timestamp));
        }
        reader.close();
    }

    public void run() {
        for (int i = 0; i < nodes.size(); i++) {
            Node requestingNode = nodes.get(i);
            System.out.println("Node " + requestingNode.getId() +
                    " is requesting to enter critical section with timestamp " + requestingNode.getTimestamp());

            for (int j = 0; j < nodes.size(); j++) {
                Node respondingNode = nodes.get(j);
                if (respondingNode.getId() != requestingNode.getId()) {
                    if (requestingNode.getTimestamp() > respondingNode.getTimestamp()) {
                        respondingNode.addReply(requestingNode.getId());
                        System.out.println("Node " + respondingNode.getId() +
                                " has replied to Node " + requestingNode.getId());
                    } else if (requestingNode.getTimestamp() == respondingNode.getTimestamp()) {
                        if (requestingNode.getId() < respondingNode.getId()) {
                            respondingNode.addReply(requestingNode.getId());
                            System.out.println("Node " + respondingNode.getId() +
                                    " has replied to Node " + requestingNode.getId());
                        } else {
                            requestingNode.addDeferred(respondingNode.getId());
                        }
                    } else {
                        requestingNode.addDeferred(respondingNode.getId());
                    }
                }
            }

            if (requestingNode.canEnterCS(nodes)) {
                System.out.println("Node " + requestingNode.getId() + " entered critical section");

                for (int deferredNode : requestingNode.getDeferred()) {
                    Node deferred = nodes.get(deferredNode);
                    deferred.addReply(requestingNode.getId());
                    System.out.println("Node " + deferred.getId() + " has replied to Node " + requestingNode.getId());
                }

                requestingNode.getDeferred().clear();
            }

            System.out.println(); // Print an empty line for clarity
        }
    }

    public static void main(String[] args) {
        RicartAgrawalaDMEAlgorithm algorithm = new RicartAgrawalaDMEAlgorithm();

        try {
            algorithm.readFromFile("input1.txt");
            algorithm.run();
        } catch (IOException e) {
            System.out.println("Error reading the input file: " + e.getMessage());
        }
    }
}
