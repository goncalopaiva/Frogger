package edu.ufp.inf.sd.rmi.server;

import edu.ufp.inf.sd.rmi.client.ObserverRI;
import edu.ufp.inf.sd.rmi.server.GameSessionRI;
import edu.ufp.inf.sd.rmi.server.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DB extends UnicastRemoteObject {

    private static DB db = null;
    private final Set<User> users;
    private final HashMap<String, GameSessionRI> sessions;
    private final HashMap<String, FroggerGameRI> games;
    private final HashMap<String, ObserverRI> workers;

    private DB() throws RemoteException {
        this.users = new HashSet<>();
        this.workers  = new HashMap<>();
        this.sessions = new HashMap<>();
        this.games = new HashMap<>();

        User user1 = new User("admin", "admin");
        User user2 = new User("ufp", "ufp");
        User user3 = new User("user", "user");
        User user4 = new User("guest", "guest");
        this.users.add(user1);
        this.users.add(user2);
        this.users.add(user3);
        this.users.add(user4);

    }

    public static synchronized DB getInstance() throws RemoteException {
        if (db == null)
            db = new DB();
        return db;
    }

    public Set<User> getUsers() {
        return users;
    }



    public boolean existsUser(String uname, String pw) throws RemoteException {
        for (User u : this.users) {
            if (u.getName().compareTo(uname) == 0 && u.getPassword().compareTo(pw) == 0)
                return true;
        }
        return false;
    }

    public User getUserByCredentials(String uname, String pw) throws RemoteException {
        for (User u : this.users) {
            if (u.getName().compareTo(uname) == 0 && u.getPassword().compareTo(pw) == 0)
                return u;
        }
        return null;
    }

    public boolean registerUser(String uname, String pw) throws RemoteException {
        if (!existsUser(uname, pw)) {
            users.add(new User(uname, pw));
            return true;
        }
        return false;
    }

    public HashMap<String, GameSessionRI> getSessions() {
        return sessions;
    }

    public void addWorker(String user, ObserverRI worker) {
        workers.put(user, worker);
    }

    public void removeWorker(String user, ObserverRI worker) {
        workers.remove(user, worker);
    }

    public void addSession(String user, GameSessionRI session) {
        sessions.put(user, session);
    }

    public void removeSession(String user, GameSessionRI session) {
        sessions.remove(user, session);
    }

    public void addGame(String user, FroggerGameRI game) {
        games.put(user, game);
    }

    public void removeSession(String user, FroggerGameRI game) {
        games.remove(user, game);
    }
}
