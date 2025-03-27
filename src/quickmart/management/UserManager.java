package quickmart.management;

import quickmart.models.User;
import quickmart.storage.UserStorage;

public class UserManager {
    public void registerUser(User user) {
        UserStorage.addUser(user); // Add user to storage
    }
}
