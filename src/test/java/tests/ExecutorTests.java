package floydwarshall.tests;

import floydwarshall.executor.Executor;
import floydwarshall.executor.Edge;
import floydwarshall.executor.PathEnds;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExecutorTests {
    Executor executor;
    static int verticesAmount;
    static Integer[][] expectedInitialMatrix;
    static Integer[][] expectedFinalMatrix;

    @BeforeAll
    static void initMatrix() {
        verticesAmount = 6;

        expectedInitialMatrix = new Integer[][] {
            {null, null, 1, null, 2, 3},
            {null, null, null, null, 4, 5},
            {null, null, null, 6, 7, null},
            {null, null, null, null, null, null},
            {null, null, null, null, null, 8},
            {null, 9, null, null, null, null},
        };

        expectedFinalMatrix = new Integer[][] {
            {null, 12, 1, 7, 2, 3},
            {null, 14, null, null, 4, 5},
            {null, 24, null, 6, 7, 15},
            {null, null, null, null, null, null},
            {null, 17, null, null, 21, 8},
            {null, 9, null, null, 13, 14},
        };
    }

    @BeforeEach
    void initExecutor() {
        executor = new Executor();

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 2, 1));
        edges.add(new Edge(0, 4, 2));
        edges.add(new Edge(0, 5, 3));
        edges.add(new Edge(1, 4, 4));
        edges.add(new Edge(1, 5, 5));
        edges.add(new Edge(2, 3, 6));
        edges.add(new Edge(2, 4, 7));
        edges.add(new Edge(4, 5, 8));
        edges.add(new Edge(5, 1, 9));

        executor.setGraph(verticesAmount, edges);
    }

    @Test
    void testEmptyGraph() {
        Executor _executor = new Executor();
        assertTrue(_executor.isFinished());
    }

    @Test
    void testMatrixInitialization() {
        for (int i = 0; i < verticesAmount; i++) {
            for (int j = 0; j < verticesAmount; j++) {
                assertEquals(expectedInitialMatrix[i][j],
                             executor.getPathLength(new PathEnds(i, j)));
            }
        }
    }

    @Test
    void testFinalMatrix() {
        executor.toEnd();

        for (int i = 0; i < verticesAmount; i++) {
            for (int j = 0; j < verticesAmount; j++) {
                assertEquals(expectedFinalMatrix[i][j],
                             executor.getPathLength(new PathEnds(i, j)));
            }
        }
    }

    @Test
    void testNextPrevCell() {
        executor.step(verticesAmount);
        assertEquals(executor.getK(), 0);
        assertEquals(executor.getFrom(), 1);
        assertEquals(executor.getTo(), 0);

        executor.step(verticesAmount * (verticesAmount - 1));
        assertEquals(executor.getK(), 1);
        assertEquals(executor.getFrom(), 0);
        assertEquals(executor.getTo(), 0);

        executor.step(verticesAmount * verticesAmount * (verticesAmount - 1));
        assertTrue(executor.isFinished());
    }

    @Test
    void testHistory() {
        executor.step(100);
        executor.step(-10);
        executor.step(-90);

        for (int i = 0; i < verticesAmount; i++) {
            for (int j = 0; j < verticesAmount; j++) {
                assertEquals(expectedInitialMatrix[i][j],
                             executor.getPathLength(new PathEnds(i, j)));
            }
        }
    }

    @Test
    void testObserversNotification() {
        class Observer {
            public boolean wasNotified = false;
            public void notifyChanged() {
                wasNotified = true;
            }
        }

        Observer[] observers = new Observer[] {
            new Observer(),
            new Observer(),
            new Observer(),
            new Observer(),
            new Observer(),
        };

        for (Observer observer : observers) {
            executor.addObserver(observer::notifyChanged);
        }

        executor.step(1);

        for (Observer observer : observers) {
            assertTrue(observer.wasNotified);
        }
    }
}
