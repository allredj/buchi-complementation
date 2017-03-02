/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buchicomp;

import automata.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeMap;

/**
 *
 * This is the main class of the algorithm. It contains a static method that
 * takes a Buechi automaton as input and returns the complement Buechi
 * automaton.
 *
 * @author Joel Allred, University of Fribourg <joel.allred@unifr.ch>
 */
public abstract class TupleConstruction {

    private static Automaton iA; //input automaton
    private static Automaton oA; //output automaton
    private static long time;
    private static boolean isComplete;
    private static boolean leftOptim;
    private static boolean twoOneOptim;
    private static boolean deleteZeroLessOptim;
    private static int simpleLabel;
    private static boolean SCCStableOptim;
    private static int scc_C=0;
    private static Stack<State> scc_S = new Stack();
    private static Stack<State> scc_P = new Stack();

    /**
     * This method takes a Buechi automaton "a" as input and returns a Buechi
     * automaton which accepts the complement language
     *
     * @param a the input automaton
     * @param opt use completeness optimisation
     * * @param leftOptim use left optimisation optimisation
     * @param leftOpt true to apply left optimisation
     * @param twoOneOpt true to apply 2-1-optimisation
     * @param deleteZeroLess true to apply single bracket optimisation
     * @return the complement automaton
     */
    public static Automaton tupleConstruction(Automaton a, boolean opt, boolean leftOpt, boolean twoOneOpt, boolean deleteZeroLess, boolean SCCStable) {

        iA = a;
        isComplete = opt && iA.isComplete();
        leftOptim = leftOpt;
        twoOneOptim = twoOneOpt;
        deleteZeroLessOptim = deleteZeroLess;
        SCCStableOptim = SCCStable;
        //System.out.println("size of input: " + iA.getStates().size());
        oA = new Automaton();

        /* set alphabet (same as input automaton) */
        oA.setSigma(iA.getSigma());

        /* check execution time */
        Long startTime = System.currentTimeMillis();
        
        simpleLabel=0;
        
        /* Apply the construction */
        System.out.println("Upper construction");
        upperConstruction();
        
        /* find SCCs */
        if(SCCStableOptim){
            System.out.println("Find SCCs");
            findSCCs(oA.getInit());
            //System.out.println("Set of SCCs = " + oA.getSetofSCC().toString());
            System.out.println("# of SCCs: " + oA.getSCCCollection().getArray().size());
            
        }

        /* assign SCCs and authorised transitions of the lower part */
        if (SCCStableOptim) {
            //System.out.println("Assign SCCs");
            //assignSCCsAndAuth();

            for (State s : oA.getStates().values()) {
                System.out.println("State " + s.getLabel() + " has auth trans " + s.getAuthLower());
            }
        }

        System.out.println("lower construction");
        lowerConstruction();

        time = System.currentTimeMillis() - startTime;
        System.out.println("complementation time: " + time + " ms");
        oA.setTime(time);

        /* return the constructed automaton */
        return oA;
    }

    

