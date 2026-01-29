public class Vertex {
    private double x, y, z, w;

    public Vertex(double x, double y, double z, double w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vertex cross(Vertex v){
        double cX = y * v.getZ() - z * v.getY();
        double cY = z * v.getX() - x * v.getZ();
        double cZ = x * v.getY() - y * v.getX();

        return new Vertex(cX, cY, cZ, 1);
    }

    public Vertex normalize(){
        double mag = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        return new Vertex(x/mag, y/mag, z/mag, 1);
    }

    public double dot(Vertex v){
        return x*v.getX() + y*v.getY() + z*v.getZ();
    }

    public Vertex subtract(Vertex v){
        return new Vertex(x - v.getX(), y - v.getY(), z - v.getZ(), 1);
    }

    public Vertex add(Vertex v){
        return new Vertex(x + v.getX(), y + v.getY(), z + v.getZ(), 1);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }
}
