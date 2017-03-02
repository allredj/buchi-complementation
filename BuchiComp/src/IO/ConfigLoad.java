/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IO;

import GUI.MainGUI;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import java.io.IOException;
import org.jdom.JDOMException;

/**
 * This class contains a static method importXMLFile(String inputFile) that is
 * used for importing an automaton structure from an .XML file.
 *
 * @author Joel Allred, University of Fribourg <joel.allred@unifr.ch>
 */
public class ConfigLoad {

    public static void configLoad(String inputFile, MainGUI frame) {

        /* initialize automaton document */
        Document config = null;

        SAXBuilder builder = new SAXBuilder();
        try {
            config = builder.build(inputFile);
        } catch (JDOMException | IOException ex) {
            System.out.println("Cannot load config.xml");
        }

        if (config != null) {

            String pathToGoal = config.getRootElement().getChild("paths").getChild("goalpath").getAttributeValue("path");
            System.out.println("path to goal = " +pathToGoal);
            frame.goalPath=pathToGoal;
            
        }
    }

}
