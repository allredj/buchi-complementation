/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automata;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * The Tuple Label is a special kind of labeling used during the construction of
 * the complement automaton.
 *
 * It contains a list of Tuple Elements and also a String label which is used
 * for backward compatibility to the input and output automaton, and also for
 * the hashing.
 *
 * @author Joel Allred, University of Fribourg <joel.allred@unifr.ch>
 */
public class TupleLabel {

    private ArrayList<TupleElement> elements = new ArrayList();

    /**
     * Constructor for Tuple Label
     *
     * @param elements
     */
    public TupleLabel(ArrayList<TupleElement> elements) {
        this.elements = elements;

    }

    /**
     * Alternate constructor for empty TupleLabel
     */
    public TupleLabel() {
    }

    private String buildElementString(TupleElement el, String opBracket, String clBracket) {
        String result = "";
        result += opBracket;
        int i = 0;
        for (State s : el.getStates().values()) {
            result += s.getLabel();
            if (i < el.getStates().size() - 1) {
                result += ",";
            }
            i++;
        }
        result += clBracket;
        return result;
    }

    /**
     * returns the string interpretation of the label
     *
     * @return
     */
    public String getString() {

        if(elements.isEmpty()){
            return "sink";
        }
        
        String label = "";

        if (!elements.isEmpty() && elements.get(0).getColour() == 3) {
            label = label + "#u";
        }

        for (TupleElement el : elements) {
            label+=el.toString();
            
//            if (el.getColour() == 3) {
//                label += buildElementString(el, "{", "}");
//            } else if (el.getColour() == 0) {
//                label += buildElementString(el, "{", "}");
//            } else if (el.getColour() == 1) {
//                label += buildElementString(el, "(", ")");
//            } else if (el.getColour() == 2) {
//                label += buildElementString(el, "[", "]");
//            }
        }

        return label;
    }

    /**
     * returns the list of TupleElement in the label
     *
     * @return list of TupleElement in the label
     */
    public ArrayList<TupleElement> getElements() {
        return elements;
    }

    
    
    /**
     * Adds a TupleElement to the left of the tuple, and applies merging:
     * if new element has colour 1, merge with right element of colour 1
     * if new element has colour 2, merge with right element of colour 1 or 2
     * and change colour to 2.
     *
     * @param el
     */
    
    public void addElementLeft(TupleElement el) {

        //System.out.println("adding " + el.getPrettyString() + " to left of " + this.getString());
        
//         this.elements.add(0, el);
        if (!elements.isEmpty()) {
            int col = elements.get(0).getColour();
            
            if((col== 1 || col == 2) && (el.getColour()==col)){

                /* join new with leftmost tuple */
                TreeMap<Integer, State> states = new TreeMap<>();
                states.putAll(elements.get(0).getStates());
                states.putAll(el.getStates());

                TupleElement joinTuple = new TupleElement(states, (byte) col);
                this.elements.set(0, joinTuple);
            } else {
                this.elements.add(0, el);
            }

        } else {
            this.elements.add(0, el);
        }

    }
    
    /* removes element e from the tuple */
    public void removeElement(TupleElement e){
        this.elements.remove(e);
    }
    
    
    /**
     * Returns true iff the rightmost element of the tuple has colour 2.
     * @return boolean
     */
    public boolean hasRightMostColourTwo(){
        if(elements.isEmpty()) return false;
        return (elements.get(elements.size()-1).getColour() == (byte) 2);
    }
    
    
    /*
    * applies the left optimisation (group all elements before the first element
    of colour 0 into a single element of colour 2)
    */
    
    public void leftOptim(){
        //System.out.println("left optimising tuple " + this.getString());
        /* traverse label from left to right */
        
        /* new set of states */
        TreeMap<Integer, State> states = new TreeMap<>();
        
        while(!elements.isEmpty() && elements.get(0).getColour() != (byte) 0){
            //System.out.println("  element " + elements.get(0).getPrettyString() + " will be joined");
            /* add states to new element */                
            states.putAll(elements.get(0).getStates());
            elements.remove(0);
        }
        
                               
        
        if (!states.isEmpty()) {
            
            /* create new element from collected states */
            TupleElement joinTuple = new TupleElement(states, (byte) 2);

            /* add element to left of tuple */
            addElementLeft(joinTuple);
            //System.out.println("optimised tuple: " + this.getString());
        }

    }
    
    /*
    * applies the left 2-1-colour optimisation (group each sequence of
    a 2-color and a 1-color element into an element of colour 2
    */
    
    public void TwoOneOptim(){
        //System.out.println("2-1-optimising tuple " + this.getString());
        /* traverse label from left to right */
        int size = elements.size();
        for (int i = 0; i < size - 1; i++) {

            TupleElement thisElement = elements.get(i);
            TupleElement nextElement = elements.get(i+1);
            
            /* 2-coloured followed by 1-coloured sets are joint into a 2-coloured set */
            if(thisElement.getColour()==2 && nextElement.getColour()==1){
                
                System.out.print("2-1-optim: " + this.getString() + " rewritten to ");
                /* put states into 2-coloured element */
                thisElement.getStates().putAll(nextElement.getStates());
                
                /* remove 1-coloured element */
                elements.remove(nextElement);
                
                System.out.print(this.getString() + "\n");
                
                /* recheck for thisElement */
                i--;
                size--;
                        
            }
            
        }


    }

}
