/*
This will be done by Devinn
 */

public class Vector3
{
    double x, y, z;

    Vector3(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    Vector3 subtract(Vector3 v)
    {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    Vector3 cross(Vector3 v)
    {
        return new Vector3(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
        );
    }

    double dot(Vector3 v)
    {
        return x * v.x + y * v.y + z * v.z;
    }

    Vector3 normalize()
    {
        double length = Math.sqrt(x * x + y * y + z * z);
        return new Vector3(x / length, y / length, z / length);
    }
    Vector3 multiply(double scalar)
    {
        return new Vector3(x * scalar, y * scalar, z * scalar);
    }
    Vector3 add(Vector3 v)
    {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

}

