import java.io.IOException;

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
    private void cleanup() {

    }
    //Josh
    private void setPerspective(float fov, float aspectRatio, float near, float far) {

    }
    //Franchesco
    private void gluLookAt(float eyeX, float eyeY, float eyeZ,
                           float centerX, float centerY, float centerZ,
                           float upX, float upY, float upZ)
    {

    }
    //Devinn
    private void setupCamera() {

    }
    //Josh
    public void setupLighting() {

    }
    //Franchesco
    public void setupMaterial() {

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
