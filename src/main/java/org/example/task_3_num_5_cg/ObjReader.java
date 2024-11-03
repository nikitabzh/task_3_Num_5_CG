package org.example.task_3_num_5_cg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ObjReader {
    static Model loadModel(String filepath) throws IOException {
        Model model = new Model();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("v ")) {
                    String[] parts = line.split(" ");
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);
                    model.addVertex(new Vertex(x, y, z));
                } else if (line.startsWith("f ")) {
                    String[] parts = line.split(" ");
                    List<Integer> indices = new ArrayList<>();
                    for (int i = 1; i < parts.length; i++) {
                        indices.add(Integer.parseInt(parts[i].split("/")[0]) - 1);
                    }
                    model.addFace(new Face(indices));
                }
            }
        }
        return model;
    }
}
