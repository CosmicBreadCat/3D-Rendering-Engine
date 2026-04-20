package ui;

import lighting.Light;
import math.Vector4;
import rendering.GridRenderer;
import rendering.SolidRenderer;
import rendering.WireframeRenderer;
import scene.Camera;
import scene.Mesh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Canvas extends JPanel{
    private JPanel canvasPanel;

    private final Mesh mesh;
    private final Camera camera;
    private final Light light;
    private final SolidRenderer solidRenderer = new SolidRenderer(100,100);
    private final WireframeRenderer wireframeRenderer = new WireframeRenderer(100,100);
    private final GridRenderer gridRenderer = new GridRenderer(100,100);

    private boolean showWireFrame = false, isMiddleMouseDown = false;
    private int lastMouseX, lastMouseY, orbitOrientation= 1, zoomSpeed = 1;

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
                gridRenderer.resize(newSize.width, newSize.height);
            }
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (SwingUtilities.isMiddleMouseButton(mouseEvent)) {
                    lastMouseX = mouseEvent.getX();
                    lastMouseY = mouseEvent.getY();
                    isMiddleMouseDown = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                if (SwingUtilities.isMiddleMouseButton(mouseEvent)) {
                    isMiddleMouseDown = false;
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                if (isMiddleMouseDown){
                    int dx = mouseEvent.getX() - lastMouseX;
                    int dy = mouseEvent.getY() - lastMouseY;
                    lastMouseX = mouseEvent.getX();
                    lastMouseY = mouseEvent.getY();

                    if(mouseEvent.isShiftDown()){
                        handlePan(dx, dy);
                    }else {
                        handleOrbit(dx, dy);
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                handleZoom(mouseWheelEvent.getPreciseWheelRotation());
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
            gridRenderer.resize(getWidth(), getHeight());
        }

        g2.setColor(Color.black);
        g2.fillRect(0,0,getWidth(), getHeight());

        renderMesh(g2);
        gridRenderer.render(g2, camera);
    }

    private void handleOrbit(int dx, int dy){
        Vector4 offset = camera.getLocation().subtract(camera.getLook());
        double r = Math.sqrt(offset.getX()*offset.getX() + offset.getY()*offset.getY() + offset.getZ()*offset.getZ());
        double theta = Math.atan2(offset.getX(), offset.getZ());
        double phi = Math.asin(offset.getY() / r);

        double sensitivity = 0.005;
        theta = theta + orbitOrientation * dx * sensitivity;
        phi = Math.clamp(phi + orbitOrientation * dy * sensitivity, -Math.PI/2 + 0.01, Math.PI/2 - 0.01);

        double newX = r * Math.sin(theta) * Math.cos(phi);
        double newY = r * Math.sin(phi);
        double newZ = r * Math.cos(theta) * Math.cos(phi);

        camera.setLocation(new Vector4(
                camera.getLook().getX() + newX,
                camera.getLook().getY() + newY,
                camera.getLook().getZ() + newZ, 1
        ));

        System.out.printf("Look: %.2f %.2f %.2f | Loc: %.2f %.2f %.2f%n",
                camera.getLook().getX(), camera.getLook().getY(), camera.getLook().getZ(),
                camera.getLocation().getX(), camera.getLocation().getY(), camera.getLocation().getZ());
        repaint();
    }

    private void handlePan(int dx, int dy){
        Vector4 offset = camera.getLocation().subtract(camera.getLook());
        double r = Math.sqrt(offset.getX()*offset.getX() + offset.getY()*offset.getY() + offset.getZ()*offset.getZ());

        Vector4 forward = camera.getLocation().subtract(camera.getLook()).normalize();
        Vector4 right = camera.getWorldUp().cross(forward).normalize();
        Vector4 up = forward.cross(right);

        double panSpeed = r * 0.001;
        Vector4 delta = right.scale((double) -dx * panSpeed).add(up.scale((double) -dy * panSpeed));

        camera.setLocation(camera.getLocation().add(delta));
        camera.setLook(camera.getLook().add(delta));
        repaint();
    }

    private void handleZoom(double delta){
        Vector4 offset = camera.getLocation().subtract(camera.getLook());
        double r = Math.sqrt(offset.getX()*offset.getX() + offset.getY()*offset.getY() + offset.getZ()*offset.getZ());
        double newR = Math.max(0.5, r + delta * 0.5 * zoomSpeed);
        Vector4 newOffset = offset.normalize().scale(newR);
        camera.setLocation(new Vector4(
                camera.getLook().getX() + newOffset.getX(),
                camera.getLook().getY() + newOffset.getY(),
                camera.getLook().getZ() + newOffset.getZ(), 1
        ));
        repaint();
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

    public int getZoomSpeed() {
        return zoomSpeed;
    }

    public void setZoomSpeed(int zoomSpeed) {
        this.zoomSpeed = zoomSpeed;
    }

    public boolean isShowWireFrame() {
        return showWireFrame;
    }

    public void invertOrbit(){
        orbitOrientation *= -1;
    }

    public void setShowWireFrame(boolean showWireFrame) {
        this.showWireFrame = showWireFrame;
    }
}
