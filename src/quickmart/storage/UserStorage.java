package quickmart.storage;

import quickmart.models.User;
import java.util.ArrayList;
import java.util.List;

public class UserStorage {
    private static List<User> users = new ArrayList<>();
    private static int userIdCounter = 1;

    public static void addUser(User user) {
        users.add(user);
    }

    public static User getUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }

    public static boolean validateCredentials(String email, String password) {
        User user = getUserByEmail(email);
        return user != null && user.validatePassword(password);
    }

    public static int getNextUserId() {
        return userIdCounter++;
    }
}
