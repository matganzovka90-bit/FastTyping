package speed.fasttyping.model;

import java.time.LocalDateTime;

public class TypingResult {
    private int id;
    private int userId;
    private int wpm;
    private double accuracy;
    private int errors;
    private String modeName;
    private LocalDateTime createdAt;

    public TypingResult(int userId, int wpm, double accuracy, int errors, String modeName, LocalDateTime createdAt) {
        this.userId = userId;
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.errors = errors;
        this.modeName = modeName;
        this.createdAt = createdAt;
    }
    public TypingResult(int userId, int wpm, double accuracy, int errors, String modeName) {
        this(userId, wpm, accuracy, errors, modeName, LocalDateTime.now());
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() { return id; }

    public int getUserId() {
        return userId;
    }

    public int getWpm() {
        return wpm;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public int getErrors() {
        return errors;
    }

    public String getModeName() {
        return modeName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
