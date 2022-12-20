package edu.ufp.inf.sd.rmi.server.State;

import edu.ufp.inf.sd.rmi.client.ObserverRI;

import java.io.Serializable;
import java.rmi.RemoteException;

public class StateMovimento extends State implements Serializable {
    private Integer froggerID;
    private Integer direction;

    public StateMovimento(int pontuacao, int timer, int level, Integer id, int direction) {
        super(pontuacao, timer, level);
        this.froggerID = id;
        this.direction = direction;
    }

    public void execute(ObserverRI observerRI) {
        try {
            observerRI.move(froggerID, direction);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public Integer getFroggerID() {
        return froggerID;
    }

    public void setFroggerID(Integer froggerID) {
        this.froggerID = froggerID;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public String toString() {
        return "StateMovimento,"+getPontuacao()+","+getTempo()+","+getNivel()+","+froggerID+","+direction;
    }
}