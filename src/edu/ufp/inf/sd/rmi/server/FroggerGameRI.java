package edu.ufp.inf.sd.rmi.server;

import edu.ufp.inf.sd.rmi.client.ObserverRI;
import edu.ufp.inf.sd.rmi.server.State.State;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FroggerGameRI extends Remote {


    int getDificuldade() throws RemoteException;

    void setDificuldade(int dificuldade) throws RemoteException;

    void attachGame(ObserverRI observer) throws RemoteException;

    void dettachGame(ObserverRI observer) throws RemoteException;

    void updateGameState() throws RemoteException;

    void setGameState(State state) throws RemoteException;

    State getGameState() throws RemoteException;
}
