package speed.fasttyping.strategy;

import speed.fasttyping.util.TextProvider;

public class EasyStrategy implements TextProviderStrategy {
    @Override
    public String getText() {
        return TextProvider.fetchRandomQuote();
    }

    @Override
    public int getDurationTime() {
        return 0;
    }
    @Override
    public String getModeName() {
        return "Easy";
    }
}
