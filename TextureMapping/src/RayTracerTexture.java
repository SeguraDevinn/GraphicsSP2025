// javac RayTracerTexture.java
// java RayTracerTexture

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class RayTracerTexture
{
    public static void main(String[] args) throws IOException
    {
        Scene scene = OBJParser.parse("TextureMapping\\src\\uvSphere.obj");
        scene.spheres.add(new Sphere(new Vector3(0, 0, 0), 1.0));
        Camera camera = new Camera(new Vector3(0, 0, 3), new Vector3(0, 0, 0), 90);

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
        BufferedImage texture = ImageIO.read(new File("TextureMapping\\src\\texture.png")); // Load texture image

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
        ImageIO.write(image, "png", new File("TextureMapping\\src\\outputUvSphereModified.png"));

        exportOBJ(scene, "TextureMapping\\src\\outputSphere.obj");
        System.out.println("OBJ file saved successfully.");
    }

    public static void exportOBJ(Scene scene, String filePath) throws IOException
    {
        File file = new File(filePath);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        // Write vertices
        for (Vector3 vertex : scene.vertices)
        {
            writer.write("v " + vertex.x + " " + vertex.y + " " + vertex.z + "\n");
        }

        // Write UV coordinates
        for (Vector2 uv : scene.uvs)
        {
            writer.write("vt " + uv.x + " " + uv.y + "\n");
        }

        // Write faces (assume triangles)
        for (Face face : scene.faces)
        {
            writer.write("f " + (face.vertices[0] + 1) + "/" + (face.uvs[0] + 1) + " "
                    + (face.vertices[1] + 1) + "/" + (face.uvs[1] + 1) + " "
                    + (face.vertices[2] + 1) + "/" + (face.uvs[2] + 1) + "\n");
        }

        writer.close();
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

    private static Vector2 computeSphericalUV(Vector3 point, Sphere sphere)
    {
        Vector3 normalizedPoint = point.subtract(sphere.center).normalize();

        // create variables for latitude and longitude and normalize them (u,v)
        double theta = Math.atan2(normalizedPoint.z, normalizedPoint.x);
        double phi = Math.acos(normalizedPoint.y);

        double u = (theta + Math.PI) / (2 * Math.PI);
        double v = phi / Math.PI;

        return new Vector2(u, v);
    }


    private static Color traceRay(Ray ray, Scene scene, BufferedImage texture)
    {
        Intersection closestIntersection = null;
        Sphere closestSphere = null;

        for (Sphere sphere : scene.spheres)
        {
            Intersection intersection = RayTracer.intersectRaySphere(ray, sphere);
            if (intersection.hit && (closestIntersection == null || intersection.distance < closestIntersection.distance))
            {
                closestIntersection = intersection;
                closestSphere = sphere;
            }
        }

        if (closestIntersection != null)
        {
            Vector2 sphericalUV = computeSphericalUV(closestIntersection.point, closestSphere);
            Color color = sampleTexture(texture, sphericalUV);

            // Debugging Output
            System.out.println("Hit: " + closestIntersection.point + " UV: " + sphericalUV.x + ", " + sphericalUV.y);

            return color;
        }
        else
        {
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