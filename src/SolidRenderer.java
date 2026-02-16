import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class SolidRenderer extends Renderer{
    private BufferedImage img = null;
    private Vector4 lightDir = new Vector4(0, 5, -1, 0).normalize();

    public SolidRenderer(int width, int height) {
        super(width, height);
    }

    @Override
    public void render(Graphics2D g2, Camera camera, Mesh mesh) {
        clearImg();
        Matrix4 model = mesh.getModelMatrix();
        Matrix4 view = camera.getViewMatrix();
        Matrix4 projection = camera.getProjectionMatrix((double) getWidth() /getHeight());

        Matrix4 MV = view.multiply(model);

        double[][] zBuffer = new double[getHeight()][getWidth()];
        Arrays.stream(zBuffer).forEach(y -> Arrays.fill(y, Double.POSITIVE_INFINITY));

        for (Triangle tri : mesh.getTris()) {
            // View space vertices
            Vector4 v1View = MV.multiply(tri.getV1());
            Vector4 v2View = MV.multiply(tri.getV2());
            Vector4 v3View = MV.multiply(tri.getV3());

            if (shouldCullTriangle(v1View, v2View, v3View)) continue;

            // Calculate lighting in view space
            Color shadedColor = flatShading(v1View, v2View, v3View, tri.getColor());

            // Transform to clip space
            Vector4 v1 = projection.multiply(v1View);
            v1 = new Vector4(v1.getX()/v1.getW(), v1.getY()/v1.getW(), v1.getZ()/v1.getW(), 1);
            Vector4 v2 = projection.multiply(v2View);
            v2 = new Vector4(v2.getX()/v2.getW(), v2.getY()/v2.getW(), v2.getZ()/v2.getW(), 1);
            Vector4 v3 = projection.multiply(v3View);
            v3 = new Vector4(v3.getX()/v3.getW(), v3.getY()/v3.getW(), v3.getZ()/v3.getW(), 1);

            // Convert to screen space
            v1 = new Vector4(v1.getX() * getWidth()/2 + (double) getWidth() /2, v1.getY() * getHeight()/2 + (double) getHeight() /2, v1.getZ(), 1);
            v2 = new Vector4(v2.getX() * getWidth()/2 + (double) getWidth() /2, v2.getY() * getHeight()/2 + (double) getHeight() /2, v2.getZ(), 1);
            v3 = new Vector4(v3.getX() * getWidth()/2 + (double) getWidth() /2, v3.getY() * getHeight()/2 + (double) getHeight() /2, v3.getZ(), 1);

            rasterizeTriangle(zBuffer, v1, v2, v3, shadedColor);
        }
        g2.drawImage(img, 0, 0, null);
    }

    private void rasterizeTriangle(double[][] zBuffer, Vector4 v1, Vector4 v2, Vector4 v3, Color color) {
        int width = getWidth();
        int height = getHeight();

        int minX = (int) Math.min(Math.min(v1.getX(), v2.getX()), v3.getX());
        minX = Math.max(0, minX);
        int maxX = (int) Math.max(Math.max(v1.getX(), v2.getX()), v3.getX());
        maxX = Math.min(width - 1, maxX);

        int minY = (int) Math.min(Math.min(v1.getY(), v2.getY()), v3.getY());
        minY = Math.max(0, minY);
        int maxY = (int) Math.max(Math.max(v1.getY(), v2.getY()), v3.getY());
        maxY = Math.min(height - 1, maxY);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                double[] baryCoords = barycentricCoordinates(x, y, v1, v2, v3);
                if (isNegative(baryCoords)) continue;

                double depth = v1.getZ()*baryCoords[0] + v2.getZ()*baryCoords[1] + v3.getZ()*baryCoords[2];

                if (depth < zBuffer[y][x]){
                    zBuffer[y][x] = depth;
                    img.setRGB(x, y, color.getRGB());
                }
            }
        }
    }

    private Color flatShading(Vector4 v1, Vector4 v2, Vector4 v3, Color color){
        Vector4 edge1 = v2.subtract(v1);
        Vector4 edge2 = v3.subtract(v1);
        Vector4 normal = edge1.cross(edge2).normalize();

        double diffuse = Math.max(0, normal.dot(lightDir));
        double ambient = 0.2;
        double intensity = ambient + (1.0 - ambient) * diffuse;

        int r = (int)(color.getRed() * intensity);
        int g = (int)(color.getGreen() * intensity);
        int b = (int)(color.getBlue() * intensity);

        return new Color(r, g, b);
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

    public Vector4 getLightDir() {
        return lightDir;
    }

    public void setLightDir(Vector4 lightDir) {
        this.lightDir = lightDir;
    }
}
