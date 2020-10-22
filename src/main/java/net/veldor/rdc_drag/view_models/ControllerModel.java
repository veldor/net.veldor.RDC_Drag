package net.veldor.rdc_drag.view_models;


import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.veldor.rdc_drag.controllers.Controller;
import net.veldor.rdc_drag.controllers.InfoController;
import net.veldor.rdc_drag.controllers.MainController;
import net.veldor.rdc_drag.utils.Converter;
import net.veldor.rdc_drag.utils.HttpConnection;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ControllerModel {
    public Controller createNewWindow(String view, String title, Stage owner) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view));
        Parent root = loader.load();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        if(owner != null){
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(owner);
        }
        stage.show();
        Controller controller = (loader.getController());
        controller.init(stage);
        return controller;
    }

    public void handleFiles(List<File> files, Label statusLabel, MainController mainController) {
        StringBuilder status = new StringBuilder();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                this.updateMessage("Начинаю обработку файлов: всего " + files.size());
                List<File> convertedFiles = Converter.convertFiles(files);
                this.updateMessage("Файлы конвертированы, их осталось " + convertedFiles.size());
                if(convertedFiles.size() > 0){
                    String fileName;
                    for (File f :
                            convertedFiles) {
                        fileName = HttpConnection.sendFile(f);
                        if(fileName != null){
                            status.append("Добавлено заключение ");
                            status.append(fileName);
                            status.append("\n");
                        }
                    }
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            if (event != null && event.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
                mainController.filesUploaded(status.toString());
                statusLabel.textProperty().unbind();
            }
        });
        statusLabel.textProperty().bind(task.messageProperty());
        new Thread(task).start();
    }

    public void createInfoWindow(String message, Stage owner) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/info_window.fxml"));
        Parent root = loader.load();
        stage.setTitle("Информация");
        stage.setScene(new Scene(root));
        if(owner != null){
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(owner);
        }
        InfoController controller = (loader.getController());
        controller.init(stage);
        controller.setMessage(message);
        stage.show();
    }
}

