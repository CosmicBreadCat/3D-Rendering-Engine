public class Matrix4 {
    double[] values = new double[16];

    public Matrix4() {
        values = new double[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1};
    }

    public double[] getValues() {
        return values;
    }

    public void setValue(int id, double value){
        values[id] = value;
    }

    public void setValues(double[] values) {
        this.values = values;
    }

    public double getValue(int id){
        return values[id];
    }

    public Matrix4 multiply(Matrix4 B) {
        Matrix4 output = new Matrix4();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum += values[i*4 + k] * B.getValue(k*4 + j);
                }

                output.setValue(i*4 + j, sum);
            }
        }

        return output;
    }

    public Vertex multiply(Vertex v) {
        return new Vertex(
                v.getX()*values[0] + v.getY()*values[1] + v.getZ()*values[2] + v.getW()*values[3],
                v.getX()*values[4] + v.getY()*values[5] + v.getZ()*values[6] + v.getW()*values[7],
                v.getX()*values[8] + v.getY()*values[9] + v.getZ()*values[10] + v.getW()*values[11],
                v.getX()*values[12] + v.getY()*values[13] + v.getZ()*values[14] + v.getW()*values[15]
        );
    }

    public static Matrix4 createTranslationMatrix(double transX, double transY, double transZ){
        Matrix4 output = new Matrix4();
        output.setValue(3, transX);
        output.setValue(7, transY);
        output.setValue(11, transZ);
        return output;
    }

    public static Matrix4 createScalingMatrix(double scaleX, double scaleY, double scaleZ){
        Matrix4 output = new Matrix4();
        output.setValue(0, scaleX);
        output.setValue(5, scaleY);
        output.setValue(10, scaleZ);
        return output;
    }

    public static Matrix4 createXRotationMatrix(double phi){
        Matrix4 output = new Matrix4();
        double angle = Math.toRadians(phi);
        double sin = Math.sin(angle), cos = Math.cos(angle);

        output.setValue(5, cos);
        output.setValue(6, sin);
        output.setValue(9, -sin);
        output.setValue(10, cos);
        return output;
    }

    public static Matrix4 createYRotationMatrix(double phi){
        Matrix4 output = new Matrix4();
        double angle = Math.toRadians(phi);
        double sin = Math.sin(angle), cos = Math.cos(angle);

        output.setValue(0, cos);
        output.setValue(2, sin);
        output.setValue(8, -sin);
        output.setValue(10, cos);
        return output;
    }

    public static Matrix4 createZRotationMatrix(double phi){
        Matrix4 output = new Matrix4();
        double angle = Math.toRadians(phi);
        double sin = Math.sin(angle), cos = Math.cos(angle);

        output.setValue(0, cos);
        output.setValue(1, -sin);
        output.setValue(4, sin);
        output.setValue(5, cos);
        return output;
    }
}
