/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automata;

import java.util.ArrayList;

/**
 * This class represents a state of the Automaton
 * 
 * The label can be either only a string (e.g. for input and output automata )
 * or can hold a tupleLabel as label (for the interim automaton of the algorithm.)
 * 
 * For performance reasons, the State holds a reference to its outgoing transitions.
 * 
 * @author Joel Allred, University of Fribourg <joel.allred@unifr.ch>
 */
public class State{

    /**
     * local variables
     */
    private boolean accepting = false;
    private String label;
    private final TupleLabel tupleLabel;
    private int intLabel;
    private final ArrayList<Transition> transitions = new ArrayList();
    private SCC scc;
    private int preorder=-1; //for finding SSCs
    
    /* Set of characters authorised in lower part (within same SCC)
    * This variable is only for states of the upper part.
    */
    private final ArrayList<String> authLowerTrans = new ArrayList();
    
    /* corresponding upper state of a lower state 
    (to determine SCC) */
    private State upperState;


    

    /**
     * Constructor for the State (simple string version)
     * @param label 
     * @param intLabel

     */
    public State(String label, int intLabel) {
        this.label = label;
        this.intLabel = intLabel;
        this.tupleLabel = null;
    }

    
    /**
     * Constructor (alternate) for the State, which records the tupleLabel,
     * as well as constructing the corresponding string (necessary for hashing)
     * @param tupleLabel
     * @param intLabel
     */
    public State(TupleLabel tupleLabel, int intLabel) {
        this.tupleLabel = tupleLabel;
        this.label = tupleLabel.getString();
        this.intLabel = intLabel;
    }
    


    public void setSCC(SCC scc, ArrayList<String> sigma){
        /* update variable */
        this.scc = scc;
        
        /* set authorised transitions of the lower part */
        for(String ch : sigma){
                State successor = this.getDestination(ch).get(0); //(upper autom is deterministic)
                if(scc!=null && scc.contains(successor)){
                    this.addAuthLower(ch);
                }
            }
        
    }
    
    public SCC getSCC(){
        return scc;
    }
    
    public void addAuthLower(String ch){
        authLowerTrans.add(ch);
    }
    
    public ArrayList<String> getAuthLower(){
        return authLowerTrans;
    }
    
    public boolean isInAuthLower(String c){
        return authLowerTrans.contains(c);
    }
    
    public void setUpper(State s){
        upperState=s;
    }
    
    public State getUpper(){
        return upperState;
    }
    
    
    /**
     * Returns true if the state is accepting, false otherwise
     * @return 
     */
    public boolean isAccepting() {
        return accepting;
    }

    /**
     * Sets the acceptance of the state to true or false
     * @param accepting 
     */
    public void setAccepting(boolean accepting) {
        this.accepting = accepting;
    }

    /**
     * Returns the String label of the State
     * @return 
     */
    public String getLabel() {
        return label;
    }

    
    public void setLabel(String s){
        label=s;
    }


    /**
     * Returns the tuple label of the State
     * (This may be null in the case of the import or export automata,
     * which only uses the String value label).
     * @return TupleLabel
     */
    public TupleLabel getTupleLabel() {
        return tupleLabel;
    }

    /**
     * Returns the integer value of the simple label of the State
     * @return intLabel
     */
    public int getIntLabel() {
        return intLabel;
    }
    
     /**
     * Sets the value of the int label
     * @param label
     */
    public void setIntLabel(int label) {
        intLabel=label;
    }
    
    public String getIntLabelAsString() {
        return Integer.toString(intLabel);
    }


    /**
     * Adds a single transition to the list of transitions
     * @param t 
     */
    public void addTransition(Transition t) {
        this.transitions.add(t);
    }

    /**
     * Returns the list of all states such that there exists a transition
     * of which this state is the origin.
     * @param c
     * @return list of states
     */
    public ArrayList<State> getDestination(String c) {
        ArrayList<State> result = new ArrayList();
        for (Transition t : transitions) {
            if (t.getCh().equals(c)) {
                result.add(t.getTo());
            }
        }
        return result;
    }
    
    /**
     * Returns true iff transition exists for symbol c
     * @param c
     * @return true iff transition exists for symbol c
     */
    public boolean transitionExists(String c) {
        boolean b = false;
        for (Transition t : transitions) {
            if (t.getCh().equals(c)) {
                b = true;
            }
        }
        return b;
    }
    
    
    /**
     * 
     * @return true if the state is in the upper part of the automaton,
     * i.e. it contains only elements of colour 3
     */
    public boolean isUpper(){
        for(TupleElement te : tupleLabel.getElements()){
            if(te.getColour()<3){
                return false;
            }
        }
        return true;
    }

    public int getPreorder() {
        return preorder;
    }

    public void setPreorder(int preorder) {
        this.preorder = preorder;
    }
   

    
}
