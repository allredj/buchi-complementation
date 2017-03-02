/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automata;

/**
 * This class represents a Transition, holding the origin state, the destination
 * state and the transition symbol (character).
 * 
 * @author Joel Allred, University of Fribourg <joel.allred@unifr.ch>
 */
public class Transition {
    State from;
    State to;
    String ch;
    boolean lower;

    /**
     * Constructor for the State
     * @param from
     * @param to
     * @param ch 
     */
    public Transition(State from, State to, String ch) {
        this.from = from;
        this.to = to;
        this.ch = ch;
    }

    /**
     * Returns the origin state of the Transition
     * @return 
     */
    public State getFrom() {
        return from;
    }

    /**
     * Returns the destination State of the transition
     * @return 
     */
    public State getTo() {
        return to;
    }

    /**
     * Returns the symbol of the transition
     * @return 
     */
    public String getCh() {
        return ch;
    }
   
    
    
}
