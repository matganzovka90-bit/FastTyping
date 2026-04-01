package speed.fasttyping.observer;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class ErrorObserver implements TypingObserver{

    private final Label errorLabel;

    public ErrorObserver(Label errorLabel) { this.errorLabel = errorLabel; }

    @Override
    public void onUpdate(int wpm, double accuracy, int errors) {
        Platform.runLater(() -> errorLabel.setText(String.valueOf(errors)));
    }
}
