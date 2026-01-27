import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class Canvas extends JPanel{
    private JPanel canvasPanel;
    private final Mesh mesh;
    int rotX = 0, rotY = 0, rotZ = 0;

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

    public void renderMesh(Graphics2D g2) {
        g2.translate(getWidth() / 2, getHeight() / 2);
        g2.setColor(Color.WHITE);

        Matrix4 rotX = Matrix4.createXRotationMatrix(this.rotX);
        Matrix4 rotY = Matrix4.createYRotationMatrix(this.rotY);
        Matrix4 rotZ = Matrix4.createZRotationMatrix(this.rotZ);

        Matrix4 model = rotX.multiply(rotY.multiply(rotZ));

        BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
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

    public int getRotX() {
        return rotX;
    }

    public void setRotX(int rotX) {
        this.rotX = rotX;
    }

    public int getRotY() {
        return rotY;
    }

    public void setRotY(int rotY) {
        this.rotY = rotY;
    }

    public int getRotZ() {
        return rotZ;
    }

    public void setRotZ(int rotZ) {
        this.rotZ = rotZ;
    }
}
