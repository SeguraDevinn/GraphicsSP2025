public class Face
{
    int[] vertices;  // Indices of the vertices
    int[] uvs;       // Indices of the corresponding UV coordinates
    boolean isPartOfFace;  // Marks whether this triangle is part of a larger face

    Face(int[] vertices, int[] uvs, boolean isPartOfFace)
    {
        this.vertices = vertices;
        this.uvs = uvs;
        this.isPartOfFace = isPartOfFace;
    }
}