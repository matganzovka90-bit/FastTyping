package speed.fasttyping.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.util.Duration;
import speed.fasttyping.dao.DatabaseConnection;
import speed.fasttyping.dao.TypingResultDao;
import speed.fasttyping.model.TypingResult;
import speed.fasttyping.observer.AccuracyObserver;
import speed.fasttyping.observer.WpmObserver;
import speed.fasttyping.strategy.EasyStrategy;
import speed.fasttyping.strategy.MarathonStrategy;
import speed.fasttyping.strategy.TimeAttackStrategy;
import speed.fasttyping.strategy.TypingSession;
import speed.fasttyping.util.SessionManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainWindowController {
    @FXML private Label textToTypeLabel;
    @FXML private Label modeLabel;
    @FXML private Label wpmLabel;
    @FXML private Label accuracyLabel;
    @FXML private TextField userInputField;

    private final TypingSession session = new TypingSession(new EasyStrategy());

    private Timeline timer;
    private int timeLeft;

    @FXML
    public void initialize() {
        session.addObserver(new WpmObserver(wpmLabel));
        session.addObserver(new AccuracyObserver(accuracyLabel));

        userInputField.setOnKeyTyped(e -> {
            String typed = userInputField.getText();
            String expected = textToTypeLabel.getText();

            if(typed.length() == 1)
                startTimer();

            session.onKeyTyped(typed, expected);

            if(session.isCompleted(typed, expected)) {
                onSessionCompleted();
            }
        });
    }

    @FXML
    private void handleStatsClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/speed/fasttyping/view/stats.fxml")
            );
            Parent root = loader.load();

            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, bounds.getWidth(), bounds.getHeight()));
            stage.setTitle("Статистика");
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        if (timer != null) timer.stop();

        modeLabel.setText("Режим: " + session.getModeName() + " · Завантаження...");
        textToTypeLabel.setText("Завантаження тексту...");
        userInputField.clear();
        userInputField.setDisable(true);

        new Thread(() -> {
            String text = session.getText();
            Platform.runLater(() -> {
                textToTypeLabel.setText(text);
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

    private void startTimer() {
        if(timer != null)
            timer.stop();

        timeLeft = session.getDurationSeconds();
        if(timeLeft == 0)
            return;

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            modeLabel.setText("Режим: " + session.getModeName() + " · " + timeLeft + " сек");

            if(timeLeft <= 0) {
                timer.stop();
                onSessionCompleted();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void onSessionCompleted() {
        userInputField.setDisable(true);
        if(timer != null) timer.stop();

        if(!SessionManager.getInstance().isLoggedIn()) {
            return;
        }

        int userId = SessionManager.getInstance().getCurrentUser().getId();

        TypingResult result = new TypingResult(
                userId,
                session.getLastWpm(),
                session.getLastAccuracy(),
                session.getLastErrors(),
                session.getModeName()
        );

        try{
            Connection connection = DatabaseConnection.getInstance().getConnection();
            TypingResultDao dao = new TypingResultDao(connection);
            dao.createTable();
            dao.save(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}