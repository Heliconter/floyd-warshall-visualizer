package floydwarshall.saveload;

public class EdgeInfrom {
    public char nameStartNode;
    public char nameEndNode;
    public int weight;
    EdgeInfrom(char nameNode1, char nameNode2, int weight) {
        this.nameStartNode = nameNode1;
        this.nameEndNode = nameNode2;
        this.weight = weight;
    }
}

