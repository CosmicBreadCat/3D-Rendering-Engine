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
            path.moveTo(t.getV1().getX(), t.getV1().getY());
            path.lineTo(t.getV2().getX(), t.getV2().getY());
            path.lineTo(t.getV3().getX(), t.getV3().getY());
            path.closePath();
            g2.draw(path);
        }
    }
}
