/*
 * Gonçalo Paiva Copyright (c) 2022.
 *
 */

package edu.ufp.inf.sd.rmi.server;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class GameSessionImpl extends UnicastRemoteObject implements GameSessionRI {

    private DB db;
    private HashMap<String, FroggerGameRI> games;
    private FroggerServer server;
    private User user;
    private String username;

    public GameSessionImpl(FroggerServer server, User user) throws RemoteException {
        super();
        this.games = new HashMap<>();
        this.server = server;
        this.user = user;
        this.username = user.getName();
        this.db = DB.getInstance();
    }

    @Override
    public User getUser() throws RemoteException{
        return this.user;
    }

    @Override
    public String getUsername() throws RemoteException {
        return this.username;
    }

    @Override
    public HashMap getGames() throws RemoteException{
        return this.games;
    }

    @Override
    public FroggerGameRI createGame(int dificuldade) throws RemoteException{
        FroggerGameRI froggerGameRI = new FroggerGameImpl();
        games.put(user.getName(), froggerGameRI);
        db.addGame(user.getName(), froggerGameRI);
        return  froggerGameRI;
    }

    @Override
    public String listFoggerGames() throws RemoteException{
        StringBuilder stringBuilder = new StringBuilder();
        /*if (!db.getGames().isEmpty()){
            for (FroggerGameImpl fg : db.getGames()) {
                stringBuilder.append(fg.toString()).append("\n");
            }
            return stringBuilder.toString();
        }

         */
        return "No games";
    }

    /*
    @Override
    public FroggerGameRI newGame(FroggerClient client, int dificuldade) throws RemoteException {
        FroggerGameRI game = new FroggerGameImpl();
        this.games.put(user.getName(), game);
        return game;
    }

     */

}

