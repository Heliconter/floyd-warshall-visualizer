package floydwarshall.executor;

public interface ExecutorInterface {
    void setGraph(int verticesAmount, Edge[] edges); // Is verticesAmount useful?
    Integer getPathLength(PathEnds pathEnds);
    Integer getEdgeWeight(Edge edge);
    void step(int amount);
    void toEnd();
    boolean isFinished();
    void addObserver(ExecutorStepObserver observer); // Should save observer to some array
}
