package edu.ufp.inf.sd.rmi.client;

import edu.ufp.inf.sd.rmi.client.ObserverRI;
import edu.ufp.inf.sd.rmi.server.FroggerGameRI;
import edu.ufp.inf.sd.rmi.server.State;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {

    private String id;
    private FroggerClient client;
    private State lastObserverState;
    protected FroggerGameRI subjectRI;

    public ObserverImpl(String id, FroggerClient client, FroggerGameRI subjectRI) throws RemoteException{
        super();
        this.client = client;
        this.id = id;
        this.lastObserverState =  new State(id,"");
        this.subjectRI = subjectRI;
        this.subjectRI.attach(this);
    }

    @Override
    public void update() throws RemoteException{
        this.lastObserverState=subjectRI.getState();
        //this.chatFrame.updateTextArea();
        this.client.updateGamesDecorrer();
        this.client.updateUsers();
    }

    protected State getLastObserverState(){
        return this.lastObserverState;
    }
}
