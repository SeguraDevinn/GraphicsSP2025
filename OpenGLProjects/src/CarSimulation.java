//  javac -classpath "lib/lwjgl-release-3.3.6-custom/*" src/CarSimulation.java
//  java -XstartOnFirstThread \-Djava.library.path="lib/lwjgl-release-3.3.6-custom" \-classpath "lib/lwjgl-release-3.3.6-custom/*:src" \CarSimulation

import org.lwjgl.BufferUtils;
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
    private float x = 0, y = 0, z = 0; // car's position
    private float speed = 0; // current speed
    private float angle = 0; // direction the car is facing
    private float maxSpeed = 0.1f;
    private float acceleration = 0.01f;
    private float friction = 0.98f;
    private float turnSpeed = 2.0f; // speed of turning

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

    public void update()
    {
        // update position based on speed and angle
        x += speed * Math.sin(Math.toRadians(angle));
        z += speed * Math.cos(Math.toRadians(angle));

        // apply friction to slow down the car naturally
        speed *= friction;
    }

    public void render(Terrain terrain)
    {
        // get the heights of each wheel
        float frontLeftWheelY = terrain.getTerrainHeightAt(x - 0.9f, z + 1.5f);
        float frontRightWheelY = terrain.getTerrainHeightAt(x + 0.9f, z + 1.5f);
        float rearLeftWheelY = terrain.getTerrainHeightAt(x - 0.9f, z - 1.5f);
        float rearRightWheelY = terrain.getTerrainHeightAt(x + 0.9f, z - 1.5f);

        // calculate the average height of the car body (based on wheel heights)
        float averageHeight = (frontLeftWheelY + frontRightWheelY + rearLeftWheelY + rearRightWheelY) / 4.0f;

        // car body dimensions
        float carBodyHeight = 0.5f; // the height of the car body

        // adjust the height of the car body to be above the wheels
        // the car body is raised by half of its height so the bottom aligns with the wheels
        float carBodyOffset = 4.0f * carBodyHeight + carBodyHeight / 2.0f;

        // calculuate pitch (forward/backward tilt) and roll (side tilt)
        float pitch = (frontLeftWheelY + frontRightWheelY) / 2.0f - (rearLeftWheelY + rearRightWheelY) / 2.0f;
        float roll = (frontLeftWheelY - rearLeftWheelY) / 2.0f - (frontRightWheelY + rearRightWheelY) / 2.0f;

        // apply the calculated pitch, roll, and average height to the car body
        GL11.glPushMatrix();

        // Translate the car body to the average height plus the offset to position it above the wheels
        GL11.glTranslatef(x, averageHeight + carBodyOffset, z);

        // rotate the car for pitch (tilt forward/backward) and roll (tilt left/right)
        GL11.glRotatef(roll * 10.0f, 0, 0, 1); // roll around the z-axis
        GL11.glRotatef(pitch * 10.0f, 1, 0, 0); // pitch around x-axis

        // rotate the car in the direction its facing
        GL11.glRotatef(angle, 0, 1, 0);

        // render the car body
        renderCarBody(); // call the updated renderCarBody method

        // render the wheels
        renderWheels(terrain); // render the wheels based on terrain
    }

    private void renderCarBody()
    {
        GL11.glColor3f(1.0f, 0.2f, 0.2f ); // slightly lighter red for the car body
        GL11.glShadeModel(GL11.GL_SMOOTH); // smooth shading for Phong

        FloatBuffer carBodySpecular = BufferUtils.createFloatBuffer(4).put(new float[] {0.9f, 0.9f, 0.9f, 1.0f});
        carBodySpecular.flip();
        GL11.glMaterialfv(GL11.GL_FRONT, GL11.GL_SPECULAR, carBodySpecular);
        GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, 64.0f) // high shininness for car body

        float length = 4.0f;
        float width = 2.0f;
        float height = 0.5f;

        GL11. glBegin(GL11.GL_QUADS);

        // Front face
        GL11.glNormal3f(0,0,1);
        GL11.glVertex3f(-width / 2, -height / 2, length / 2);
        GL11.glVertex3f(width / 2, -height / 2, length / 2);
        GL11.glVertex3f(width / 2, height / 2, length / 2);
        GL11.glVertex3f(-width / 2, height / 2, length / 2);

        // back face (z = -length/2
        GL11.glVertex3f(-width / 2, -height / 2, -length / 2);
        GL11.glVertex3f(width / 2, -height / 2, -length / 2);
        GL11.glVertex3f(width / 2, height / 2, -length / 2);
        GL11.glVertex3f(-width / 2, height / 2, -length / 2);

        // left face (x = -width / 2)
        GL11.glVertex3f(-width / 2, -height / 2, -length / 2);
        GL11.glVertex3f(-width / 2, -height / 2, length / 2);
        GL11.glVertex3f(-width / 2, height / 2, length / 2);
        GL11.glVertex3f(-width / 2, height / 2, -length / 2);

        // right face (x = +width/2)
        GL11.glVertex3f(width / 2, -height / 2, -length / 2);
        GL11.glVertex3f(width / 2, -height / 2, length / 2);
        GL11.glVertex3f(width / 2, height / 2, length / 2);
        GL11.glVertex3f(width / 2, height / 2, -length / 2);

        // top face (y = +height/2)
        GL11.glVertex3f(-width / 2, height / 2, -length / 2);
        GL11.glVertex3f(width / 2, height / 2, -length / 2);
        GL11.glVertex3f(width / 2, -height / 2, length / 2);
        GL11.glVertex3f(-width / 2, -height / 2, length / 2);

        // bottom face (y = -height/2)
        GL11.glVertex3f(-width / 2, -height / 2, -length / 2);
        GL11.glVertex3f(width / 2, -height / 2, -length / 2);
        GL11.glVertex3f(width / 2, -height / 2, length / 2);
        GL11.glVertex3f(-width / 2, -height / 2, length / 2);

        GL11.glEnd();
    }

    private void renderWheel()
    {
        float radius = 0.4f;
        float width = 0.2f;
        int numSegments = 36;

        GL11.glColor3f(0.2f, 0.2f, 0.2f);  // dark gray for wheels
        GL11.glShadeModel(GL11.GL_SMOOTH);

        FloatBuffer wheelSpecular = BufferUtils.createFloatBuffer(4).put(new float[] {0.5f, 0.5f, 0.5f, 1.0f});
        wheelSpecular.flip();
        GL11.glMaterialfv(GL11.GL_FRONT, GL11.GL_SPECULAR, wheelSpecular);
        GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, 16.0f); // low shininess for wheels

        GL11.glPushMatrix();
        GL11.glRotatef(90, 0, 1,0);

        // front face (at z = -width/2)
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex3f(0.0f, 0.0f, -width / 2); // center of the circle
        for (int i = 0; i <= numSegments; i++)
        {
            double angle = 2 * Math.PI * i / numSegments;
            GL11.glVertex3f((float) Math.cos(angle) * radius,
                            (float) Math.sin(angle) * radius,
                            -width / 2);
        }
        GL11.glEnd();

        // rear face (at z = +width/2)
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex3f(0.0f, 0.0f, width / 2); // center of the circle
        for (int i = 0; i <= numSegments; i++)
        {
            double angle = 2 * Math.PI * i / numSegments;
            GL11.glVertex3f((float) Math.cos(angle) * radius,
                            (float) Math.sin(angle) * radius,
                            width / 2);
        }
        GL11.glEnd();

        GL11.glBegin(GL11.GL_QUAD_STRIP);
        for (int i = 0; i <= numSegments; i++)
        {
            double angle = 2 * Math.PI * i / numSegments;
            float x = (float) Math.cos(angle) * radius;
            float y = (float) Math.sin(angle) * radius;

            // set normals to make wheel sides visible
            GL11.glNormal3f(x, y, 0);
            GL11.glVertex3f(x, y, -width / 2);
            GL11.glVertex3f(x, y, width / 2);
        }
        GL11.glEnd();

        GL11.glPopMatrix();
    }

    private void renderWheels(Terrain terrain)
    {
        GL11.glColor3f(0.0f, 0.0f, 0.0f); // black color for wheels

        // define the wheel height offset
        float wheelHeightOffset = 0.8f; // 0.3f; // lower the wheels by this amount relative to the car body

        // front-left wheel
        GL11.glPushMatrix();
        float frontLeftWheelY = terrain.getTerrainHeightAt(this.getX() - 0.9f, this.getZ() + 1.5f);
        GL11.glTranslatef(-0.9f, frontLeftWheelY + 0.5f - wheelHeightOffset, 1.5f); // lower the wheel by the offset
        renderWheel(); // render the wheel
        GL11.glPopMatrix();

        // front-right wheel
        GL11.glPushMatrix();
        float frontRightWheelY = terrain.getTerrainHeightAt(this.getX() + 0.9f, this.getZ() + 1.5f);
        GL11.glTranslatef(0.9f, frontRightWheelY + 0.5f - wheelHeightOffset, 1.5f); // lower the wheel by the offset
        renderWheel();
        GL11.glPopMatrix();

        // rear-left wheel
        GL11.glPushMatrix();
        float rearLeftWheelY = terrain.getTerrainHeightAt(this.getX() -0.9f, this.getZ() - 1.5f);
        GL11.glTranslatef(-0.9f, rearLeftWheelY + 0.5f - wheelHeightOffset, -1.5f); // lower the wheel by the offset
        renderWheel();
        GL11.glPopMatrix();

        //rear-right wheel
        GL11.glPushMatrix();
        float rearRightWheelY = terrain.getTerrainHeightAt(this.getX() + 0.9f, this.getZ() - 1.5f);
        GL11.glTranslatef(0.9f, rearRightWheelY + 0.5f - wheelHeightOffset, -1.5f); // lower the wheel by the offset
        renderWheel();
        GL11.glPopMatrix();
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

