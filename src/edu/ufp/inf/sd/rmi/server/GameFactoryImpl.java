package edu.ufp.inf.sd.rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class GameFactoryImpl extends UnicastRemoteObject implements GameFactoryRI {

    private DB db;
    private HashMap<String, GameSessionRI> sessions;
    private FroggerServer server;

    public GameFactoryImpl(FroggerServer server) throws RemoteException {
        super();
        this.sessions = new HashMap<>();
        this.server = server;
        this.db = DB.getInstance();
    }

    @Override
    public boolean register(String username, String password) throws RemoteException{
        System.out.println("GameFactoryImpl -> User " + username + " registered.");
        return db.registerUser(username, password);
    }

    @Override
    public GameSessionRI login(String username, String password) throws RemoteException {
        if (db.existsUser(username, password)) {
            if (!sessions.containsKey(username)) {
                GameSessionRI sessionRI = new GameSessionImpl(this.server, db.getUserByCredentials(username, password));
                this.sessions.put(username, sessionRI);
                //this.server.getSessions().put(username, sessionRI);
                System.out.println("GameFactoryImpl -> User " + username + " login.");
                db.addSession(username, sessionRI);
                return sessionRI;
            } else {
                return sessions.get(username);
            }
        }
        return null;
    }

    protected void removeSession(String username) throws RemoteException {
        this.sessions.remove(username);
        //this.server.getUsers().remove(username);
        //this.server.getSessions().remove(username);
        System.out.println("GameFactoryImpl -> User " + username + " logout.");
    }

    @Override
    public HashMap<String, GameSessionRI> getSessions() throws RemoteException {
        return sessions;
    }

    @Override
    public void setSessions(HashMap<String, GameSessionRI> sessions) throws RemoteException{
        this.sessions = sessions;
    }
}