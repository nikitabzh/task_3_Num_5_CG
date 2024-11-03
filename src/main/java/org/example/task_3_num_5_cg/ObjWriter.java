package org.example.task_3_num_5_cg;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class ObjWriter {

    public static void saveModel(Model model, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Запись вершин в формате с точками вместо запятых
            for (Vertex vertex : model.vertices) {
                writer.write(String.format(Locale.US, "v %.6f %.6f %.6f\n", vertex.x, vertex.y, vertex.z));
            }

            // Проверка и запись граней
            for (Face face : model.faces) {
                StringBuilder faceLine = new StringBuilder("f");
                for (int index : face.vertexIndices) {
                    // Проверка на корректность индекса, чтобы избежать ошибок
                    if (index >= 0 && index < model.vertices.size()) {
                        faceLine.append(" ").append(index + 1); // Индексация OBJ начинается с 1
                    } else {
                        System.err.println("Ошибка: некорректный индекс вершины " + index);
                    }
                }
                writer.write(faceLine.toString() + "\n");
            }
        }
    }
}