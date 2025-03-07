// javac -classpath "lib/lwjgl-release-3.3.6-custom/*" src/FluidSimulation.java
// java -XstartOnFirstThread \-Djava.library.path="lib/lwjgl-release-3.3.6-custom" \-classpath "lib/lwjgl-release-3.3.6-custom/*:src" \FluidSimulation

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FluidSimulation {

    private long window;
    private int width = 800;
    private int height = 600;

    private float panTiltX = 0; // forward and backward tilt
    private float panTiltZ = 0; // left and right tilt

    private float tiltSpeed = 0.5f;
    private float maxTilt = 30.0f; // maximum tilt angle

    private Water water;
    public static void main(String[] args) {
        new FluidSimulation().run();
    }

    public void run() {
        init();
        loop();
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    private void init() {
        GLFW.glfwInit();
        window = GLFW.glfwCreateWindow(width, height, "Cake Pan With Water Simulation", 0, 0);
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        // Set clear color to white
        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // RGBA, where each component is between 0.0 and 1.0

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(-10, 10, -10, 10, 0.01f, 100.0f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        // Enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);

        float[] lightPosition = {0.0f, 5.0f, 10.0f, 1.0f};
        float[] lightAmbient = {0.2f, 0.2f, 0.2f, 1.0f};
        float[] lightDiffuse = {0.8f, 0.8f, 0.8f, 1.0f};

        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPosition);
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_AMBIENT, lightAmbient);
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, lightDiffuse);

        GL11.glShadeModel(GL11.GL_SMOOTH);

        water = new Water(500); // Adjust this number if needed
    }


    private void loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glLoadIdentity();

            // Set camera perspective
            GL11.glTranslatef(0, -4, -16);
            GL11.glRotatef(45, 1, 0, 0);

            // Update pan tilt
            updatePanTilt();

            // Render pan and water
            renderPan();
            water.update(panTiltX, panTiltZ); // Pass current pan tilt to the water simulation
            water.render();

            // Swap buffers and poll events
            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    private void updatePanTilt() {
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
            panTiltZ = Math.max(panTiltZ + tiltSpeed, -maxTilt);
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
            panTiltZ = Math.min(panTiltZ - tiltSpeed, maxTilt);
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
            panTiltX = Math.max(panTiltX - tiltSpeed, -maxTilt);
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
            panTiltX = Math.min(panTiltX + tiltSpeed, maxTilt);
        }

        // Decay tilt back to center over time
        panTiltX *= 0.98f;
        panTiltZ *= 0.98f;
    }

    private void renderPan() {
        GL11.glPushMatrix();
        GL11.glRotatef(panTiltX, 1, 0, 0);
        GL11.glRotatef(panTiltZ, 0, 0, 1);

        // Set material properties for the pan
        float[] materialAmbient = {0.3f, 0.3f, 0.3f, 1.0f};
        float[] materialDiffuse = {0.6f, 0.6f, 0.6f, 1.0f};
        GL11.glMaterialfv(GL11.GL_FRONT, GL11.GL_AMBIENT, materialAmbient);
        GL11.glMaterialfv(GL11.GL_FRONT, GL11.GL_DIFFUSE, materialDiffuse);

        // Draw the bottom and walls of the pan
        GL11.glBegin(GL11.GL_QUADS);
        // Draw the bottom
        GL11.glNormal3f(0, 1, 0);
        GL11.glVertex3f(-4, 0, -4);
        GL11.glVertex3f(4, 0, -4);
        GL11.glVertex3f(4, 0, 4);
        GL11.glVertex3f(-4, 0, 4);
        // Draw the sides
        drawSides();
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    private void drawSides() {
        // Left side
        GL11.glNormal3f(-1, 0, 0);
        GL11.glVertex3f(-4, 0, -4);
        GL11.glVertex3f(-4, 0, 4);
        GL11.glVertex3f(-4, 2, 4);
        GL11.glVertex3f(-4, 2, -4);
        // Right side
        GL11.glNormal3f(1, 0, 0);
        GL11.glVertex3f(4, 0, -4);
        GL11.glVertex3f(4, 0, 4);
        GL11.glVertex3f(4, 2, 4);
        GL11.glVertex3f(4, 2, -4);
        // Front side
        GL11.glNormal3f(0, 0, 1);
        GL11.glVertex3f(-4, 0, 4);
        GL11.glVertex3f(4, 0, 4);
        GL11.glVertex3f(4, 2, 4);
        GL11.glVertex3f(-4, 2, 4);
        // Back side
        GL11.glNormal3f(0, 0, -1);
        GL11.glVertex3f(-4, 0, -4);
        GL11.glVertex3f(4, 0, -4);
        GL11.glVertex3f(4, 2, -4);
        GL11.glVertex3f(-4, 2, -4);
    }
}

class Particle {
    public float x, y, z;
    public float vx = 0, vy = 0, vz = 0;
    private float gravity = -9.18f;
    private float damping = 0.85f;
    private float panBoundary = 4.0f;
    private Random random = new Random();
    private float movementMultiplier = 1.5f; // To make movements faster as requested

    public Particle(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        resetVelocity();
    }

    public void update(float tiltX, float tiltZ) {
        vy += gravity * 0.01f;
        float tiltForceX = -(float) Math.sin(Math.toRadians(tiltZ)) * movementMultiplier;
        float tiltForceZ = (float) Math.sin(Math.toRadians(tiltX)) * movementMultiplier;
        vx += tiltForceX * 0.02f;
        vz += tiltForceZ * 0.02f;
        x += vx * 0.01f;
        y += vy * 0.01f;
        z += vz * 0.01f;
        checkBoundaries();
    }

    private void checkBoundaries() {
        if (Math.abs(x) > panBoundary || Math.abs(z) > panBoundary || y < -3.0f) {
            resetParticle();
        }
    }

    private void resetParticle() {
        x = (random.nextFloat() * 2 * panBoundary) - panBoundary;
        y = 0;
        z = (random.nextFloat() * 2 * panBoundary) - panBoundary;
        resetVelocity();
    }

    private void resetVelocity() {
        vx = (random.nextFloat() - 0.5f) * 0.1f;
        vz = (random.nextFloat() - 0.5f) * 0.1f;
    }
}

class Water {
    private List<Particle> particles;
    private Random rand = new Random();

    public Water(int count) {
        particles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            particles.add(new Particle(rand.nextFloat() * 6 - 3, 0, rand.nextFloat() * 6 - 3));
        }
    }

    public void update(float tiltX, float tiltZ) {
        for (Particle particle : particles) {
            particle.update(tiltX, tiltZ);
        }
    }

    public void render() {
        GL11.glDisable(GL11.GL_LIGHTING); // Disable lighting to make the particles more visible
        GL11.glEnable(GL11.GL_BLEND); // Enable blending to support transparency
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // Set blending function for semi-transparent particles

        GL11.glPointSize(10.0f); // Set the point size to 10.0f, which increases the particle size

        GL11.glBegin(GL11.GL_POINTS); // Begin drawing points
        GL11.glColor4f(0.0f, 0.0f, 1.0f, 0.5f); // Set color to blue with half transparency
        for (Particle particle : particles) {
            GL11.glVertex3f(particle.x, particle.y, particle.z); // Draw each particle at its coordinates
        }
        GL11.glEnd(); // End drawing points

        GL11.glDisable(GL11.GL_BLEND); // Disable blending after drawing particles
        GL11.glEnable(GL11.GL_LIGHTING); // Re-enable lighting for other scene elements
    }
}



