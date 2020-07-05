package floydwarshall.executor;

public interface ExecutorInterface {
    void setGraph(int verticesAmount, Edge[] edges);
    Integer getPathLength(PathEnds pathEnds);
    int getVerticesAmount();
    void step(int amount);
    void toEnd();
    boolean isFinished();
    void addObserver(ExecutorObserver observer);
}
