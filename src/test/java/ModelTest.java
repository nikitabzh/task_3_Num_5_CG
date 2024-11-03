package org.example.task_3_num_5_cg;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

class ModelTest {

    @Test
    void testDeleteVertex() {
        Model model = new Model();
        model.addVertex(new Vertex(1, 0, 0));
        model.addVertex(new Vertex(0, 1, 0));
        model.addVertex(new Vertex(0, 0, 1));

        model.addFace(new Face(List.of(0, 1, 2)));
        model.deleteVertex(1);

        assertEquals(2, model.vertices.size());
        assertEquals(0, model.faces.size());
    }
}
