package speed.fasttyping.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindowController {

    @FXML
    private void handleLoginButtonClick(ActionEvent event) {
        openAuthWindow(event, false);
    }

    @FXML
    private void handleRegistrationButtonClick(ActionEvent event) {
        openAuthWindow(event, true);
    }

    private void openAuthWindow(ActionEvent event, boolean startOnRegister) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/speed/fasttyping/view/auth.fxml")
            );
            Parent root = loader.load();

            if (startOnRegister) {
                AuthController authController = loader.getController();
                authController.onRegisterTabClick();
            }

            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, bounds.getWidth(), bounds.getHeight()));
            stage.setTitle("Speed Typing — Акаунт");
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Помилка при відкритті вікна авторизації: " + e.getMessage());
        }
    }
}