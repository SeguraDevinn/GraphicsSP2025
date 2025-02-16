/*
Josh will do this class
 */

public class Intersection
{
    boolean hit;
    double distance;
    Vector3 point;
    Vector3 normal;

    Intersection(boolean hit, double distance, Vector3 point, Vector3 normal)
    {
        this.hit = hit;
        this.distance = distance;
        this.point = point;
        this.normal = normal;
    }
}
