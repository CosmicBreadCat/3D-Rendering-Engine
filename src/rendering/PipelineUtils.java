package rendering;

import math.Vector4;
import scene.Triangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PipelineUtils {
    protected int width, height;

    public PipelineUtils(int width, int height) {
        this.width = width;
        this.height = height;
    }

    protected boolean shouldCullTriangle(Vector4 v1, Vector4 v2, Vector4 v3){
        Vector4 edge1 = v2.subtract(v1);
        Vector4 edge2 = v3.subtract(v1);
        Vector4 normal = edge1.cross(edge2);

        double dotProd = normal.dot(v1);

        return dotProd > 0;
    }

    protected List<Vector4> clipTriangle(Vector4 v1, Vector4 v2, Vector4 v3) {
        List<Vector4> polygon = new ArrayList<>(List.of(v1, v2, v3));

        for (FrustumPlanes plane : FrustumPlanes.values()) {
            polygon = clipAgainstPlane(polygon, plane);
            if (polygon.isEmpty()) return polygon;
        }

        return polygon;
    }

    private List<Vector4> clipAgainstPlane(List<Vector4> vertices, FrustumPlanes plane) {
        List<Vector4> output = new ArrayList<>();
        int n = vertices.size();

        for (int i = 0; i < n; i++) {
            Vector4 curr = vertices.get(i);
            Vector4 next = vertices.get((i + 1) % n);

            double dCurr = signedDist(curr, plane);
            double dNext = signedDist(next, plane);

            if (dCurr >= 0) output.add(curr);

            if ((dCurr >= 0) != (dNext >= 0)) {
                double t = dCurr / (dCurr - dNext);
                output.add(clipInterpolate(curr, next, t));
            }
        }

        return output;
    }

    private void clipAgainstPlaneWithAttribs(
            List<Vector4> camVerts, List<Vector4> lightVerts, FrustumPlanes plane,
            List<Vector4> camOut, List<Vector4> lightOut) {

        int n = camVerts.size();
        for (int i = 0; i < n; i++) {
            Vector4 currCam   = camVerts.get(i);
            Vector4 nextCam   = camVerts.get((i + 1) % n);
            Vector4 currLight = lightVerts.get(i);
            Vector4 nextLight = lightVerts.get((i + 1) % n);

            double dCurr = signedDist(currCam, plane);
            double dNext = signedDist(nextCam, plane);

            if (dCurr >= 0) {
                camOut.add(currCam);
                lightOut.add(currLight);
            }

            if ((dCurr >= 0) != (dNext >= 0)) {
                double t = dCurr / (dCurr - dNext);
                camOut.add(clipInterpolate(currCam, nextCam, t));
                lightOut.add(clipInterpolate(currLight, nextLight, t));
            }
        }
    }

    private double signedDist(Vector4 v, FrustumPlanes plane) {
        return switch (plane) {
            case NEAR   -> v.getZ() + v.getW();
            case FAR    -> v.getW() - v.getZ();
            case LEFT   -> v.getX() + v.getW();
            case RIGHT  -> v.getW() - v.getX();
            case BOTTOM -> v.getY() + v.getW();
            case TOP    -> v.getW() - v.getY();
        };
    }

    protected Vector4 clipInterpolate(Vector4 a, Vector4 b, double t) {
        return new Vector4(
                a.getX() + t * (b.getX() - a.getX()),
                a.getY() + t * (b.getY() - a.getY()),
                a.getZ() + t * (b.getZ() - a.getZ()),
                a.getW() + t * (b.getW() - a.getW())
        );
    }

    protected List<Triangle> triangulate(List<Vector4> polygon, Color color) {
        List<Triangle> subtriangles = new ArrayList<>();
        for (int i = 1; i < polygon.size() - 1; i++) {
            subtriangles.add(new Triangle(polygon.get(0), polygon.get(i), polygon.get(i + 1), color));
        }
        return subtriangles;
    }

    protected boolean isNegative(double[] arr) {
        for (double num : arr) {
            if (num < 0) return true;
        }
        return false;
    }

    protected double[] getBarycentricCoordinates(double px, double py, Vector4 v1, Vector4 v2, Vector4 v3) {
        double denominator = (v2.getY() - v3.getY())*(v1.getX() - v3.getX()) + (v3.getX() - v2.getX())*(v1.getY() - v3.getY());

        if(Math.abs(denominator) < 0.0001){
            return new double[]{-1,-1,-1};
        }

        double w1 = ((v2.getY() - v3.getY())*(px - v3.getX()) + (v3.getX() - v2.getX())*(py - v3.getY())) / denominator;
        double w2 = ((v3.getY() - v1.getY())*(px - v3.getX()) + (v1.getX() - v3.getX())*(py - v3.getY())) / denominator;
        double w3 = 1 - w1 - w2;

        return new double[]{w1,w2,w3};
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
