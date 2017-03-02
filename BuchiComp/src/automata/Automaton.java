/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automata;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * This is the container for all the automaton elements (states, initial state,
 * alphabet, transitions and accepting states)
 *
 * States are stored in a TreeMap<String,State> where the key is the label
 * outputted by the getString() method of the TupleLabel.
 *
 * @author Joel Allred, University of Fribourg <joel.allred@unifr.ch>
 */
public class Automaton {

    private final TreeMap<String, State> Q = new TreeMap();
    private State init;
    private ArrayList<String> Sigma = new ArrayList();
    private final ArrayList<Transition> delta = new ArrayList();
    private final ArrayList<State> accepting = new ArrayList();
    private long time = 0;
    private boolean emptyLanguage = true;
    private final SccCollection SCCs = new SccCollection();

    public boolean isEmptyLanguage() {
        return emptyLanguage;
    }

    public void setEmptyLanguage(boolean emptyLanguage) {
        this.emptyLanguage = emptyLanguage;
    }

    public SccCollection getSCCCollection() {
        return SCCs;
    }

    public void addSCC(SCC scc) {
        //System.out.println("adding SCC: " + scc.getString());

        /* Check each scc if common states with new scc */
        for (int i = 0; i < SCCs.getArray().size(); i++) {
            SCC seti = SCCs.getArray().get(i);
            for (State s : scc) {
                if (seti.contains(s)) {
                    /* add states of existing set to new scc */
                    scc.addAll(seti);
                    /* remove existing set */
                    SCCs.getArray().remove(seti);
                    i--;
                    break;
                }
            }
        }

        if (!scc.isEmpty()) {
            State[] set = new State[scc.size()];
            scc.toArray(set);
            SCCs.getArray().add(scc);
        }

        /* add SCC to each state */
        State[] states = new State[scc.size()];
        for (State state : scc.toArray(states)) {
            state.setSCC(scc, Sigma);
        }

    }

    public SCC getSCC(State s) {

        for (SCC scc : SCCs.getArray()) {
            if (scc.contains(s)) {
                return scc;
            }
        }

        /* if no SCC, return null */
        return null;

    }

    /**
     * Returns the time needed to compute the complementation
     *
     * @return
     */
    public long getTime() {
        return time;
    }

    /**
     * Returns true iff the Automaton is complete
     *
     * @return
     */
    public boolean isComplete() {

        /* for each state in the input automaton */
        for (State s : Q.values()) {
            /* for each symbol */
            for (String c : Sigma) {
                /* if no transition exists for that symbol, return true */
                if (!s.transitionExists(c)) {
                    return false;

                }
            }
        }
        return true;
    }

    /**
     * Sets the value of the time needed to compute the complementation
     *
     * @param time
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Basic constructor for Automaton
     */
    public Automaton() {
    }

    /**
     * Adds regular state to q to the HashMap of Automaton
     *
     * @param q new State to be added to Automaton
     */
    public void addState(State q) {
        Q.put(q.getLabel(), q);
    }

    /**
     * Add tuple state (composed) to the HashMap (list of states) of Automaton
     *
     * @param q
     */
    public void addTupleState(State q) {
        Q.put(q.getLabel(), q);
        System.out.println("adding state " + q.getLabel());
    }

    /**
     * Returns the alphabet of the Automaton
     *
     * @return The ArrayList of alphabet
     */
    public ArrayList<String> getSigma() {
        return Sigma;
    }

    /**
     * Sets the alphabet of the Automaton
     *
     * @param Sigma The ArrayList of alphabet
     */
    public void setSigma(ArrayList<String> Sigma) {
        this.Sigma = Sigma;
    }

    /**
     * Returns the HashMap of States
     *
     * @return the HashMap of States
     */
    public TreeMap<String, State> getStates() {
        return Q;
    }

    /**
     * Returns the State of the HashMap that corresponding to key id
     *
     * @param id
     * @return
     */
    public State getStateById(String id) {
        return Q.get(id);

    }

    /**
     * Sets q as the unique initial state of Automaton
     *
     * @param q initial state
     */
    public void setInit(State q) {
        this.init = q;
    }

    /**
     * Returns the unique initial state of Automaton
     *
     * @return initial state
     */
    public State getInit() {
        return init;
    }

    /**
     * Adds a transition to the list of transitions of Automaton
     *
     * @param t transition
     */
    public void addTransition(Transition t) {
        this.delta.add(t);
        /* add transition to state */
        State s = t.getFrom();
        this.getStateById(s.getLabel()).addTransition(t);
    }

    /**
     * Returns the entire list of transitions
     *
     * @return transitions
     */
    public ArrayList<Transition> getTransitions() {
        return delta;
    }

    /**
     * Returns the list of accepting states
     *
     * @return accepting states
     */
    public ArrayList<State> getAccepting() {
        return accepting;
    }

    /**
     * Returns the number of states in the upper part
     *
     * @return number of states in upper part
     */
    public int getSizeUpperPart() {
        int i = 0;

        for (String key : this.Q.keySet()) {
            if (Q.get(key).isUpper()) {
                i++;
            }
        }
        return i;
    }

    /**
     * Returns the number of states in the lower part
     *
     * @return number of states in lower part
     */
    public int getSizeLowerPart() {
        int i = 0;

        for (String key : this.Q.keySet()) {
            if (!Q.get(key).isUpper()) {
                i++;
            }
        }
        return i;
    }

    /**
     * Adds state q to list of accepting states of Automaton
     *
     * @param q
     */
    public void addAccepting(State q) {
        this.accepting.add(q);
    }

}
