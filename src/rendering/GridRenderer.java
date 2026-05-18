package rendering;

import math.Matrix4;
import math.Vector4;
import scene.Camera;

import java.awt.*;

public class GridRenderer extends PipelineUtils {
    private static final int HALF_EXTENT = 50;
    private static final float TILE_SIZE = 1.0f;

    public GridRenderer(int width, int height) {
        super(width, height);
    }

    public void render(FrameBuffer frameBuffer, Camera camera) {
        Vector4 look = camera.getLook();
        int snapX = (int) Math.floor(look.getX());
        int snapZ = (int) Math.floor(look.getZ());

        Matrix4 VP = camera.getProjectionMatrix((double) width / height).multiply(camera.getViewMatrix());

        Color gridColor = new Color(60, 60, 60);
        for (int i = -HALF_EXTENT; i <= HALF_EXTENT; i++) {
            float linePos = i * TILE_SIZE;

            drawLine(frameBuffer, VP,
                    new Vector4(snapX + linePos, 0, snapZ - HALF_EXTENT, 1),
                    new Vector4(snapX + linePos, 0, snapZ + HALF_EXTENT, 1),
                    gridColor, 0);

            drawLine(frameBuffer, VP,
                    new Vector4(snapX - HALF_EXTENT, 0, snapZ + linePos, 1),
                    new Vector4(snapX + HALF_EXTENT, 0, snapZ + linePos, 1),
                    gridColor, 0);
        }

        drawLine(frameBuffer, VP,
                new Vector4(-1000, 0, 0, 1),
                new Vector4( 1000, 0, 0, 1),
                Color.RED, -1);

        drawLine(frameBuffer, VP,
                new Vector4(0, 0, -1000, 1),
                new Vector4(0, 0,  1000, 1),
                Color.BLUE, -1);
    }

    private void drawLine(FrameBuffer frameBuffer, Matrix4 VP, Vector4 a, Vector4 b, Color color, double depthBias) {
        Vector4 ca = VP.multiply(a);
        Vector4 cb = VP.multiply(b);

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

        // Perspective divide
        double ax = ca.getX() / ca.getW(), ay = ca.getY() / ca.getW(), az = ca.getZ() / ca.getW();
        double bx = cb.getX() / cb.getW(), by = cb.getY() / cb.getW(), bz = cb.getZ() / cb.getW();

        // NDC to screen
        double sx0 = ax * width  / 2.0 + width  / 2.0;
        double sy0 = ay * height / 2.0 + height / 2.0;
        double sx1 = bx * width  / 2.0 + width  / 2.0;
        double sy1 = by * height / 2.0 + height / 2.0;

        int steps = (int) Math.max(Math.abs(sx1 - sx0), Math.abs(sy1 - sy0)) + 1;
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            int px = (int)(sx0 + t * (sx1 - sx0));
            int py = (int)(sy0 + t * (sy1 - sy0));
            double pz = az + t * (bz - az);

            if (px >= 0 && px < width && py >= 0 && py < height) {
                frameBuffer.setPixel(px, py, pz, color, depthBias);
            }
        }
    }
}