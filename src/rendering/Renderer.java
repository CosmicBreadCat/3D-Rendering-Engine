package rendering;

import lighting.Light;
import scene.Camera;
import scene.Mesh;

import java.awt.*;

public abstract class Renderer extends PipelineUtils{
    public Renderer(int width, int height) {
        super(width, height);
    }

    public abstract void render(Graphics2D g2, Camera camera, Mesh mesh, Light light);
}
