import java.io.IOException;
import java.nio.FloatBuffer;

import static sun.rmi.transport.TransportConstants.Version;

public class CelShading {

    private long window;
    private Model model;
    //Devinn
    public void run() {
        System.out.println("Starting LWJGL " + Version + "!");
        try {
            init();

            if (model == null) {
                throw new RuntimeException("Model could not be loaded. Exiting...");

            }
            loop();

            cleanup();
        } finally {
            GLFWErrorCallback callback = glfwSetErrorCallback(null);

            if (callback != null) {
                callback.free();
            }
            glfwTerminate();
        }

    }
    //Josh
    private void init()
    {
        // Load the OBJ model
        try
        {
            model = loadModel("sphere.obj");
            if (model == null)
            {
                throw new RuntimeException("Failed to load the model.");
            }
            else
            {
                System.out.println("Model loaded Successfully");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Error loading model", e);
        }

        // Initialize GLFW
        if (!glfwInit())
        {
            throw new IllegalStateException("Unable to initliaze GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(800, 600, "Cel Shading with LWJGL", NULL, NULL);
        if (window == NULL)
        {
            throw new RuntimeException("Failed to creare the GLFW window");
        }
        else
        {
            System.out.println("GLFW window created successfully.");
        }

        // Center the window on the screen
        GLFWVidMode vidmode = glfwGetVideoMode (glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - 800) / 2, (vidmode.height() - 600) / 2);

        // Make the openGL context current
        glfwMakeContextCurrent(window);

        // Enable V-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    //Franchesco
    private void loop() {
        // This line is critical for LWJIGL’s interoperation with GLFW’s OpenGL context, or any context that is
        // managed externally. LWJGL detects the context that is current in the current thread, creates GLCapabilities
        // instance and makes the OpenGL bindings available for use.
        GL.createCapabilities();

        // Set up OpenGL settings, e.g., lighting, shading
        setupLighting();
        setupMaterial();

        // Set clear color
        glClearColor (0.0f, 1.0f, 1.0f, 0.0f);

        // Run the rendering loop until the user has attempted to Close the window or pressed the ESC key
        while (!glfwWindowShouldClose(window)) {
            glClear
        }

    }
    //Devinn
    private void cleanup()
    {
        // free the key callback if it was set
        if (glfwSetKeyCallback(window, null) != null)
    {
        glfwSetKeyCallback(window, null).free();
    }

    // destroy the window if it exists
    if (window != NULL)
    {
        glfwDestroyWindow(window);
    }
    }
    //Josh
    private void setPerspective(float fov, float aspectRatio, float near, float far)
    {
        float fH = (float) Math.tan(Math.toRadians(fov / 2)) *
                near;
        float fW = fH * aspectRatio;

        glFrustum(-fW, fW, -fH, fH, near, far);
    }

    //Franchesco
    private void gluLookAt(float eyeX, float eyeY, float eyeZ,
                           float centerX, float centerY, float centerZ,
                           float upX, float upY, float upZ)
    {
        FloatBuffer matrix = BufferUtils.createFLoatBuffer(16);
        // calculate forward vector
        float fx = centerX - eyeX;
        float fy = centerY - eyeY;
        float fz = centerZ - eyeZ;
        float rlf = 1.0f / (float) Math.sqrt(fx * fx + fy * fy + fz * fz);
        fx *= rlf;
        fy *= rlf;
        fz *= rlf;

        //calculate up vector
        float rlsu = 1.0f / (float) Math.sqrt(upX * upX + upY * upY + upZ * upZ);
        float sx = upY * fz - upZ * fy;
        float sy = upZ * fx - upX * fz;
        float sz = upX * fy - upY * fx;

        // Recompute up vector
        float ux = fy * sz - fz * sy;
        float uy = fz * sx - fx * sz;
        float uz = fx * sy - fy * sx;

        matrix.put(new float[] {
                sx, ux, -fx, 0.0f,
                sy, uy, -fy, 0.0f,
                sz, uz, -fz, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        });
        matrix.flip();

        glMultMatrixf(matrix);   // apply the matrix

        glTranslatef(-eyeX, -eyeY, -eyeZ);  // translate to camera position
    }
    //Devinn
    private void setupCamera()
    {
        // Set up projection matrix
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        // Create a perspective projection
        // Field of view: 45 degrees, aspect ratio: window width / height, near: 0.1, far: 100
        float aspectRatio = 800.0f / 600.0f;
        setPerspective(45.0f, aspectRatio, 0.1f, 100.0f);

        // set up modelview matrix (camera/view transformation)
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        // move the camera back a bit so we can see the object
        gluLookAt(0.0f, 0.0f, 10.0f, // eye/camera transformation
                0.0f, 0.0f, 0.0f, // center of the scene (what were looking at)
                0.0f, 1.0f, 0.0f); // Up vector
    }

    //Josh
    public void setupLighting()
    {
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);

        // Configure OpenGL to use the vertex colors for lighting calculations
        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);

        // Set light parameters
        FloatBuffer ambientLight = BufferUtils.createFloatBuffer(4).put(new float[] {0.2f, 0.2f, 0.2f, 1.0f});
        ambientLight.flip();
        glLightfv(GL_LIGHT0, GL_AMBIENT, ambientLight);

        FloatBuffer diffuseLight = BufferUtils.createFloatBuffer(4).put(new float[] {0.9f, 0.9f, 0.9f, 1.0f});
        diffuseLight.flip();
        glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuseLight);

        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4).put(new float[] {10.0f, 15.0f, 10.0f, 1.0f});
        lightPosition.flip();
        glLightfv(GL_LIGHT0, GL_POSITION, lightPosition);

        FloatBuffer noSpecular = BufferUtils.createFloatBuffer(4).put(new float[] {0.0f, 0.0f, 0.0f, 1.0f});
        noSpecular.flip();
        glLightfv(GL_LIGHT0, GL_SPECULAR, noSpecular);

        glShadeModel(GL_FLAT);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
    }

    //Franchesco
    public void setupMaterial()
    {
        // define material properties
        FloatBuffer matAmbient = BufferUtils.createFloatBuffer(4).put(new float[] {0.2f, 0.2f, 0.2f, 1.0f});
        FloatBuffer matDiffuse = BufferUtils.
    }
    //Devinn
    public void renderModel(Model model) {

    }
    //Josh
    private float[] calculateFaceNormal(Model model, int[] face) {

    }
    //Franchesco
    private float calculateDiffuseLight(float[] normal, float[] lightDir) {

    }
    //Devinn
    private float quantizeLight(float intensity, int levels) {

    }
    //Josh
    public void renderOutline(Model model) {

    }
    //Franchesco
    public static void main(String[] args) {
        new CelShading().run();
    }

    /*
    This is the start of the classes and its functions
     */
    //Devinn
    public static class Model{

    }
    //Josh
    public static Model loadModel(String filename) throws IOException {

    }

}
