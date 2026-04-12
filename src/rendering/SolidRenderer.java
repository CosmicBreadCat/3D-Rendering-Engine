package rendering;

import lighting.Light;
import math.Matrix4;
import math.Vector4;
import scene.Camera;
import scene.Mesh;
import scene.Triangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class SolidRenderer extends Renderer{
    private BufferedImage img = null;
    private final ShadowMapper shadowMapper;
    private boolean shadowsEnabled = true;
    private final double SHADOW_BIAS = 0.005;
    private final int SHADOW_KERNEL_SIZE = 2;

    public SolidRenderer(int width, int height) {
        super(width, height);
        shadowMapper = new  ShadowMapper(width, height);
    }

    @Override
    public void render(Graphics2D g2, Camera camera, Mesh mesh, Light light) {
        clearImg();
        Matrix4 model = mesh.getModelMatrix();
        Matrix4 view = camera.getViewMatrix();
        Matrix4 projection = camera.getProjectionMatrix((double) getWidth() /getHeight());

        Matrix4 MV = view.multiply(model);

        Matrix4 lightMVP = light.getProjectionMatrix().multiply(light.getViewMatrix().multiply(model));
        double[][] shadowMap = shadowMapper.map(mesh, lightMVP);

        double[][] zBuffer = new double[getHeight()][getWidth()];
        Arrays.stream(zBuffer).forEach(y -> Arrays.fill(y, Double.POSITIVE_INFINITY));

        for (Triangle tri : mesh.getTris()) {
            // View space vertices
            Vector4 v1View = MV.multiply(tri.getV1());
            Vector4 v2View = MV.multiply(tri.getV2());
            Vector4 v3View = MV.multiply(tri.getV3());

            if (shouldCullTriangle(v1View, v2View, v3View)) continue;

            // Calculate simple lighting in view space
            Color shadedColor = flatShading(v1View, v2View, v3View, tri.getColor(), light.getDirection());

            // Transform to clip space
            Vector4 v1 = projection.multiply(v1View);
            v1 = new Vector4(v1.getX()/v1.getW(), v1.getY()/v1.getW(), v1.getZ()/v1.getW(), 1);
            Vector4 v2 = projection.multiply(v2View);
            v2 = new Vector4(v2.getX()/v2.getW(), v2.getY()/v2.getW(), v2.getZ()/v2.getW(), 1);
            Vector4 v3 = projection.multiply(v3View);
            v3 = new Vector4(v3.getX()/v3.getW(), v3.getY()/v3.getW(), v3.getZ()/v3.getW(), 1);

            // Convert to screen space
            v1 = new Vector4(v1.getX() * getWidth()/2 + (double) getWidth()/2, v1.getY() * getHeight()/2 + (double) getHeight()/2, v1.getZ(), 1);
            v2 = new Vector4(v2.getX() * getWidth()/2 + (double) getWidth()/2, v2.getY() * getHeight()/2 + (double) getHeight()/2, v2.getZ(), 1);
            v3 = new Vector4(v3.getX() * getWidth()/2 + (double) getWidth()/2, v3.getY() * getHeight()/2 + (double) getHeight()/2, v3.getZ(), 1);

            if (shadowsEnabled) {
                double sw = shadowMapper.getWidth(), sh = shadowMapper.getHeight();

                // Light space vertices
                Vector4 v1Light = lightMVP.multiply(tri.getV1());
                v1Light = new Vector4(v1Light.getX()/v1Light.getW(), v1Light.getY()/v1Light.getW(), v1Light.getZ()/v1Light.getW(), 1);
                Vector4 v2Light = lightMVP.multiply(tri.getV2());
                v2Light = new Vector4(v2Light.getX()/v2Light.getW(), v2Light.getY()/v2Light.getW(), v2Light.getZ()/v2Light.getW(), 1);
                Vector4 v3Light = lightMVP.multiply(tri.getV3());
                v3Light = new Vector4(v3Light.getX()/v3Light.getW(), v3Light.getY()/v3Light.getW(), v3Light.getZ()/v3Light.getW(), 1);

                v1Light = new Vector4(v1Light.getX() * sw/2 + sw/2, v1Light.getY() * sh/2 + sh/2, v1Light.getZ(), 1);
                v2Light = new Vector4(v2Light.getX() * sw/2 + sw/2, v2Light.getY() * sh/2 + sh/2, v2Light.getZ(), 1);
                v3Light = new Vector4(v3Light.getX() * sw/2 + sw/2, v3Light.getY() * sh/2 + sh/2, v3Light.getZ(), 1);

                Color ambientColor = applyIntensity(tri.getColor(), 0.2);
                rasterizeTriangleWithShadow(zBuffer, v1, v2, v3, shadedColor, ambientColor, v1Light, v2Light, v3Light, shadowMap);
            } else {
                rasterizeTriangle(zBuffer, v1, v2, v3, shadedColor);
            }
        }
        g2.drawImage(img, 0, 0, null);
    }

    private void rasterizeTriangle(double[][] zBuffer, Vector4 v1, Vector4 v2, Vector4 v3, Color color) {
        int minX = Math.max(0, (int) Math.min(Math.min(v1.getX(), v2.getX()), v3.getX()));
        int maxX = Math.min(getWidth() - 1, (int) Math.max(Math.max(v1.getX(), v2.getX()), v3.getX()));
        int minY = Math.max(0, (int) Math.min(Math.min(v1.getY(), v2.getY()), v3.getY()));
        int maxY = Math.min(getHeight() - 1, (int) Math.max(Math.max(v1.getY(), v2.getY()), v3.getY()));

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                double[] baryCoords = getBarycentricCoordinates(x, y, v1, v2, v3);
                if (isNegative(baryCoords)) continue;

                double depth = v1.getZ()*baryCoords[0] + v2.getZ()*baryCoords[1] + v3.getZ()*baryCoords[2];

                if (depth < zBuffer[y][x]){
                    zBuffer[y][x] = depth;
                    img.setRGB(x, y, color.getRGB());
                }
            }
        }
    }

    private void rasterizeTriangleWithShadow(double[][] zBuffer, Vector4 v1, Vector4 v2, Vector4 v3, Color shadedColor, Color ambientColor,
                                             Vector4 v1Light, Vector4 v2Light, Vector4 v3Light, double[][] shadowMap) {

        int minX = Math.max(0, (int) Math.min(Math.min(v1.getX(), v2.getX()), v3.getX()));
        int maxX = Math.min(getWidth() - 1, (int) Math.max(Math.max(v1.getX(), v2.getX()), v3.getX()));
        int minY = Math.max(0, (int) Math.min(Math.min(v1.getY(), v2.getY()), v3.getY()));
        int maxY = Math.min(getHeight() - 1, (int) Math.max(Math.max(v1.getY(), v2.getY()), v3.getY()));

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                double[] b = getBarycentricCoordinates(x, y, v1, v2, v3);
                if (isNegative(b)) continue;

                double depth = v1.getZ()*b[0] + v2.getZ()*b[1] + v3.getZ()*b[2];
                if (depth < zBuffer[y][x]) {
                    zBuffer[y][x] = depth;
                    double shadowFactor = getShadowFactor(b, v1Light, v2Light, v3Light, shadowMap);
                    Color color = blendColors(shadedColor, ambientColor, shadowFactor);
                    img.setRGB(x, y, color.getRGB());
                }
            }
        }
    }

    private double getShadowFactor(double[] b, Vector4 v1Light, Vector4 v2Light, Vector4 v3Light, double[][] shadowMap){
        double lx = v1Light.getX()*b[0] + v2Light.getX()*b[1] + v3Light.getX()*b[2];
        double ly = v1Light.getY()*b[0] + v2Light.getY()*b[1] + v3Light.getY()*b[2];
        double lightDepth = v1Light.getZ()*b[0] + v2Light.getZ()*b[1] + v3Light.getZ()*b[2];

        int x = (int) lx, y = (int) ly, count = 0;
        for (int i = -SHADOW_KERNEL_SIZE; i < SHADOW_KERNEL_SIZE+1; i++) {
            for (int j = -SHADOW_KERNEL_SIZE; j < SHADOW_KERNEL_SIZE+1; j++) {
                int ys = y+i, xs = x+j;
                if (xs < 0 || xs >= shadowMapper.getWidth() || ys < 0 || ys >= shadowMapper.getHeight()) continue;
                if (lightDepth > shadowMap[ys][xs] + SHADOW_BIAS) count++;
            }
        }

        int kernelWidth = SHADOW_KERNEL_SIZE * 2 + 1;
        return count / (double) (kernelWidth * kernelWidth);
    }

    private Color blendColors(Color lit, Color shadow, double shadowFactor) {
        int r = (int)(lit.getRed()   * (1 - shadowFactor) + shadow.getRed()   * shadowFactor);
        int g = (int)(lit.getGreen() * (1 - shadowFactor) + shadow.getGreen() * shadowFactor);
        int b = (int)(lit.getBlue()  * (1 - shadowFactor) + shadow.getBlue()  * shadowFactor);
        return new Color(r, g, b);
    }

    private Color flatShading(Vector4 v1, Vector4 v2, Vector4 v3, Color color, Vector4 lightDir){
        Vector4 edge1 = v2.subtract(v1);
        Vector4 edge2 = v3.subtract(v1);
        Vector4 normal = edge1.cross(edge2).normalize();

        double diffuse = Math.max(0, normal.dot(lightDir));
        double ambient = 0.2;
        double intensity = ambient + (1.0 - ambient) * diffuse;

        return applyIntensity(color, intensity);
    }

    private void clearImg(){
        if(img == null || img.getWidth() != width || img.getHeight() != height){
            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        }else {
            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    img.setRGB(i, j, Color.BLACK.getRGB());
                }
            }
        }
    }

    private Color applyIntensity(Color color, double intensity) {
        return new Color(
                (int)(color.getRed() * intensity),
                (int)(color.getGreen() * intensity),
                (int)(color.getBlue() * intensity)
        );
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        shadowMapper.resize(width, height);
    }

    public boolean isShadowsEnabled() {
        return shadowsEnabled;
    }

    public void setShadowsEnabled(boolean shadowsEnabled) {
        this.shadowsEnabled = shadowsEnabled;
    }
}
