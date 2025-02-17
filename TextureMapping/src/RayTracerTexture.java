// javac RayTracerTexture.java
// java RayTracerTexture

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;

public class RayTracerTexture
{
    public static void main(String[] args) throws IOException
    {
        Scene scene = OBJParser.parse("TextureMapping/src/uvSphere.obj");
        scene.spheres.add(new Sphere(new Vector3(0, 0, 0), 1.0));
        Camera camera = new Camera(new Vector3(0, 0, -3), new Vector3(0, 0, 0), 90);

        // Rotate for 30 degrees around the X-axis
        Matrix4x4 rotationMatrix = RotationUtil.glRotate(30, 1, 0, 0);

        // Apply rotation to the object
        RotationUtil.applyRotation(scene, rotationMatrix);

        // Rotate for 30 degrees around the Y-axis
        rotationMatrix = RotationUtil.glRotate(30, 0, 1, 0);

        // Apply rotation to the object
        RotationUtil.applyRotation(scene, rotationMatrix);

        int width = 800;
        int height = 600;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage texture = ImageIO.read(new File("TextureMapping/src/texture.png")); // Load texture image

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                double ndcX = (x + 0.5) / width;
                double ndcY = (y + 0.5) / height;
                double screenX = 2 * ndcX - 1;
                double screenY = 1 - 2 * ndcY;
                double aspectRatio = (double) width / height;
                screenX *= aspectRatio;

                Vector3 rayDirection = new Vector3(screenX, screenY, -1).normalize();
                Ray ray = new Ray(camera.position, rayDirection);

                Color color = traceRay(ray, scene, texture);
                image.setRGB(x, y, color.getRGB());
            }
        }
        ImageIO.write(image, "png", new File("TextureMapping\\src\\output.png"));

        exportSphere(scene, "TextureMapping\\src\\outputSphere.obj");
        System.out.println("OBJ file saved successfully.");
    }

    public static void exportSphere(Scene scene, String filePath) throws IOException
    {
       File outputFile = new File(filePath);
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

        // Write vertex positions
        for (Vector3 point : scene.vertices) {
            bw.write("v " + point.x + " " + point.y + " " + point.z);
            bw.newLine();
        }

        // Write texture coordinates (UVs)
        for (Vector2 texCoord : scene.uvs) {
            bw.write("vt " + texCoord.x + " " + texCoord.y);
            bw.newLine();
        }

        // Write faces (assuming each face is a triangle)
        for (Face triangle : scene.faces) {
            int vertIndex1 = triangle.vertices[0] + 1;
            int vertIndex2 = triangle.vertices[1] + 1;
            int vertIndex3 = triangle.vertices[2] + 1;

            int uvIndex1 = triangle.uvs[0] + 1;
            int uvIndex2 = triangle.uvs[1] + 1;
            int uvIndex3 = triangle.uvs[2] + 1;

            String faceLine = String.format("f %d/%d %d/%d %d/%d",
                    vertIndex1, uvIndex1,
                    vertIndex2, uvIndex2,
                    vertIndex3, uvIndex3);
            bw.write(faceLine);
            bw.newLine();
        }
    }
}
    private static Color sampleTexture(BufferedImage texture, Vector2 uv)
    {
        int texWidth = texture.getWidth();
        int texHeight = texture.getHeight();

        // Ensure UV coordinates are within the [0,1] range
        uv.x = Math.max(0, Math.min(uv.x, 1));
        uv.y = Math.max(0, Math.min(uv.y, 1));

        // Convert UV coordinates to texture pixel coordinates
        int texX = (int)(uv.x * (texWidth - 1));
        int texY = (int)(uv.y * (texHeight - 1));

        // Get the color from the texture
        return new Color(texture.getRGB(texX, texY));
    }

    private static Vector2 SphereUV(Vector3 point, Sphere sphere) {

    Vector3 relativePos = point.subtract(sphere.center).normalize();


    double longitude = Math.atan2(relativePos.z, relativePos.x);
    double latitude = Math.acos(relativePos.y);


    double uCoord = (longitude + Math.PI) / (2 * Math.PI);
    double vCoord = latitude / Math.PI;

    return new Vector2(uCoord, vCoord);
}


    private static Color traceRay(Ray ray, Scene scene, BufferedImage texture) {
    Intersection nearestIntersection = null;
    Sphere nearestSphere = null;

    for (Sphere s : scene.spheres) {
        Intersection currentIntersection = RayTracer.intersectRaySphere(ray, s);
        if (currentIntersection.hit && (nearestIntersection == null || currentIntersection.distance < nearestIntersection.distance)) {
            nearestIntersection = currentIntersection;
            nearestSphere = s;
        }
    }

    if (nearestIntersection != null) {
        // Calculate the UV coordinates on the sphere using the modified function name.
        Vector2 uvCoordinates = SphereUV(nearestIntersection.point, nearestSphere);
        // Sample the texture using the computed UV coordinates.
        Color sampledColor = sampleTexture(texture, uvCoordinates);
        return sampledColor;
    } else {
        return new Color(0, 0, 0);
    }
}



    private static Vector2 adjustUVForWholeFace(Vector2 interpolatedUV, Scene scene, Face closestFace)
    {
        // Get the UV coordinates of all triangles in the face
        Vector2 uv0 = scene.uvs.get(closestFace.uvs[0]);
        Vector2 uv1 = scene.uvs.get(closestFace.uvs[1]);
        Vector2 uv2 = scene.uvs.get(closestFace.uvs[2]);

        // Find the minimum and maximum UV coordinates for the entire face (bounding box)
        double minU = Math.min(uv0.x, Math.min(uv1.x, uv2.x));
        double maxU = Math.max(uv0.x, Math.max(uv1.x, uv2.x));
        double minV = Math.min(uv0.y, Math.min(uv1.y, uv2.y));
        double maxV = Math.max(uv0.y, Math.max(uv1.y, uv2.y));
        // Normalize the interpolated UV within the face’s bounding box
        double normalizedU = (interpolatedUV.x - minU) / (maxU - minU);
        double normalizedV = (interpolatedUV.y - minV) / (maxV - minV);

        // Ensure UV values are clamped between 0 and 1 to avoid out-of-bounds texture lookup
        normalizedU = Math.max(0, Math.min(1, normalizedU));
        normalizedV = Math.max(0, Math.min(1, normalizedV));

        return new Vector2(normalizedU, normalizedV);
    }

    private static Vector3 CalculateBarycentricCoordinates(Vector3 p, Vector3 a, Vector3 b, Vector3 c)
    {
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

        return new Vector3(u, v, w);
    }

    private static Vector2 interpolateUV(Vector3 barycentricCoords, Vector2 uv0, Vector2 uv1, Vector2 uv2)
    {
        double u = barycentricCoords.x * uv0.x + barycentricCoords.y * uv1.x + barycentricCoords.z * uv2.x;
        double v = barycentricCoords.x * uv0.y + barycentricCoords.y * uv1.y + barycentricCoords.z * uv2.y;
        return new Vector2(u, v);
    }
}