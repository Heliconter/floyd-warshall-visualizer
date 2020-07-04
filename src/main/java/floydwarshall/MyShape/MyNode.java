package floydwarshall.MyShape;

import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class MyNode {

    private Ellipse ellipse;
    private ArrayList<MyLine> linesStartPoint;
    private ArrayList<MyLine> linesEndPoint;
    private Text text;
    public static final int radius = 10;

    public MyNode(double centerX, double centerY) {
        ellipse = new Ellipse();
        ellipse.setCenterX(centerX);
        ellipse.setCenterY(centerY);
        ellipse.setRadiusX(radius);
        ellipse.setRadiusY(radius);
        ellipse.setFill(Color.SNOW);
        ellipse.setStroke(Color.BLACK);
        text = new Text(centerX,centerY,"A");
        text.setX(centerX-4);   // для выравнивания
        text.setY(centerY+3);   // для выравнивания
        linesStartPoint = new ArrayList<>();
        linesEndPoint = new ArrayList<>();
    }

    public void updatePosition(double centerX, double centerY){
        ellipse.setCenterX(centerX);
        ellipse.setCenterY(centerY);
        text.setX(centerX-4);   // для выравнивания
        text.setY(centerY+3);   // для выравнивания
        for (MyLine line : linesStartPoint) {
            line.setStartX(centerX);
            line.setStartY(centerY);
            line.updateTriangle();
            if (!line.isConvex()) {
                line.setControlX(centerX);
                line.setControlY(centerY);
            }else {
                MyMath.setControlPointForOnlyLine(line);
            }
        }
        for (MyLine line : linesEndPoint) {
            line.setEndX(centerX);
            line.setEndY(centerY);
            line.updateTriangle();
            if (line.isConvex()){
                MyMath.setControlPointForOnlyLine(line);
            }
        }
    }

    public Ellipse getEllipse() {
        return ellipse;
    }

    public Text getText() {
        return text;
    }

    public ArrayList<MyLine> getLinesStartPoint() {
        return linesStartPoint;
    }

    public ArrayList<MyLine> getLinesEndPoint() {
        return linesEndPoint;
    }

    public void addLineStartPoint(MyLine line){
        linesStartPoint.add(line);
    }

    public void addLineEndPoint(MyLine line){
        linesEndPoint.add(line);
    }

    public void popLineStartPoint(MyLine line) {
        if (line!=null) {
            linesStartPoint.remove(line);
        }
    }

    public void popLineEndPoint(MyLine line){
        if (line!=null) {
            linesEndPoint.remove(line);
        }
    }

   /* public void deleteNode(GraphView group){
        group.getChildren().remove(ellipse);
        group.getChildren().remove(text);
        for (MyLine line : linesStartPoint) {
            group.getChildren().remove(line);
            group.getChildren().remove(line.getTriangle());
        }
        for (MyLine line : linesEndPoint) {
            group.getChildren().remove(line);
            group.getChildren().remove(line.getTriangle());

        }
    }*/

    public void setName(String name){
        text.setText(name);
    }

    public void drawFront(){
        ellipse.toFront();
        text.toFront();
    }


}
