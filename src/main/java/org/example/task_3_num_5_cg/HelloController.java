package org.example.task_3_num_5_cg;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    private Model model;

    @FXML
    protected void onLoadModelClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open OBJ File");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try {
                model = ObjReader.loadModel(file.getAbsolutePath());
                welcomeText.setText("Model loaded successfully!");
            } catch (IOException e) {
                welcomeText.setText("Failed to load model.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void onDeleteVertexClick() {
        if (model != null) {
            int vertexIndex = 0; // Получите индекс вершины от пользователя
            model.deleteVertex(vertexIndex);
            welcomeText.setText("Vertex deleted successfully!");
        } else {
            welcomeText.setText("No model loaded.");
        }
    }

    @FXML
    protected void onSaveModelClick() {
        if (model != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save OBJ File");
            File file = fileChooser.showSaveDialog(new Stage());
            if (file != null) {
                try {
                    ObjWriter.saveModel(model, file.getAbsolutePath());
                    welcomeText.setText("Model saved successfully!");
                } catch (IOException e) {
                    welcomeText.setText("Failed to save model.");
                    e.printStackTrace();
                }
            }
        } else {
            welcomeText.setText("No model loaded.");
        }
    }
}
