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
    private void init() {

    }
    //Franchesco
    private void loop() {

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
