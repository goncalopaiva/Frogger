package edu.ufp.inf.sd.rmi.client;

import edu.ufp.inf.sd.rmi.server.FroggerGameRI;
import edu.ufp.inf.sd.rmi.server.GameSessionRI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObserverRI extends Remote {


    void update() throws RemoteException;


}
