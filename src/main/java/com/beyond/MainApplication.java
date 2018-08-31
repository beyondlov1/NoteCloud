package com.beyond;

import com.beyond.f.F;
import com.beyond.service.remote.DocumentService;
import com.beyond.service.remote.impl.DocumentServiceImpl;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainApplication  extends Application {

    private Logger logger = LogManager.getLogger();

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




        DocumentService documentService = new DocumentServiceImpl(F.DEFAULT_REMOTE_URL,F.DEFAULT_XML_PATH);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    documentService.synchronize();
                    logger.info("synchronize success");
                }catch (Exception e){
                    e.printStackTrace();
                    logger.info("connect timeout");
                }
            }
        };
        timer.schedule(timerTask,0,5*60*1000);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                timer.cancel();
                F.logger.info("timer cancel");
            }
        });
    }
}