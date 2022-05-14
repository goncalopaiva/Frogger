package edu.ufp.inf.sd.rmi.server;

import edu.ufp.inf.sd.rmi.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FroggerGameRI extends Remote {
    void attach(ObserverRI observerRI) throws RemoteException;

    void detach(ObserverRI obsRI) throws RemoteException;

    State getState() throws RemoteException;

    void setGameState(State state) throws RemoteException;

    void startGame() throws RemoteException;
}
