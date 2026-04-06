package rendering;

import lighting.Light;
import math.Matrix4;
import math.Vector4;
import scene.Camera;
import scene.Mesh;
import scene.Triangle;

import java.awt.*;
import java.awt.geom.Path2D;

public class WireframeRenderer extends Renderer{

    public WireframeRenderer(int width, int height) {
        super(width, height);
    }

    @Override
    public void render(Graphics2D g2, Camera camera, Mesh mesh, Light light) {
        Matrix4 model = mesh.getModelMatrix();
        Matrix4 view = camera.getViewMatrix();
        Matrix4 projection = camera.getProjectionMatrix((double) getWidth() /getHeight());

        Matrix4 MVP = projection.multiply(view.multiply(model));

        for (Triangle tri : mesh.getTris()) {
            // Transform to clip space
            Vector4 v1 = MVP.multiply(tri.getV1());
            v1 = new Vector4(v1.getX()/v1.getW(), v1.getY()/v1.getW(), v1.getZ()/v1.getW(), 1);
            Vector4 v2 = MVP.multiply(tri.getV2());
            v2 = new Vector4(v2.getX()/v2.getW(), v2.getY()/v2.getW(), v2.getZ()/v2.getW(), 1);
            Vector4 v3 = MVP.multiply(tri.getV3());
            v3 = new Vector4(v3.getX()/v3.getW(), v3.getY()/v3.getW(), v3.getZ()/v3.getW(), 1);

            // Convert to screen space
            v1 = new Vector4(v1.getX() * getWidth()/2 + (double) getWidth() /2, v1.getY() * getHeight()/2 + (double) getHeight() /2, v1.getZ(), 1);
            v2 = new Vector4(v2.getX() * getWidth()/2 + (double) getWidth() /2, v2.getY() * getHeight()/2 + (double) getHeight() /2, v2.getZ(), 1);
            v3 = new Vector4(v3.getX() * getWidth()/2 + (double) getWidth() /2, v3.getY() * getHeight()/2 + (double) getHeight() /2, v3.getZ(), 1);

            // Draw lines connecting vertices
            Path2D path = new Path2D.Double();
            path.moveTo(v1.getX(), v1.getY());
            path.lineTo(v2.getX(), v2.getY());
            path.lineTo(v3.getX(), v3.getY());
            path.closePath();
            g2.draw(path);
        }
    }
}
