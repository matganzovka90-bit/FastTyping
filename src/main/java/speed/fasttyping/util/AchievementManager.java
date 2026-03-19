package speed.fasttyping.util;

import speed.fasttyping.dao.AchievementDao;
import speed.fasttyping.dao.DaoFactory;
import speed.fasttyping.model.Achievement;
import speed.fasttyping.model.AchievementCondition;
import speed.fasttyping.model.AchievementContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AchievementManager {
    private static final AchievementManager instance = new AchievementManager();

    private final List<Achievement> achievements = new ArrayList<>();
    private final List<AchievementCondition> conditions = new ArrayList<>();

    private AchievementManager() {
        registerAchievements();
    }

    public static AchievementManager getInstance() {
        return instance;
    }

    private void registerAchievements() {
        register(
                new Achievement("first_100", "Перші кроки", "Надрукуйте 100 символів", "🎯"),
                context -> context.getTotalCharsTyped() >= 100
        );
        register(
                new Achievement("speed_60", "Швидкісний", "Досягніть 60 WPM", "⚡"),
                context -> context.getWpm() >= 60
        );
        register(
                new Achievement("speed_100", "Блискавка", "Досягніть 100 WPM", "🚀"),
                context -> context.getWpm() >= 100
        );
        register(
                new Achievement("perfect_accuracy", "Перфекціоніст", "Завершіть сесію з точністю 100%", "✨"),
                context -> context.getAccuracy() >= 100.0 && context.getErrors() == 0
        );
        register(
                new Achievement("marathon_no_errors", "Марафонець", "Пройдіть марафон без помилок", "🏃"),
                context -> "Марафон".equals(context.getModeName()) && context.getErrors() == 0
        );
        register(
                new Achievement("sessions_10", "Практик", "Завершіть 10 сесій", "📚"),
                context -> context.getTotalSessions() >= 10
        );
        register(
                new Achievement("sessions_50", "Майстер", "Завершіть 50 сесій", "🏆"),
                context -> context.getTotalSessions() >= 50
        );
        register(
                new Achievement("accuracy_95", "Точний стрілець", "Завершіть сесію з точністю 95%+", "🎯"),
                context -> context.getAccuracy() >= 95.0
        );
        register(
                new Achievement("time_attack_win", "Переможець часу", "Завершіть атаку часу", "⏱️"),
                context -> "Атака часу".equals(context.getModeName())
        );
    }

    private void register(Achievement achievement, AchievementCondition condition) {
        achievements.add(achievement);
        conditions.add(condition);
    }

    public void loadFromDatabase(int userId) {
        try {
            AchievementDao dao = DaoFactory.getInstance().getAchievementDao();
            Set<String> unlockedIds = dao.getUnlockedIds(userId);

            for(Achievement a : achievements) {
                if(unlockedIds.contains(a.getId())) {
                    a.unlock();
                }
                else {
                    a.reset();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Achievement> checkAndUnlock(AchievementContext context, int userId) {
        List<Achievement> newlyUnlocked = new ArrayList<>();

        for(int i = 0; i < achievements.size(); i++) {
            Achievement achievement = achievements.get(i);
            AchievementCondition condition = conditions.get(i);

            if(!achievement.isUnlocked() && condition.isMet(context)) {
                achievement.unlock();
                newlyUnlocked.add(achievement);

                try {
                    AchievementDao dao = DaoFactory.getInstance().getAchievementDao();
                    dao.save(userId, achievement.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return Collections.unmodifiableList(newlyUnlocked);
    }

    public List<Achievement> getAllAchievements() {
        return Collections.unmodifiableList(achievements);
    }

    public List<Achievement> getUnlockedAchievements() {
        List<Achievement> unlocked = new ArrayList<>();
        for (Achievement a : achievements) {
            if (a.isUnlocked()) unlocked.add(a);
        }
        return unlocked;
    }

    public int getUnlockedCount() {
        int count = 0;
        for (Achievement a : achievements) {
            if (a.isUnlocked()) count++;
        }
        return count;
    }

    public int getTotalCount() {
        return achievements.size();
    }
}
