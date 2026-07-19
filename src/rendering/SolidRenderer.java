package rendering;

import lighting.Light;
import math.Matrix4;
import math.Vector4;
import scene.Camera;
import scene.Mesh;
import scene.Triangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolidRenderer extends PipelineUtils{
    private final ShadowMapper shadowMapper;
    private boolean shadowsEnabled = true;
    private static final double SHADOW_BIAS = 0.005;
    private static final int SHADOW_KERNEL_SIZE = 5;

    public SolidRenderer(int width, int height) {
        super(width, height);
        shadowMapper = new  ShadowMapper(width, height);
    }

   public void render( Camera camera, Mesh mesh, Light light, FrameBuffer frameBuffer) {
        Matrix4 model = mesh.getModelMatrix();
        Matrix4 view = camera.getViewMatrix();
        Matrix4 projection = camera.getProjectionMatrix((double) getWidth() /getHeight());

        Matrix4 MV = view.multiply(model);

        Matrix4 lightMVP = light.getProjectionMatrix().multiply(light.getViewMatrix().multiply(model));
        double[][] shadowMap = shadowMapper.map(mesh, lightMVP);

        for (Triangle tri : mesh.getTris()) {
            // View space vertices
            Vector4 v1View = MV.multiply(tri.getV1());
            Vector4 v2View = MV.multiply(tri.getV2());
            Vector4 v3View = MV.multiply(tri.getV3());

            if (shouldCullTriangle(v1View, v2View, v3View)) continue;

            // Calculate simple lighting in view space
            Vector4 v1World = model.multiply(tri.getV1());
            Vector4 v2World = model.multiply(tri.getV2());
            Vector4 v3World = model.multiply(tri.getV3());

            Color shadedColor = flatShading(v1World, v2World, v3World, tri.getColor(), light.getDirection());

            // Transform to clip space
            Vector4 v1 = projection.multiply(v1View);
            Vector4 v2 = projection.multiply(v2View);
            Vector4 v3 = projection.multiply(v3View);

            // Sutherland-Hodgman clipping
            List<Vector4> camPoly = new ArrayList<>(List.of(v1, v2, v3));

            if (shadowsEnabled) {
                double sw = shadowMapper.getWidth(), sh = shadowMapper.getHeight();

                // Light verts in raw clip space — NO persp divide yet, so clipTriangleDual can interpolate them correctly
                List<Vector4> lightPoly = new ArrayList<>(List.of(
                        lightMVP.multiply(tri.getV1()),
                        lightMVP.multiply(tri.getV2()),
                        lightMVP.multiply(tri.getV3())
                ));

                camPoly = clipTriangleDual(camPoly, lightPoly);
                Color ambientColor = applyIntensity(tri.getColor(), 0.2);

                for (int i = 1; i < camPoly.size() - 1; i++) {
                    Vector4 cv1 = mapToSpace(perspectiveDivide(camPoly.get(0)),  getWidth(), getHeight());
                    Vector4 cv2 = mapToSpace(perspectiveDivide(camPoly.get(i)),  getWidth(), getHeight());
                    Vector4 cv3 = mapToSpace(perspectiveDivide(camPoly.get(i+1)), getWidth(), getHeight());

                    Vector4 lv1 = mapToSpace(perspectiveDivide(lightPoly.get(0)),  sw, sh);
                    Vector4 lv2 = mapToSpace(perspectiveDivide(lightPoly.get(i)),  sw, sh);
                    Vector4 lv3 = mapToSpace(perspectiveDivide(lightPoly.get(i+1)), sw, sh);

                    rasterizeTriangleWithShadow(frameBuffer, cv1, cv2, cv3, shadedColor, ambientColor, lv1, lv2, lv3, shadowMap);
                }
            } else {
                for (Triangle subTri : triangulate(clipTriangle(camPoly), shadedColor)) {
                    v1 = mapToSpace(perspectiveDivide(subTri.getV1()), getWidth(), getHeight());
                    v2 = mapToSpace(perspectiveDivide(subTri.getV2()), getWidth(), getHeight());
                    v3 = mapToSpace(perspectiveDivide(subTri.getV3()), getWidth(), getHeight());
                    rasterizeTriangle(frameBuffer, v1, v2, v3, shadedColor);
                }
            }
        }
    }

    private void rasterizeTriangle(FrameBuffer frameBuffer, Vector4 v1, Vector4 v2, Vector4 v3, Color color) {
        int minX = Math.max(0, (int) Math.min(Math.min(v1.getX(), v2.getX()), v3.getX()));
        int maxX = Math.min(getWidth() - 1, (int) Math.max(Math.max(v1.getX(), v2.getX()), v3.getX()));
        int minY = Math.max(0, (int) Math.min(Math.min(v1.getY(), v2.getY()), v3.getY()));
        int maxY = Math.min(getHeight() - 1, (int) Math.max(Math.max(v1.getY(), v2.getY()), v3.getY()));

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                double[] baryCoords = getBarycentricCoordinates(x, y, v1, v2, v3);
                if (isNegative(baryCoords)) continue;

                double depth = v1.getZ()*baryCoords[0] + v2.getZ()*baryCoords[1] + v3.getZ()*baryCoords[2];

                frameBuffer.setPixel(x, y, depth, color);
            }
        }
    }

    private void rasterizeTriangleWithShadow(FrameBuffer frameBuffer, Vector4 v1, Vector4 v2, Vector4 v3, Color shadedColor, Color ambientColor,
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
                if (depth < frameBuffer.getZBuffer()[y][x]) {
                    double shadowFactor = getShadowFactor(b, v1Light, v2Light, v3Light, shadowMap);
                    Color color = blendColors(shadedColor, ambientColor, shadowFactor);

                    frameBuffer.setPixel(x, y, depth, color);
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
        Vector4 normal = edge2.cross(edge1).normalize();

        double diffuse = Math.max(0, normal.dot(lightDir));
        double ambient = 0.2;
        double intensity = ambient + (1.0 - ambient) * diffuse;

        return applyIntensity(color, intensity);
    }

    private Color applyIntensity(Color color, double intensity) {
        return new Color(
                (int)(color.getRed() * intensity),
                (int)(color.getGreen() * intensity),
                (int)(color.getBlue() * intensity)
        );
    }

    private Vector4 perspectiveDivide(Vector4 v){return new Vector4(v.getX() / v.getW(), v.getY() / v.getW(), v.getZ() / v.getW(), 1);}

    private Vector4 mapToSpace(Vector4 v, double width, double height){return new Vector4(v.getX() * width / 2 +  width / 2, v.getY() * height / 2 + height / 2, v.getZ(), 1);}

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
