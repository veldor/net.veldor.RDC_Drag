package net.veldor.rdc_drag.controllers;

import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.veldor.rdc_drag.utils.HttpConnection;
import net.veldor.rdc_drag.utils.Properties;
import net.veldor.rdc_drag.view_models.ControllerModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainController implements Controller {

    public Label statusLabel;
    public VBox mainContainer;
    private ControllerModel mMyModel;
    private Stage mStage;

    public void init(Stage primaryStage) {
        mStage = primaryStage;
        mMyModel = new ControllerModel();
        // попробую получить токен аутентификации
        String token = Properties.getToken();
        if (token != null) {
            System.out.println("Have token");
            // у нас есть токен, проверю его актуальность
            if (!HttpConnection.checkToken(token)) {
                openLoginWindow(primaryStage);
            }
        } else {
            System.out.println("Have no token");
            // токена нет, открою окно логина
            openLoginWindow(primaryStage);
        }
        /*// назначу временный токен
        try {
            Properties.saveToken("test");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // если нет токена аутентификации- покажу окно ввода данных аутентификации
        try {
            HttpConnection.sendPost();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void openLoginWindow(Stage stage) {
        try {
            mMyModel.createNewWindow("/login.fxml",
                    "Аутентификация",
                    stage
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleDragOver(DragEvent dragEvent) {
        try{
            dragEvent.acceptTransferModes(TransferMode.COPY);
            statusLabel.setText("Бросьте файлы сюда");
            dragEvent.consume();
        }
        catch (Throwable e){
            e.printStackTrace();
        }
    }

    public void handleDragDropped(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            List<File> files = db.getFiles();
            mMyModel.handleFiles(files, statusLabel, this);
            success = true;
        }
        /* let the source know whether the string was successfully
         * transferred and used */
        dragEvent.setDropCompleted(success);
        dragEvent.consume();
    }

    public void filesUploaded(String s) {
        try {
            mMyModel.createInfoWindow(s, mStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
