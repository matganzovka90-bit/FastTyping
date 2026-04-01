package speed.fasttyping.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import speed.fasttyping.dao.DaoFactory;
import speed.fasttyping.dao.DatabaseConnection;
import speed.fasttyping.dao.DatabaseInitializer;
import speed.fasttyping.util.SessionManager;

import java.sql.Connection;
import java.sql.SQLException;


public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        new DatabaseInitializer(conn).initialize();

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        String fxml;
        String title;

        if (SessionManager.getInstance().tryAutoLogin()) {
            fxml = "main.fxml";
            title = "Тренажер сліпого друку";
        } else {
            fxml = "auth.fxml";
            title = "Вхід";
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());

        stage.setTitle(title);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
