package rendering;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class FrameBuffer {
    private int width, height;
    private double[][] zBuffer;
    private BufferedImage img;

    public FrameBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        zBuffer = new double[height][width];
        Arrays.stream(zBuffer).forEach(y -> Arrays.fill(y, Double.POSITIVE_INFINITY));
    }

    public void setPixel(int x, int y, double depth, Color color) {
        if (depth < zBuffer[y][x]) {
            zBuffer[y][x] = depth;
            img.setRGB(x, y, color.getRGB());
        }
    }

    public void setPixel(int x, int y, double depth, Color color, double depthBias) {
        if (depth + depthBias < zBuffer[y][x]) {
            zBuffer[y][x] = depth;
            img.setRGB(x, y, color.getRGB());
        }
    }

    public void clearBuffer(){
        if(img == null || img.getWidth() != width || img.getHeight() != height){
            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            zBuffer = new double[height][width];
        }else {
            Graphics2D g2d = img.createGraphics();
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
            g2d.setComposite(AlphaComposite.SrcOver); // Restore default composite
            g2d.dispose();
        }

        for (double[] row : zBuffer) Arrays.fill(row, Double.POSITIVE_INFINITY);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double[][] getZBuffer() {
        return zBuffer;
    }

    public BufferedImage getImg() {
        return img;
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        clearBuffer();
    }
}
