package speed.fasttyping.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import speed.fasttyping.dao.DatabaseConnection;
import speed.fasttyping.dao.UserDao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class AuthController {

    @FXML private Button loginTabBtn;
    @FXML private Button registerTabBtn;
    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label errorLabel;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private VBox confirmBox;
    @FXML private Button submitBtn;

    private boolean isLoginMode = true;


    private static final String TAB_ACTIVE =
            "-fx-background-color: #3c3f41; -fx-text-fill: #4b6eaf; " +
                    "-fx-font-size: 15px; -fx-font-weight: bold; -fx-cursor: hand; " +
                    "-fx-pref-width: 180; -fx-pref-height: 44; " +
                    "-fx-background-radius: 10 10 0 0; -fx-border-color: transparent;";

    private static final String TAB_INACTIVE =
            "-fx-background-color: #2b2b2b; -fx-text-fill: #777777; " +
                    "-fx-font-size: 15px; -fx-font-weight: bold; -fx-cursor: hand; " +
                    "-fx-pref-width: 180; -fx-pref-height: 44; " +
                    "-fx-background-radius: 10 10 0 0; -fx-border-color: transparent;";


    @FXML
    protected void onLoginTabClick() {
        isLoginMode = true;
        clearError();
        clearFields();


        loginTabBtn.setStyle(TAB_ACTIVE);
        registerTabBtn.setStyle(TAB_INACTIVE);

        titleLabel.setText("Вітаємо назад");
        subtitleLabel.setText("Увійдіть, щоб продовжити тренування");
        submitBtn.setText("Увійти");

        confirmBox.setVisible(false);
        confirmBox.setManaged(false);
    }

    @FXML
    protected void onRegisterTabClick() {
        isLoginMode = false;
        clearError();
        clearFields();

        registerTabBtn.setStyle(TAB_ACTIVE);
        loginTabBtn.setStyle(TAB_INACTIVE);

        titleLabel.setText("Створити акаунт");
        subtitleLabel.setText("Зареєструйтесь, щоб зберігати результати");
        submitBtn.setText("Зареєструватися");

        confirmBox.setVisible(true);
        confirmBox.setManaged(true);
    }


    @FXML
    protected void onSubmitClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (isLoginMode) {
            handleLogin(username, password);
        } else {
            handleRegister(username, password);
        }
    }


    @FXML
    protected void onBackClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/speed/fasttyping/view/main.fxml")
            );
            Parent root = loader.load();

            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, bounds.getWidth(), bounds.getHeight()));
            stage.setTitle("Тренажер сліпого друку");
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Помилка при поверненні на головну");
        }
    }


    private void handleLogin(String username, String password) {
        if (!validateBasicFields(username, password)) return;

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            UserDao userDao = new UserDao(conn);

            boolean success = userDao.login(username, password);

            if (!success) {
                showError("Невірне ім'я або пароль");
            }

        } catch (SQLException e) {
            showError("Помилка з'єднання з базою даних");
            e.printStackTrace();
        }
    }


    private void handleRegister(String username, String password) {
        if (!validateBasicFields(username, password)) return;

        String confirm = confirmPasswordField.getText();

        if (!password.equals(confirm)) {
            showError("Паролі не співпадають");
            return;
        }

        if (password.length() < 6) {
            showError("Пароль має бути мінімум 6 символів");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            UserDao userDao = new UserDao(conn);
            userDao.createUserTable();

            if (userDao.existsByUsername(username)) {
                showError("Користувач з таким іменем вже існує");
                return;
            }

            userDao.create(username, password);

            onLoginTabClick();
            usernameField.setText(username);

        } catch (SQLException e) {
            showError("Помилка з'єднання з базою даних");
            e.printStackTrace();
        }
    }


    private boolean validateBasicFields(String username, String password) {
        if (username.isEmpty()) {
            showError("Введіть ім'я користувача");
            return false;
        }
        if (password.isEmpty()) {
            showError("Введіть пароль");
            return false;
        }
        return true;
    }


    private void showError(String message) {
        errorLabel.setText(message);
    }

    private void clearError() {
        errorLabel.setText("");
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
}