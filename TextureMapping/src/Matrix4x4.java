/*
This will be done by Franchesco
 */
public class Matrix4x4
{
    private double[][] m;   // 4x4 matrix
// constructor for an empty matrix
    public Matrix4x4()
    {
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
                 m33)
        {
            m = new double[4][4];
            m[0][0] = m00; m[0][1] = m01; m[0][2] = m02; m[0][3] = m03
                ;
            m[1][0] = m10; m[1][1] = m11; m[1][2] = m12; m[1][3] = m13
                ;
            m[2][0] = m20; m[2][1] = m21; m[2][2] = m22; m[2][3] = m23
                ;
            m[3][0] = m30; m[3][1] = m31; m[3][2] = m32; m[3][3] = m33
                ;
        }

        // Matrix multiplication with another Matrix4x4

}