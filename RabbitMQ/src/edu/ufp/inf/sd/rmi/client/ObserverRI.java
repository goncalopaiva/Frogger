package edu.ufp.inf.sd.rmi.client;

import edu.ufp.inf.sd.rmi.server.FroggerGameRI;
import edu.ufp.inf.sd.rmi.server.State.State;
import edu.ufp.inf.sd.rmi.server.State.StateTransito;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObserverRI extends Remote {


    void setGame(FroggerGameRI game) throws RemoteException;

    void waitUsers() throws RemoteException;

    void update(State state) throws RemoteException;

    void move(Integer playerIndex, Integer direction) throws RemoteException;

    void setPlayerNumber(Integer number) throws RemoteException;

    void moverTransito(StateTransito stateTransito) throws RemoteException;

    String getUsername() throws RemoteException;

    void setUsername(String username) throws RemoteException;
}
