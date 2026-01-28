import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Canvas extends JPanel{
    private JPanel canvasPanel;
    private final Mesh mesh;
    private int rotX = 0, rotY = 0, rotZ = 0;
    private boolean showWireFrame = false;
    private BufferedImage img = null;

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
        g2.setColor(Color.WHITE);

        Matrix4 rotX = Matrix4.createXRotationMatrix(this.rotX);
        Matrix4 rotY = Matrix4.createYRotationMatrix(this.rotY);
        Matrix4 rotZ = Matrix4.createZRotationMatrix(this.rotZ);

        Matrix4 model = rotX.multiply(rotY.multiply(rotZ.multiply(Matrix4.createScalingMatrix(100,100,100))));

        if (showWireFrame){
            renderWireFrame(g2, model);
        }else {
            renderSolid(g2, model);
        }
    }

    private void renderWireFrame(Graphics2D g2,Matrix4 model){
        g2.translate(getWidth() / 2, getHeight() / 2);
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

    private void renderSolid(Graphics2D g2, Matrix4 model){
        //clear image
        if(img == null || img.getWidth() != getWidth() || img.getHeight() != getHeight()){
            img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        }else {
            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < getHeight(); j++) {
                    img.setRGB(i, j, Color.BLACK.getRGB());
                }
            }
        }

        double[][] zBuffer = new double[getHeight()][getWidth()];
        Arrays.stream(zBuffer).forEach(y -> Arrays.fill(y, Double.POSITIVE_INFINITY));

        for (Triangle tri : mesh.getTris()) {
            Vertex v1 = model.multiply(tri.getV1());
            Vertex v2 = model.multiply(tri.getV2());
            Vertex v3 = model.multiply(tri.getV3());

//            if (shouldCullTriangle(v1, v2, v3)) continue;

            rasterizeTriangle(g2, zBuffer, v1, v2, v3, tri.getColor());
        }
        g2.drawImage(img, 0, 0, null);
    }

    private boolean shouldCullTriangle(Vertex v1, Vertex v2, Vertex v3){
        Vertex edge1 = new Vertex(v2.getX()-v1.getX(), v2.getY()-v1.getY(), v2.getZ()-v1.getZ(), 1);
        Vertex edge2 = new Vertex(v3.getX()-v1.getX(), v3.getY()-v1.getY(), v3.getZ()-v1.getZ(), 1);

        double normalX = edge1.getY() * edge2.getZ() - edge1.getZ() * edge2.getY();
        double normalY = edge1.getZ() * edge2.getX() - edge1.getX() * edge2.getZ();
        double normalZ = edge1.getX() * edge2.getY() - edge1.getY() * edge2.getX();

        double viewX = v1.getX();
        double viewY = v1.getY();
        double viewZ = v1.getZ();

        double dotProd = normalX * viewX + normalY * viewY + normalZ * viewZ;

        return dotProd < 0;
    }

    private void rasterizeTriangle(Graphics2D g2, double[][] zBuffer, Vertex v1, Vertex v2, Vertex v3, Color color) {
        int width = getWidth();
        int height = getHeight();

        int minX = (int) Math.min(Math.min(v1.getX(), v2.getX()), v3.getX());
        minX = Math.max(-width/2, minX);
        int maxX = (int) Math.max(Math.max(v1.getX(), v2.getX()), v3.getX());
        maxX = Math.min(width/2, maxX);

        int minY = (int) Math.min(Math.min(v1.getY(), v2.getY()), v3.getY());
        minY = Math.max(-height/2, minY);
        int maxY = (int) Math.max(Math.max(v1.getY(), v2.getY()), v3.getY());
        maxY = Math.min(height/2, maxY);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                double[] baryCoords = barycentricCoordinates(x, y, v1, v2, v3);
                if (isNegative(baryCoords)) continue;
                int shiftedX = x+width/2, shiftedY = y+height/2;
                double depth = v1.getZ()*baryCoords[0] + v2.getZ()*baryCoords[1] + v3.getZ()*baryCoords[2];

                if (depth < zBuffer[shiftedY][shiftedX]){
                    zBuffer[shiftedY][shiftedX] = depth;
                    img.setRGB(shiftedX, shiftedY, color.getRGB());
                }
            }
        }
    }

    private double[] barycentricCoordinates(double px, double py, Vertex v1, Vertex v2, Vertex v3) {
        double  denom = (v2.getY() - v3.getY())*(v1.getX() - v3.getX()) + (v3.getX() - v2.getX())*(v1.getY() - v3.getY());

        if(Math.abs(denom) < 0.0001){
            return new double[]{-1,-1,-1};
        }

        double w1 = ((v2.getY() - v3.getY())*(px - v3.getX()) + (v3.getX() - v2.getX())*(py - v3.getY())) / denom;
        double w2 = ((v3.getY() - v1.getY())*(px - v3.getX()) + (v1.getX() - v3.getX())*(py - v3.getY())) / denom;
        double w3 = 1 - w1 - w2;

        return new double[]{w1,w2,w3};
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

    public boolean isShowWireFrame() {
        return showWireFrame;
    }

    public void setShowWireFrame(boolean showWireFrame) {
        this.showWireFrame = showWireFrame;
    }

    public static boolean isNegative(double[] arr) {
        for (double num : arr) {
            if (num < 0) return true;
        }
        return false;
    }
}
