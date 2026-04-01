package speed.fasttyping.strategy;

import speed.fasttyping.observer.TypingObserver;

import java.util.ArrayList;
import java.util.List;

public class TypingSession {
    private TypingStrategy strategy;

    private long startTime = 0;

    private List<TypingObserver> observers = new ArrayList<>();

    private int lastWpm = 0;
    private double lastAccuracy = 100.0;
    private int lastErrors = 0;

    public TypingSession(TypingStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(TypingStrategy strategy) {
        this.strategy = strategy;
        this.startTime = 0;
        this.lastWpm = 0;
        this.lastAccuracy = 100.0;
        this.lastErrors = 0;
    }

    public void addObserver(TypingObserver o) {
        observers.add(o);
    }

    private void notifyObservers(int wpm, double accuracy, int errors) {
        for (TypingObserver o : observers) {
            o.onUpdate(wpm, accuracy, errors);
        }
    }
    public void onKeyTyped(String typed, String expected) {
        lastWpm = calculateWpm(typed);
        lastAccuracy = calculateAccuracy(typed, expected);
        lastErrors = countErrors(typed, expected);
        notifyObservers(lastWpm, lastAccuracy, lastErrors);
    }

    public boolean isCompleted(String typed, String expected) {
        return typed.length() >= expected.length();
    }

    private int calculateWpm(String typed) {
        if(typed.isEmpty()) {
            return 0;
        }

       if(startTime == 0) {
           startTime = System.currentTimeMillis();
       }

       long elapsedMillis = System.currentTimeMillis() - startTime;
       double minutes = elapsedMillis / 60000.0;

       if(minutes == 0) {
           return 0;
       }

       return (int) ((typed.length() / 5.0) / minutes);
    }
    private double calculateAccuracy(String typed, String expected) {
        if(typed.isEmpty())
            return 100.0;

        int correct = 0;
        int length = Math.min(typed.length(), expected.length());

        for(int i = 0; i < length; i++) {
            if(typed.charAt(i) == expected.charAt(i))
                correct++;
        }

        double accuracy = (correct / (double) typed.length()) * 100.0;
        return Math.round(accuracy * 10.0) / 10.0;
    }

    private int countErrors(String typed, String expected) {
        if(typed.isEmpty())
            return 0;

        int errors = 0;
        int length = Math.min(typed.length(), expected.length());

        for(int i = 0; i < length; i++) {
            if(typed.charAt(i) != expected.charAt(i))
                errors++;
        }

        return errors;
    }

    public int getLastWpm() {
        return lastWpm;
    }

    public double getLastAccuracy() {
        return lastAccuracy;
    }

    public int getLastErrors() {
        return lastErrors;
    }

    public String getText() {
        return strategy.getText();
    }

    public String getUkrainianText() {
        return strategy.getUkrainianText();
    }

    public int getDurationSeconds() {
        return strategy.getDurationTime();
    }

    public String getModeName() {
        return strategy.getModeName();
    }

    public TypingStrategy getStrategy() {
        return strategy;
    }
}