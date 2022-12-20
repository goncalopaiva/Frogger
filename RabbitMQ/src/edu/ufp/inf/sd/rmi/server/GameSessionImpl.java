/*
 * Gonçalo Paiva Copyright (c) 2022.
 *
 */

package edu.ufp.inf.sd.rmi.server;


import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class GameSessionImpl extends UnicastRemoteObject implements GameSessionRI {

    private DB db;
    private ArrayList<FroggerGameRI> games;
    private FroggerServer server;
    private User user;
    private String username;

    public GameSessionImpl(FroggerServer server, User user) throws RemoteException {
        super();
        this.games = new ArrayList<>();
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
    public ArrayList<FroggerGameRI> getGames() throws RemoteException {
        return this.games;
    }

    @Override
    public FroggerGameRI createGame(int dificuldade) throws IOException {
        FroggerGameRI froggerGameRI = new FroggerGameImpl();
        games.add(froggerGameRI);
        db.addGame(user.getName(), froggerGameRI);
        return froggerGameRI;
    }

    @Override
    public void createGame1(int dificuldade) throws IOException, TimeoutException {
        FroggerGameRI froggerGameRI = new FroggerGameImpl();
        games.add(froggerGameRI);
        db.addGame(user.getName(), froggerGameRI);
    }



}

