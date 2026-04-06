package ui;

import lighting.Light;
import rendering.SolidRenderer;
import rendering.WireframeRenderer;
import scene.Camera;
import scene.Mesh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Canvas extends JPanel{
    private JPanel canvasPanel;
    private final Mesh mesh;
    private final Camera camera;
    private final Light light;
    private boolean showWireFrame = false;
    private final SolidRenderer solidRenderer = new SolidRenderer(100,100);
    private final WireframeRenderer wireframeRenderer = new WireframeRenderer(100,100);

    public Canvas(Mesh mesh, Camera camera, Light light) {
        setBackground(Color.GRAY);

        this.mesh = mesh;
        this.camera = camera;
        this.light = light;

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension newSize = e.getComponent().getSize();

                solidRenderer.resize(newSize.width, newSize.height);
                wireframeRenderer.resize(newSize.width, newSize.height);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (solidRenderer.getWidth() != getWidth() || solidRenderer.getHeight() != getHeight()) {
            solidRenderer.resize(getWidth(), getHeight());
            wireframeRenderer.resize(getWidth(), getHeight());
        }

        g2.setColor(Color.black);
        g2.fillRect(0,0,getWidth(), getHeight());

        renderMesh(g2);
    }

    public void renderMesh(Graphics2D g2) {
        g2.setColor(Color.WHITE);

        if (showWireFrame){
            wireframeRenderer.render(g2, camera, mesh, light);
        }else {
            solidRenderer.render(g2, camera, mesh, light);
        }
    }

    public double getRotX() {
        return mesh.getRotX();
    }

    public void setRotX(int rotX) {
        mesh.setRotX(rotX);
    }

    public double getRotY() {
        return mesh.getRotY();
    }

    public void setRotY(int rotY) {
        mesh.setRotY(rotY);
    }

    public double getRotZ() {
        return mesh.getRotZ();
    }

    public void setRotZ(int rotZ) {
        mesh.setRotZ(rotZ);
    }

    public boolean isShowWireFrame() {
        return showWireFrame;
    }

    public void setShowWireFrame(boolean showWireFrame) {
        this.showWireFrame = showWireFrame;
    }
}
