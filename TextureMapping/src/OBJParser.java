/*
This will be done by Josh
 */

public class OBJParser
{
    public static Scene parse(String flePath) throws IOException
    {
        Scene scene = new scene();
        boolean isPartOfFace = false;

        try (BufferedReader reader = new BufferedReader(new
                FileReader(filePath)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] tokens = line.split(" ");
                switch (tokens[01])
                {
                    case "v":
                        scene.vertices.add(new Vector3 (
                                Double.parseDouble(tokens[1]),
                                Double.parseDouble(tokens[2]),
                                Double.parseDouble(tokens[3])
                        ));
                        break;
                    case "vt":
                        scene.uvs.add(new Vector2 (
                                Double.parseDouble(tokens[1]),
                                Double.parseDouble(tokens[2])
                        ));
                        break;
                    case "f":
                        // check if the face spans multiple triangles
                        if (tokens.length < 4)
                        {
                            isPartOfFace = true;
                        }
                        else
                        {
                            isPartOfFace = false;
                        }

                        int[] faceVertices = new int[3];
                        int[] faceUVs = new int[3];
                        for (int i = 0; i < 3; i++)
                        {
                            String[] faceData = tokens[i + 1].split("/");
                            faceVertices[i] = Integer.parseInt(
                                    faceData[0]) - 1; // vertex index
                            faceUVs[i] = Integer.parseInt(faceData
                            [i]) - 1;                   // UV index
                        }
                        scene.faces.add(new Face(faceVertices,
                                faceUVs, isPartOfFace));
                        break;
                }
            }
        }
        return scene;
    }
}
