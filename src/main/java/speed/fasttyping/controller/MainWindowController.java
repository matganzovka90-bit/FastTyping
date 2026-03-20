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
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import speed.fasttyping.dao.AchievementDao;
import speed.fasttyping.dao.DaoFactory;
import speed.fasttyping.dao.TypingResultDao;
import speed.fasttyping.model.Achievement;
import speed.fasttyping.model.AchievementContext;
import speed.fasttyping.model.TypingResult;
import speed.fasttyping.observer.AccuracyObserver;
import speed.fasttyping.observer.ErrorObserver;
import speed.fasttyping.observer.WpmObserver;
import speed.fasttyping.strategy.EasyStrategy;
import speed.fasttyping.strategy.MarathonStrategy;
import speed.fasttyping.strategy.TimeAttackStrategy;
import speed.fasttyping.strategy.TypingSession;
import speed.fasttyping.util.AchievementManager;
import speed.fasttyping.util.SceneNavigator;
import speed.fasttyping.util.SessionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    @FXML private Label achievementPopup;

    private final TypingSession session = new TypingSession(new EasyStrategy());

    private Timeline timer;
    private int timeLeft;
    private boolean isResetting = false;

    @FXML
    private ToggleButton langToggle;
    private boolean isUkrainian = false;

    @FXML
    public void initialize() {
        session.addObserver(new WpmObserver(wpmLabel));
        session.addObserver(new AccuracyObserver(accuracyLabel));
        session.addObserver(new ErrorObserver(errorsLabel));

        updateAuthBar();

        Text placeholder = new Text("Оберіть режим щоб почати...");
        placeholder.setFill(Color.web("#a9b7c6"));
        placeholder.setStyle("-fx-font-size: 24px; -fx-font-family: 'Monospaced';");
        textFlow.getChildren().add(placeholder);

        userInputField.textProperty().addListener((obs, oldVal, newVal) -> {
            if(isResetting) return;

            if (newVal.length() < oldVal.length()) {
                isResetting = true;
                Platform.runLater(() -> {
                    userInputField.setText(oldVal);
                    userInputField.positionCaret(oldVal.length());
                    isResetting = false;
                });
                return;
            }

            renderText(newVal);
            if (newVal.length() == 1) startTimer();
            session.onKeyTyped(newVal, currentText);
            if (session.isCompleted(newVal, currentText)) onSessionCompleted();
        });
    }

    @FXML
    private void handleLangToggle() {
        isUkrainian = langToggle.isSelected();
        langToggle.setText(isUkrainian ? "ua UA" : "en EN");
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

        Text loading = new Text("Завантаження тексту...");
        loading.setFill(Color.web("#a9b7c6"));
        loading.setStyle("-fx-font-size: 24px; -fx-font-family: 'Monospaced';");
        textFlow.getChildren().clear();
        textFlow.getChildren().add(loading);

        isResetting = true;
        userInputField.clear();
        isResetting = false;

        userInputField.setDisable(true);

        new Thread(() -> {
            String text = isUkrainian
                    ? session.getUkrainianText()
                    : session.getText();

            Platform.runLater(() -> {
                currentText = text;
                renderText("");
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

    private void renderText(String typed) {
        textFlow.getChildren().clear();

        for (int i = 0; i < currentText.length(); i++) {
            Text letter = new Text(String.valueOf(currentText.charAt(i)));
            letter.setStyle("-fx-font-size: 24px; -fx-font-family: 'Monospaced';");

            if (i < typed.length()) {
                if (typed.charAt(i) == currentText.charAt(i)) {
                    letter.setFill(Color.web("#629755"));
                } else {
                    letter.setFill(Color.web("#cc3232"));
                }
            } else if (i == typed.length()) {
                letter.setFill(Color.web("#ffffff"));
            } else {
                letter.setFill(Color.web("#a9b7c6"));
            }

            textFlow.getChildren().add(letter);
        }
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
            TypingResultDao dao = DaoFactory.getInstance().getTypingResultDao();

            dao.save(result);

            int totalSessions = dao.getByUserId(userId).size();

            AchievementContext context = new AchievementContext(
                    session.getLastWpm(),
                    session.getLastAccuracy(),
                    session.getLastErrors(),
                    currentText.length(),
                    session.getModeName(),
                    totalSessions
            );

            List<Achievement> unlocked = AchievementManager.getInstance()
                    .checkAndUnlock(context, userId);

            if(!unlocked.isEmpty()) {
                showAchievementPopups(unlocked, 0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAchievementPopups(List<Achievement> list, int index) {
        if (index >= list.size()) return;

        Achievement a = list.get(index);

        Platform.runLater(() -> {
            achievementPopup.setText(a.getIcon() + "  " + a.getTitle() + "\n" + a.getDescription());
            achievementPopup.setVisible(true);
            achievementPopup.setManaged(true);
        });

        new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            achievementPopup.setVisible(false);
            achievementPopup.setManaged(false);

            new Timeline(new KeyFrame(Duration.millis(500), e2 ->
                    showAchievementPopups(list, index + 1)
            )).play();
        })).play();
    }
}