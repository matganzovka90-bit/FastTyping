package speed.fasttyping.strategy;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import speed.fasttyping.util.TextProvider;

public class VanishingStrategy implements TypingStrategy{
    private int vanishAfterSeconds;

    public VanishingStrategy(int vanishAfterSeconds) {
        this.vanishAfterSeconds = vanishAfterSeconds;
    }

    @Override
    public String getText() {
        return TextProvider.fetchRandomQuote();
    }

    @Override
    public String getUkrainianText() {
        return TextProvider.fetchUkrainianQuote();
    }

    @Override
    public int getDurationTime() {
        return 0;
    }

    @Override
    public String getModeName() {
        return "Vanishing";
    }

    @Override
    public void onTextLoaded(TextFlow textFlow, TextField inputField, Runnable onVanish) {
        Timeline[] holder = new Timeline[1];

        Runnable resetTimer = () -> {
            if (holder[0] != null) holder[0].stop();

            holder[0] = new Timeline(new KeyFrame(Duration.seconds(vanishAfterSeconds), e -> {
                String typed = inputField.getText();
                for (int i = typed.length(); i < textFlow.getChildren().size(); i++) {
                    if (textFlow.getChildren().get(i) instanceof Text t) {
                        t.setFill(Color.TRANSPARENT);
                    }
                }
                onVanish.run();
            }));
            holder[0].play();
        };

        resetTimer.run();

        inputField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > oldVal.length()) {
                resetTimer.run();
            }
        });
    }
}