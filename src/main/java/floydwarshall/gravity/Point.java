package floydwarshall.gravity;

public interface Point {
    public double getX();
    public double getY();
    public double getDX();
    public double getDY();
    public boolean getAffectedByGravity();
    public void setX(double value);
    public void setY(double value);
    public void setDX(double value);
    public void setDY(double value);
    public void setAffectedByGravity(boolean value);
}
