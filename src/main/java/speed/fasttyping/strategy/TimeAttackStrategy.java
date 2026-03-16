package speed.fasttyping.strategy;

import speed.fasttyping.util.TextProvider;

public class TimeAttackStrategy implements TextProviderStrategy {

    @Override
    public String getText() {
        return TextProvider.fetchMultipleQuotes(3);
    }

    @Override
    public int getDurationTime() {
        return 60;
    }

    @Override
    public String getModeName() {
        return "Time attack";
    }
}