package floydwarshall.executor;

import java.util.ArrayList;

public interface ExecutorInterface {
    void setGraph(int verticesAmount, ArrayList<Edge> edges);
    Integer getPathLength(PathEnds pathEnds);
    int getK();
    int getFrom();
    int getTo();
    int getVerticesAmount();
    void step(int amount);
    void toEnd();
    boolean isFinished();
    void addObserver(ExecutorObserver observer);
}
