package edu.ufp.inf.sd.rmi.client;

import edu.ufp.inf.sd.rmi.client.frogger.Main;
import edu.ufp.inf.sd.rmi.server.FroggerGameRI;
import edu.ufp.inf.sd.rmi.server.State.State;
import edu.ufp.inf.sd.rmi.server.State.StateTransito;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {

    private FroggerGameRI game;
    private State state;
    private boolean gameReady;
    private Main gameInst;
    public int playerNumber;
    private String username;

    protected ObserverImpl() throws RemoteException {
        super();
        gameReady = false;
    }

    @Override
    public void setGame(FroggerGameRI game) throws RemoteException {
        this.game = game;
    }

    @Override
    public void waitUsers() throws RemoteException{
        //JOptionPane.showConfirmDialog(null, "Waiting for other users");
        System.out.println("ObserverImpl -> waitUsers -> Waiting for other users");
    }

    @Override
    public void update(State state) throws RemoteException {
        if(!gameReady) {

            Runnable runnable = () -> {
                try {
                    this.gameInst = new Main(playerNumber);
                    this.gameInst.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();
            gameReady = true;

        } else {
            state.execute(this);
        }
        this.state = state;
    }

    @Override
    public void move(Integer playerIndex, Integer direction) throws RemoteException {
        if(gameInst != null) {
            this.gameInst.move(playerIndex, direction);
        }
    }


    @Override
    public void moverTransito(StateTransito event) throws RemoteException {
        if(gameInst != null) {
            gameInst.transito(event.getTipo(), event.getPosition(), event.getVelocity(),
                    event.getSpriteName(), event.getDeltaMs());
        }
    }


    @Override
    public void setPlayerNumber(Integer number) throws RemoteException{
        playerNumber = number;
    }

    @Override
    public String getUsername() throws RemoteException {
        return username;
    }

    @Override
    public void setUsername(String username) throws RemoteException{
        this.username = username;
    }
}
