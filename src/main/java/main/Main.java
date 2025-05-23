package main;

import controller.AddDeposit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
            Scene scene = new Scene(loader.load());

            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style/styles.css")).toExternalForm());

            primaryStage.setTitle("Управління банківськими вкладами");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception e) {
            logger.error("Помилка при запуску програми", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}