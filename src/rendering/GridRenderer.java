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

    public void render(Graphics2D g2, Camera camera) {
        Vector4 look = camera.getLook();
        int snapX = (int) Math.floor(look.getX());
        int snapZ = (int) Math.floor(look.getZ());

        Matrix4 VP = camera.getProjectionMatrix((double) width / height).multiply(camera.getViewMatrix());

        g2.setColor(new Color(60, 60, 60));
        for (int i = -HALF_EXTENT; i <= HALF_EXTENT; i++) {
            float linePos = i * TILE_SIZE;

            // lines parallel to z-axis
            drawLine(g2, VP,
                    new Vector4(snapX + linePos, 0, snapZ - HALF_EXTENT, 1),
                    new Vector4(snapX + linePos, 0, snapZ + HALF_EXTENT, 1));

            // lines parallel to x-axis
            drawLine(g2, VP,
                    new Vector4(snapX - HALF_EXTENT, 0, snapZ + linePos, 1),
                    new Vector4(snapX + HALF_EXTENT, 0, snapZ + linePos, 1));
        }

        // x-axis (red)
        g2.setColor(Color.RED);
        drawLine(g2, VP,
                new Vector4(-1000, 0, 0, 1),
                new Vector4( 1000, 0, 0, 1));

        // z-axis (blue)
        g2.setColor(Color.BLUE);
        drawLine(g2, VP,
                new Vector4(0, 0, -1000, 1),
                new Vector4(0, 0,  1000, 1));
    }

    private void drawLine(Graphics2D g2, Matrix4 VP, Vector4 a, Vector4 b) {
        Vector4 ca = VP.multiply(a);
        Vector4 cb = VP.multiply(b);

        // signed distance to near plane: z + w >= 0
        double da = ca.getZ() + ca.getW();
        double db = cb.getZ() + cb.getW();

        boolean aIn = da >= 0;
        boolean bIn = db >= 0;

        if (!aIn && !bIn) return; // both behind near plane

        // clip the outside endpoint to the near plane
        if (!aIn) {
            double t = da / (da - db);
            ca = clipInterpolate(ca, cb, t);
        } else if (!bIn) {
            double t = da / (da - db);
            cb = clipInterpolate(ca, cb, t);
        }

        int[] sa = toScreen(ca);
        int[] sb = toScreen(cb);
        g2.drawLine(sa[0], sa[1], sb[0], sb[1]);
    }

    private int[] toScreen(Vector4 clip) {
        double ndcX =  clip.getX() / clip.getW();
        double ndcY =  clip.getY() / clip.getW();
        int sx = (int)(ndcX * width / 2.0 + width / 2.0);
        int sy = (int)(ndcY * height / 2.0 + height / 2.0);
        return new int[]{ sx, sy };
    }
}
