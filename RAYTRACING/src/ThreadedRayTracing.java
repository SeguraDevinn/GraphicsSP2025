import java.io.IOException;



public class ThreadedRayTracing {
    public static void main(String[] args) throws IOException {
        Scene scene = OBJParser.parse("teapot.obj");
        Camera camera = new Camera(new Vector3(0, 2, 5), new Vector3(0, 0, -1), 90);


    }
}


