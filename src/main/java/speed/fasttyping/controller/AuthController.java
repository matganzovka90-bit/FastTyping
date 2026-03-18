package speed.fasttyping.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import speed.fasttyping.dao.DaoFactory;
import speed.fasttyping.dao.UserDao;
import speed.fasttyping.model.User;
import speed.fasttyping.util.SceneNavigator;
import speed.fasttyping.util.SessionManager;

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
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.navigateTo(stage, "main.fxml", "Тренажер сліпого друку");
    }


    private void handleLogin(String username, String password) {
        if (!validateBasicFields(username, password)) return;

        try {
            UserDao dao = DaoFactory.getInstance().getUserDao();

            User user = dao.login(username, password);

            if (user == null) {
                showError("Невірне ім'я або пароль");
            } else {
                SessionManager.getInstance().login(user);
                goToMain();
            }

        } catch (SQLException e) {
            showError("Помилка з'єднання з базою даних");
            e.printStackTrace();
        }
    }

    private void goToMain() {
        Stage stage = (Stage) submitBtn.getScene().getWindow();
        SceneNavigator.navigateTo(stage, "main.fxml", "Тренажер сліпого друку");
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
            UserDao dao = DaoFactory.getInstance().getUserDao();

            if (dao.existsByUsername(username)) {
                showError("Користувач з таким іменем вже існує");
                return;
            }

            dao.create(username, password);

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