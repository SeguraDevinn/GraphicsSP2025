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

    private float panTiltX = 0;
    private float panTiltZ = 0;

    private float tiltSpeed = 0.5f;
    private float maxTilt = 30.0f;

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
        GL11.glClearColor(0.0f, 0.0f,1.0f,1.0f);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(-10,10,-10,10,0.01f,100.0f);
        GL11.glMatrixMode(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);

        float[] lightPosition = {0.0f, 5.0f, 10.0f, 1.0f};
        float[] lightAmbient = {0.2f, 0.2f, 0.2f, 1.0f};
        float[] lightDiffuse = {0.8f, 0.8f, 0.8f, 1.0f};

        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION,lightPosition);
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_AMBIENT,lightPosition);
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE,lightPosition);

        GL11.glShadeModel(GL11.GL_SMOOTH);

        water = new Water(500);
    }

    private void loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glLoadIdentity();

            // set camera perspective
            GL11.glTranslatef(0, -4, -16);
            GL11.glRotatef(45, 1, 0, 0);

            // update pan tilt
            updatePanTilt();

            // render pan and water
            renderPan();
            water.update(panTiltX, panTiltZ); // pass current pan tilt to the water simulation
            water.render();

            // swap buffers and poll events
            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }

    }

    private void updatePanTilt()
    {
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS)
        {
            panTiltZ = Math.max(panTiltZ + tiltSpeed, maxTilt); // // tilt left
        }
        else if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS)
        {
            panTiltZ = Math.min(panTiltZ - tiltSpeed, -maxTilt); // tilt right
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS)
        {
            panTiltX = Math.max(panTiltX - tiltSpeed, -maxTilt); // tilt forward
        }
        else if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS)
        {
            panTiltX = Math.min(panTiltX + tiltSpeed, maxTilt); // tilt backward
        }

        // decay tilt back to center over time
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

        // Draw the bottom of the cake pan (rectangle)
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glNormal3f(0, 1, 0); // Normal facing upwards
        GL11.glVertex3f(-4, 0, -4);
        GL11.glVertex3f(4, 0, -4);
        GL11.glVertex3f(4, 0, 4);
        GL11.glVertex3f(-4, 0, 4);
        GL11.glEnd();

        // Draw the walls of the cake pan (4 sides)
        GL11.glBegin(GL11.GL_QUADS);

        // Left side
        GL11.glNormal3f(-1, 0, 0); // Normal facing left
        GL11.glVertex3f(-4, 0, -4);
        GL11.glVertex3f(-4, 0, 4);
        GL11.glVertex3f(-4, 2, 4);
        GL11.glVertex3f(-4, 2, -4);

        GL11.glNormal3f(1, 0, 0); // Normal facing right
        GL11.glVertex3f(4, 0, -4);
        GL11.glVertex3f(4, 0, 4);
        GL11.glVertex3f(4, 2, 4);
        GL11.glVertex3f(4, 2, -4);
        // Front side
        GL11.glNormal3f(0, 0, 1); // Normal facing front
        GL11.glVertex3f(-4, 0, 4);
        GL11.glVertex3f(4, 0, 4);
        GL11.glVertex3f(4, 2, 4);
        GL11.glVertex3f(-4, 2, 4);
        // Back side i
        GL11.glNormal3f(0, 0, -1); // Normal facing back
        GL11.glVertex3f(-4, 0, -4);
        GL11.glVertex3f(4, 0, -4);
        GL11.glVertex3f(4, 2, -4);
        GL11.glVertex3f(-4, 2, -4);

        GL11.glEnd();
        GL11.glPopMatrix();


    }




}

class Particle {

    public float  x, y, z;
    public float vx = 0, vy = 0, vz = 0;
    private float gravity = -9.18f;
    private float damping = 0.85f;
    private float panBoundary = 4.0f;
    private float outOfBoundsThreshold;
    private Random random = new Random();

    private float repulsionRadius = 0.5f;
    private float repulsionStrength = 0.005f;

    public boolean isOutOfBounds = false;



    public Particle(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void applyRepulsion(Particle other) {
        float dx = other.x - this.x;
        float dy = other.y - this.y;
        float dz = other.z - this.z;
        float distanceSquared = dx * dx + dy * dy + dz * dz;

        if (distanceSquared < repulsionRadius * repulsionRadius && distanceSquared > 0.001f) {
            float distance = (float) Math.sqrt(distanceSquared);
            float force = repulsionStrength / distanceSquared;

            vx -= force * (dx / distance);
            vy -= force * (dy / distance);
            vz -= force * (dz / distance);
        }

    }

    public float cakePanPosition(float x, float z, float tX, float tZ) {
        float ret = 0.0f;
        float deg2rad = 0.0174533f;
        float bottom = -3.0f;

        float normalx = (float) (Math.sin(tZ * deg2rad));
        float normaly = (float) (Math.cos(tZ * deg2rad) * Math.cos(tX * deg2rad));
        float normalz = (float) (Math.cos(tZ * deg2rad) * Math.sin(tX * deg2rad));

        ret = (x * normalx - z * normalz)/normaly;

        if (ret < bottom) ret = bottom;

        return ret;
    }

    public void update(float tiltX, float tiltZ) {

        vy += gravity * 0.01f;

        float tiltForceX = -(float) Math.sin(Math.toRadians(tiltZ));
        float tiltForceZ = (float) Math.sin(Math.toRadians(tiltX));

        vx += tiltForceX * 0.02f;
        vz += tiltForceZ * 0.02f;

        x *= vx * 0.01f;
        y *= vy * 0.01f;
        z *= vz * 0.01f;

        y = cakePanPosition(x, z, tiltX, tiltZ);

        vx *= 0.99f;
        vz *= 0.99f;

        if (Math.abs(x) > panBoundary || Math.abs(z) > panBoundary) {
            if (Math.abs(x) > panBoundary) {
                vx = -vx * damping + (random.nextFloat() - 0.5f) * 0.05f;
                x = Math.signum(x) * panBoundary;
            }
            if (Math.abs(z) > panBoundary) {
                vz = -vz * damping + (random.nextFloat() - 0.5f) * 0.05f;
                z = Math.signum(z) * panBoundary;
            }
        }

        float bottom = -3.0f;

        if (Math.abs(x) > outOfBoundsThreshold || Math.abs(z) > outOfBoundsThreshold || y < bottom) {
            isOutOfBounds = true;
        }

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
        for (int i = 0; i < particles.size();i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                particles.get(i).applyRepulsion(particles.get(j));
                particles.get(j).applyRepulsion(particles.get(i));
            }
        }

        for (int i = particles.size() - 1; i >= 0;i--) {
            Particle particle = particles.get(i);
            particle.update(tiltX, tiltZ);

            if (particle.isOutOfBounds) {
                particles.remove(i);
            }
        }
    }

    public void render() {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPointSize(5.0f);
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glColor3f(0.0f, 0.0f, 1.0f);
        for (Particle particle : particles) {
            GL11.glVertex3f(particle.x, particle.y, particle.z);
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_LIGHTING);
    }
}


