import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FluidSimulation {
    public static void main(String[] args) {

    }

    public void run() {

    }
    private void init() {

    }

    private void loop() {

    }

    private void updatePanTilt() {

    }

    private void renderPan() {

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
        float tiltForcez = -(float) Math.sin(Math.toRadians(tiltX));

        vx += tiltForceX * 0.02f;
        vz += tiltForcez * 0.02f;

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


