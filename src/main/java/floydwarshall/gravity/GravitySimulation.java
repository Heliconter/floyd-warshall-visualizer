package floydwarshall.gravity;

import java.util.ArrayList;
import java.util.Arrays;

public class GravitySimulation {
    private double CHARGE_STRENGTH = 10;
    private double EDGE_STRENGTH = 15;
    private double GRAVITY_STRENGTH = 0.007;
    private double IDEAL_EDGE_DISTANCE = 140;
    private double REPULSION_DISTANCE = 140;

    private boolean[][] adjMatrix = new boolean[0][0];
    private Point center = null;

    public void simulationStep(ArrayList<? extends Point> points, int steps) {
        if (steps < 0) {
            throw new IllegalArgumentException("Amount of steps to simulate must be positive");
        }
        calculateVectors(points, steps);
    }

    public void simulationStep(ArrayList<? extends Point> points) {
        calculateVectors(points, 1);
    }

    public void setGravityCenter(Point center) {
        this.center = center;
    }

    public void updateAdjacencyMatrix(ArrayList<? extends Point> points, ArrayList<? extends Edge> edges) {
        int size = points.size();
        adjMatrix = new boolean[size][size];

        for (boolean[] row : adjMatrix) {
            Arrays.fill(row, false);
        }

        for (Edge edge : edges) {
            // TODO: find a better way for getting indices
            int indexFrom = indexOfPoint(points, edge.getFromPoint());
            int indexTo = indexOfPoint(points, edge.getToPoint());
            adjMatrix[indexFrom][indexTo] = true;
            adjMatrix[indexTo][indexFrom] = true;
        }

    }

    private void updateVectors(Point point1, Point point2, double attraction_force) {
        double dx = point2.getX() - point1.getX();
        double dy = point2.getY() - point1.getY();
        double vector_size = Math.sqrt(dx * dx + dy * dy);

        // spread points apart if they are inside each other
        if (vector_size < 1e-9) {
            double angle = Math.random() * 2 * Math.PI;
            dx = Math.sin(angle);
            dy = Math.cos(angle);
            vector_size = 1;
        }

        // update velocities
        point1.setDX(point1.getDX() + attraction_force * dx / vector_size);
        point1.setDY(point1.getDY() + attraction_force * dy / vector_size);
        point2.setDX(point2.getDX() - attraction_force * dx / vector_size);
        point2.setDY(point2.getDY() - attraction_force * dy / vector_size);
    }

    private void calculateAttractions(ArrayList<? extends Point> points) {
        int size = points.size();

        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                double dist = distance(points.get(i), points.get(j));

                if (adjMatrix[i][j]) {
                    double force = (dist < 1e-9) ? 1000 : EDGE_STRENGTH * (dist - IDEAL_EDGE_DISTANCE) / dist;

                    updateVectors(points.get(i), points.get(j), force);
                } else if (dist < REPULSION_DISTANCE) {
                    double force = (dist < 1e-9) ? 1000 : CHARGE_STRENGTH * (dist - REPULSION_DISTANCE) / dist;

                    updateVectors(points.get(i), points.get(j), force);
                }
            }
        }
    }

    private void gravitateTowards(ArrayList<? extends Point> points, Point center) {
        // update velocity of points considering gravitation center
        for (Point point : points) {
            double dist = distance(point, center);
            updateVectors(point, center, dist * GRAVITY_STRENGTH);
        }
    }

    private void calculateVectors(ArrayList<? extends Point> points, int iterations) {
        while (iterations-- > 0) {
            for (Point point : points) {
                if (!point.getAffectedByGravity()) {
                    continue;
                }

                // move point
                point.setX(point.getX() + point.getDX());
                point.setY(point.getY() + point.getDY());
                point.setDX(0);
                point.setDY(0);
            }

            // recalculate velocity vectors
            calculateAttractions(points);

            // gravitate towards center
            if (center != null) {
                gravitateTowards(points, center);
            }
        }
    }

    private double distance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(point1.getX() - point2.getX(), 2) + Math.pow(point1.getY() - point2.getY(), 2));
    }

    private int indexOfPoint(ArrayList<? extends Point> points, Point point) {
        for (int i = 0; i < points.size(); i++) {
            if (point == points.get(i)) {
                return i;
            }
        }
        return -1;
    }
}
