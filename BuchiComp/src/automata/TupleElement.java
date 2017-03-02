/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automata;

import java.util.TreeMap;

/**
 * This class represents an element of the tuple which composes the special
 * kind of labeling of states which is used * during the construction 
 * of the complement automaton.
 * A tuple element is composed of a list of states and a colour, which is a byte
 * value which can range from 0 to 3.
 * 
 * 
 * @author Joel Allred, University of Fribourg <joel.allred@unifr.ch>
 */
public class TupleElement {
    //ArrayList<State> states = new ArrayList();
    TreeMap<Integer,State> states = new TreeMap();
    byte colour;


    /**
     * Constructor for TupleElement
     * @param states
     * @param colour 
     */
    public TupleElement(TreeMap<Integer,State> states, Byte colour) {
        this.states = states;
        this.colour = colour;
    }
    
    /**
     * Alternate empty constructor for TupleElement
     */
    public TupleElement() {
        this.states = new TreeMap();
    }



    /**
     * Returns the states contained in the tuple element
     * @return 
     */
    public TreeMap<Integer,State> getStates() {
        return states;
    }

    /**
     * Sets the entire list of states of the tuple
     * @param states 
     */
    public void setStates(TreeMap<Integer, State> states) {
        this.states = states;
    }

    /**
     * Returns the colour of the tuple
     * @return 
     */
    public Byte getColour() {
        return colour;
    }
    
    /**
     * Returns a string representing the tuple (only for test purposes)
     * @return String
     */
    @Override
    public String toString() {
        String s =  "";
        if(colour == 0||colour == 3){
            s="{";
        }
        if(colour == 1){
            s="(";
        }
        if(colour == 2){
            s="[";
        }
        
        int i=0;
        for(State state : states.values()){
            s=s+state.getLabel();
            if(i<states.size()-1){
            s=s+",";
            }
            i++;
        }
        if(colour == 0||colour == 3){
            s=s+"}";
        }
        if(colour == 1){
            s=s+")";
        }
        if(colour == 2){
            s=s+"]";
        }
        return s;
    }

    /**
     * Sets the colour of the tuple
     * @param colour 
     */
    public void setColour(Byte colour) {
        this.colour = colour;
    }
    
    /**
     * Adds a state to the list of states of the tuple
     * @param s 
     */
    public void addState(State s){
        states.put(s.getIntLabel(),s);
    }

  
}
