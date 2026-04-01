package speed.fasttyping.util;

import speed.fasttyping.dao.DaoFactory;
import speed.fasttyping.model.User;

import java.sql.SQLException;

public class SessionManager {

    private static volatile SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if(instance == null) {
            synchronized (SessionManager.class) {
                if(instance == null) {
                    instance = new SessionManager();
                }
            }
        }

        return instance;
    }

    public void login(User user, boolean rememberMe) {
        this.currentUser = user;
        try {
            DaoFactory.getInstance().getUserDao().setRememberMe(user.getId(), rememberMe);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean tryAutoLogin() {
        try {
            User rememberedUser = DaoFactory.getInstance().getUserDao().findRememberedUser();
            if (rememberedUser != null) {
                this.currentUser = rememberedUser;
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Помилка автологіну: " + e.getMessage());
        }
        return false;
    }

    public void logout() {
        if (currentUser != null) {
            try {
                DaoFactory.getInstance().getUserDao().setRememberMe(currentUser.getId(), false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        this.currentUser = null;
    }

    public void updateUsername(String newUsername) {
        if (currentUser != null) {
            currentUser.setUsername(newUsername);
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
