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

    public TypingResult(int id, int userId, int wpm, double accuracy, int errors, String modeName) {
        this.id = id;
        this.userId = userId;
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.errors = errors;
        this.modeName = modeName;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
