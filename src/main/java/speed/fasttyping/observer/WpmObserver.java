package speed.fasttyping.observer;

import javafx.application.Platform;

import javafx.scene.control.Label;

public class WpmObserver implements TypingObserver {

    private final Label wpmLabel;

    public WpmObserver(Label wpmLabel) {
        this.wpmLabel = wpmLabel;
    }

    @Override
    public void onUpdate(int wpm, double accuracy, int errors) {
        Platform.runLater(() -> wpmLabel.setText(String.valueOf(wpm)));
    }
}
