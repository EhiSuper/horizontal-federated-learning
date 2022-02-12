package it.unipi.dsmt.horizontalFederatedLearning.service.db;

import it.unipi.dsmt.horizontalFederatedLearning.entities.*;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.*;

import java.util.HashMap;
import java.util.List;

public class UserService {
    private LevelDB db;
    private static int counterID;

    public UserService(LevelDB db) {
        this.db = db;
        setCounterID();
    }


    private void setCounterID() {
        List<String> keys = db.findKeysByPrefix("User:");
        if (keys != null)
            for (String key : keys) {
                int id = Integer.parseInt(key.split(":")[1]);
                if (id > counterID)
                    counterID = id;
            }
    }

    public void register(User user) throws RegistrationException {
        HashMap<String, String> map = new HashMap<>();
        if (findUserByUsername(user.getUsername()) != null)
            throw new RegistrationException("Username already taken");
        String prefixKey = "User:" + ++counterID + ":";
        map.put(prefixKey + "username", user.getUsername());
        map.put(prefixKey + "firstName", user.getFirstName());
        map.put(prefixKey + "lastName", user.getLastName());
        map.put(prefixKey + "password", user.getPassword());
        map.put(prefixKey + "admin", String.valueOf(user.getAdmin()));
        db.putBatchValues(map);
    }

    public User login(String username, String password) throws LoginException {
        List<String> keys = db.findKeysByPrefix("User:");
        for (String key : keys) {
            if (key.endsWith("username") && db.getValue(key).equals(username)) {
                String id = key.split(":")[1];
                String correctPassword = db.getValue("User:" + id + ":password");
                if (password.equals(correctPassword))
                    return findUserByUsername(username);
                else throw new LoginException("The password is not correct");
            }
        }
        throw new LoginException("Username not present in the database");
    }

    public User findUserByUsername(String username) {
        List<String> keys = db.findKeysByPrefix("User:");
        for (String key : keys) {
            if (key.endsWith("username") && db.getValue(key).equals(username)) {
                int id = Integer.parseInt(key.split(":")[1]);
                String password = db.getValue("User:" + id + ":password");
                String firstName = db.getValue("User:" + id + ":firstName");
                String lastName = db.getValue("User:" + id + ":lastName");
                String admin = db.getValue("User:" + id + ":admin");
                if(admin == null)
                    return new User(id, firstName, lastName, username, password);
                else return new User(id, firstName, lastName, username, password, Boolean.parseBoolean(admin));
            }
        }
        return null;
    }

    public User findUserById(int id) {
        List<String> keys = db.findKeysByPrefix("User:" + id);
        if (keys.size() == 0)
            return null;
        String username = db.getValue("User:" + id + ":username");
        String password = db.getValue("User:" + id + ":password");
        String firstName = db.getValue("User:" + id + ":firstName");
        String lastName = db.getValue("User:" + id + ":lastName");
        String admin = db.getValue("User:" + id + ":admin");
        if(admin == null)
            return new User(id, firstName, lastName, username, password);
        else return new User(id, firstName, lastName, username, password, Boolean.parseBoolean(admin));
    }

    public void updateUser(User user) {
        db.deleteValue("User:" + user.getId() + ":username");
        db.putValue("User:" + user.getId() + ":username", user.getUsername());
        db.deleteValue("User:" + user.getId() + ":password");
        db.putValue("User:" + user.getId() + ":password", user.getPassword());
        db.deleteValue("User:" + user.getId() + ":firstName");
        db.putValue("User:" + user.getId() + ":firstName", user.getFirstName());
        db.deleteValue("User:" + user.getId() + ":lastName");
        db.putValue("User:" + user.getId() + ":lastName", user.getLastName());
    }
}
