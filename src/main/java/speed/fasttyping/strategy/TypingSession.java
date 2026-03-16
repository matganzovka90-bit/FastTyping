package speed.fasttyping.strategy;

public class TypingSession {
    private TypingStrategy strategy;

    public TypingSession(TypingStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(TypingStrategy strategy) {
        this.strategy = strategy;
    }

    public String getText() {
        return strategy.getText();
    }

    public int getDurationSeconds() {
        return strategy.getDurationTime();
    }

    public String getModeName() {
        return strategy.getModeName();
    }
}