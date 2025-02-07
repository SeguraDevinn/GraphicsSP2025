public class RenderTask implements Runnable {
    private Scene scene;
    private Camera camera;
    private BufferedImage image;
    private int width;
    private int height;
    private int startY;
    private int endY;

    public RenderTask(Scene scene, Camera camera, BufferedImage image, int width, int height, int startY, int endY) {
        this.scene = scene;
        this.camera = camera;
        this.image = image;
        this.width = width;
        this.height = height;
        this.startY = startY;
        this.endY = endY;
    }

    @Override
    public void run() {
        for (int y = startY; y < endY; y++) {
            for (int x = 0; x < width; x++) {
                double ndcX = (x + 0.5) / width;
                double ndcY = (y + 0.5) / height;
                double screenX = 2 * ndcX - 1;
                double screenY = 1 - 2 * ndcY;
                double aspectRatio = (double) width / height;
                screenX *= aspectRatio;

                Vector3 rayDirection = new Vector3(screenX, screenY, -1).normalize();
                Ray ray = new Ray(camera.position, rayDirection);

                Color color = traceRay(ray, scene, 0); // Recursion depth
                synchronized (image) {
                    image.setRGB(x, y, color.getRGB());
                }
            }
        }
    }

    private static Color traceRay(Ray ray, Scene scene, int depth) {
        if (depth > MAX_REFLECTION_DEPTH) {
            return new Color(0, 0, 0); // Return black for maximum recursion depth
        }

        Intersection closestIntersection = null;
        for (Face face : scene.faces) {
            Vector3 v0 = scene.vertices.get(face.vertices[0]);
            Vector3 v1 = scene.vertices.get(face.vertices[1]);
            Vector3 v2 = scene.vertices.get(face.vertices[2]);

            Intersection intersection = RayTracer.intersectRayTriangle(ray, v0, v1, v2);
            if (intersection.hit && (closestIntersection == null || intersection.distance < closestIntersection.distance)) {
                closestIntersection = intersection;
            }
        }

        if (closestIntersection != null) {
            // Calculate reflection
            Vector3 reflectedDirection = reflect(ray.direction, closestIntersection.normal).normalize();
            Ray reflectedRay = new Ray(closestIntersection.point, reflectedDirection);

            // Trace the reflected ray recursively
            Color reflectedColor = traceRay(reflectedRay, scene, depth + 1);

            // Mix original hit color with reflected color (e.g., assume reflective surfaces are white)
            int r = (int) (0.5 * reflectedColor.getRed() + 0.5 * 255);
            int g = (int) (0.5 * reflectedColor.getGreen() + 0.5 * 255);
            int b = (int) (0.5 * reflectedColor.getBlue() + 0.5 * 255);

            return new Color(Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
        } else {
            return new Color(0, 0, 0); // Background color
        }
    }

    // Reflect the direction vector using the normal
    private static Vector3 reflect(Vector3 direction, Vector3 normal) {
        double dotProduct = direction.dot(normal);
        return direction.subtract(normal.multiply(2 * dotProduct));
    }

    private static final int MAX_REFLECTION_DEPTH = 3;
}

