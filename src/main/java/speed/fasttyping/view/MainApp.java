package speed.fasttyping.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("main.fxml")
        );

        Parent root = fxmlLoader.load();
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());

        stage.setTitle("Тренажер сліпого друку");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