    /**
     * Applies the tuple construction to construct a new automaton representing
     * the complement of the input automaton.
     *
     */
    private static void upperConstruction() {

        /* initialise working set, which holds states still to be processed */
        LinkedList<State> workingSet = new LinkedList();

        /* create initial state out of a set containing only the initial state
         * of the input automaton  */
        State initState = iA.getInit();
        TreeMap<Integer, State> initSet = new TreeMap();
        initSet.put(0, initState);

        /* set colour of initial state to 3 (upper automaton) */
        TupleElement initEl = new TupleElement(initSet, (byte) 3);

        /* Create tupleLabel for initial state */
        ArrayList<TupleElement> initTuple = new ArrayList();
        initTuple.add(initEl);
        TupleLabel initLabel = new TupleLabel(initTuple);


        /* Create initial state */
        State init = new State(initLabel, simpleLabel++);
        
        /* initialise dummy upper state */
        init.setUpper(init);

        /* add initial state to the output automaton */
        oA.addTupleState(init);
        oA.setInit(init);

        /* add initial state to working set */
        workingSet.add(init);


        /* while working set is non empty */
        while (!workingSet.isEmpty()) {

            /* take first element of working set */
            State currentWorkingState = workingSet.pop();

            /* for each symbol */
            for (String c : oA.getSigma()) {

                /* initialize new tuple for upper part of automaton */
                TupleLabel newUpperTupleLabel = new TupleLabel();

                /* list of already added states (to remove duplicates from right) */
                HashSet<String> added = new HashSet();

                /* for each element in predecessor tuple (start from right) */
                int iterator = currentWorkingState.getTupleLabel().getElements().size() - 1;
                while (iterator >= 0) {

                    TupleElement predecessorElement = currentWorkingState.getTupleLabel().getElements().get(iterator);

                    /* initialize successor states for upper automaton*/
                    TupleElement upperRightSucc = new TupleElement(); //right successor
                    TupleElement upperLeftSucc = new TupleElement(); // left successor


                    /* build right and left sets successors (upper and lower part) */
                    for (State stateInCurrent : predecessorElement.getStates().values()) {

                        for (State succState : stateInCurrent.getDestination(c)) {

                            /* put in right set if accepting and not already exists */
                            if (succState.isAccepting() && !added.contains(succState.getLabel())) {
                                upperRightSucc.addState(succState);
                                added.add(succState.getLabel());
                            }

                            /* put in left set if non-accepting and not already exists */
                            if (!succState.isAccepting() && !added.contains(succState.getLabel())) {

                                upperLeftSucc.addState(succState);
                                added.add(succState.getLabel());
                            }

                        }
                    }

                    /* colour the sets (upper) */
                    upperRightSucc.setColour((byte) 3);
                    upperLeftSucc.setColour((byte) 3);          

                    /* if the set of accepting successors is not-empty */
                    if (!upperRightSucc.getStates().isEmpty()) {
                        newUpperTupleLabel.addElementLeft(upperRightSucc);
                    }

                    if (!upperLeftSucc.getStates().isEmpty()) {
                        newUpperTupleLabel.addElementLeft(upperLeftSucc);
                    }

                    iterator--;
                }


                /* even if the tuple is empty, add the new state to the upper part
                 (acts as a sink state if tuple empty) */

                /* add new state to upper part */
                State upperState;

                /* also add new state in upper aut. if current state is in upper aut. */
                if (currentWorkingState.isUpper()) {

                    /**
                     * Check if the constructed label exists already in the
                     * automaton,
                     */
                    /* if does not exist in automaton */
                    if (!oA.getStates().containsKey(newUpperTupleLabel.getString())) {

                        /* create the state */
                        upperState = new State(newUpperTupleLabel, simpleLabel++);
                        
                        /* associate dummy upperState */
                        upperState.setUpper(upperState);

                        /* add state to automaton */
                        oA.addTupleState(upperState);

                        /* add newly created state to working set */
                        workingSet.add(upperState);

                        /* if tuple is empty, set as accepting (sink state) */
                        if (newUpperTupleLabel.getElements().isEmpty()) {
                            upperState.setAccepting(true);
                            oA.addAccepting(upperState);
                            System.out.println("setting " + upperState.getLabel() + " as accepting");

                        }
                    } else {

                        /* in the case the destination state already exists, 
                         we refer to it */
                        upperState = oA.getStateById(newUpperTupleLabel.getString());
                    }


                    /* add transition from current state to new state */
                    oA.addTransition(new Transition(currentWorkingState, upperState, c));

                    
                }

            }
        }

    }
    
