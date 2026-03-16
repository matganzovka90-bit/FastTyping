package speed.fasttyping.observer;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class AccuracyObserver implements TypingObserver {
    private final Label accuracyLabel;

    public AccuracyObserver(Label accuracyLabel) {
        this.accuracyLabel = accuracyLabel;
    }

    @Override
    public void onUpdate(int wpm, double accuracy, int errors) {
        Platform.runLater(() ->
                accuracyLabel.setText(String.format("%.0f%%", accuracy)));
    }
}
