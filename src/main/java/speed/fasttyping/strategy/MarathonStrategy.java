package speed.fasttyping.strategy;

import speed.fasttyping.util.TextProvider;

public class MarathonStrategy implements TypingStrategy {
    @Override
    public String getText() {
        return TextProvider.fetchMultipleQuotes(5);
    }

    public String getUkrainianText() {
        return TextProvider.fetchMultipleUkrainianQuotes(5);
    }

    @Override
    public int getDurationTime() {
        return 180;
    }

    @Override
    public String getModeName() {
        return "Marathon";
    }
}