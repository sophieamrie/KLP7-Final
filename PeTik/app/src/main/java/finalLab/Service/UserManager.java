package finalLab.Service;

import finalLab.Model.User;
import java.util.List;

public class UserManager extends BaseFileService<User> {

    private static final String USER_FILE = "users.csv";

    public UserManager() {
        super(USER_FILE);
    }

    @Override
    protected User parseFromString(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 3) {
            return new User(parts[0], parts[1], Integer.parseInt(parts[2]));
        }
        return null;
    }

    @Override
    protected String convertToString(User user) {
        return user.getUsername() + "," + user.getPassword() + "," + user.getBalance();
    }

    @Override
    protected String getEntityId(User user) {
        return user.getUsername();
    }

    @Override
    public User findById(String username) {
        return loadAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean delete(String username) {
        List<User> allUsers = loadAll();
        boolean removed = allUsers.removeIf(user -> user.getUsername().equals(username));
        if (removed) {
            return rewriteFile(allUsers);
        }
        return false;
    }

    public boolean register(String username, String password) {
        if (findById(username) != null) {
            return false;
        }

        User newUser = new User(username, password, 0);
        return save(newUser);
    }

    public User login(String username, String password) {
        return loadAll().stream()
                .filter(user -> user.getUsername().equals(username) &&
                        user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public User getUser(String username) {
        return findById(username);
    }

    public void updateUserBalance(String username, int newBalance) {
        User user = findById(username);
        if (user != null) {
            User updatedUser = new User(username, user.getPassword(), newBalance);
            update(username, updatedUser);
        }
    }
}
