package IO;

import automata.*;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * This class contains a static method export(Automaton a, String filename) that
 * is used for exporting an automaton structure to a .dot file (then readable by
 * dot readers such as Graphviz.
 *
 * @author Joel Allred, University of Fribourg <joel.allred@unifr.ch>
 */
public class DotExport {

    /**
     * This method exports the automaton 'a' to a .dot file located by the
     * string argument 'filename'.
     *
     * @param a
     * @param filename
     */
    public static void export(Automaton a, String filename) {

        try {
            try (FileWriter writer = new FileWriter(filename)) {

                /* append initial function */
                writer.append("digraph automaton{");
                writer.append('\n');

                /* append rankdir imformation */
                writer.append("rankdir=LR;");
                writer.append('\n');

                /* append size information */
                writer.append("size=\"8,5\"");
                writer.append('\n');

                /* add accepting states */
                writer.append("node [shape = doublecircle];");
                boolean existAccept = false;
                for (State s : a.getStates().values()) {
                    if (s.isAccepting()) {
                        writer.append("\"" + s.getLabel() + "\" ");
                        existAccept = true;
                    }
                }

                /* add end semi-column only if
                 * positive number of accepting states */
                if (existAccept) {
                    writer.append(';');
                }
                writer.append('\n');

                writer.append("node [shape = circle];");

                /* append non-accepting states */
                /* TO BE REMOVED */
                for (State s : a.getStates().values()) {
                    if (!s.isAccepting()) {
                        writer.append("\"" + s.getLabel() + "\" ");
                    }
                }

                writer.append(';');
                writer.append('\n');

                /* append transitions */
                for (Transition t : a.getTransitions()) {
                    writer.append("\"" + t.getFrom().getLabel() + "\"  -> \"" + t.getTo().getLabel() + "\" [ label = \"" + t.getCh() + "\" ];");
                    writer.append('\n');
                }

                writer.append("}");

                writer.flush();

            }

        } catch (IOException e) {
        }
    }
}
