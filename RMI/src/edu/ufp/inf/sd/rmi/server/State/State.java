
package edu.ufp.inf.sd.rmi.server.State;

import edu.ufp.inf.sd.rmi.client.ObserverRI;

import java.io.Serializable;

public class State implements Serializable {
    private int pontuacao;
    private int tempo;
    private int nivel;

    public State(int pontuacao, int tempo, int nivel) {
        this.pontuacao = pontuacao;
        this.tempo = tempo;
        this.nivel = nivel;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public void execute(ObserverRI observerRI) {

    }

    public String toString() {
        return "State,"+ pontuacao +","+ tempo+","+ nivel;
    }
}
