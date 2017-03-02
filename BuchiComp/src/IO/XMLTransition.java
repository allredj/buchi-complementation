/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IO;

/**
 * This class is used by XML import to create transitions from the XML file
 * 
 * @author Joel Allred, University of Fribourg <joel.allred@unifr.ch>
 */
public class XMLTransition {
    private final String from;
    private final String to;
    private final String symbol;

    /**
     * Constructor for XMLTransition (only for XML parsing use)
     * @param from the origin state of this transition
     * @param to the destination state of this transition
     * @param symbol the symbol of this transition
     */
    public XMLTransition(String from, String to, String symbol) {
        this.from = from;
        this.to = to;
        this.symbol = symbol;
    }

    /**
     * 
     * @return the origin state of this transition 
     */
    public String getFrom() {
        return from;
    }

    /**
     * 
     * @return the destination state of this transition
     */
    public String getTo() {
        return to;
    }

    /**
     * 
     * @return the symbol of this transition
     */
    public String getSymbol() {
        return symbol;
    }
    
    
    
}
