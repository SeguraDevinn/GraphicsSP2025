/*
This will be done by Franchesco
 */
public class Matrix4x4 {
    private double[][] m;   // 4x4 matrix

    // constructor for an empty matrix
    public Matrix4x4() {
        m = new double[4][4];
    }

    // contructor for a matrix with initial values
    public Matrix4x4(double m00, double m01, double m02, double
            m03,
                     double m10, double m11, double m12, double
                             m13,
                     double m20, double m21, double m22, double
                             m23,
                     double m30, double m31, double m32, double
                             m33) {
        m = new double[4][4];
        m[0][0] = m00;
        m[0][1] = m01;
        m[0][2] = m02;
        m[0][3] = m03
        ;
        m[1][0] = m10;
        m[1][1] = m11;
        m[1][2] = m12;
        m[1][3] = m13
        ;
        m[2][0] = m20;
        m[2][1] = m21;
        m[2][2] = m22;
        m[2][3] = m23
        ;
        m[3][0] = m30;
        m[3][1] = m31;
        m[3][2] = m32;
        m[3][3] = m33
        ;
    }

    // Matrix multiplication with another Matrix4x4
    public Matrix4x4 multiply(Matrix4x4 other) {
        Matrix4x4 result = new Matrix4x4();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.m[i][j] = 0;
                for (int k = 0; k < 4; k++) {
                    result.m[i][j] += this.m[i][k] * other.m[k][j];
                }
            }
        }
        return result;
    }

    // multiply the matrix with a vector3 (for 3D transformations)
    public Vector3 multiply(Vector3 v) {
        double x = v.x * m[0][0] + v.y * m[1][0] + v.z * m[2][0] + m[3][0];
        double y = v.x * m[0][1] + v.y * m[1][1] + v.z * m[2][1] + m[3][1];
        double z = v.x * m[0][2] + v.y * m[1][2] + v.z * m[2][2] + m[3][2];
        return new Vector3(x, y, z);
    }

    // static function to create an identity matrix
    public static Matrix4x4 identity() {
        return new Matrix4x4(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );
    }

    // static function to create a rotation matrix around the x-axis
    public static Matrix4x4 rotationX(double angle)
    {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Matrix4x4 (
          1, 0, 0, 0,
          0, cos, -sin, 0,
          0, sin, cos, 0,
          0, 0, 0, 1
        );
    }

    // static function to create a rotation matrix around the y-axis
    public static Matrix4x4 rotationY(double angle)
    {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Matrix4x4(
                cos, 0, sin, 0,
                0, 1, 0, 0,
                -sin, 0, cos, 0,
                0, 0, 0, 1
        );
    }

    //  static function to create a rotation matrix around the z-axis
    public static Matrix4x4 rotationZ(double angle)
    {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Matrix4x4(
                cos, -sin, 0, 0,
                sin, cos, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 0
        );
    }
}