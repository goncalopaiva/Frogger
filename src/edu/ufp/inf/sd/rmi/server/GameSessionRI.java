package edu.ufp.inf.sd.rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface GameSessionRI extends Remote {

    User getUser() throws RemoteException;

    String getUsername() throws RemoteException;

    HashMap getGames() throws RemoteException;

    FroggerGameRI createGame(int dificuldade) throws RemoteException;

    String listFoggerGames() throws RemoteException;

    //FroggerGameRI newGame(FroggerClient client, int dificuldade) throws RemoteException;
}
