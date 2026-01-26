import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class Canvas extends JPanel{
    private JPanel canvasPanel;
    private Mesh mesh;

    public Canvas(Mesh mesh) {
        setBackground(Color.GRAY);
        this.mesh = mesh;
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.black);
        g2.fillRect(0,0,getWidth(), getHeight());

        renderMesh(g2);
    }

    public void renderMesh(Graphics2D g2){
        g2.translate(getWidth() / 2, getHeight() / 2);
        g2.setColor(Color.WHITE);
        for (Triangle t : mesh.getTris()) {
            Path2D path = new Path2D.Double();
            path.moveTo(t.v1.getX(), t.v1.getY());
            path.lineTo(t.v2.getX(), t.v2.getY());
            path.lineTo(t.v3.getX(), t.v3.getY());
            path.closePath();
            g2.draw(path);
        }
    }
}
