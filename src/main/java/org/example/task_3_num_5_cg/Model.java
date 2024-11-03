package org.example.task_3_num_5_cg;

import java.util.ArrayList;
import java.util.List;

class Model {
    List<Vertex> vertices = new ArrayList<>();
    List<Face> faces = new ArrayList<>();

    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
    }

    public void addFace(Face face) {
        faces.add(face);
    }

    void deleteVertex(int vertexIndex) {
        vertices.remove(vertexIndex);
        // Remove faces that reference this vertex
        faces.removeIf(face -> face.vertexIndices.contains(vertexIndex));
        // Update indices in remaining faces
        for (Face face : faces) {
            face.vertexIndices.replaceAll(index -> index > vertexIndex ? index - 1 : index);
        }
    }
}
