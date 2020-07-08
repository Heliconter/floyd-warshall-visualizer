package floydwarshall.saveload;

import java.util.ArrayList;

public class GraphInform {
    private boolean isDataReadCorrect;
    private ArrayList<NodeInform> nodes;
    private ArrayList<EdgeInfrom> edges;

    public GraphInform(boolean isDataReadCorrect, ArrayList<NodeInform> localPoints, ArrayList<EdgeInfrom> localEdges) {
        this.isDataReadCorrect = isDataReadCorrect;
        this.nodes = localPoints;
        this.edges = localEdges;
    }

    public boolean isDataReadCorrect() {
        return isDataReadCorrect;
    }

    public ArrayList<NodeInform> getNodes() {
        return nodes;
    }

    public ArrayList<EdgeInfrom> getEdges() {
        return edges;
    }
}
