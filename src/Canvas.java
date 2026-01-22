import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Canvas extends JPanel{
    private JPanel canvasPanel;
    private ArrayList<Point> points = new ArrayList<>();
    private final int POINT_SIZE = 20;

    public Canvas() {
        setBackground(Color.GRAY);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        g.setColor(Color.BLUE);
        for (Point point: points){
            g.fillRect(getScreenX(point), getScreenY(point), POINT_SIZE, POINT_SIZE);
        }
    }

    private int getScreenX(Point point) {
        // projects the point into the plane of the screen and then shifts its coordinates
        // to the coordinates of the screen
        return Math.round((((float) Canvas.this.getWidth() /2) + point.getX()/(point.getZ()/100)) - (float) POINT_SIZE /2);
    }

    private int getScreenY(Point point) {
        // same as X but Y is inverted because screen axis is negative
        return Math.round((((float) Canvas.this.getHeight() /2) - point.getY()/(point.getZ()/100)) - (float) POINT_SIZE /2);
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
