package net.veldor.rdc_drag.view_models;


import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.veldor.rdc_drag.controllers.Controller;
import net.veldor.rdc_drag.controllers.LoginController;
import net.veldor.rdc_drag.utils.HttpConnection;

import java.io.IOException;

public class LoginModel extends ControllerModel {
    private final LoginController mController;

    public LoginModel(LoginController controller) {
        super();
        mController = controller;
    }

    public void login(String login, String password, Label statusLine, LoginController controller){
        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                this.updateMessage("Отправляю запрос");
                String result = HttpConnection.login(login, password);
                System.out.println(result);
                this.updateMessage(result);
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            if (event != null && event.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
                controller.loginFinished();
            }
        });
        statusLine.textProperty().bind(task.messageProperty());
        new Thread(task).start();
   }
}

