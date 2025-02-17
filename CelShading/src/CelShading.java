import java.io.IOException;

public class CelShading {
    //Devinn
    public void run() {

    }
    //Josh
    private void init() {

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
