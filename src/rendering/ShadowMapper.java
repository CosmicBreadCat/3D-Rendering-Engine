package rendering;

import math.Matrix4;
import math.Vector4;
import scene.Mesh;
import scene.Triangle;

import java.util.Arrays;

public class ShadowMapper extends PipelineUtils{
    public ShadowMapper(int width, int height) {
        super(width, height);
    }

    public double[][] map(Mesh mesh, Matrix4 lightMVP){
        double[][] shadowMap = new double[height][width];
        Arrays.stream(shadowMap).forEach(y -> Arrays.fill(y, Double.POSITIVE_INFINITY));

        for (Triangle tri : mesh.getTris()){
            Vector4 v1 = lightMVP.multiply(tri.getV1());
            Vector4 v2 = lightMVP.multiply(tri.getV2());
            Vector4 v3 = lightMVP.multiply(tri.getV3());

            v1 = new Vector4(v1.getX()/v1.getW(), v1.getY()/v1.getW(), v1.getZ()/v1.getW(), 1);
            v2 = new Vector4(v2.getX()/v2.getW(), v2.getY()/v2.getW(), v2.getZ()/v2.getW(), 1);
            v3 = new Vector4(v3.getX()/v3.getW(), v3.getY()/v3.getW(), v3.getZ()/v3.getW(), 1);

            v1 = new Vector4(v1.getX() * width/2 + (double) width/2, v1.getY() * height/2 + (double) height/2, v1.getZ(), 1);
            v2 = new Vector4(v2.getX() * width/2 + (double) width/2, v2.getY() * height/2 + (double) height/2, v2.getZ(), 1);
            v3 = new Vector4(v3.getX() * width/2 + (double) width/2, v3.getY() * height/2 + (double) height/2, v3.getZ(), 1);

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
                    double[] baryCoords = getBarycentricCoordinates(x, y, v1, v2, v3);
                    if (isNegative(baryCoords)) continue;

                    double depth = v1.getZ()*baryCoords[0] + v2.getZ()*baryCoords[1] + v3.getZ()*baryCoords[2];

                    if (depth < shadowMap[y][x]){
                        shadowMap[y][x] = depth;
                    }
                }
            }
        }

        return shadowMap;
    }
}
