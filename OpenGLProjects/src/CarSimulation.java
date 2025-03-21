//  javac -classpath "lib/lwjgl-release-3.3.6-custom/*" src/CarSimulation.java
//  java -XstartOnFirstThread \-Djava.library.path="lib/lwjgl-release-3.3.6-custom" \-classpath "lib/lwjgl-release-3.3.6-custom/*:src" \CarSimulation

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org .lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class CarSimulation {
    private long window;
    private int width = 800;
    private int height = 600;
    private Car car;
    private Terrain terrain;

    public static void main(String[] args) {
        new CarSimulation().run();
    }

    public void run() {
        init();
        loop();
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }
    private void init() {

    }

    private void loop() {

    }

    public void initLighting() {

    }

    private void setPerspectiveProjection(float fov, float aspect, float zNear, float zFar) {

    }

    private void setupCamera() {

    }

    private float lerp(float start, float end, float alpha) {
        return start + alpha * (end - start);
    }

    private float cameraX = 0;
    private float cameraY = 5;
    private float cameraZ = 10;

    private void updateCamera(Car car) {

    }

    private void normalize(float[] v) {

    }

    private float[] crossProduct(float[] a, float[] b) {
        return new float[] {
          a[1] * b[2] - a[2] * b[1],
          a[2] * b[0] - a[0] * b[2],
          a[0] * b[1] - a[1] * b[0]
        };
    }

    private float dotProduct(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    private void updateCarMovement() {

    }
}

class Car {
    private float x = 0, y = 0, z = 0;
    private float speed = 0;
    private float angle = 0;
    private float maxSpeed = 0.1f;
    private float acceleration = 0.01f;
    private float friction = 0.98f;
    private float turnSpeed = 2.0f;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getAngle() {
        return angle;
    }

    public void accelerate() {
        if (speed < maxSpeed) {
            speed += acceleration;
        }
    }

    public void decelerate() {
        if (speed > -maxSpeed) {
            speed -= acceleration;
        }
    }

    public void turnLeft() {
        angle += turnSpeed;
    }

    public void turnRight() {
        angle -= turnSpeed;
    }

    public void update() {

    }

    public void render(Terrain terrain) {

    }

    private void renderCarBody() {

    }

    private void renderWheel() {

    }

    private void renderWheels(Terrain terrain) {

    }

}

class OBJLoader {
    public static Model loadModel(String fileName) throws IOException {

    }


}

class Model {
    private float[] vertices;
    private float[] normals;
    private int[] indices;
}

class Terrain {
    private Model model;

    public Terrain(String objFilePath) {

    }

    public void render() {

    }

    public float getTerrainHeight(float x, float z) {
        return 0.0f;
    }

    private boolean isPointInTriangle(float px, float pz, float v1X, float v1Z,
                                      float v2X, float v2z,
                                      float v3X, float v3z) {

    }

    private float sign(float px, float pz,
                       float v1X, float v1Z,
                       float v2X, float v2Z) {
        return (px - v2X) * (v1Z - v2Z) - (v1X - v2X) * (pz - v2Z);
    }

    private float interpolateHeight(float x, float z,
                                    float v1X, float v1Y, float v1z,
                                    float v2X, float v2Y, float v2Z,
                                    float v3X, float v3Y, float v3Z) {

    }

    private float triangleArea(float x1, float z1,
                               float x2, float z2,
                               float x3, float z3) {

    }
}

