package lighting;

import math.Matrix4;
import math.Vector4;

public class DirectionalLight extends Light{
    private Vector4 look, worldUp;
    private double orthoSize;

    public DirectionalLight(Vector4 location, Vector4 look, Vector4 worldUp, double near, double far, double orthoSize) {
        super(location, near, far);
        this.look = look;
        this.worldUp = worldUp;
        this.orthoSize = orthoSize;
    }

    @Override
    public Matrix4 getViewMatrix() {
        Matrix4 view = new Matrix4();

        Vector4 forward = location.subtract(look);
        forward = forward.normalize();
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

    @Override
    public Matrix4 getProjectionMatrix() {
        Matrix4 projection = new Matrix4();

        projection.setValue(0, 1/orthoSize);
        projection.setValue(5, 1/orthoSize);
        projection.setValue(10, -2.0 / (far - near));
        projection.setValue(11, -(far + near) / (far - near));

        return projection;
    }

    @Override
    public Vector4 getDirection() {
        return location.subtract(look).normalize();
    }

    public Vector4 getLook() {
        return look;
    }

    public void setLook(Vector4 look) {
        this.look = look;
    }

    public Vector4 getWorldUp() {
        return worldUp;
    }

    public void setWorldUp(Vector4 worldUp) {
        this.worldUp = worldUp;
    }

    public double getOrthoSize() {
        return orthoSize;
    }

    public void setOrthoSize(double orthoSize) {
        this.orthoSize = orthoSize;
    }
}
