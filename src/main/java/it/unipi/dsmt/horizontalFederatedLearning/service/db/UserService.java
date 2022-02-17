package it.unipi.dsmt.horizontalFederatedLearning.service.db;

import it.unipi.dsmt.horizontalFederatedLearning.entities.*;
import it.unipi.dsmt.horizontalFederatedLearning.service.exceptions.*;

import java.util.HashMap;
import java.util.List;

public class UserService {
    private static LevelDB db;
    private static int counterID;

    static {
        db = LevelDB.getInstance();
        setCounterID();
    }

    private synchronized static void setCounterID() {
        List<String> keys = db.findKeysByPrefix("User:");
        if (keys != null)
            for (String key : keys) {
                int id = Integer.parseInt(key.split(":")[1]);
                if (id > counterID)
                    counterID = id;
            }
    }

    public static void register(User user) throws RegistrationException {
        HashMap<String, String> map = new HashMap<>();
        if (findUserByUsername(user.getUsername()) != null)
            throw new RegistrationException("Username already taken");
        if(user.getId() == 0) {
            synchronized(ExperimentService.class) {
                if(user.getId() == 0)
                    user.setId(++counterID);
            }
        }
        String prefixKey = "User:" + user.getId() + ":";
        map.put(prefixKey + "username", user.getUsername());
        map.put(prefixKey + "firstName", user.getFirstName());
        map.put(prefixKey + "lastName", user.getLastName());
        map.put(prefixKey + "password", user.getPassword());
        if(user.getAdmin() == true)
            map.put(prefixKey + "admin", String.valueOf(user.getAdmin()));
        db.putBatchValues(map);
    }

    public static User login(String username, String password) throws LoginException {
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
        throw new LoginException("Username not valid!");
    }

    public static User findUserByUsername(String username) {
        List<String> keys = db.findKeysByPrefix("User:");
        for (String key : keys) {
            if (key.endsWith("username") && db.getValue(key).equals(username)) {
                int id = Integer.parseInt(key.split(":")[1]);
                String password = db.getValue("User:" + id + ":password");
                String firstName = db.getValue("User:" + id + ":firstName");
                String lastName = db.getValue("User:" + id + ":lastName");
                boolean admin = false;
                if(db.getValue("User:" + id + ":admin") != null)
                    admin = Boolean.parseBoolean(db.getValue("User:" + id + ":admin"));
                return new User(id, firstName, lastName, username, password, admin);
            }
        }
        return null;
    }

    public static User findUserById(int id) {
        List<String> keys = db.findKeysByPrefix("User:" + id +":");
        if (keys.size() == 0)
            return null;
        String username = db.getValue("User:" + id + ":username");
        String password = db.getValue("User:" + id + ":password");
        String firstName = db.getValue("User:" + id + ":firstName");
        String lastName = db.getValue("User:" + id + ":lastName");
        boolean admin = false;
        if(db.getValue("User:" + id + ":admin") != null)
            admin = Boolean.parseBoolean(db.getValue("User:" + id + ":admin"));
        return new User(id, firstName, lastName, username, password, admin);
    }


    public static void deleteUserById(int id) {
        List<String> keys = db.findKeysByPrefix("User:"+id+":");
        for(String key: keys) {
            db.deleteValue(key);
        }
    }

    public static void updateUser(User user){
        HashMap<String, String> map = new HashMap<>();
        deleteUserById(user.getId());
        String prefixKey = "User:" + user.getId() + ":";
        map.put(prefixKey + "username", user.getUsername());
        map.put(prefixKey + "firstName", user.getFirstName());
        map.put(prefixKey + "lastName", user.getLastName());
        map.put(prefixKey + "password", user.getPassword());
        if(user.getAdmin() == true)
            map.put(prefixKey + "admin", String.valueOf(user.getAdmin()));
        db.putBatchValues(map);
    }
}
