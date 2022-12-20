package edu.ufp.inf.sd.rmi.server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public interface GameSessionRI extends Remote {

    User getUser() throws RemoteException;

    String getUsername() throws RemoteException;

    //HashMap getGames() throws RemoteException;

    ArrayList<FroggerGameRI> getGames() throws RemoteException;

    FroggerGameRI createGame(int dificuldade) throws IOException, TimeoutException;

    void createGame1(int dificuldade) throws IOException, TimeoutException;


}