    /**
     * Applies the tuple construction to construct a new automaton representing
     * the complement of the input automaton.
     *
     */
    private static void lowerConstruction() {
        
        /* initialise working set, which holds states still to be processed */
        LinkedList<State> workingSet = new LinkedList();

        /* add states of upper part to working set */
        workingSet.addAll(oA.getStates().values());


        /* while working set is non empty */
        while (!workingSet.isEmpty()) {

            /* take first element of working set */
            State currentWorkingState = workingSet.pop();
           
            
            /* for each symbol */
            for (String c : oA.getSigma()) {
                
                /* if the symbol is not authorised (i.e. the transitions does not
                remain in the same SCC, then skip.
                */
  //              System.out.println("currentworkingstate = " + currentWorkingState.getLabel());
//                System.out.println("  upper = " + currentWorkingState.getUpper().getLabel());
                if(SCCStableOptim && !currentWorkingState.getUpper().isInAuthLower(c)){
                    continue;
                }
                
                
                

                /* initialize new tuple for lower part of automaton */
                TupleLabel newLowerTupleLabel = new TupleLabel();

                /* list of already added states (to remove duplicates from right) */
                HashSet<String> added = new HashSet();

                /* indicate whether the newly created lower state is accepting */
                boolean accepting = true;

                /* for each element in predecessor tuple (start from right) */
                int iterator = currentWorkingState.getTupleLabel().getElements().size() - 1;
                while (iterator >= 0) {

                    TupleElement predecessorElement = currentWorkingState.getTupleLabel().getElements().get(iterator);

                    /* initialize successor states for lower automaton */
                    TupleElement lowerRightSucc = new TupleElement(); //right successor 
                    TupleElement lowerLeftSucc = new TupleElement(); // left successor 

                    /* build right and left sets successors */
                    for (State stateInCurrent : predecessorElement.getStates().values()) {

                        for (State succState : stateInCurrent.getDestination(c)) {

                            /* put in right set if accepting and not already exists */
                            if (succState.isAccepting() && !added.contains(succState.getLabel())) {
                                lowerRightSucc.addState(succState);
                                added.add(succState.getLabel());
                            }

                            /* put in left set if non-accepting and not already exists */
                            if (!succState.isAccepting() && !added.contains(succState.getLabel())) {
                                lowerLeftSucc.addState(succState);
                                added.add(succState.getLabel());
                            }

                        }
                    }

                    /* 
                     * colour the sets
                     */
                    Byte predColour = predecessorElement.getColour();
                    //System.out.println("predColour = " + predColour);

                    /* if predecessor state is lower and non-accepting */
                    if (!currentWorkingState.isAccepting() && !currentWorkingState.isUpper()) {

                        if (predColour == 0) {
                            /* colour 1 introduced on right successor if predecessor 
                             has colour 0*/
                            lowerRightSucc.setColour((byte) 1);
                            lowerLeftSucc.setColour((byte) 0);
                        } else {
                            /* colours are copied otherwise */
                            lowerRightSucc.setColour(predColour);
                            lowerLeftSucc.setColour(predColour);
                        }
                    } else { /* predecessor has no colour 2 (break point) */

                        if (predColour == 1) { // if predecessor has colour 1
                            lowerRightSucc.setColour((byte) 2);
                            lowerLeftSucc.setColour((byte) 2);
                        } else { // if predecessor has colour 1
                            lowerRightSucc.setColour((byte) 2);
                            lowerLeftSucc.setColour((byte) 0);
                        }

                    }

                    /* if the set of accepting successors is not-empty */
                    if (!lowerRightSucc.getStates().isEmpty()) {
                        newLowerTupleLabel.addElementLeft(lowerRightSucc);
                    }

                    if (!lowerLeftSucc.getStates().isEmpty()) {
                        newLowerTupleLabel.addElementLeft(lowerLeftSucc);
                    }

                    iterator--;
                }

                /* apply left optimisation */
                if (leftOptim) {
                    newLowerTupleLabel.leftOptim();
                }

                /* apply 2-1-colour optimisazion */
                if (twoOneOptim) {
                    newLowerTupleLabel.TwoOneOptim();
                }

                /* Set acceptance of new state */
                for (TupleElement el : newLowerTupleLabel.getElements()) {
                    if (el.getColour() == 2) {
                        accepting = false;
                    }
                }

                /* ZeroLess optimisation: delete states which contain no 
                 0-coloured component */
                boolean deleteBrackets = false;
                if (deleteZeroLessOptim) {
                    deleteBrackets = true;
                    for (TupleElement te : newLowerTupleLabel.getElements()) {
                        if (te.getColour() == 0) {
                            deleteBrackets = false;
                        }
                    }
                }


                /* if the tuple is not empty, add the new state to the lower part*/
                /* optimisation only for complete input automata:
                 remove states with right most element of colour 2 */
                if (!newLowerTupleLabel.getElements().isEmpty()
                        && !(isComplete && newLowerTupleLabel.hasRightMostColourTwo())
                        && (!deleteBrackets)) {

                    /* add new state to lower part */
                    State lowerState;
                    
                    
                    

                    /**
                     * Check if the constructed label is empty or exists already
                     * in the automaton,
                     */
                    /* if does not exist in automaton */
                    if (!oA.getStates().containsKey(newLowerTupleLabel.getString())) {

                        /* create the state */
                        lowerState = new State(newLowerTupleLabel, simpleLabel++);
                        
                        /* update correspong upper state of successor (for SCCStableOptim) */
                        if(SCCStableOptim){
                        lowerState.setUpper(currentWorkingState.getUpper().getDestination(c).get(0));
                        //System.out.println("lower " + lowerState.getLabel() + " is assigned upper " + currentWorkingState.getUpper().getDestination(c).get(0).getLabel());
                        }
                        /* is the state accepting? */
                        if (accepting) {

                            lowerState.setAccepting(true);
                            oA.addAccepting(lowerState);

                            /* accepted language is not empty */
                            oA.setEmptyLanguage(false);
                        }

                        /* add state to automaton */
                        oA.addTupleState(lowerState);

                        /* add newly created state to working set */
                        workingSet.add(lowerState);

                    } else {

                        /* in the case the destination state already exists, 
                         we refer to it by lowerState */
                        lowerState = oA.getStateById(newLowerTupleLabel.getString());

                    }

                    /* add transition from current state to new or referenced state */
                    oA.addTransition(new Transition(currentWorkingState, lowerState, c));

                }

            }
        }

    }
    
    
   
    
    /* wikipedia */
    private static void findSCCs(State v){

        /* Set the preorder number of v to scc_c, and increment scc_c. */
        v.setPreorder(scc_C++);
        
        /* Push v onto scc_S and also onto scc_P. */
        scc_S.push(v);
        scc_P.push(v);

        /* find successors of v */
        ArrayList<State> succ = new ArrayList();
        for (String symb : oA.getSigma()) {
            succ.addAll(v.getDestination(symb));
        }
        
        /* For each edge from v to a neighbouring vertex w */
        for (State w : succ) {
        /* If the preorder number of w has not yet been assigned, recursively search w;*/
            if(w.getPreorder()==-1){
                findSCCs(w);
            } else {
                /* Otherwise, if w has not yet been assigned to a strongly connected component: */
                if(w.getSCC() == null){
                    /* Repeatedly pop vertices from scc_P until the top element of scc_P
                    has a preorder number less than or equal to the preorder number of w. */
                    while(scc_P.peek().getPreorder() > w.getPreorder()){
                        scc_P.pop();
                    }
                }
            }
        }

        /* If v is the top element of scc_P:*/
        if (v == scc_P.peek()) {
            SCC newSCC = new SCC();
            State pop;
            /* Pop vertices from scc_S until v has been popped, and assign the popped vertices to a new component.*/
            do {
                pop = scc_S.pop();
                newSCC.add(pop);
            } while (pop != v);
            /*Pop v from scc_P.*/
            scc_P.pop();
            oA.addSCC(newSCC);
        }
    }

    
    /* assign SCCs to states and update authorised transitions of the lower part */
//    private static void assignSCCsAndAuth(){
//        for(State s : oA.getStates().values()){
//            SCC scc = oA.getSCC(s);
//            s.setSCC(scc);
//            for(String ch : oA.getSigma()){
//                State successor = s.getDestination(ch).get(0); //(upper autom is deterministic)
//                if(scc!=null && scc.contains(successor)){
//                    s.addAuthLower(ch);
//                }
//            }
//        }
//    }
}
