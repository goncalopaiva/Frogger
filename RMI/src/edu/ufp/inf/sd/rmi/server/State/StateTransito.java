package edu.ufp.inf.sd.rmi.server.State;

import edu.ufp.inf.sd.rmi.client.ObserverRI;
import edu.ufp.inf.sd.rmi.server.Coordenadas;

import java.io.Serializable;
import java.rmi.RemoteException;

public class StateTransito extends State implements Serializable {
    private String tipo;
    private Coordenadas position;
    private Coordenadas velocity;
    private String spriteName;
    private long deltaMs;

    public StateTransito(int score, int timer, int level, String type, Coordenadas position, Coordenadas velocity, String spriteName, long deltaMs) {
        super(score, timer, level);
        this.tipo = type;
        this.position = position;
        this.velocity = velocity;
        this.spriteName = spriteName;
        this.deltaMs = deltaMs;
    }

    public void execute(ObserverRI observerRI) {
        try {
            observerRI.moverTransito(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Coordenadas getPosition() {
        return position;
    }

    public void setPosition(Coordenadas position) {
        this.position = position;
    }

    public Coordenadas getVelocity() {
        return velocity;
    }

    public void setVelocity(Coordenadas velocity) {
        this.velocity = velocity;
    }

    public String getSpriteName() {
        return spriteName;
    }

    public void setSpriteName(String spriteName) {
        this.spriteName = spriteName;
    }

    public long getDeltaMs() {
        return deltaMs;
    }

    public void setDeltaMs(long deltaMs) {
        this.deltaMs = deltaMs;
    }

    public String toString() {
        return "StateTransito,"+ getPontuacao()+","+ getTempo()+"," + getNivel()+","+ tipo +","+
                position.toString()+","+ position.toString()+","+ spriteName+","+ +deltaMs;
    }
}
