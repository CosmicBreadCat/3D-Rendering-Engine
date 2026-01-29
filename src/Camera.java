import java.util.Arrays;

public class Camera {
    Vertex location, look, worldUp;
    double fov, near, far;

    public Camera(Vertex location, Vertex look, Vertex up, double fov, double near, double far) {
        this.location = location;
        this.look = look;
        this.worldUp = up;
        this.fov = fov;
        this.near = near;
        this.far = far;
    }

    public Matrix4 getViewMatrix(){
        Matrix4 view = new Matrix4();

        Vertex forward = location.subtract(look);
        forward = forward.normalize();
        Vertex right = worldUp.cross(forward).normalize();
        Vertex up = forward.cross(right);

        view.setValue(0, right.getX());
        view.setValue(1, right.getY());
        view.setValue(2, right.getZ());
        view.setValue(3, -right.dot(location));
        view.setValue(4, up.getX());
        view.setValue(5, up.getY());
        view.setValue(6, up.getZ());
        view.setValue(7, -up.dot(location));
        view.setValue(8, -forward.getX());
        view.setValue(9, -forward.getY());
        view.setValue(10, -forward.getZ());
        view.setValue(11, -forward.dot(location));

        return view;
    }

    public Matrix4 getProjectionMatrix(double aspect){
        Matrix4 projection = new Matrix4();

        double f = 1/Math.tan(Math.toRadians(fov)/2);
        projection.setValue(0, f/aspect);
        projection.setValue(5, f);
        projection.setValue(10, (far + near)/(near - far));
        projection.setValue(11, (2 * far * near)/(near - far));
        projection.setValue(14, -1);
        projection.setValue(15, 0);

        return projection;
    }

    public Vertex getLocation() {
        return location;
    }

    public void setLocation(Vertex location) {
        this.location = location;
    }

    public Vertex getLook() {
        return look;
    }

    public Vertex getWorldUp() {
        return worldUp;
    }

    public void setWorldUp(Vertex worldUp) {
        this.worldUp = worldUp;
    }

    public void setLook(Vertex look) {
        this.look = look;
    }

    public double getFov() {
        return fov;
    }

    public void setFov(double fov) {
        this.fov = fov;
    }

    public double getNear() {
        return near;
    }

    public void setNear(double near) {
        this.near = near;
    }

    public double getFar() {
        return far;
    }

    public void setFar(double far) {
        this.far = far;
    }
}
