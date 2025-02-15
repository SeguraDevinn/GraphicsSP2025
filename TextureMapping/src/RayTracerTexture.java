/*
This will be done by Devinn
 */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RayTracerTexture {
    public static void main(String[] args) throws IOException {
        Scene scene = OBJParser.parse("cube.obj");
        Camera camera = new Camera(new Vector3(0,0,3), new Vector3(0,0,0), 90);
        // Rotate 30 degrees around X-axis
        Matrix4x4 rotationMatrix = RotationUtil.glRotate(30,1,0,0);

        RotationUtil.applyRotation(scene, rotationMatrix);

        rotationMatrix = RotationUtil.glRotate(30,0,1,0);

        RotationUtil.applyRotation(scene, rotationMatrix);

        int width = 800;
        int height = 600;
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB );
        BufferedImage texture = ImageIO.read(new File("texture.png"));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double ndcX = (x + 0.5) / width;
                double ndcY = (y + 0.5) / height;
                double screenX = 2 * ndcX - 1;
                double screenY = 1 - 2 * ndcY;
                double aspectRatio = (double) width / height;
                screenX *= aspectRatio;

                Vector3 rayDirection = new Vector3(screenX, screenY, -1).normalize();
                Ray ray = new Ray(camera.position, rayDirection);
                Color color = traceRay(ray, scene, texture);
                image.setRGB(x ,y, color.getRGB());
            }
        }
        ImageIO.write(image, "png", new File("output.png"));
    }

    private static Color traceRay(Ray ray, Scene scene, BufferedImage texture) {
        Intersection closestIntersecion = null;
        Face closestFace = null;

        for (Face face : scene.faces) {
            Vector3 v0 = scene.vertices.get(face.vertices[0]);
            Vector3 v1 = scene.vertices.get(face.vertices[1]);
            Vector3 v2 = scene.vertices.get(face.vertices[2]);

            Intersection intersection = RayTracer.intersectRayTriangle(ray, v0, v1, v2);
            if (intersection.hit && (closestIntersecion == null || intersection.distance < closestIntersecion.distance)) {
                closestIntersecion = intersection;
                closestFace = face;
            }
        }
        if (closestIntersecion != null && closestFace != null) {
            Vector3 v0 = scene.vertices.get(closestFace.vertices[0]);
            Vector3 v1 = scene.vertices.get(closestFace.vertices[1]);
            Vector3 v2 = scene.vertices.get(closestFace.vertices[2]);

            Vector2 uv0 = scene.uvs.get(closestFace.uvs[0]);
            Vector2 uv1 = scene.uvs.get(closestFace.uvs[1]);
            Vector2 uv2 = scene.uvs.get(closestFace.uvs[2]);

            Vector3 barycentricCoords = calculateBarycentricCoordinates(closestIntersecion.point, v0, v1, v2);

            Vector2  interpolatedUV = interpolateUV(barycentricCoords, uv0, uv1, uv2);
            interpolatedUV = adjustUVForWholeFace(interpolatedUV,scene, closestFace);
            return  sampleTexture(texture, interpolatedUV);
        } else {
            return new Color(0,0,0);
        }
    }

    private static Color sampleTexture(BufferedImage texture, Vector2 uv) {
        int textWidth = texture.getWidth();
        int textHeight = texture.getHeight();

        uv.x = Math.max(0, Math.min(uv.x, 1));
        uv.y = Math.max(0, Math.min(uv.y, 1));

        int texX = (int) (uv.x * (textWidth - 1));
        int texY = (int) (uv.y * (textHeight - 1));

        return new Color(texture.getRGB(texX, texY));
    }


    private static Vector2 adjustUVForWholeFace(Vector2 interpolatedUV, Scene scene, Face closestFace) {
        Vector2 uv0 = scene.uvs.get(closestFace.uvs[0]);
        Vector2 uv1 = scene.uvs.get(closestFace.uvs[1]);
        Vector2 uv2 = scene.uvs.get(closestFace.uvs[2]);

        double minU = Math.min(uv0.x, Math.min(uv1.x, uv2.x));
        double maxU = Math.min(uv0.x, Math.min(uv1.x, uv2.x));
        double minv = Math.min(uv0.y, Math.min(uv1.x, uv2.x));
        double maxv = Math.min(uv0.y, Math.min(uv1.x, uv2.x));

        double normalizedU = (interpolatedUV.x - minU) / (maxU - minU);
        double normalizedV = (interpolatedUV.y - minv) / (maxv - minv);

        normalizedU = Math.max(0, Math.min(1, normalizedU));
        normalizedV = Math.max(0, Math.min(1, normalizedV));

        return new Vector2(normalizedU, normalizedV);
    }

    private static Vector2 interpolateUV(Vector3 barycentricCoords, Vector2 uv0, Vector2 uv1, Vector2 uv2) {
        double u = barycentricCoords.x * uv0.x + barycentricCoords.y * uv1.x + barycentricCoords.z * uv2.x;
        double v = barycentricCoords.x * uv0.y + barycentricCoords.y * uv1.y + barycentricCoords.z * uv2.y;
        return new Vector2(u,v);
    }

    private static Vector3 calculateBarycentricCoordinates(Vector3 p, Vector3 a, Vector3 b, Vector3 c) {
        Vector3 v0 = b.subtract(a);
        Vector3 v1 = c.subtract(a);
        Vector3 v2 = p.subtract(a);

        double d00 = v0.dot(v0);
        double d01 = v0.dot(v1);
        double d11 = v1.dot(v1);
        double d20 = v2.dot(v0);
        double d21 = v2.dot(v1);

        double denom = d00 * d11 - d01 * d01;
        double v = (d11 * d20 - d01 * d21) / denom;
        double w = (d00 * d21 - d01 * d20) / denom;
        double u = 1.0 - v - w;
        return new Vector3(u,v,w);

    }
}
