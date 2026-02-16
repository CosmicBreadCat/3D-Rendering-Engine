import java.util.ArrayList;

public class Mesh {
    private double rotX = 0, rotY = 0, rotZ = 0;
    private double transX = 0, transY = 0, transZ = 0;
    private double scaleX = 1, scaleY = 1, scaleZ = 1;

    ArrayList<Triangle> tris = new ArrayList<>();

    public Mesh(ArrayList<Triangle> tris) {
        this.tris = tris;
    }

    public Matrix4 getModelMatrix(){
        Matrix4 trans = Matrix4.createTranslationMatrix(transX,transY,transZ);
        Matrix4 rotX = Matrix4.createXRotationMatrix(this.rotX);
        Matrix4 rotY = Matrix4.createYRotationMatrix(this.rotY);
        Matrix4 rotZ = Matrix4.createZRotationMatrix(this.rotZ);
        Matrix4 scale = Matrix4.createScalingMatrix(scaleX,scaleY,scaleZ);

        return trans.multiply(rotX.multiply(rotY.multiply(rotZ.multiply(scale))));
    }

    public ArrayList<Triangle> getTris() {
        return tris;
    }

    public void addTri(Triangle tri){
        if (!tris.contains(tri)){
            tris.add(tri);
        }
    }

    public void removeTri(Triangle tri){
        tris.remove(tri);
    }

    public void setTris(ArrayList<Triangle> tris) {
        this.tris = tris;
    }

    public double getRotX() {
        return rotX;
    }

    public void setRotX(double rotX) {
        this.rotX = rotX;
    }

    public double getRotY() {
        return rotY;
    }

    public void setRotY(double rotY) {
        this.rotY = rotY;
    }

    public double getRotZ() {
        return rotZ;
    }

    public void setRotZ(double rotZ) {
        this.rotZ = rotZ;
    }

    public double getTransX() {
        return transX;
    }

    public void setTransX(double transX) {
        this.transX = transX;
    }

    public double getTransY() {
        return transY;
    }

    public void setTransY(double transY) {
        this.transY = transY;
    }

    public double getTransZ() {
        return transZ;
    }

    public void setTransZ(double transZ) {
        this.transZ = transZ;
    }

    public double getScaleX() {
        return scaleX;
    }

    public void setScaleX(double scaleX) {
        if (scaleX == 0){
            throw new RuntimeException("ScaleX can't be 0");
        }else{
            this.scaleX = scaleX;
        }
    }

    public double getScaleY() {
        return scaleY;
    }

    public void setScaleY(double scaleY) {
        if (scaleY == 0){
            throw new RuntimeException("ScaleY can't be 0");
        }else{
            this.scaleY = scaleY;
        }
    }

    public double getScaleZ() {
        return scaleZ;
    }

    public void setScaleZ(double scaleZ) {
        if (scaleZ == 0){
            throw new RuntimeException("ScaleZ can't be 0");
        }else{
            this.scaleZ = scaleZ;
        }
    }
}
