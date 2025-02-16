import java.util.Vector;

/*
This will be done by my boy franchesco
 */
public class RayTracer
{
    public static Intersection intersectRayTriangle(Ray ray, Vector3 v0, Vector3 v1, Vector3 v2)
    {
        final double EPSILON = 0.0000001;
        Vector3 edge1 = v1.subtract(v0);
        Vector3 edge2 = v2.subtract(v0);
        Vector3 h = ray.direction.cross(edge2);
        double a = edge1.dot(h);
        if (a > -EPSILON && a < EPSILON)
            return new Intersection(false, 0, null, null);

        double f = 1.0 / a;
        Vector3 s = ray.origin.subtract(v0);
        double u = f * s.dot(h);
        if (u < 0.0 || u > 1.0)
            return new Intersection(false, 0, null, null);

        Vector3 q = s.cross(edge1);
        double v = f * ray.direction.dot(q);
        if (v < 0.0 || u + v > 1.0)
            return new Intersection(false, 0, null, null);

        double t = f * edge2.dot(q);
        if (t > EPSILON)
        {
            Vector3 intersectionPoint = new Vector3(
                    ray.origin.x + ray.direction.x * t,
                    ray.origin.y + ray.direction.y * t,
                    ray.origin.z + ray.direction.z * t
            );

            Vector3 normal = edge1.cross(edge2).normalize();
            return new Intersection(true, t, intersectionPoint, normal);
        }
        else
            return new Intersection(false, 0, null, null);
    }

    public static Intersection intersectRaySphere(Ray ray, Sphere sphere)
    {
        Vector3 oc = ray.origin.subtract(sphere.center);
        double a = ray.direction.dot(ray.direction);
        double b = 2.0 * oc.dot(ray.direction);
        double c = oc.dot(oc) - (sphere.radius * sphere.radius);
        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0)
        {
            return new Intersection(false, 0, null, null);
        }

        double sqrtD = Math.sqrt(discriminant);
        double t1 = (-b - sqrtD) / (2.0 * a);
        double t2 = (-b + sqrtD) / (2.0 * a);
        double t = (t1 > 0) ? t1 : ((t2 > 0) ? t2 : -1);

        if (t < 0)
        {
            return new Intersection(false, 0, null, null);
        }

        Vector3 hitPoint = ray.origin.add(ray.direction.multiply(t));
        Vector3 normal = hitPoint.subtract(sphere.center).normalize();

        return new Intersection(true, t, hitPoint, normal);
    }

}
