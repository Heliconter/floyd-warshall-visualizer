package floydwarshall.executor;

import java.util.ArrayList;
import java.util.Stack;

public class Executor implements ExecutorInterface {
    private int k;
    private int from;
    private int to;
    private int verticesAmount;
    private Integer[][] matrix;

    private Stack<Integer> history;

    private ArrayList<ExecutorObserver> observers;

    private boolean notificationsEnabled = true;

    public Executor() {
        matrix = new Integer[0][0];
        verticesAmount = 0;
        k = 0;
        observers = new ArrayList<ExecutorObserver>();
    }

    public void setGraph(int verticesAmount, ArrayList<Edge> edges) {
        k = 1;
        from = 0;
        to = 0;
        this.verticesAmount = verticesAmount;
        history = new Stack<Integer>();

        matrix = new Integer[verticesAmount][verticesAmount];

        // fill matrix with infinities (Integer.MAX_VALUE)
        for (int i = 0; i < verticesAmount; i++) {
            for (int j = 0; j < verticesAmount; j++) {
                matrix[i][j] = null;
            }
        }

        // fill edge weights
        for (Edge edge : edges) {
            if (edge.from != edge.to) {
                matrix[edge.from][edge.to] = edge.weight;
            }
        }

        // fill diagonal with 0
        for (int i = 0; i < verticesAmount; i++) {
            matrix[i][i] = 0;
        }

        notifyObservers();
    }

    // NOTE: returns null if the path has not yet been found
    public Integer getPathLength(PathEnds pathEnds) {
        Integer length = matrix[pathEnds.from][pathEnds.to];
        return length;
    }

    public int getVerticesAmount() {
        return verticesAmount;
    }

    public void step(int amount) {
        if (amount > 0) {
            stepForward(amount);
        } else {
            stepBackward(amount);
        }

    };

    public int getK() {
        return k;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public void toEnd() {
        notificationsEnabled = false;

        while (!isFinished()) {
            step(100);
        }

        notificationsEnabled = true;
        notifyObservers();
    };

    public boolean isFinished() {
        return k == verticesAmount;
    };

    public void addObserver(ExecutorObserver observer) {
        observers.add(observer);
    };

    private void stepForward(int amount) {
        for (; amount > 0 && !isFinished(); amount--) {
            history.push(matrix[from][to]);

            // update value
            if (matrix[from][k] != null && matrix[k][to] != null) {
                if (matrix[from][to] != null) {
                    matrix[from][to] = Math.min(matrix[from][to],
                                                matrix[from][k] + matrix[k][to]);
                } else {
                    matrix[from][to] = matrix[from][k] + matrix[k][to];
                }
            }
            nextCell();
        }

        notifyObservers();
    }

    private void stepBackward(int amount) {
        if (history == null) {
            return;
        }

        amount = -amount;
        for (; (amount > 0) && !history.isEmpty(); amount--) {
            prevCell();

            // restore previous value
            matrix[from][to] = history.pop();
        }

        notifyObservers();
    }

    private void nextCell() {
        if (++to == verticesAmount) {
            to = 0;
            if (++from == verticesAmount) {
                from = 0;
                k++;
            }
        }
    }

    private void prevCell() {
        if (--to == -1) {
            to = verticesAmount - 1;
            if (--from == -1) {
                from = verticesAmount - 1;
                k--;
            }
        }
    }

    private void notifyObservers() {
        if (!notificationsEnabled) {
            return;
        }

        for (ExecutorObserver observer : observers) {
            observer.stateChanged();
        }
    }
}
