package net.veldor.rdc_drag.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.veldor.rdc_drag.utils.HttpConnection;
import net.veldor.rdc_drag.utils.Properties;
import net.veldor.rdc_drag.view_models.ControllerModel;
import net.veldor.rdc_drag.view_models.LoginModel;

import java.io.IOException;

public class LoginController implements Controller {

    public TextField user; // Поле ввода имени пользователя
    public TextField password; // поле ввода пароля
    public Button loginButton; // кнопка логина
    public Label statusLine; // статус операции

    private LoginModel mMyModel;
    private Stage mStage;

    public void init(Stage stage) {
        mMyModel = new LoginModel(this);
        mStage = stage;
    }

    public void doLogin(ActionEvent actionEvent) {
        // если заполнены логин и пароль- отправлю запрос на аутентификацию, иначе сфокусируюсь на первом незаполненном поле
        String login = user.getText();
        if(login == null || login.isEmpty()){
            user.requestFocus();
            statusLine.setText("Нужно заполнить логин");
            return;
        }
        String pass = password.getText();
        if(pass == null || pass.isEmpty()){
            password.requestFocus();
            statusLine.setText("Нужно заполнить пароль");
            return;
        }
        // если есть данные- отправлю запрос
        user.setDisable(true);
        password.setDisable(true);
        loginButton.setDisable(true);
        mMyModel.login(login, pass, statusLine, this);
    }

    public void loginFinished() {
        if(Properties.getToken() != null && HttpConnection.checkToken(Properties.getToken())){
            mStage.close();
        }
        else{
            user.setDisable(false);
            password.setDisable(false);
            loginButton.setDisable(false);
        }
    }
}
