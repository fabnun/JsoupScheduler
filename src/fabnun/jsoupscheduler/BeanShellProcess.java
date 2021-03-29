/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fabnun.jsoupscheduler;

import bsh.Interpreter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author fabian
 */
public class BeanShellProcess {

    public final Thread thread;
    String title;

    @SuppressWarnings({"CallToPrintStackTrace", "CallToThreadStartDuringObjectConstruction", "LeakingThisInConstructor"})
    public BeanShellProcess(String title, String code, Map input) {
        this.title = title;
        thread = new Thread(() -> {
            Map map = new HashMap<>();

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); PrintStream pst = new PrintStream(baos)) {
                StringReader reader = new StringReader(code);
                Interpreter bsh = new Interpreter(reader, pst, System.err, false);
                bsh.set("input", input);
                bsh.set("output", map);
                bsh.set("tools", Ui.tools);
                bsh.run();
                removeToList(this);
            } catch (Exception e) {
                Ui.tools.err("<><><><><><><><><><><><><><><><><><><><><><>");
                Ui.tools.err("ERROR AL EJECUTAR PROCESO " + title);
                Ui.tools.err(e.getLocalizedMessage());
                Ui.tools.err("<><><><><><><><><><><><><><><><><><><><><><>");
            }
        });
        thread.setName(title);
        thread.start();
        addToList(this);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof BeanShellProcess) && ((BeanShellProcess) o).title.equals(this.title);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.title);
        return hash;
    }

    @Override
    public String toString() {
        return title;
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    static void addToList(BeanShellProcess process) {
        synchronized (Ui.listModel) {
            Ui.listModel.addElement(process);
        }
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    static void removeToList(BeanShellProcess process) {
        synchronized (Ui.listModel) {
            Ui.listModel.removeElement(process);
        }
    }

}