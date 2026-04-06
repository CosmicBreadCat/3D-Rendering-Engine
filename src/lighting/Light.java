package lighting;

import math.Matrix4;
import math.Vector4;

public abstract class Light {
    protected Vector4 location;
    protected double near, far;

    public Light(Vector4 location, double near, double far) {
        this.location = location;
        this.near = near;
        this.far = far;
    }

    public abstract Matrix4 getViewMatrix();
    public abstract  Matrix4 getProjectionMatrix();
    public abstract Vector4 getDirection();

    public Vector4 getLocation() {
        return location;
    }

    public void setLocation(Vector4 location) {
        this.location = location;
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
