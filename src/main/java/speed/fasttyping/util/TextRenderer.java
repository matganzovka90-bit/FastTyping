package speed.fasttyping.util;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class TextRenderer {

    private static final String FONT_STYLE = "-fx-font-size: 24px; -fx-font-family: 'Monospaced';";
    private static final Color COLOR_CORRECT = Color.web("#629755");
    private static final Color COLOR_ERROR = Color.web("#cc3232");
    private static final Color COLOR_CURRENT = Color.web("#ffffff");
    private static final Color COLOR_DEFAULT = Color.web("#a9b7c6");

    private final TextFlow textFlow;

    public TextRenderer(TextFlow textFlow) {
        this.textFlow = textFlow;
    }

    public void render(String currentText, String typed) {
        textFlow.getChildren().clear();
        for (int i = 0; i < currentText.length(); i++) {
            Text letter = new Text(String.valueOf(currentText.charAt(i)));
            letter.setStyle(FONT_STYLE);
            letter.setFill(getColor(currentText, typed, i));
            textFlow.getChildren().add(letter);
        }
    }

    public void showMessage(String message) {
        textFlow.getChildren().clear();
        Text text = new Text(message);
        text.setFill(COLOR_DEFAULT);
        text.setStyle(FONT_STYLE);
        textFlow.getChildren().add(text);
    }

    private Color getColor(String currentText, String typed, int i) {
        if (i < typed.length()) {
            return typed.charAt(i) == currentText.charAt(i) ? COLOR_CORRECT : COLOR_ERROR;
        }
        if (i == typed.length()) return COLOR_CURRENT;
        return COLOR_DEFAULT;
    }
}
