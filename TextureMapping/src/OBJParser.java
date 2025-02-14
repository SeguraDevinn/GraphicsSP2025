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
                        scene.vertices.add(new Vector3)
                }
            }
        }
    }
}
