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

    public Matrix4 multiply(Matrix4 B){
        Matrix4 output = new Matrix4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    double res = values[i*4 + k] * B.getValue(k*4 + j);
                    output.setValue(i*4+k,res);
                }
            }
        }
        return output;
    }

    public static Matrix4 createTranslationMatrix(double transX, double transY, double transZ){
        Matrix4 output = new Matrix4();
        output.setValue(3, transX);
        output.setValue(7, transY);
        output.setValue(11, transZ);
        return output;
    }

    public static Matrix4 createScalingMatrix(double transX, double transY, double scaleZ){
        Matrix4 output = new Matrix4();
        output.setValue(0, transX);
        output.setValue(5, transY);
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
