package speed.fasttyping.strategy;

import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;

public interface TypingStrategy {
    String getText();
    int getDurationTime();
    String getModeName();

    default String getUkrainianText() {
        return getText();
    }

    default void onTextLoaded(TextFlow textFlow, TextField inputField, Runnable onVanish){}
}