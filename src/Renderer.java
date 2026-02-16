import java.awt.*;

public abstract class Renderer {
    private int rotX = 0, rotY = 0, rotZ = 0;
    protected int width;
    protected int height;

    public Renderer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public abstract void render(Graphics2D g2, Camera camera, Mesh mesh);

    protected boolean shouldCullTriangle(Vector4 v1, Vector4 v2, Vector4 v3){
        Vector4 edge1 = v2.subtract(v1);
        Vector4 edge2 = v3.subtract(v1);
        Vector4 normal = edge1.cross(edge2);

        double dotProd = normal.dot(v1);

        return dotProd < 0;
    }

    protected boolean isNegative(double[] arr) {
        for (double num : arr) {
            if (num < 0) return true;
        }
        return false;
    }

    protected double[] barycentricCoordinates(double px, double py, Vector4 v1, Vector4 v2, Vector4 v3) {
        double denominator = (v2.getY() - v3.getY())*(v1.getX() - v3.getX()) + (v3.getX() - v2.getX())*(v1.getY() - v3.getY());

        if(Math.abs(denominator) < 0.0001){
            return new double[]{-1,-1,-1};
        }

        double w1 = ((v2.getY() - v3.getY())*(px - v3.getX()) + (v3.getX() - v2.getX())*(py - v3.getY())) / denominator;
        double w2 = ((v3.getY() - v1.getY())*(px - v3.getX()) + (v1.getX() - v3.getX())*(py - v3.getY())) / denominator;
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void resize(int width, int height){
        this.width = width;
        this.height = height;
    }
}
