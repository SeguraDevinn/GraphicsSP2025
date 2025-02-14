/*
This will be done by Josh
 */

public class RotationUtil {
    // Function to stimulate glRotatef, rotating around a specified axis by a certain angle
    public static Matrix4x4 glRotate(double angle, double x, double y, double z) {
        double radAngle = Math.toRadians(angle); // convert angle to radians
        Matrix4x4 rotationMatrix;

        // normalize the axis vector (x, y, z)
        double length = Math.sqrt(x * x + y * y + z * z);
        x /= length;
        y /= length;
        z /= length;

        // if rotating around x-axis
        if (x == 1 && y == 0 && z == 0) {
            rotationMatrix = new Matrix4x4(
                    1, 0, 0, 0,
                    0, Math.cos(radAngle), -Math.sin(radAngle), 0,
                    0, Math.sin(radAngle), Math.cos(radAngle), 0,
                    0, 0, 0, 1
            );
        }
        // if rotating around the y-axis
        else if (x == 0 && y == 1 && z == 0) {
            rotationMatrix = new Matrix4x4(
                    Math.cos(radAngle), 0, Math.sin(radAngle), 0,
                    0, 1, 0, 0,
                    -Math.sin(radAngle), 0, Math.cos(radAngle), 0,
                    0, 0, 0, 1
            );
        }
        // if rotating around z-axis
        else if (x == 0 && y == 0 && z == 1) {
            rotationMatrix = new Matrix4x4(
                    Math.cos(radAngle), -Math.sin(radAngle), 0, 0,
                    Math.sin(radAngle), Math.cos(radAngle), 0, 0,
                    0, 0, 1, 0,
                    0, 0, 0, 1
            );
        }
        else
        {
            // for more general cases of arbitrary axis rotation,
            // you could implement Rodrigues' rotation formula
            throw new UnsupportedOperationException("Only X, Y, Z axis rotations are supported");
        }
        return rotationMatrix;
    }

    //Apply the rotation matrix to each vertex of the subject
    public static void applyRotation(Scene scene, Matrix4x4
        rotationMatrix)
    {
        for (int i = 0; i < scene.vertices.size(); i++)
        {
            Vector3 rotatedVertex = rotationMatrix.multiply(scene.vertices.get(i));
            scene.vertices.set(i, rotatedVertex); // update each vertex with the rotated value
        }
    }
}
