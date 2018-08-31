package com.beyond;

import com.beyond.f.F;
import com.beyond.service.remote.DocumentService;
import com.beyond.service.remote.impl.DocumentServiceImpl;
import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sun.applet.Main;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

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


        DocumentService documentService = new DocumentServiceImpl("https://yura.teracloud.jp/dav/test.xml",F.DEFAULT_XML_PATH);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    documentService.synchronize();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(timerTask,0,5*60*1000);
    }
}