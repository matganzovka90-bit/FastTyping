package speed.fasttyping.strategy;

public interface TypingStrategy {
    String getText();
    int getDurationTime();
    String getModeName();

    default String getUkrainianText() {
        return getText();
    }
}