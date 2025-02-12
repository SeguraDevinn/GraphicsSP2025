public class RayTracer {
    public static Intersection intersectRayTriangle(Ray ray, Vector3 v0, Vector3 v1, Vector3 v2) {
        final double EPSILON = 0.0000001;
        Vector3 edge1 = v1.subtract(v0);
        Vector3 edge2 = v2.subtract(v0);
        Vector3 h = ray.direction.cross(edge2);
        double a = edge1.dot(h);
        if (a > -EPSILON && a < EPSILON) {
            return new Intersection(false, 0, null, null);
        }
        double f = 1.0 / a;
        Vector3 s = ray.origin.subtract(v0);
        double u = f * s.dot(h);
        if (u < 0.0 || u > 1.0) {
            return new Intersection(false, 0, null, null);
        }

        Vector3 q = s.cross(edge1);
        double v = f * ray.direction.dot(q);
        if (v < 0.0 || u + v > 1.0) {
            return new Intersection(false, 0, null, null);
        }

        double t = f * edge2.dot(q);
        if (t > EPSILON) {
            Vector3 intersectionPoint = ray.origin.add(ray.direction.multiply(t));
            Vector3 normal = edge1.cross(edge2).normalize();
            return new Intersection(true, t, intersectionPoint, normal);
        } else {
            return new Intersection(false, 0, null, null);
        }
    }

    public static double getShadowFactor(Vector3 point, Vector3 lightPos, Scene scene) {
        int shadowSamples = 20; // Number of rays for soft shadow sampling
        int shadowCount = 0;
        double lightRadius = 0.3; // Soft shadow radius

        for (int i = 0; i < shadowSamples; i++) {
            // Generate a random offset for area light effect
            Vector3 randomOffset = new Vector3(
                (Math.random() - 0.5) * lightRadius,
                (Math.random() - 0.5) * lightRadius,
                (Math.random() - 0.5) * lightRadius
            );
            Vector3 sampleLightPos = lightPos.add(randomOffset);

            Vector3 shadowRayDir = sampleLightPos.subtract(point).normalize();
            Ray shadowRay = new Ray(point.add(shadowRayDir.multiply(0.05)), shadowRayDir); // Offset to prevent self-shadowing

            for (Face face : scene.faces) {
                Vector3 v0 = scene.vertices.get(face.vertices[0]);
                Vector3 v1 = scene.vertices.get(face.vertices[1]);
                Vector3 v2 = scene.vertices.get(face.vertices[2]);

                Intersection shadowIntersection = intersectRayTriangle(shadowRay, v0, v1, v2);
                if (shadowIntersection.hit) {
                    shadowCount++;
                    break; // Stop checking this sample if shadow is found
                }
            }
        }

        return (double) shadowCount / shadowSamples; // 0 = no shadow, 1 = full shadow
    }
}
