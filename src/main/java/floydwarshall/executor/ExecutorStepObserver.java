package floydwarshall.executor;

@FunctionalInterface
public interface ExecutorStepObserver {
    void stepExecuted(PathEnds pathEnds);
}
