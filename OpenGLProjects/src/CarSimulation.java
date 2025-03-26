//  javac -classpath "lib/lwjgl-release-3.3.6-custom/*" src/CarSimulation.java
//  java -XstartOnFirstThread \-Djava.library.path="lib/lwjgl-release-3.3.6-custom" \-classpath "lib/lwjgl-release-3.3.6-custom/*:src" \CarSimulation

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

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
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("unable to initialize GLFW");
        }

        window = GLFW.glfwCreateWindow(width, height, "Car Simulation", 0,0 );
        if (window == 0) {
            throw new RuntimeException("Failed to create the GLFW");
        }

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        setPerspectiveProjection(45.0f, (float) 800 / 600, 0.1f, 100.0f);
        GL11.glMatrixMode(GL11.GL_COLOR_MATERIAL);
        GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);

        // Define light properties
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4).put(new float[] {0.0f, 10.0f, 10.0f, 1.0f});
        lightPosition.flip();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // Initalize the car and the terrain
        car = new Car();
        terrain = new Terrain("fractal_terrain.obj"); // Load the terrain from an OBJ file
    }

    private void loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glLoadIdentity();

            //update car movemenr based on user input
            updateCarMovement();

            //update camera to track the car
            terrain.render();
            car.update();
            car.render(terrain);

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    public void initLighting() {
        // Enable lighting and first light
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_EQUAL);

        // Set the light position
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4).put(new float[] {0.0f, 10.0f, 10.0f, 1.0f});
        lightPosition.flip();
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPosition);

        // set brighter, diffuse, and specular light
        FloatBuffer ambientLight = BufferUtils.createFloatBuffer(4).put(new float[] {0.4f, 0.4f, 0.4f, 1.0f});
        ambientLight.flip();
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_AMBIENT, ambientLight);

        // diffuse
        FloatBuffer diffuseLight = BufferUtils.createFloatBuffer(4).put(new float[] {1.0f, 1.0f, 1.0f, 1.0f});
        diffuseLight.flip();
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, diffuseLight);

        // specular
        FloatBuffer specularLight = BufferUtils.createFloatBuffer(4).put(new float[] {1.0f, 1.0f, 1.0f, 1.0f});
        specularLight.flip();
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_SPECULAR, specularLight);

        // enable color materials to allow vertex colors with lighting
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);

        // set material properties
        FloatBuffer materialAmbient = BufferUtils.createFloatBuffer(4).put(new float[] {0.6f, 0.6f, 0.6f, 1.0f});
        materialAmbient.flip();
        GL11.glMaterialfv(GL11.GL_FRONT, GL11.GL_AMBIENT, materialAmbient);

        // material diffuse
        FloatBuffer materialDiffuse = BufferUtils.createFloatBuffer(4).put(new float[] {0.8f, 0.8f, 0.8f, 1.0f});
        materialDiffuse.flip();
        GL11.glMaterialfv(GL11.GL_FRONT, GL11.GL_DIFFUSE, materialDiffuse);

        // material specular
        FloatBuffer materialSpecular = BufferUtils.createFloatBuffer(4).put(new float[] {1.0f, 1.0f, 1.0f, 1.0f});
        materialSpecular.flip();
        GL11.glMaterialfv(GL11.GL_FRONT, GL11.GL_SPECULAR, materialSpecular);

        GL11.glMaterialf(GL11.GL_FRONT,GL11.GL_SHININESS, 50.0f);

        // set global ambient light
        FloatBuffer globalAmbient = BufferUtils.createFloatBuffer(4).put(new float[] {0.5f, 0.5f, 0.5f, 1.0f});
        globalAmbient.flip();
        GL11.glLightModelfv(GL11.GL_LIGHT_MODEL_AMBIENT, globalAmbient);

    }

    private void setPerspectiveProjection(float fov, float aspect, float zNear, float zFar) {
        float ymax = (float) (zNear * Math.tan(Math.toRadians(fov / 2.0)));
        float xmax = ymax * aspect;
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glFrustum(-xmax, xmax, -ymax, ymax, zNear, zFar);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

    }

    private void setupCamera() {
        GL11.glTranslatef(0, -5, -20); //adjust this for better view
        GL11.glRotatef(20,1,0,0); // slight downward angle
    }

    private float lerp(float start, float end, float alpha) {
        return start + alpha * (end - start);
    }

    private float cameraX = 0;
    private float cameraY = 5;
    private float cameraZ = 10;

    private void updateCamera(Car car) {
        float cameraDistance = 10.0f; //distance behind the car
        float cameraHeight = 5.0f; // height above the car

        // calculate the desired camera position behinf and above the car
        float targetCameraX = car.getX() - (float) (Math.sin(Math.toRadians(car.getAngle())) * cameraDistance);
        float targetCameraZ = car.getZ() - (float) (Math.cos(Math.toRadians(car.getAngle())) * cameraDistance);
        float targetCameraY = car.getY() + cameraDistance;

        // smoothly interpolate between current camera position and target position
        float alpha = 0.1f;
        cameraX =lerp(cameraX, targetCameraX, alpha);
        cameraY =lerp(cameraY, targetCameraY, alpha);
        cameraZ =lerp(cameraZ, targetCameraZ, alpha);

        //reset the model-view matrix
        GL11.glLoadIdentity();

        //set the camera to look at the car
        gluLookAt(cameraX, cameraY, cameraZ, car.getX(), car.getY(), car.getZ(), 0.0f, 1.0f, 0.0f);
    }

    private void gluLookAt(float eyeX, float eyeY, float eyeZ,
                           float centerX, float centerY, float centerZ,
                           float upX, float upY, float upZ) {
        // step 1: Calculate the forward vector (the direction the camera is looking)
        float[] forward = {centerX - eyeX, centerY - eyeY, centerZ - eyeZ};
        normalize(forward);

        // step 2: define the up vector (Y-axis typically)
        float[] up = {upX, upY, upZ};

        // step 3: calculate the side (right) vector using cross product of forward and up
        float[] side = crossProduct(forward, up);
        normalize(side);

        // step 4: Recalculate the tune up vector (should be perpendicular to both side and forward
        up = crossProduct(side, forward);

        // step 5: create lookAt matrix
        FloatBuffer viewMatrix = BufferUtils.createFloatBuffer(16);

        viewMatrix.put(new float[] {
                side[0], up[0], -forward[0], 0,
                side[1], up[1], -forward[1], 0,
                side[2], up[2], -forward[2], 0,
                -dotProduct(side, new float[] {eyeX, eyeY, eyeZ}),
                -dotProduct(up, new float[] {eyeX, eyeY, eyeZ}),
                -dotProduct(forward, new float[] {eyeX, eyeY, eyeZ}),
        });
        viewMatrix.flip();

        // step 6: apply the view matrix
        GL11.glMultMatrixf(viewMatrix);
    }

    // Utility function for vector math
    private void normalize(float[] v) {
        float length = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);

        if (length != 0) {
            v[0] /= length;
            v[1] /= length;
            v[2] /= length;
        }
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
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
            car.accelerate();
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
            car.decelerate();
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
            car.turnLeft();
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
            car.turnRight();
        }
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
    public static Model loadModel(String fileName) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        List<Float> vertices = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<int[]> faces = new ArrayList<>();
        while ((line = reader.readLine()) != null)
        {
            String[] parts = line.split("\\s+");
            if (tokens[0].equals("v"))
            {
                float[] vertex = {Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])};
                vertices.add(vertex[0]);
            }
            else if (tokens[0].equals("vn"))
            {
                float[] normal = {Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])};
                normals.add(normal);
            }
            else if (tokens[0].equals("f"))
            {
                int[] face = {Integer.parseInt(tokens[1].split ("/")[0]) - 1, Integer.parseInt(tokens[2].split ("/")[0]) - 1, Integer.parseInt(tokens[3].split ("/")[0]) - 1};
                faces.add(face);
            }
        }

        float[] verticesArray = new float[vertices.size() * 3];
        float[] normalsArray = new float[normals.size() * 3];
        int[] indicesArray = new int[faces.size() * 3];

        int vertexIndex = 0;
        for (float[] vertex : vertices)
        {
            verticesArray[vertexIndex++] = vertex[0];
            verticesArray[vertexIndex++] = vertex[1];
            verticesArray[vertexIndex++] = vertex[2];
        }

        int normalIndex = 0;
        for (float[] normal : normals)
        {
            normalsArray[normalIndex++] = normal[0];
            normalsArray[normalIndex++] = normal[1];
            normalsArray[normalIndex++] = normal[2];
        }

        int faceIndex = 0;
        for (int[] face : faces)
        {
            indicesArray[faceIndex++] = face[0];
            indicesArray[faceIndex++] = face[1];
            indicesArray[faceIndex++] = face[2];
        }

        reader.close();
        return new Model(verticesArray, normalsArray, indicesArray);
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

