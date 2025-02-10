import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;



public class ThreadedRayTracing {
    static String filePath = "/Users/devinnsegura/IdeaProjects/Graphics/RAYTRACING/src/teapot.obj";
    public static void main(String[] args) throws IOException {
        Scene scene = OBJParser.parse(filePath);
        int originalVertexCount = scene.vertices.size();

        Scene secondTeapot = OBJParser.parse(filePath);
        translateScene(secondTeapot, new Vector3(2.5, 0, 4.0));
        adjustFaceIndices(secondTeapot, originalVertexCount);
        scene.vertices.addAll(secondTeapot.vertices);
        scene.faces.addAll(secondTeapot.faces);
        Camera camera = new Camera(new Vector3(0, 2, 6), new Vector3(0, 0, -1), 100);
        int width = 800;
        int height = 600;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int numThreads = Runtime.getRuntime().availableProcessors();
        int rowsPerThread = height / numThreads;
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            int startY = i * rowsPerThread;
            int endY = (i == numThreads - 1) ? height : startY + rowsPerThread;

            Thread thread = new Thread(new RenderTask(scene, camera, image, width, height, startY, endY));
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ImageIO.write(image, "png", new File("output.png"));
    }

    private static void adjustFaceIndices(Scene scene, int vertexOffset) {
        for (Face face : scene.faces) {
            for (int i = 0; i < face.vertices.length; i++) {
                face.vertices[i] += vertexOffset;
            }
        }
    }

    private static void translateScene(Scene scene, Vector3 translation) {
        for (int i = 0; i < scene. vertices.size(); i++) {
            Vector3 v = scene.vertices.get(i);
            Vector3 newV = new Vector3(v.x + translation.x, v.y + translation.y, v.z + translation.z);
            scene.vertices.set(i, newV);
        }
    }

}


