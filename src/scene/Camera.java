package scene;

import math.Matrix4;
import math.Vector4;

public class Camera {
    private Vector4 location, look, worldUp;
    private double fov, near, far;

    public Camera(Vector4 location, Vector4 look, Vector4 worldUp, double fov, double near, double far) {
        this.location = location;
        this.look = look;
        this.worldUp = worldUp;
        this.fov = fov;
        this.near = near;
        this.far = far;
    }

    public Matrix4 getViewMatrix(){
        Matrix4 view = new Matrix4();

        Vector4 forward = location.subtract(look).normalize();
        Vector4 right = worldUp.cross(forward).normalize();
        Vector4 up = forward.cross(right);

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

    public Vector4 getLocation() {
        return location;
    }

    public void setLocation(Vector4 location) {
        this.location = location;
    }

    public Vector4 getLook() {
        return look;
    }

    public Vector4 getWorldUp() {
        return worldUp;
    }

    public void setWorldUp(Vector4 worldUp) {
        this.worldUp = worldUp;
    }

    public void setLook(Vector4 look) {
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
