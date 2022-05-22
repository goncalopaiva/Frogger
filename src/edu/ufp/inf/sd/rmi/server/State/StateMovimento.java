package edu.ufp.inf.sd.rmi.server.State;

import java.io.Serializable;

public class StateMovimento extends State implements Serializable {
    private Integer froggerID;
    private Integer direction;

    public StateMovimento(int pontuacao, int timer, int level, Integer id, int direction) {
        super(pontuacao, timer, level);
        this.froggerID = id;
        this.direction = direction;
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
}