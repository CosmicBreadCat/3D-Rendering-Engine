package rendering;

import math.Matrix4;
import math.Vector4;
import scene.Camera;
import scene.Mesh;
import scene.Triangle;

import java.awt.*;

public class WireframeRenderer extends PipelineUtils {

    public WireframeRenderer(int width, int height) {
        super(width, height);
    }

    public void render(Camera camera, Mesh mesh, FrameBuffer frameBuffer) {
        Matrix4 model = mesh.getModelMatrix();
        Matrix4 view = camera.getViewMatrix();
        Matrix4 projection = camera.getProjectionMatrix((double) getWidth() /getHeight());

        Matrix4 MVP = projection.multiply(view.multiply(model));

        for (Triangle tri : mesh.getTris()) {
            Vector4 v1 = MVP.multiply(tri.getV1());
            Vector4 v2 = MVP.multiply(tri.getV2());
            Vector4 v3 = MVP.multiply(tri.getV3());

            drawLine(frameBuffer, v1, v2, Color.WHITE);
            drawLine(frameBuffer, v2, v3, Color.WHITE);
            drawLine(frameBuffer, v3, v1, Color.WHITE);
        }
    }

    private void drawLine(FrameBuffer frameBuffer, Vector4 ca, Vector4 cb, Color color) {
        double da = ca.getZ() + ca.getW();
        double db = cb.getZ() + cb.getW();

        boolean aIn = da >= 0;
        boolean bIn = db >= 0;

        if (!aIn && !bIn) return;

        if (!aIn) {
            double t = da / (da - db);
            ca = clipInterpolate(ca, cb, t);
        } else if (!bIn) {
            double t = da / (da - db);
            cb = clipInterpolate(ca, cb, t);
        }

        double ax = ca.getX() / ca.getW(), ay = ca.getY() / ca.getW(), az = ca.getZ() / ca.getW();
        double bx = cb.getX() / cb.getW(), by = cb.getY() / cb.getW(), bz = cb.getZ() / cb.getW();

        double sx0 = ax * width / 2.0 + width / 2.0;
        double sy0 = ay * height / 2.0 + height / 2.0;
        double sx1 = bx * width / 2.0 + width / 2.0;
        double sy1 = by * height / 2.0 + height / 2.0;

        int steps = (int) Math.max(Math.abs(sx1 - sx0), Math.abs(sy1 - sy0)) + 1;
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            int px = (int)(sx0 + t * (sx1 - sx0));
            int py = (int)(sy0 + t * (sy1 - sy0));
            double pz = az + t * (bz - az);

            if (px >= 0 && px < width && py >= 0 && py < height) {
                frameBuffer.setPixel(px, py, pz, color);
            }
        }
    }
}