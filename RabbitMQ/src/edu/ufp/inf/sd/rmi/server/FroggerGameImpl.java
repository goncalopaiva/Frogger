package edu.ufp.inf.sd.rmi.server;

import edu.ufp.inf.sd.rmi.client.ObserverRI;
import edu.ufp.inf.sd.rmi.server.State.State;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class FroggerGameImpl extends UnicastRemoteObject implements FroggerGameRI {

    private ArrayList<ObserverRI> observers;
    private State state;

    private int dificuldade;

   public FroggerGameImpl() throws RemoteException {
       super();
       this.observers = new ArrayList<>();
   }

    @Override
    public int getDificuldade() throws RemoteException{
        return dificuldade;
    }

    @Override
    public void setDificuldade(int dificuldade) throws RemoteException {
        this.dificuldade = dificuldade;
    }

    @Override
    public void attachGame(ObserverRI observer) throws RemoteException {

        System.out.println("FroggerGameImpl -> attachGame()");

        observers.add(observer);
        observer.setGame(this);
        observer.setPlayerNumber(observers.size() -1);

        DB.getInstance().game = this;

        if (observers.size() == 2) {
            updateGameState();
        }
        else {
            for (ObserverRI observerRI : observers) {
             observerRI.waitUsers();
            }
        }


        System.out.println("FroggerGameImpl -> attachGame()");
    }

    @Override
    public void dettachGame(ObserverRI observer) throws RemoteException {
        observers.remove(observer);
    }

    @Override
    public void updateGameState() throws RemoteException {
        System.out.println("FroggerGameImpl -> updateGameState()");
        for(ObserverRI observer : observers) {
            observer.update(this.state);
        }
    }

    @Override
    public void setGameState(State state) throws RemoteException {
        System.out.println("\t\tFroggerGameImpl -> setGameState() -> " + state.toString());
        this.state = state;
        updateGameState();
    }

    @Override
    public State getGameState() throws RemoteException {
        return this.state;
    }

    @Override
    public ArrayList<ObserverRI> getObservers() throws RemoteException{
        return observers;
    }

}
