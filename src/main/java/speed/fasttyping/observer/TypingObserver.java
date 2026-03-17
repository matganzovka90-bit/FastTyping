package speed.fasttyping.observer;

public interface TypingObserver {
    void onUpdate(int wpm, double accuracy, int errors);
}
