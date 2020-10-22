package net.veldor.rdc_drag;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.veldor.rdc_drag.controllers.MainController;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Drag me");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

        final MainController controller = loader.getController();
        controller.init(primaryStage);
    }


    public static void main(String[] args) {
        BasicConfigurator.configure();
        List<Logger> loggers = Collections.list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        launch(args);
    }
}
