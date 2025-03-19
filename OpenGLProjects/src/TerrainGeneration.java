import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TerrainGeneration
{
    private static final int SIZE = 256; // grid size
    private static final float SCALE = 50f; // controls steepness
    private static final float ROUGHNESS = 0.5f; // determines fractal variation

    public static void main(String[] args)
    {
        float[][] heightMap = generateHeightMap(SIZE, SCALE, ROUGHNESS);
        try
        {
            saveToObjFile("fractal_terrain.obj", heightMap);
            System.out.println("Terrain saved as fractal_terrain.obj");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // generate a fractal heightmap using Simplex noise
    private static float [][] generateHeightMap(int size, float scale, float roughness)
    {
        float[][] heightMap = new float[size][size];
        Random random = new Random();
        SimplexNoise noise = new SimplexNoise();

        for (int x = 0; x < size; x++)
        {
            for (int z = 0; z < size; z++)
            {
                float nx = x / (float) size - 0.5f;
                float nz = z / (float) size - 0.5f;
                heightMap[x][z] = noise.noise(nx * scale, nz * scale) * roughness;
            }
        }
        return heightMap;
    }

    private static float[][][] calculateNormals(float[][] heightMap)
    {
        int size = heightMap.length;
        float[][][] normals = new float[size][size][3];

        for (int x = 0; x < size; x++)
        {
            for (int z =  0; z < size; z++)
            {
                // compute vectors using neighboring heights
                float[] normal = new float[3];

                // get height differences using surrounding points (with boundary checks)
                float heightL = (x > 0) ? heightMap[x - 1][z] : heightMap[x][z];
                float heightR = (x < size - 1) ? heightMap[x +1][z] : heightMap[x][z];
                float heightD = (z > 0) ? heightMap[x][z -1] : heightMap[x][z];
                float heightU = (z < size - 1) ? heightMap[x][z + 1] : heightMap[x][z];

                // calculate gradient vectors
                float[] vector1 = {1.0f, heightR - heightL, 0.0f};
                    // vector in the x direction
                float[] vector2 = {0.0f, heightU - heightD, 1.0f};
                    // vector in the z direction

                // compute cross product to get the normal
                normal[0] = vector1[1] * vector2[2] - vector1[2] * vector2[1]; // x component
                normal[1] = vector1[2] * vector2[0] - vector1[0] * vector2[2]; // y component
                normal[2] = vector1[0] * vector2[1] - vector1[1] * vector2[0]; // z component

                // normalize the normal
                float length = (float) Math.sqrt(normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2]);
                if (length != 0)
                {
                    normal[0] /= length;
                    normal[1] /= length;
                    normal[2] /= length;
                }

                // store the normal
                normals[x][z] = normal;
            }
        }
        return normals;
    }

    //saves the generated heightmap into Wavefront .obj file with normals
    private static void saveToObjFile(String fileName, float[][] heightMap) throws IOException {
        int size = heightMap.length;
        float[][][] normals = calculateNormals(heightMap);


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
            //writing vertices
            for (int z = 0; z < size; z++) {
                for (int x = 0; x < size; x++) {
                    writer.write("v " + x + " " + heightMap[x][z] + " " + z + "\n");
                }
            }

            //writing normals
            for (int z = 0; z < size;z++) {
                for (int x = 0; x < size; x++) {
                    float[] normal = normals[x][z];
                    writer.write("vn " + normal[0] + " " + normal[1] + " " + normal[2] + "\n");
                }
            }

            // write faces (triangle)
            for (int z = 0; z < size; z++) {
                for (int x = 0; x < size - 1; x++) {
                    int topLeft = (z * size) + x + 1;
                    int topRight = topLeft + 1;
                    int bottomLeft = topLeft + size;
                    int bottomRight = bottomLeft + 1;

                    // writing faces with normals
                    writer.write("f " + topLeft + "//" + topLeft +
                            " " + bottomLeft + "//" + bottomLeft +
                            " " + topRight + "//" + topRight + " " + "\n");
                    writer.write("f " + topRight + "//" + topRight +
                            " " + bottomLeft + "//" + bottomLeft +
                            " " + bottomRight + "//" + bottomRight + "\n");
                }
            }
        }
    }


}


