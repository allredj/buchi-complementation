/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buchicomp;

import GUI.MainGUI;
import IO.XMLImport;
import automata.Automaton;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joel
 */
public class AltCompl implements Runnable {

    private final String type;
    private final String inputPath;
    private final MainGUI gui;
    private final long timeout;
    private final String opt;

    public AltCompl(String type, String opt, MainGUI gui, String inputPath, long timeout) {
        if (!(type.equals("slice") || type.equals("rank") || type.equals("piterman") || type.equals("safra"))) {
            System.out.println("Alt Compl: Wrong type of complement");
        }
        this.type = type;
        this.inputPath = inputPath;
        this.gui = gui;
        this.timeout = timeout;
        this.opt = opt;
        
    }

    @Override
    public void run() {

        computeCompl(timeout);

    }

    private void computeCompl(long timeout) {
        boolean failed = false;

        System.out.println("Running " + type + " complementation");

        String outputPath = "automata/compl/output_" + type + "_compl.gff";

        PrintWriter out;
        try {
            out = new PrintWriter("scripts/alt_compl_script");
            out.println("complement -m " + type + " " + opt + " -t " + timeout/1000 + " -o \"" + outputPath + "\" \"" + inputPath + "\";");
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AltCompl.class.getName()).log(Level.SEVERE, null, ex);
        }

        String[] commands = new String[2];
        commands[0] = gui.goalPath;
        commands[1] = "batch scripts/alt_compl_script";

        
        try {
            System.out.print("Starting complementation...");
            executeCommandLine(commands, timeout);
            System.out.print("done\n");
        } catch (IOException | InterruptedException ex) {
            failed = true;
            Logger.getLogger(AltCompl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            failed = true;
            Logger.getLogger(AltCompl.class.getName()).log(Level.WARNING, type + " complementation has timed out", ex);
        }

        
        
        int states = 0;
        int transitions = 0;

        if (!failed) {
            System.out.print("Fetching result...");
            Automaton result = XMLImport.importXMLFile(outputPath, gui);
            System.out.print("done\n");
            states = result.getStates().size();
            transitions = result.getTransitions().size();
        }

        gui.updateAlt(type, states, transitions, failed);

    }

    /*
     http://stackoverflow.com/questions/808276/how-to-add-a-timeout-value-when-using-javas-runtime-exec
     */
    /* execute command line */
    public static int executeCommandLine(final String[] commandLine, final long timeout)
            throws IOException, InterruptedException, TimeoutException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(commandLine);
        /* Set up process I/O. */

        Worker worker = new Worker(process);
        worker.start();
        try {
            worker.join(timeout);
            if (worker.exit != null) {
                return worker.exit;
            } else {
                throw new TimeoutException();
            }
        } catch (InterruptedException ex) {
            worker.interrupt();
            Thread.currentThread().interrupt();
            throw ex;
        } finally {
            process.destroy();
        }
    }

    private static class Worker extends Thread {

        private final Process process;
        private Integer exit;

        private Worker(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            try {
                exit = process.waitFor();
            } catch (InterruptedException ignore) {
            }
        }
    }

}
