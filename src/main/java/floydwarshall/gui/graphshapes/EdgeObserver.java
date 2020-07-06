package floydwarshall.gui.graphshapes;

@FunctionalInterface
public interface EdgeObserver {
    void edgeChanged(Line edge);
}
