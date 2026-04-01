package speed.fasttyping.util;

import java.util.Optional;

public class InputValidator {
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_PASSWORD_LENGTH = 50;

    public Optional<String> validateUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.of("Введіть ім'я користувача");
        }
        if (username.length() < MIN_USERNAME_LENGTH) {
            return Optional.of("Ім'я має бути мінімум " + MIN_USERNAME_LENGTH + " символи");
        }
        if (username.length() > MAX_USERNAME_LENGTH) {
            return Optional.of("Ім'я має бути максимум " + MAX_USERNAME_LENGTH + " символів");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return Optional.of("Ім'я може містити лише літери, цифри та '_'");
        }
        return Optional.empty();
    }

    public Optional<String> validatePassword(String password) {
        if (password == null || password.isBlank()) {
            return Optional.of("Введіть пароль");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return Optional.of("Пароль має бути мінімум " + MIN_PASSWORD_LENGTH + " символів");
        }
        return Optional.empty();
    }

    public Optional<String> validateConfirmPassword(String password, String confirm) {
        if (confirm == null || confirm.isBlank()) {
            return Optional.of("Підтвердіть пароль");
        }
        if (!password.equals(confirm)) {
            return Optional.of("Паролі не співпадають");
        }
        return Optional.empty();
    }

    public Optional<String> validateAll(String username, String password) {
        Optional<String> usernameError = validateUsername(username);
        if (usernameError.isPresent()) return usernameError;

        Optional<String> passwordError = validatePassword(password);
        if (passwordError.isPresent()) return passwordError;

        return Optional.empty();
    }

    public Optional<String> validateAllWithConfirm(String username, String password, String confirm) {
        Optional<String> basicError = validateAll(username, password);
        if (basicError.isPresent()) return basicError;

        return validateConfirmPassword(password, confirm);
    }

    public Optional<String> validatePasswordStrength(String password) {
        if (!password.matches(".*[A-Z].*")) {
            return Optional.of("Пароль має містити хоча б одну велику літеру");
        }
        if (!password.matches(".*[0-9].*")) {
            return Optional.of("Пароль має містити хоча б одну цифру");
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            return Optional.of("Пароль має бути максимум " + MAX_PASSWORD_LENGTH + " символів");
        }
        return Optional.empty();
    }

    public Optional<String> validateNewPassword(String newPassword, String confirm) {
        Optional<String> lengthError = validatePassword(newPassword);
        if (lengthError.isPresent()) return lengthError;

        Optional<String> strengthError = validatePasswordStrength(newPassword);
        if (strengthError.isPresent()) return strengthError;

        return validateConfirmPassword(newPassword, confirm);
    }
}
