package rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProjectRI extends Remote {

    public void print(String msg) throws RemoteException;

    public boolean login(String user, String password) throws RemoteException;


}
