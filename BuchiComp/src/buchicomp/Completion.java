/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buchicomp;

import automata.*;

/**
 *
 * 
 *
 * @author Joel Allred, University of Fribourg <joel.allred@unifr.ch>
 */
public abstract class Completion {

    private static Automaton iA; //input automaton

    /**
     * Completes the input
     *
     * @param a the input automaton
     * @return the complement automaton
     */
    public static Automaton Complete(Automaton a) {

        iA = a;      

        inputCompletion();


        /* return the constructed automaton */
        return iA;
    }
    
    
    /* Makes the input automaton complete */
    private static void inputCompletion(){
        
        /* check if already complete */
        
        boolean alreadyComplete = iA.isComplete();
       
        
        if (!alreadyComplete) {
            /* add sink state */
            System.out.println("Creating sink state");
            State sink = new State("sink", -1);
            iA.addState(sink);

            /*
             * add missing transisitions
             */
            /* for each state in the input automaton */
            for (State s : iA.getStates().values()) {

                /* for each symbol */
                for (String c : iA.getSigma()) {

                    /* if no transition exists for that symbol, add a transition to the sink state */
                    if (!s.transitionExists(c)) {
                        System.out.println("adding transition " + s.getLabel() + "--" + c + "->" + sink.getLabel());
                        Transition t = new Transition(s, sink, c);
                        iA.addTransition(t);
                    }
                }
            }
        } else {

            System.out.println("Automaton is already complete.");
        }
    }

}
