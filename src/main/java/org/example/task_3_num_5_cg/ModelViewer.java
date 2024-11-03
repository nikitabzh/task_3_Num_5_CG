package org.example.task_3_num_5_cg;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class ModelViewer extends Application {

    private final Model model = new Model();
    private final Canvas canvas = new Canvas(800, 600);

    private double mouseX, mouseY;
    private double angleX = 0, angleY = 0;
    private double scale = 1.0;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        Group group = new Group();
        group.getChildren().add(canvas);

        SubScene scene3D = new SubScene(group, 800, 600);
        scene3D.setFill(Color.LIGHTGRAY);
        root.setCenter(scene3D);

        // Создаем кнопки
        Button loadButton = new Button("Load OBJ");
        Button deleteButton = new Button("Delete Vertex");
        Button saveButton = new Button("Save OBJ");

        loadButton.setOnAction(e -> loadModelFromFile(stage));
        deleteButton.setOnAction(e -> deleteVertex());
        saveButton.setOnAction(e -> saveModel(stage));

        // Размещаем кнопки в VBox и устанавливаем в левую часть BorderPane
        VBox buttonBox = new VBox(10, loadButton, deleteButton, saveButton); // 10 - отступ между кнопками
        root.setLeft(buttonBox);

        // Добавляем обработчики событий для вращения и масштабирования
        scene3D.setOnMousePressed(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });

        scene3D.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - mouseX;
            double deltaY = event.getSceneY() - mouseY;
            angleX += deltaY * 0.5;
            angleY += deltaX * 0.5;
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
            renderModel();
        });

        scene3D.setOnScroll(event -> {
            double delta = event.getDeltaY();
            scale += delta > 0 ? 0.1 : -0.1;
            if (scale < 0.1) scale = 0.1;
            renderModel();
        });

        stage.setScene(new Scene(root));
        stage.setTitle("3D Model Viewer");
        stage.show();
    }

    private void loadModel(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Пропускаем пустые строки и строки-комментарии, начинающиеся с #
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Обработка строк для вершин
                if (line.startsWith("v ")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length == 4) {
                        try {
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);
                            double z = Double.parseDouble(parts[3]);
                            model.addVertex(new Vertex(x, y, z));
                        } catch (NumberFormatException e) {
                            System.out.println("Ошибка парсинга вершины: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Ошибка парсинга вершины: неверное количество координат в строке - " + line);
                    }
                }

                // Обработка строк для текстурных координат
                else if (line.startsWith("vt ")) {
                    // Можно пропустить или обработать, если нужно
                    continue; // Игнорируем текстурные координаты в данном случае
                }

                // Обработка строк для граней
                else if (line.startsWith("f ")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 4) {
                        Face face = new Face(new ArrayList<>());
                        for (int i = 1; i < parts.length; i++) {
                            try {
                                String[] indices = parts[i].split("/");
                                int vertexIndex = Integer.parseInt(indices[0]) - 1;
                                face.vertexIndices.add(vertexIndex);
                            } catch (NumberFormatException e) {
                                System.out.println("Ошибка парсинга индекса вершины: " + e.getMessage() + " в строке: " + line);
                            }
                        }
                        model.addFace(face);
                    } else {
                        System.out.println("Ошибка парсинга грани: недостаточно элементов в строке - " + line);
                    }
                }

                // Другие строки, такие как группы объектов (например, "g")
                else if (line.startsWith("g ")) {
                    // Можно пропустить или использовать для разделения объектов
                    continue;
                }
            }
            renderModel();
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }


    private void loadModelFromFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open OBJ File");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            loadModel(file.getAbsolutePath());
        }
    }

    private void saveModel(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save OBJ File");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                // Перед сохранением модели убедимся, что `model` соответствует текущему состоянию
                ObjWriter.saveModel(model, file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Ошибка при сохранении файла: " + e.getMessage());
            }
        }
    }


    private void deleteVertex() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Vertex");
        dialog.setHeaderText("Enter the vertex index to delete:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(vertexIndexStr -> {
            try {
                int vertexIndex = Integer.parseInt(vertexIndexStr) - 1;
                if (vertexIndex >= 0 && vertexIndex < model.vertices.size()) {
                    model.deleteVertex(vertexIndex);
                    renderModel();
                } else {
                    System.out.println("Invalid vertex index.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        });
    }

    private void renderModel() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);

        double cosX = Math.cos(Math.toRadians(angleX));
        double sinX = Math.sin(Math.toRadians(angleX));
        double cosY = Math.cos(Math.toRadians(angleY));
        double sinY = Math.sin(Math.toRadians(angleY));

        for (Face face : model.faces) {
            if (face.vertexIndices.size() >= 2) {
                for (int i = 0; i < face.vertexIndices.size(); i++) {
                    Vertex v1 = rotateAndScaleVertex(model.vertices.get(face.vertexIndices.get(i)), cosX, sinX, cosY, sinY);
                    Vertex v2 = rotateAndScaleVertex(model.vertices.get(face.vertexIndices.get((i + 1) % face.vertexIndices.size())), cosX, sinX, cosY, sinY);

                    gc.strokeLine(
                        v1.x + canvas.getWidth() / 2,
                        v1.y + canvas.getHeight() / 2,
                        v2.x + canvas.getWidth() / 2,
                        v2.y + canvas.getHeight() / 2
                    );
                }
            }
        }

        for (int i = 0; i < model.vertices.size(); i++) {
            Vertex v = rotateAndScaleVertex(model.vertices.get(i), cosX, sinX, cosY, sinY);
            gc.fillText(String.valueOf(i + 1), v.x + canvas.getWidth() / 2, v.y + canvas.getHeight() / 2);
        }
    }

    private Vertex rotateAndScaleVertex(Vertex v, double cosX, double sinX, double cosY, double sinY) {
        double y = v.y * cosX - v.z * sinX;
        double z = v.y * sinX + v.z * cosX;
        double x = v.x * cosY + z * sinY;
        z = -v.x * sinY + z * cosY;
        return new Vertex(x * scale * 100, -y * scale * 100, z * scale * 100);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
