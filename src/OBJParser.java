import java.io.*;
import java.lang.*;

public class OBJParser {
    public static Scene parse(String filePath) throws IOException {
        Scene scene = new Scene();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                switch (tokens[0]) {
                    case "v":
                        scene.vertices.add(new Vector3(
                                Double.parseDouble(tokens[1]),
                                Double.parseDouble(tokens[2]),
                                Double.parseDouble(tokens[3])
                        ));
                        break;
                    case "f":
                        int[] faceVertices = new int[tokens.length - 1];
                        for (int i = 0; i < faceVertices.length; i++) {
                            faceVertices[i] = Integer.parseInt(tokens[i + 1].split("/")[0]) - 1;
                        }
                        scene.faces.add(new Face(faceVertices));
                        break;
                }
            }
        }
        return scene;
    }
}
