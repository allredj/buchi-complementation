/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IO;

import GUI.MainGUI;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import automata.*;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.jdom.JDOMException;

/**
 * This class contains a static method importXMLFile(String inputFile) that is
 * used for importing an automaton structure from an .XML file.
 *
 * @author Joel Allred, University of Fribourg <joel.allred@unifr.ch>
 */
public class XMLImport {

    public static Automaton importXMLFile(String inputFile, MainGUI frame) {

        /* initialize automaton document */
        Document autom = null;

        SAXBuilder builder = new SAXBuilder();
        try {
            autom = builder.build(inputFile);
        } catch (JDOMException | IOException ex) {
            System.out.println("Cannot load XML file");
        }

        /* create Automaton */
        Automaton a = new Automaton();

        if (autom != null) {


            /* lower case */
            if (autom.getRootElement().getChild("alphabet") != null) {

                /* check if classical alphabet */
                if (autom.getRootElement().getChild("alphabet").getAttributeValue("type").equals("classical")) {
                    importLowerCase(autom, a);
                } else {
                    System.out.println("ERROR: Alphabet must be classical! Found: " + autom.getRootElement().getChild("alphabet").getAttributeValue("type"));
                }

                /* upper case */
            } else if (autom.getRootElement().getChild("Alphabet") != null) {
                if (autom.getRootElement().getChild("Alphabet").getAttributeValue("type").equals("Classical")) {
                    importUpperCase(autom, a);
                } else {
                    System.out.println("ERROR: Alphabet must be Classical! Found: " + autom.getRootElement().getChild("Alphabet").getAttributeValue("type"));
                    frame.enableComplementation(false);
                    frame.enableCompletion(false);
                    JOptionPane.showMessageDialog(frame,
                            "Alphabet must be of classical type",
                            "Parsing Error",
                            JOptionPane.ERROR_MESSAGE);

                }

            } else {
                System.out.println("ERROR: Tag <Alphabet> or <alphabet> not found!");
                JOptionPane.showMessageDialog(frame,
                        "Alphabet tag not found",
                        "Parsing Error",
                        JOptionPane.ERROR_MESSAGE);
                frame.enableComplementation(false);
                frame.enableCompletion(false);
            }

        } else {
            /* in case automaton document is null */
            System.out.println("ERROR: Automaton was not imported from " + inputFile);
            JOptionPane.showMessageDialog(frame,
                    "Automaton was not imported from " + inputFile,
                    "Parsing Error",
                    JOptionPane.ERROR_MESSAGE);
            frame.enableComplementation(false);
            frame.enableCompletion(false);
        }

        return a;
    }

    private static void importLowerCase(Document autom, Automaton a) {
        /* load alphabet from XML structure and add to automaton */
        ArrayList<String> alphaChar = new ArrayList();
        List<Element> alphaElements;

        alphaElements = autom.getRootElement().getChild("alphabet").getChildren("symbol");
        for (Element alphaElement : alphaElements) {
            alphaChar.add(alphaElement.getText().trim());
        }
        a.setSigma(alphaChar);


        /* load states from XML structure and add to automaton */
        List<Element> stateElements = autom.getRootElement().getChild("stateSet").getChildren("state");
        int i = 0;
        for (Element stateElement : stateElements) {
            String label = stateElement.getAttributeValue("sid");
            State st = new State(label, i++);
            a.addState(st);
        }

        /* load transitions from XML structure */
        ArrayList<XMLTransition> XMLTransitions = new ArrayList();
        List<Element> transitionElements = autom.getRootElement().getChild("transitionSet").getChildren("transition");
        for (Element transitionElement : transitionElements) {
            String from = transitionElement.getChildText("from");
            String to = transitionElement.getChildText("to");
            String symbol = transitionElement.getChildText("read");
            XMLTransition t = new XMLTransition(from, to, symbol);
            XMLTransitions.add(t);
        }

        /* add transitions to states and to automaton */
        for (XMLTransition t : XMLTransitions) {
            for (String label : a.getStates().keySet()) {
                if (t.getFrom().equals(label)) {
                    State destination = a.getStateById(t.getTo());
                    State origin = a.getStateById(t.getFrom());
                    Transition trans = new Transition(origin, destination, t.getSymbol());
                    //a.getStates().get(label).addTransition(trans); //done in automaton
                    a.addTransition(trans); //add transition to automaton
                }
            }
        }

        /* set initial state from XML structure */
        List<Element> initElements = autom.getRootElement().getChild("initialStateSet").getChildren("stateID");
        a.setInit(a.getStateById(initElements.get(0).getText()));

        /* set accepting states from XML structure */
        List<Element> accElements = autom.getRootElement().getChild("acc").getChildren("stateID");
        for (Element e : accElements) {

            State acc = a.getStateById(e.getText());
            acc.setAccepting(true);
            a.addAccepting(acc);
        }
    }

    private static void importUpperCase(Document autom, Automaton a) {

        /* load alphabet from XML structure and add to automaton */
        ArrayList<String> alphaChar = new ArrayList();
        List<Element> alphaElements;

        alphaElements = autom.getRootElement().getChild("Alphabet").getChildren("Symbol");

        for (Element alphaElement : alphaElements) {
            alphaChar.add(alphaElement.getText().trim());
        }
        a.setSigma(alphaChar);


        /* load states from XML structure and add to automaton */
        List<Element> stateElements = autom.getRootElement().getChild("StateSet").getChildren("State");

        int i = 0;
        for (Element stateElement : stateElements) {
            String label = stateElement.getAttributeValue("sid");
            State st = new State(label, i++);
            a.addState(st);
        }

        /* load transitions from XML structure */
        ArrayList<XMLTransition> XMLTransitions = new ArrayList();
        List<Element> transitionElements = autom.getRootElement().getChild("TransitionSet").getChildren("Transition");

        for (Element transitionElement : transitionElements) {
            String from = transitionElement.getChildText("From");
            String to = transitionElement.getChildText("To");
            String symbol = transitionElement.getChildText("Label");
            XMLTransition t = new XMLTransition(from, to, symbol);
            XMLTransitions.add(t);
        }

        /* add transitions to states and to automaton */
        for (XMLTransition t : XMLTransitions) {
            for (String label : a.getStates().keySet()) {
                if (t.getFrom().equals(label)) {
                    State destination = a.getStateById(t.getTo());
                    State origin = a.getStateById(t.getFrom());
                    Transition trans = new Transition(origin, destination, t.getSymbol());
                    //a.getStates().get(label).addTransition(trans); //done in automaton
                    a.addTransition(trans); //add transition to automaton
                }
            }
        }

        /* set initial state from XML structure */
        List<Element> initElements = autom.getRootElement().getChild("InitialStateSet").getChildren("StateID");

        a.setInit(a.getStateById(initElements.get(0).getText()));

        /* set accepting states from XML structure */
        List<Element> accElements = autom.getRootElement().getChild("Acc").getChildren("StateID");

        for (Element e : accElements) {

            State acc = a.getStateById(e.getText());
            acc.setAccepting(true);
            a.addAccepting(acc);
        }
    }

}
