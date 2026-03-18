package speed.fasttyping.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import speed.fasttyping.dao.DatabaseConnection;
import speed.fasttyping.dao.TypingResultDao;
import speed.fasttyping.model.TypingResult;
import speed.fasttyping.observer.AccuracyObserver;
import speed.fasttyping.observer.ErrorObserver;
import speed.fasttyping.observer.WpmObserver;
import speed.fasttyping.strategy.EasyStrategy;
import speed.fasttyping.strategy.MarathonStrategy;
import speed.fasttyping.strategy.TimeAttackStrategy;
import speed.fasttyping.strategy.TypingSession;
import speed.fasttyping.util.SceneNavigator;
import speed.fasttyping.util.SessionManager;
import speed.fasttyping.util.TextRenderer;

import java.sql.Connection;
import java.sql.SQLException;

public class MainWindowController {
    @FXML private TextFlow textFlow;
    private String currentText = "";
    @FXML private Label modeLabel;
    @FXML private Label wpmLabel;
    @FXML private Label errorsLabel;
    @FXML private Label accuracyLabel;
    @FXML private TextField userInputField;

    @FXML private Button loginBtn;
    @FXML private Button registerBtn;

    private final TypingSession session = new TypingSession(new EasyStrategy());

    private Timeline timer;
    private int timeLeft;
    private boolean isResetting = false;

    private TextRenderer textRenderer;

    @FXML
    public void initialize() {
        session.addObserver(new WpmObserver(wpmLabel));
        session.addObserver(new AccuracyObserver(accuracyLabel));
        session.addObserver(new ErrorObserver(errorsLabel));

        textRenderer = new TextRenderer(textFlow);
        textRenderer.showMessage("Оберіть режим щоб почати...");

        updateAuthBar();

        userInputField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isResetting) return;

            if (newVal.length() < oldVal.length()) {
                isResetting = true;
                Platform.runLater(() -> {
                    userInputField.setText(oldVal);
                    userInputField.positionCaret(oldVal.length());
                    isResetting = false;
                });
                return;
            }

            textRenderer.render(currentText, newVal);
            if (newVal.length() == 1) startTimer();
            session.onKeyTyped(newVal, currentText);
            if (session.isCompleted(newVal, currentText)) onSessionCompleted();
        });
    }

    private void updateAuthBar() {
        if (SessionManager.getInstance().isLoggedIn()) {
            String name = SessionManager.getInstance().getCurrentUser().getUsername();
            loginBtn.setVisible(false);
            loginBtn.setManaged(false);
            registerBtn.setText(name);
            registerBtn.setOnAction(e -> handleLogout());
        }
    }

    private void handleLogout() {
        SessionManager.getInstance().logout();
        loginBtn.setVisible(true);
        loginBtn.setManaged(true);
        registerBtn.setText("Реєстрація");
        registerBtn.setOnAction(this::handleRegistrationButtonClick);
    }

    @FXML
    private void handleStatsClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.navigateTo(stage, "stats.fxml", "Статистика");
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
        textRenderer.showMessage("Завантаження тексту...");

        isResetting = true;
        userInputField.clear();
        isResetting = false;

        userInputField.setDisable(true);

        new Thread(() -> {
            String text = session.getText();
            Platform.runLater(() -> {
                currentText = text;
                textRenderer.render(currentText, "");
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
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (startOnRegister) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/speed/fasttyping/view/auth.fxml")
                );
                Parent root = loader.load();
                AuthController authController = loader.getController();
                authController.onRegisterTabClick();

                Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
                stage.setScene(new Scene(root, bounds.getWidth(), bounds.getHeight()));
                stage.setTitle("Speed Typing — Акаунт");
                stage.setMaximized(true);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            SceneNavigator.navigateTo(stage, "auth.fxml", "Speed Typing — Акаунт");
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