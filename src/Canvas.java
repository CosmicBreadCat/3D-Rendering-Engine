import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Canvas extends JPanel{
    private JPanel canvasPanel;
    private ArrayList<Point> points = new ArrayList<>();
    private final int POINT_SIZE = 20;


    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        g.setColor(Color.BLUE);
        for (Point point: points){
            g.fillRect(getScreenX(point), getScreenY(point), POINT_SIZE, POINT_SIZE);
        }
    }

    private int getScreenY(Point point) {
        return ((Canvas.this.getHeight()/2) - point.getY()) - 10;
    }

    private int getScreenX(Point point) {
        return ((Canvas.this.getWidth()/2) + point.getX()) - 10;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point){
        if (!points.contains(point)){
            points.add(point);
        }
    }

    public void removePoint(Point point){
        points.remove(point);
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }
}
