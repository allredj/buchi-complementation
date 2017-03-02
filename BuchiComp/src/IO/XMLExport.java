/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IO;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import automata.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.w3c.dom.DOMException;

/**
 * This class contains a static method export(Automaton a, String filename)
 * that is used for exporting an automaton structure to an .XML file.
 * 
 * Part of the code in this class was inspired from 
 * http://www.journaldev.com/1112/how-to-write-xml-file-in-java-dom-parser
 * (9/12/2013)
 * 
 * @author Joel Allred, University of Fribourg <joel.allred@unifr.ch>
 */
public class XMLExport {

    /**
     * This method is used to export that automaton structure a to an XML
     * document located by the filename string.
     * @param a
     * @param filename 
     */
    public static void export(Automaton a, String filename) {
        

        try {
            /* Initialize document structure */
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            
            /* add root element */
            Element rootElement = doc.createElement("structure");
            rootElement.setAttribute("label-on", "transition");
            rootElement.setAttribute("type", "fa");
            
            /* append root element to document */
            doc.appendChild(rootElement);

            /* append alphabet */
            rootElement.appendChild(getAlphabet(a, doc));
            
            /* append states */
            rootElement.appendChild(getStates(a,doc));
            
            /* append transitions */
             rootElement.appendChild(getTransitions(a,doc));
            
            /* append initial state set */
            rootElement.appendChild(getInitialStateSet(a,doc));
            
            /* append accept */
            rootElement.appendChild(getAcc(a,doc));

            /* for output to file, console */
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            
            /* for pretty print */
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);

            /* write to console or file */
            StreamResult console = new StreamResult(System.out);
            StreamResult file = new StreamResult(new File(filename));

            /* write data */
            //transformer.transform(source, console);
            transformer.transform(source, file);
            System.out.println("DONE");

        } catch (ParserConfigurationException | DOMException | TransformerFactoryConfigurationError | IllegalArgumentException | TransformerException e) {
        }
    }
  
    /* create node containing alphabet */
    private static Node getAlphabet(Automaton a, Document doc) {

        Element alpha = doc.createElement("alphabet");
        alpha.setAttribute("type", "classical");
        
        for (String c : a.getSigma()) {
            Element symbol = doc.createElement("symbol");
            symbol.appendChild(doc.createTextNode(" " + c));
            alpha.appendChild(symbol);
        }

        return alpha;
    }

    /* create node containing states */
    private static Node getStates(Automaton a, Document doc) {
        int i = 0;
        Element stateSet = doc.createElement("stateSet");
        //for (String label : a.getStates().keySet()) {
        for (State state : a.getStates().values()) {
            String simpleLabel = state.getIntLabelAsString();
            Element stateElement = doc.createElement("state");
            stateElement.setAttribute("sid", simpleLabel);
            Element labelElement = doc.createElement("label");
            labelElement.setTextContent(state.getLabel());
            stateElement.appendChild(labelElement);
            stateSet.appendChild(stateElement);
            i++;
        }
        return stateSet;
    }
    
    /* create node containing transitions */
    private static Node getTransitions(Automaton a, Document doc) {

        Element alpha = doc.createElement("transitionSet");
        int i = 0;
        for (Transition t : a.getTransitions()) {
            Element trans = doc.createElement("transition");
            trans.setAttribute("tid", Integer.toString(i++));
            Element from = doc.createElement("from");
            from.appendChild(doc.createTextNode(t.getFrom().getIntLabelAsString()));
            trans.appendChild(from);
            Element to = doc.createElement("to");
            to.appendChild(doc.createTextNode(t.getTo().getIntLabelAsString()));
            trans.appendChild(to);
            Element symb = doc.createElement("read");
            symb.appendChild(doc.createTextNode(t.getCh()));
            trans.appendChild(symb);
            alpha.appendChild(trans);
        }
        return alpha;
    }
    
    /* create node containing initial states */
    private static Node getInitialStateSet(Automaton a, Document doc){
        
        Element init = doc.createElement("initialStateSet");
        Element stateID = doc.createElement("stateID");
        stateID.appendChild(doc.createTextNode(a.getInit().getIntLabelAsString()));
        init.appendChild(stateID);
        return init;
    
    }
    
    /* create node containing accepting states */
    private static Node getAcc(Automaton a, Document doc){
        
        Element accepting = doc.createElement("acc");
        accepting.setAttribute("type", "buchi");
        for(State q : a.getAccepting()){
            Element stateID = doc.createElement("stateID");
            stateID.appendChild(doc.createTextNode(q.getIntLabelAsString()));
            accepting.appendChild(stateID);
        }
        return accepting;
    
    }
}
