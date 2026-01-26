import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;
import java.util.Arrays;

public class Canvas extends JPanel{
    private JPanel canvasPanel;
    private Mesh mesh;
    int x = 0, y = 0;

    public Canvas(Mesh mesh) {
        setBackground(Color.GRAY);
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                double yi = 180.0 / getHeight();
                double xi = 180.0 / getWidth();
                x = (int) (e.getX() * xi);
                y = -(int) (e.getY() * yi);
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
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

        Matrix4 rotX = Matrix4.createXRotationMatrix(x);
        Matrix4 rotY = Matrix4.createYRotationMatrix(y);

        Matrix4 model = rotX.multiply(rotY);

        for (Triangle tri : mesh.getTris()) {
            Vertex v1 = model.multiply(tri.getV1());
            Vertex v2 = model.multiply(tri.getV2());
            Vertex v3 = model.multiply(tri.getV3());

            Path2D path = new Path2D.Double();
            path.moveTo(v1.getX(), v1.getY());
            path.lineTo(v2.getX(), v2.getY());
            path.lineTo(v3.getX(), v3.getY());
            path.closePath();
            g2.draw(path);
        }
    }
}
