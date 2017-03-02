package GUI;

import java.io.File;

/**
 * 
 * @author Joel Allred, University of Fribourg <joel.allred@unifr.ch>
 */
public class XMLFilter extends javax.swing.filechooser.FileFilter {
    
    @Override
        public boolean accept(File file) {
            return file.isDirectory() || file.getAbsolutePath().endsWith(".xml") || file.getAbsolutePath().endsWith(".gff");
        }
        @Override
        public String getDescription() {
            return "Automaton file (*.xml, *.gff)";
        }
    
}
