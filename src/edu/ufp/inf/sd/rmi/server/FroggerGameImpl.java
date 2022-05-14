package edu.ufp.inf.sd.rmi.server;


import edu.ufp.inf.sd.rmi.client.ObserverRI;
import edu.ufp.inf.sd.rmi.client.frogger.Main;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class FroggerGameImpl extends UnicastRemoteObject implements FroggerGameRI {

    private State subjectState;
    private ArrayList<ObserverRI> observers = new ArrayList<>();

    protected FroggerGameImpl() throws RemoteException {
        super();
        this.subjectState = new State("","");
    }


    @Override
    public void attach(ObserverRI observerRI) throws RemoteException{
        if(!this.observers.contains(observerRI)) this.observers.add(observerRI);
    }

    @Override
    public void detach(ObserverRI obsRI) throws RemoteException {
        this.observers.remove(obsRI);
    }

    @Override
    public State getState() throws RemoteException {
        return this.subjectState;
    }

    @Override
    public void setGameState(State state) throws RemoteException{
        this.subjectState = state;
        //this.notifyAllObservers();
    }

    @Override
    public void startGame() throws RemoteException {
        System.out.println("Server -> FroggerGameImpl -> startGame()");

    }

    public void notifyAllObservers() throws RemoteException{
        for(ObserverRI obs : observers){
            try{
                obs.update();
            } catch (RemoteException ex){
                System.out.println(ex.toString());
            }
        }
    }


}
