package scene;

import math.Vector4;

import java.awt.*;

public class Triangle {
    private Vector4 v1, v2, v3;
    private Color color;

    public Triangle(Vector4 v1, Vector4 v2, Vector4 v3, Color color) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.color = color;
    }

    public Vector4 getV1() {
        return v1;
    }

    public void setV1(Vector4 v1) {
        this.v1 = v1;
    }

    public Vector4 getV2() {
        return v2;
    }

    public void setV2(Vector4 v2) {
        this.v2 = v2;
    }

    public Vector4 getV3() {
        return v3;
    }

    public void setV3(Vector4 v3) {
        this.v3 = v3;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
