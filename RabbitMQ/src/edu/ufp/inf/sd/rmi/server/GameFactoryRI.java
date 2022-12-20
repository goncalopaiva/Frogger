package edu.ufp.inf.sd.rmi.server;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface GameFactoryRI extends Remote {

    boolean register(String username, String password) throws RemoteException;

    GameSessionRI login(String username, String password) throws RemoteException;

    HashMap<String, GameSessionRI> getSessions() throws RemoteException;

    void setSessions(HashMap<String, GameSessionRI> sessions) throws RemoteException;
}
