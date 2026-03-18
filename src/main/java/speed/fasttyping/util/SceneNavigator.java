package speed.fasttyping.util;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class SceneNavigator {
    private static final String BASE_PATH = "/speed/fasttyping/view/";

    public static void navigateTo(Stage stage, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneNavigator.class.getResource(BASE_PATH + fxmlFile)
            );
            Parent root = loader.load();
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            stage.setScene(new Scene(root, bounds.getWidth(), bounds.getHeight()));
            stage.setTitle(title);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
