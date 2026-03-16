package speed.fasttyping.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import speed.fasttyping.strategy.EasyStrategy;
import speed.fasttyping.strategy.MarathonStrategy;
import speed.fasttyping.strategy.TimeAttackStrategy;
import speed.fasttyping.strategy.TypingSession;

import java.io.IOException;

public class MainWindowController {
    @FXML private Label textToTypeLabel;
    @FXML private Label modeLabel;
    @FXML private Label wpmLabel;
    @FXML private Label accuracyLabel;
    @FXML private TextField userInputField;

    private final TypingSession session = new TypingSession(new EasyStrategy());

    @FXML
    private void handleEasyMode() {
        session.setStrategy(new EasyStrategy());
        loadText();
    }

    @FXML
    private void handleTimeAttackMode() {
        session.setStrategy(new TimeAttackStrategy());
        loadText();
    }

    @FXML
    private void handleMarathonMode() {
        session.setStrategy(new MarathonStrategy());
        loadText();
    }

    private void loadText() {
        modeLabel.setText("Режим: " + session.getModeName() + " · Завантаження...");
        textToTypeLabel.setText("Завантаження тексту...");
        userInputField.clear();
        userInputField.setDisable(true);

        new Thread(() -> {
            String text = session.getText();
            Platform.runLater(() -> {
                textToTypeLabel.setText(text);
                String duration = session.getDurationSeconds() == 0
                        ? "без обмежень"
                        : session.getDurationSeconds() + " сек";
                modeLabel.setText("Режим: " + session.getModeName() + " · " + duration);
                userInputField.setDisable(false);
                userInputField.requestFocus();
            });
        }).start();
    }


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