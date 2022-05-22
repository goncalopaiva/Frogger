package edu.ufp.inf.sd.rmi.server.State;

import edu.ufp.inf.sd.rmi.client.Posicao;

import java.io.Serializable;

public class StateTransito extends State implements Serializable {
    private String type;
    private Posicao position;
    private Posicao velocity;
    private String spriteName;
    private long deltaMs;

    public StateTransito(int score, int timer, int level, String type, Posicao position, Posicao velocity, String spriteName, long deltaMs) {
        super(score, timer, level);
        this.type = type;
        this.position = position;
        this.velocity = velocity;
        this.spriteName = spriteName;
        this.deltaMs = deltaMs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Posicao getPosition() {
        return position;
    }

    public void setPosition(Posicao position) {
        this.position = position;
    }

    public Posicao getVelocity() {
        return velocity;
    }

    public void setVelocity(Posicao velocity) {
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
}
