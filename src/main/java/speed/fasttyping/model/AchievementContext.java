package speed.fasttyping.model;

public class AchievementContext {
    private final int wpm;
    private final double accuracy;
    private final int errors;
    private final int totalCharsTyped;
    private final String modeName;
    private final int totalSessions;

    public AchievementContext(int wpm, double accuracy, int errors,
                              int totalCharsTyped, String modeName, int totalSessions) {
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.errors = errors;
        this.totalCharsTyped = totalCharsTyped;
        this.modeName = modeName;
        this.totalSessions = totalSessions;
    }

    public int getWpm()              { return wpm; }
    public double getAccuracy()      { return accuracy; }
    public int getErrors()           { return errors; }
    public int getTotalCharsTyped()  { return totalCharsTyped; }
    public String getModeName()      { return modeName; }
    public int getTotalSessions()    { return totalSessions; }
}
