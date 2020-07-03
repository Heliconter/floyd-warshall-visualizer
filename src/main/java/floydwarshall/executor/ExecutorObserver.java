package floydwarshall.executor;

@FunctionalInterface
public interface ExecutorObserver {
    void stateChanged();
}
