import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Canvas extends JPanel{
    private JPanel canvasPanel;
    private final Mesh mesh;
    private final Camera camera;
    private int rotX = 0, rotY = 0, rotZ = 0;
    private boolean showWireFrame = false;
    private BufferedImage img = null;
    private Vertex lightDir = new Vertex(0, 5, -1, 0).normalize();

    public Canvas(Mesh mesh, Camera camera) {
        setBackground(Color.GRAY);

        this.mesh = mesh;
        this.camera = camera;
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

        Matrix4 trans = Matrix4.createTranslationMatrix(0,0,-0.2);
        Matrix4 rotX = Matrix4.createXRotationMatrix(this.rotX);
        Matrix4 rotY = Matrix4.createYRotationMatrix(this.rotY);
        Matrix4 rotZ = Matrix4.createZRotationMatrix(this.rotZ);
        Matrix4 scale = Matrix4.createScalingMatrix(1,1,1);

        Matrix4 model = trans.multiply(rotX.multiply(rotY.multiply(rotZ.multiply(scale))));

        Matrix4 view = camera.getViewMatrix();
        Matrix4 projection = camera.getProjectionMatrix((double) getWidth() /getHeight());

        Matrix4 MV = view.multiply(model);

        if (showWireFrame){
            renderWireFrame(g2, MV, projection);
        }else {
            renderSolid(g2, MV, projection);
        }
    }

    private void renderWireFrame(Graphics2D g2,Matrix4 MV, Matrix4 P){
        Matrix4 MVP = P.multiply(MV);
        for (Triangle tri : mesh.getTris()) {
            Vertex v1 = MVP.multiply(tri.getV1());
            v1 = new Vertex(v1.getX()/v1.getW(), v1.getY()/v1.getW(), v1.getZ()/v1.getW(), 1);
            Vertex v2 = MVP.multiply(tri.getV2());
            v2 = new Vertex(v2.getX()/v2.getW(), v2.getY()/v2.getW(), v2.getZ()/v2.getW(), 1);
            Vertex v3 = MVP.multiply(tri.getV3());
            v3 = new Vertex(v3.getX()/v3.getW(), v3.getY()/v3.getW(), v3.getZ()/v3.getW(), 1);

            v1 = new Vertex(v1.getX() * getWidth()/2 + (double) getWidth() /2, v1.getY() * getHeight()/2 + (double) getHeight() /2, v1.getZ(), 1);
            v2 = new Vertex(v2.getX() * getWidth()/2 + (double) getWidth() /2, v2.getY() * getHeight()/2 + (double) getHeight() /2, v2.getZ(), 1);
            v3 = new Vertex(v3.getX() * getWidth()/2 + (double) getWidth() /2, v3.getY() * getHeight()/2 + (double) getHeight() /2, v3.getZ(), 1);

            Path2D path = new Path2D.Double();
            path.moveTo(v1.getX(), v1.getY());
            path.lineTo(v2.getX(), v2.getY());
            path.lineTo(v3.getX(), v3.getY());
            path.closePath();
            g2.draw(path);
        }
    }

    private void renderSolid(Graphics2D g2, Matrix4 MV, Matrix4 P){
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
            // View space vertices
            Vertex v1View = MV.multiply(tri.getV1());
            Vertex v2View = MV.multiply(tri.getV2());
            Vertex v3View = MV.multiply(tri.getV3());

            if (shouldCullTriangle(v1View, v2View, v3View)) continue;

            // Calculate lighting in view space
            Color shadedColor = flatShading(v1View, v2View, v3View, tri.getColor());

            // Transform to clip space
            Vertex v1 = P.multiply(v1View);
            v1 = new Vertex(v1.getX()/v1.getW(), v1.getY()/v1.getW(), v1.getZ()/v1.getW(), 1);
            Vertex v2 = P.multiply(v2View);
            v2 = new Vertex(v2.getX()/v2.getW(), v2.getY()/v2.getW(), v2.getZ()/v2.getW(), 1);
            Vertex v3 = P.multiply(v3View);
            v3 = new Vertex(v3.getX()/v3.getW(), v3.getY()/v3.getW(), v3.getZ()/v3.getW(), 1);

            // Convert to screen space
            v1 = new Vertex(v1.getX() * getWidth()/2 + (double) getWidth() /2, v1.getY() * getHeight()/2 + (double) getHeight() /2, v1.getZ(), 1);
            v2 = new Vertex(v2.getX() * getWidth()/2 + (double) getWidth() /2, v2.getY() * getHeight()/2 + (double) getHeight() /2, v2.getZ(), 1);
            v3 = new Vertex(v3.getX() * getWidth()/2 + (double) getWidth() /2, v3.getY() * getHeight()/2 + (double) getHeight() /2, v3.getZ(), 1);

            rasterizeTriangle(g2, zBuffer, v1, v2, v3, shadedColor);
        }
        g2.drawImage(img, 0, 0, null);
    }

    private boolean shouldCullTriangle(Vertex v1, Vertex v2, Vertex v3){
        Vertex edge1 = v2.subtract(v1);
        Vertex edge2 = v3.subtract(v1);
        Vertex normal = edge1.cross(edge2);

        double dotProd = normal.dot(v1);

        return dotProd < 0;
    }

    private void rasterizeTriangle(Graphics2D g2, double[][] zBuffer, Vertex v1, Vertex v2, Vertex v3, Color color) {
        int width = getWidth();
        int height = getHeight();

        int minX = (int) Math.min(Math.min(v1.getX(), v2.getX()), v3.getX());
        minX = Math.max(0, minX);
        int maxX = (int) Math.max(Math.max(v1.getX(), v2.getX()), v3.getX());
        maxX = Math.min(width - 1, maxX);

        int minY = (int) Math.min(Math.min(v1.getY(), v2.getY()), v3.getY());
        minY = Math.max(0, minY);
        int maxY = (int) Math.max(Math.max(v1.getY(), v2.getY()), v3.getY());
        maxY = Math.min(height - 1, maxY);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                double[] baryCoords = barycentricCoordinates(x, y, v1, v2, v3);
                if (isNegative(baryCoords)) continue;

                double depth = v1.getZ()*baryCoords[0] + v2.getZ()*baryCoords[1] + v3.getZ()*baryCoords[2];

                if (depth < zBuffer[y][x]){
                    zBuffer[y][x] = depth;
                    img.setRGB(x, y, color.getRGB());
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

    private Color flatShading(Vertex v1, Vertex v2, Vertex v3, Color color){
        Vertex edge1 = v2.subtract(v1);
        Vertex edge2 = v3.subtract(v1);
        Vertex normal = edge1.cross(edge2).normalize();

        double diffuse = Math.max(0, normal.dot(lightDir));
        double ambient = 0.2;
        double intensity = ambient + (1.0 - ambient) * diffuse;

        int r = (int)(color.getRed() * intensity);
        int g = (int)(color.getGreen() * intensity);
        int b = (int)(color.getBlue() * intensity);

        return new Color(r, g, b);
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
