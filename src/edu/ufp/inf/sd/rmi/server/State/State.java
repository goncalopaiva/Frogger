
package edu.ufp.inf.sd.rmi.server.State;

import java.io.Serializable;

public class State implements Serializable {
    private int GameScore;
    private int levelTimer;
    private int GameLevel;

    public State(int GameScore, int levelTimer, int GameLevel) {
        this.GameScore = GameScore;
        this.levelTimer = levelTimer;
        this.GameLevel = GameLevel;
    }

    public int getGameScore() {
        return GameScore;
    }

    public int getLevelTimer() {
        return levelTimer;
    }

    public int getGameLevel() {
        return GameLevel;
    }
}
