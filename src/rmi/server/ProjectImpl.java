package rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectImpl extends UnicastRemoteObject implements ProjectRI {

    public ProjectImpl() throws RemoteException {
        super();
    }

    @Override
    public void print(String msg) throws RemoteException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "someone called me with msg = {0}", new Object[]{msg});
    }

    @Override
    public boolean login(String user, String password) throws RemoteException {
        //User user1 = new User("user", "password");

        System.out.println("Entering login()");
        System.out.println("Leaving login()");

        //return Objects.equals(user, user1.getUsername()) && Objects.equals(password, user1.getPassword());
        return true;
    }

}
