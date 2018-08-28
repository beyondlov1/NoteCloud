import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class TestJavaFx extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(TestJavaFx.class.getClassLoader().getResource("views/main.fxml")));
        primaryStage.setTitle("hello world");
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();

    }
}
