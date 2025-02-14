public class Face
{
    int[] vertices;     // indices of the vertices
    int[] uvs;      // indices of the corresponding UV coordinates
    boolean isPartOfFace;   // marks whether this triangle is part of a larger face

    Face(int[] vertices, int[] uvs, boolean isPartOfFace)
    {
        this.vertices = vertices;
        this.uvs = uvs;
        this.isPartOfFace = isPartOfFace;
    }
}
