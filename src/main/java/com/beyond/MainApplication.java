package com.beyond;

import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sun.applet.Main;

import java.util.Objects;

public class MainApplication  extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getClassLoader().getResource("views/main.fxml")));
        primaryStage.setTitle("NoteCloud");
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
