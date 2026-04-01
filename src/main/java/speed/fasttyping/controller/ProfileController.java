package speed.fasttyping.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import speed.fasttyping.dao.DaoFactory;
import speed.fasttyping.dao.UserDao;
import speed.fasttyping.model.User;
import speed.fasttyping.util.InputValidator;
import speed.fasttyping.util.SceneNavigator;
import speed.fasttyping.util.SessionManager;

import java.sql.SQLException;
import java.util.Optional;

public class ProfileController {

    @FXML private TextField usernameField;
    @FXML private Label usernameStatusLabel;

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label passwordStatusLabel;

    private User currentUser;
    private final InputValidator validator = new InputValidator();

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            usernameField.setText(currentUser.getUsername());
        }
    }

    @FXML
    private void onSaveUsernameClick() {
        String newUsername = usernameField.getText().trim();

        Optional<String> error = validator.validateUsername(newUsername);
        if (error.isPresent()) {
            showStatus(usernameStatusLabel, error.get(), true);
            return;
        }

        try {
            UserDao dao = DaoFactory.getInstance().getUserDao();
            if (dao.existsByUsername(newUsername)) {
                showStatus(usernameStatusLabel, "Такий логін вже зайнятий", true);
                return;
            }
            dao.updateUsername(currentUser.getId(), newUsername);
            SessionManager.getInstance().updateUsername(newUsername);
            showStatus(usernameStatusLabel, "Логін успішно змінено!", false);
        } catch (SQLException e) {
            showStatus(usernameStatusLabel, "Помилка з'єднання з БД", true);
            e.printStackTrace();
        }
    }

    @FXML
    private void onSavePasswordClick() {
        String current = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (current.isEmpty()) {
            showStatus(passwordStatusLabel, "Введіть поточний пароль", true);
            return;
        }

        Optional<String> error = validator.validateNewPassword(newPass, confirm);
        if (error.isPresent()) {
            showStatus(passwordStatusLabel, error.get(), true);
            return;
        }

        try {
            UserDao dao = DaoFactory.getInstance().getUserDao();
            if (!dao.verifyPassword(currentUser.getId(), current)) {
                showStatus(passwordStatusLabel, "Поточний пароль невірний", true);
                return;
            }
            dao.updatePassword(currentUser.getId(), newPass);
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            showStatus(passwordStatusLabel, "Пароль успішно змінено!", false);
        } catch (SQLException e) {
            showStatus(passwordStatusLabel, "Помилка з'єднання з БД", true);
            e.printStackTrace();
        }
    }

    @FXML
    private void onBackClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.navigateTo(stage, "main.fxml", "Тренажер сліпого друку");
    }

    private void showStatus(Label label, String message, boolean isError) {
        label.setText(message);
        label.setStyle(isError ? "-fx-text-fill: #e06c75;" : "-fx-text-fill: #98c379;");
    }
}