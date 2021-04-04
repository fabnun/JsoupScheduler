/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fabnun.jsoupscheduler;

import com.formdev.flatlaf.IntelliJTheme;
import com.mongodb.client.FindIterable;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.StringBufferInputStream;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

/**
 *
 * @author fabian
 */
public class Ui extends javax.swing.JFrame {

    public static TreeMap<String, String> map = new TreeMap<>();
    public static TreeMap<String, String> input = new TreeMap<>();
    public static DefaultListModel<BeanShellProcess> listModel;
    public static Ui instance;

    int buttonTable = -1;
    LinkedList<org.bson.Document> docs = new LinkedList<>();

    @SuppressWarnings("ConvertToStringSwitch")
    void showTable(FindIterable iterable, String name, String[] buttons, String[] cols) {

        Iterator it = iterable.iterator();
        TreeSet<String> colset = new TreeSet<>();
        int rowSize = 0;
        while (it.hasNext()) {
            org.bson.Document doc = (org.bson.Document) it.next();
            for (String s : doc.keySet()) {
                colset.add(s);
            }
            rowSize++;
        }
        int colSize = colset.size();
        final boolean[] editable = new boolean[colSize];
        final Class[] classes = new Class[colSize];
        final LinkedList<int[]> pos = new LinkedList();
        final int[] siz = new int[colSize];
        Object[] colsRead = (Object[]) colset.toArray();

        if (cols != null) {
            for (String c : cols) {
                String[] def = c.split(":");
                if (def.length > 1) {
                    String col = def[0];
                    int idx = Arrays.binarySearch(colsRead, col);
                    if (idx > -1) {
                        for (int j = 1; j < def.length; j++) {
                            String s = def[j].trim();
                            if (null != s) {
                                if (s.equals("edit")) {
                                    editable[idx] = true;
                                } else if (s.equals("boolean")) {
                                    classes[idx] = Boolean.class;
                                } else if (s.equals("int")) {
                                    classes[idx] = Integer.class;
                                } else if (s.equals("long")) {
                                    classes[idx] = Long.class;
                                } else if (s.equals("double")) {
                                    classes[idx] = Double.class;
                                } else if (s.equals("float")) {
                                    classes[idx] = Float.class;
                                } else if (s.matches("\\d+px")) {
                                    siz[idx] = Integer.parseInt(s.substring(0, s.length() - 2));
                                } else if (s.matches("p\\d")) {
                                    pos.add(new int[]{idx, Integer.parseInt(s.substring(1))});
                                }
                            }
                        }
                    }
                }
            }
        }

        Object[][] data = new Object[rowSize][colSize];
        it = iterable.iterator();
        int row = 0;

        docs.clear();
        while (it.hasNext()) {
            org.bson.Document doc = (org.bson.Document) it.next();
            docs.add(doc);
            for (String s : doc.keySet()) {
                int idx = Arrays.binarySearch(colsRead, s);
                Object val = doc.get(s);
                data[row][idx] = val;
            }
            row++;
        }

        DefaultTableModel dtm = new DefaultTableModel(data, colsRead) {

            @Override
            public Class<?> getColumnClass(int c) {
                return classes[c] == null ? String.class : classes[c];
            }

            @Override
            public boolean isCellEditable(int i, int i1) {
                return editable[i1];
            }

        };

        jTable1.setModel(dtm);

        DefaultTableCellRenderer rendar1 = new DefaultTableCellRenderer();
        rendar1.setForeground(Color.yellow);
        for (int i = 0; i < editable.length; i++) {
            if (editable[i]) {
                jTable1.getColumnModel().getColumn(i).setHeaderRenderer(rendar1);
            }
        }

        TableColumnModel colModel = jTable1.getColumnModel();
        for (int i = 0; i < siz.length; i++) {
            if (siz[i] > 0) {
                colModel.getColumn(i).setMinWidth(siz[i]);
                colModel.getColumn(i).setMaxWidth(siz[i]);
            }
        }
        for (int[] permuta : pos) {
            colModel.moveColumn(jTable1.convertColumnIndexToView(permuta[0]), jTable1.convertColumnIndexToView(permuta[1]));
        }

        jDialog1.setName(name);
        jToolBar1.removeAll();
        jToolBar1.add(Box.createHorizontalGlue());
        buttonTable = -1;
        int buttonIdx = 0;
        if (buttons != null) {
            for (String s : buttons) {
                JButton button = new JButton(s);
                button.setBackground(Color.red);
                final int buttIdx = buttonIdx;
                button.addActionListener((ActionEvent ae) -> {
                    buttonTable = buttIdx;
                    jDialog1.setVisible(false);
                });
                button.setFocusable(false);
                button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                jToolBar1.add(button);

                buttonIdx++;
            }
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        jDialog1.setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());
        setLocationRelativeTo(this);
        jDialog1.setVisible(true);
        System.out.println("RESULT " + buttonTable);

    }

    private String lastSearch = "";

    /**
     * Creates new form MainTest
     *
     * @throws java.io.FileNotFoundException
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public Ui() throws FileNotFoundException {
        instance = this;
        if (lockInstance("lockfile")) {
            File file = new File("_lastSave_.data");
            try {
                if (file.exists()) {
                    Object[] result = (Object[]) bytes2Object(Files.readAllBytes(file.toPath()));
                    setBounds((Rectangle) result[0]);
                    map = (TreeMap<String, String>) result[1];
                    updateInput();
                }
            } catch (IOException | ClassNotFoundException e) {

            }
            if ("true".equals(input.get("log2File"))) {
                File outLogFile = new File("out.log");
                File errLogFile = new File("err.log");
                SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd-HH.mm.ss");
                if (outLogFile.exists()) {
                    String writeName = "out.log";

                    try {
                        BasicFileAttributes attr = Files.readAttributes(outLogFile.toPath(), BasicFileAttributes.class
                        );
                        writeName = sdf.format(new Date(attr.creationTime().toMillis())) + "." + writeName;
                    } catch (IOException e) {
                    }
                    try {
                        Files.move(outLogFile.toPath(), new File("logHistory", writeName).toPath(), REPLACE_EXISTING);
                    } catch (IOException ex) {
                    }
                }
                if (errLogFile.exists()) {
                    String writeName = "err.log";

                    try {
                        BasicFileAttributes attr = Files.readAttributes(errLogFile.toPath(), BasicFileAttributes.class
                        );
                        writeName = sdf.format(new Date(attr.creationTime().toMillis())) + "." + writeName;
                    } catch (IOException e) {
                    }
                    try {
                        Files.move(errLogFile.toPath(), new File("logHistory", writeName).toPath(), REPLACE_EXISTING);
                    } catch (IOException ex) {
                    }
                }
                System.setOut(new PrintStream(new FileOutputStream("out.log", true)));
                System.setErr(new PrintStream(new FileOutputStream("err.log", true)));
            }

            initComponents();

            jTextField1.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent de) {
                    change(de);
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                    change(de);
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                    change(de);
                }

                void change(DocumentEvent de) {
                    if (jTextField1.hasFocus()) {
                        String newSearch = jTextField1.getText();
                        if (!newSearch.equals(lastSearch)) {
                            lastSearch = newSearch;
                            find(true);
                            jTextField1.requestFocus();
                        }
                    }
                }

            });

            jButton2.setVisible(false);
            jButton10.setVisible(false);
            jButton11.setVisible(false);
            loadGui();

            listModel = new DefaultListModel<>();
            jList1.setModel(listModel);
            updateInput();
            jTabbedPane1.addChangeListener((ChangeEvent e) -> {
                saveGui();
            });
            jSplitPane1.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, (PropertyChangeEvent pce) -> {
                saveGui();
            });
        } else {
            JOptionPane.showMessageDialog(this, "THIS APP IS ALREADY RUNNING");
            System.exit(0);
        }

        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                tableSel();
            }
        });

    }

    private static boolean lockInstance(final String lockFile) {
        try {
            final File file = new File(lockFile);
            final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            final FileLock fileLock = randomAccessFile.getChannel().tryLock();
            if (fileLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        try {
                            fileLock.release();
                            randomAccessFile.close();
                            file.delete();
                        } catch (Exception e) {
                            Ui.tools.err("Unable to remove lock file: " + lockFile);
                            e.printStackTrace();
                        }
                    }
                });
                return true;
            }
        } catch (Exception e) {
            Ui.tools.err("Unable to create and/or lock file: " + lockFile);
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] object2Bytes(Object obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();
        }
    }

    public static Object bytes2Object(byte[] bytes) throws IOException, ClassNotFoundException {
        ObjectInput in;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            in = new ObjectInputStream(bis);
            in.close();
        }
        return in.readObject();
    }

    private static boolean loaded = false;

    private void loadGui() {
        loadGui("_lastSave_.data");
    }

    private void loadGui(String name) {
        File file = new File(name);
        try {
            if (file.exists()) {
                Object[] result = (Object[]) bytes2Object(Files.readAllBytes(file.toPath()));
                setBounds((Rectangle) result[0]);
                map = (TreeMap<String, String>) result[1];
                jTabbedPane1.removeAll();
                addText("_Read_", map.get("_Read_"));
                addText("_Scheduler_", map.get("_Scheduler_"));
                addText("_Input_", map.get("_Input_"));
                for (String nam : map.keySet()) {
                    if (!nam.equals("_Input_") && !nam.equals("_Scheduler_") && !nam.equals("_Read_")) {
                        addText(nam, map.get(nam));
                    }
                }
                JScrollPane c = (JScrollPane) jTabbedPane1.getComponentAt((int) result[2]);
                JViewport viewport = c.getViewport();
                RSyntaxTextArea textArea = (RSyntaxTextArea) viewport.getComponents()[0];
                try {
                    textArea.setCaretPosition((int) result[4]);
                    textArea.setSelectionStart((int) result[5]);
                    textArea.setSelectionEnd((int) result[6]);
                } catch (Exception e) {

                }

                jTabbedPane1.setSelectedIndex((int) result[2]);
                jSplitPane1.setDividerLocation((int) result[3]);

            } else {
                addText("_Scheduler_", "bne.found 3h 2h 0h\nbne.page 5m 2h 10h 13h");
                File propFile = new File("default.properties");
                addText("_Input_", propFile.exists() ? new String(Files.readAllBytes(propFile.toPath())) : "");
            }

            loaded = true;
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }

    }

    private String oldInput = "";
    public static Tools tools;

    private String data_base_uri = "", data_base = "", data_base_indexes = "", agent = "", timeout = "";

    private void updateInput() {
        String newInput = map.get("_Input_");
        if (!oldInput.equals(newInput)) {
            Properties properties = new Properties();
            try {
                properties.load(new StringBufferInputStream(map.get("_Input_")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            TreeMap<String, String> _input = new TreeMap<>();
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                _input.put(key, value);
            }
            input = _input;
        }
        oldInput = newInput;
        boolean change = false;
        if (Ui.input.containsKey("data_base_uri") && !Ui.input.get("data_base_uri").equals(data_base_uri)) {
            data_base_uri = Ui.input.get("data_base_uri");
            change = true;
        }
        if (Ui.input.containsKey("data_base") && !Ui.input.get("data_base").equals(data_base)) {
            data_base = Ui.input.get("data_base");
            change = true;
        }
        if (Ui.input.containsKey("data_base_indexes") && !Ui.input.get("data_base_indexes").equals(data_base_indexes)) {
            data_base_indexes = Ui.input.get("data_base_indexes");
            change = true;
        }
        if (Ui.input.containsKey("agent") && !Ui.input.get("agent").equals(agent)) {
            agent = Ui.input.get("agent");
            change = true;
        }
        if (Ui.input.containsKey("timeout") && !Ui.input.get("timeout").equals(timeout)) {
            timeout = Ui.input.get("timeout");
            change = true;
        }
        if (change) {
            tools = new Tools(data_base_uri, data_base, data_base_indexes, agent, Integer.parseInt(timeout));
        }
    }

    final Debouncer debouncer = new Debouncer();

    private void saveGui(String name) {
        if (loaded) {
            int tabIdx = jTabbedPane1.getSelectedIndex();
            String tab = jTabbedPane1.getTitleAt(tabIdx);
            jButton2.setVisible(!tab.equals("_Scheduler_") && !tab.equals("_Input_") && !tab.equals("_Read_"));
            jButton10.setVisible(!tab.equals("_Scheduler_") && !tab.equals("_Input_") && !tab.equals("_Read_"));
            jButton11.setVisible(!tab.equals("_Scheduler_") && !tab.equals("_Input_") && !tab.equals("_Read_"));
            final JFrame este = this;
            debouncer
                    .debounce(Void.class, () -> {
                        System.out.println(
                                "saving...");
                        try {
                            File file = new File(name);
                            Files.write(file.toPath(), object2Bytes(new Object[]{getBounds(), map, tabIdx, jSplitPane1.getDividerLocation(), caretPosition, selStart, selEnd}));
                            updateInput();
                            System.out.println("saved...Ok");
                        } catch (java.nio.channels.ClosedByInterruptException e) {
                            System.out.println("saved...Fail");
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(este, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                            e.printStackTrace();
                            System.out.println("saved...Fail");
                        }

                    }, 1000, TimeUnit.MILLISECONDS);
        }
    }

    private void saveGui() {
        saveGui("_lastSave_.data");
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        jScrollPane3 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jButton9 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jButton12 = new javax.swing.JButton();

        jDialog1.setModal(true);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);

        jDialog1.getContentPane().add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jDialog1.getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_END);

        jScrollPane3.setMaximumSize(new java.awt.Dimension(320, 32767));
        jScrollPane3.setMinimumSize(new java.awt.Dimension(320, 20));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(320, 24));

        jEditorPane1.setContentType("text/html"); // NOI18N
        jScrollPane3.setViewportView(jEditorPane1);
        jEditorPane1.getAccessibleContext().setAccessibleDescription("text/html\n");

        jDialog1.getContentPane().add(jScrollPane3, java.awt.BorderLayout.LINE_END);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("JSoup Scheduler");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                formComponentMoved(evt);
            }
        });

        jToggleButton1.setBackground(new java.awt.Color(204, 0, 0));
        jToggleButton1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jToggleButton1.setForeground(java.awt.SystemColor.activeCaptionText);
        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fabnun/jsoupscheduler/icons/play.png"))); // NOI18N
        jToggleButton1.setText("SCHEDULE");
        jToggleButton1.setBorderPainted(false);
        jToggleButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButton1.setFocusable(false);
        jToggleButton1.setMargin(new java.awt.Insets(3, 3, 3, 3));
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jButton3.setBackground(new java.awt.Color(29, 64, 59));
        jButton3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton3.setForeground(java.awt.SystemColor.activeCaptionText);
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fabnun/jsoupscheduler/icons/export.png"))); // NOI18N
        jButton3.setText("SAVE");
        jButton3.setBorderPainted(false);
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setFocusable(false);
        jButton3.setMargin(new java.awt.Insets(3, 3, 3, 3));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(29, 64, 59));
        jButton4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton4.setForeground(java.awt.SystemColor.activeCaptionText);
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fabnun/jsoupscheduler/icons/import.png"))); // NOI18N
        jButton4.setText("LOAD");
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.setFocusable(false);
        jButton4.setMargin(new java.awt.Insets(3, 3, 3, 3));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(29, 64, 59));
        jButton5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton5.setForeground(java.awt.SystemColor.activeCaptionText);
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fabnun/jsoupscheduler/icons/net.png"))); // NOI18N
        jButton5.setText("JSOUP");
        jButton5.setBorderPainted(false);
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.setFocusable(false);
        jButton5.setMargin(new java.awt.Insets(3, 3, 3, 3));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jCheckBox1.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        jCheckBox1.setText("CASE");
        jCheckBox1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jCheckBox2.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        jCheckBox2.setText("REGEX");
        jCheckBox2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jButton6.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        jButton6.setText("PREVIOUS");
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        jButton7.setText("NEXT");
        jButton7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        jButton8.setText("REPLACE");
        jButton8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jTextField1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2)
                    .addComponent(jButton6)
                    .addComponent(jButton7)
                    .addComponent(jButton8)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jSplitPane1.setDividerLocation(800);

        jButton9.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jButton9.setForeground(java.awt.SystemColor.activeCaptionText);
        jButton9.setText("STOP SCRIPT");
        jButton9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton9.setFocusable(false);
        jButton9.setMaximumSize(new java.awt.Dimension(0, 31));
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jList1.setFocusable(false);
        jScrollPane1.setViewportView(jList1);

        jButton10.setBackground(new java.awt.Color(204, 0, 0));
        jButton10.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jButton10.setForeground(java.awt.SystemColor.activeCaptionText);
        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fabnun/jsoupscheduler/icons/play.png"))); // NOI18N
        jButton10.setText("RUN SCRIPT");
        jButton10.setBorderPainted(false);
        jButton10.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton10.setFocusable(false);
        jButton10.setMargin(new java.awt.Insets(3, 3, 3, 3));
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setBackground(new java.awt.Color(29, 64, 59));
        jButton11.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jButton11.setForeground(java.awt.SystemColor.activeCaptionText);
        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fabnun/jsoupscheduler/icons/new.png"))); // NOI18N
        jButton11.setText("REN");
        jButton11.setBorderPainted(false);
        jButton11.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton11.setFocusable(false);
        jButton11.setMargin(new java.awt.Insets(3, 3, 3, 3));
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(29, 64, 59));
        jButton2.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jButton2.setForeground(java.awt.SystemColor.activeCaptionText);
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fabnun/jsoupscheduler/icons/delete.png"))); // NOI18N
        jButton2.setText("DEL");
        jButton2.setBorderPainted(false);
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setFocusable(false);
        jButton2.setMargin(new java.awt.Insets(3, 3, 3, 3));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(29, 64, 59));
        jButton1.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jButton1.setForeground(java.awt.SystemColor.activeCaptionText);
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fabnun/jsoupscheduler/icons/new.png"))); // NOI18N
        jButton1.setText("NEW");
        jButton1.setBorderPainted(false);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setFocusable(false);
        jButton1.setMargin(new java.awt.Insets(3, 3, 3, 3));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton10)
                    .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel2);

        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTabbedPane1.setFocusable(false);
        jTabbedPane1.setFont(new java.awt.Font("DialogInput", 1, 14)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jPanel3);

        jButton12.setBackground(new java.awt.Color(29, 64, 59));
        jButton12.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton12.setForeground(java.awt.SystemColor.activeCaptionText);
        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fabnun/jsoupscheduler/icons/cfg.png"))); // NOI18N
        jButton12.setText("CFG");
        jButton12.setBorderPainted(false);
        jButton12.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton12.setFocusable(false);
        jButton12.setMargin(new java.awt.Insets(3, 3, 3, 3));
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButton1)
                        .addContainerGap())
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 988, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSplitPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jToggleButton1)
                        .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentMoved
        saveGui();
    }//GEN-LAST:event_formComponentMoved

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        saveGui();
    }//GEN-LAST:event_formComponentResized

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int idx = jTabbedPane1.getSelectedIndex();
        String tab = jTabbedPane1.getTitleAt(idx);
        if (JOptionPane.showConfirmDialog(this, "really remove " + tab + " tab?", "remove tab", JOptionPane.YES_NO_OPTION) == 0) {
            jTabbedPane1.remove(idx);
            map.remove(tab);
            saveGui();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    Scheduler scheduler;
    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        boolean activo = jToggleButton1.isSelected();
        jToggleButton1.setBackground(activo ? new Color(220, 0, 0) : new Color(144, 0, 0));
        jToggleButton1.setForeground(Color.black);
        jToggleButton1.setText(activo ? "STOP" : "SHEDULE");
        if (activo) {
            scheduler = new Scheduler(map.get("_Scheduler_"));
            scheduler.start(map);
        } else {
            scheduler.stop();
        }
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private String fileChooserFolder = ".";
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("data file (.data)", "data");
        JFileChooser fileChooser = new JFileChooser(fileChooserFolder);
        fileChooser.setSelectedFile(new File(fileChooserFolder));
        fileChooser.setFileFilter(filtro);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int valor = fileChooser.showSaveDialog(this);
        if (valor == JFileChooser.APPROVE_OPTION) {
            File selected = fileChooser.getSelectedFile();
            fileChooserFolder = selected.getAbsolutePath();
            if (!fileChooserFolder.endsWith(".data")) {
                fileChooserFolder = fileChooserFolder + ".data";
            }
            saveGui(fileChooserFolder);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("data file (.data)", "data");
        JFileChooser fileChooser = new JFileChooser(fileChooserFolder);
        fileChooser.setSelectedFile(new File(fileChooserFolder));
        fileChooser.setFileFilter(filtro);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int valor = fileChooser.showOpenDialog(this);
        if (valor == JFileChooser.APPROVE_OPTION) {
            File selected = fileChooser.getSelectedFile();
            fileChooserFolder = selected.getAbsolutePath();
            if (!fileChooserFolder.endsWith(".data")) {
                fileChooserFolder = fileChooserFolder + ".data";
            }
            loadGui(fileChooserFolder);
        }

    }//GEN-LAST:event_jButton4ActionPerformed

    Spider spider = null;
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        try {
            if (spider == null) {
                spider = new Spider();
                addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentHidden(ComponentEvent e) {
                        if (spider.isVisible()) {
                            spider.setVisible(false);
                        }
                    }
                });
            }
            spider.setVisible(true);
            setLocationRelativeTo(this);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void find(boolean previous) {
        SearchContext context = new SearchContext();
        String text = jTextField1.getText();

        context.setSearchFor(text);
        context.setMatchCase(jCheckBox1.isSelected());
        context.setRegularExpression(jCheckBox2.isSelected());
        context.setSearchForward(previous);
        context.setWholeWord(false);

        JScrollPane c = (JScrollPane) jTabbedPane1.getComponentAt(jTabbedPane1.getSelectedIndex());
        JViewport viewport = c.getViewport();
        RSyntaxTextArea textArea = (RSyntaxTextArea) viewport.getComponents()[0];
        SearchResult sr = SearchEngine.find(textArea, context);
        boolean found = sr.wasFound();
        if (!found) {
            int caret = textArea.getCaretPosition();

            textArea.setCaretPosition(previous ? 0 : textArea.getText().length());
            found = SearchEngine.find(textArea, context).wasFound();
            if (!found) {
                textArea.setCaretPosition(caret);
            }
        }
    }

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        find(true);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        find(false);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        String replaceText = JOptionPane.showInputDialog(this, "Replace text?", "REPLACE");
        if (replaceText != null) {
            SearchContext context = new SearchContext();
            String text = jTextField1.getText();
            if (text.length() == 0) {
                return;
            }
            context.setSearchFor(text);
            context.setMatchCase(jCheckBox1.isSelected());
            context.setRegularExpression(jCheckBox2.isSelected());
            context.setSearchForward(false);
            context.setWholeWord(false);
            context.setReplaceWith(replaceText);

            JScrollPane c = (JScrollPane) jTabbedPane1.getComponentAt(jTabbedPane1.getSelectedIndex());
            JViewport viewport = c.getViewport();
            RSyntaxTextArea textArea = (RSyntaxTextArea) viewport.getComponents()[0];
            SearchEngine.replaceAll(textArea, context);
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        run();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        int idx = jTabbedPane1.getSelectedIndex();
        String tab = jTabbedPane1.getTitleAt(idx);
        String newName = JOptionPane.showInputDialog(this, "Rename", tab);
        if (newName != null && !newName.trim().equals(tab) && !newName.trim().isEmpty()) {
            String val = map.get(tab);
            map.remove(tab);
            map.put(newName, val);
            jTabbedPane1.setTitleAt(idx, newName);
            saveGui();
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    @SuppressWarnings("CallToThreadStopSuspendOrResumeManager")
    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        for (Object obj : jList1.getSelectedValuesList()) {
            BeanShellProcess interpreter = (BeanShellProcess) obj;
            interpreter.thread.suspend();
            BeanShellProcess.removeToList(interpreter);
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String tab = JOptionPane.showInputDialog(this, "Tab Name?");
        if (tab != null) {
            if (map.containsKey(tab)) {
                JOptionPane.showMessageDialog(this, "this tab name exist!");
            } else {
                addText(tab);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed


    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getModifiers() == KeyEvent.CTRL_MASK && evt.getKeyCode() == KeyEvent.VK_F) {
            find(true);
        } else if (evt.getKeyCode() == KeyEvent.VK_F3) {
            find(true);
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton12ActionPerformed

    private void tableSel() {
        int[] cols = jTable1.getSelectedColumns();
        int[] rows = jTable1.getSelectedRows();
        if (cols.length == 1 && rows.length == 1) {
            org.bson.Document doc = docs.get(rows[0]);

            StringBuilder sb = new StringBuilder();
            for (String key : doc.keySet()) {
                sb.append("<b>").append(key).append(":</b>").append(doc.get(key)).append("<br><br>");
            }
            jEditorPane1.setText(sb.toString());
            jEditorPane1.setCaretPosition(0);

        }
    }

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int[] cols = jTable1.getSelectedColumns();
        int[] rows = jTable1.getSelectedRows();

        if (cols.length == 1 && rows.length == 1) {
            if (jTable1.getModel().isCellEditable(rows[0], cols[0]) && jTable1.getModel().getColumnClass(cols[0]) == String.class) {
                Object obj = jTable1.getValueAt(rows[0], cols[0]);
                if (obj != null) {
                    String text = obj.toString();
                    JTextArea textArea = new JTextArea(text);
                    textArea.setColumns(30);
                    textArea.setLineWrap(true);
                    textArea.setWrapStyleWord(true);
                    JScrollPane pane = new JScrollPane(textArea);
                    pane.setSize(new Dimension(800, 600));
                    pane.setPreferredSize(new Dimension(800, 600));
                    pane.setMinimumSize(new Dimension(800, 600));
                    pane.setMaximumSize(new Dimension(800, 600));
                    JOptionPane.showMessageDialog(null, pane, "Editar Texto",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void run() {
        String title = jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex());
        new BeanShellProcess(title, map.get(title), Ui.input);
    }

    private void addText(String name) {
        addText(name, "");
    }

    private void initTextArea(JTextArea textArea) {
        final UndoManager undo = new UndoManager();
        Document doc = textArea.getDocument();
        doc.addUndoableEditListener((UndoableEditEvent evt) -> {
            undo.addEdit(evt.getEdit());
        });
        textArea.getActionMap().put("Undo",
                new AbstractAction("Undo") {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        try {
                            if (undo.canUndo()) {
                                undo.undo();
                            }
                        } catch (CannotUndoException e) {
                        }
                    }
                });
        textArea.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

        textArea.getActionMap().put("Redo",
                new AbstractAction("Redo") {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        try {
                            if (undo.canRedo()) {
                                undo.redo();
                            }
                        } catch (CannotRedoException e) {
                        }
                    }
                });

        textArea.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

        textArea.getActionMap().put("Find",
                new AbstractAction("Find") {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        String oldText = jTextField1.getText();
                        String newText = textArea.getSelectedText();
                        if (newText == null || newText.isEmpty()) {
                            find(true);
                            jTextField1.requestFocus();
                        } else {
                            if (!oldText.equals(newText)) {
                                jTextField1.setText(newText);
                                find(true);
                            } else {
                                find(true);
                            }
                        }
                        jTextField1.setCaretPosition(jTextField1.getText().length());
                    }
                });

        textArea.getActionMap().put("F3",
                new AbstractAction("F3") {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        find(true);
                    }
                });
        textArea.getActionMap().put("run",
                new AbstractAction("run") {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        run();
                    }
                });

        textArea.getInputMap().put(KeyStroke.getKeyStroke("control F"), "Find");
        textArea.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3");

        textArea.getInputMap().put(KeyStroke.getKeyStroke("F5"), "run");

    }

    private int caretPosition = 0, selStart, selEnd;

    private void addText(final String name, String text) {
        for (int i = 0; i < jTabbedPane1.getComponentCount(); i++) {
            if (jTabbedPane1.getTitleAt(i).equals(name)) {
                JScrollPane c = (JScrollPane) jTabbedPane1.getComponentAt(i);
                JViewport viewport = c.getViewport();
                RSyntaxTextArea textArea = (RSyntaxTextArea) viewport.getComponents()[0];
                textArea.setText(text);
                initTextArea(textArea);
                return;
            }
        }
        final RSyntaxTextArea textArea = new RSyntaxTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        try {
            Theme theme = Theme.load(getClass().getResourceAsStream(
                    "/org/fife/ui/rsyntaxtextarea/themes/monokai.xml"));
            theme.apply(textArea);
            textArea.setAutoIndentEnabled(true);
            textArea.setAntiAliasingEnabled(true);
            textArea.setCodeFoldingEnabled(false);
            textArea.setSyntaxEditingStyle(
                    name.equals("_Input_") ? SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE
                            : name.equals("_Scheduler_") ? SyntaxConstants.SYNTAX_STYLE_HOSTS
                                    : name.equals("_Read_") ? SyntaxConstants.SYNTAX_STYLE_HOSTS : SyntaxConstants.SYNTAX_STYLE_JAVA);
        } catch (IOException ioe) { // Never happens
            ioe.printStackTrace();
        }
        textArea.setSelectionColor(Color.green.darker().darker().darker().darker());
        textArea.setBackground(new Color(24, 24, 24));

        Font font = textArea.getFont();
        textArea.setFont(new Font(font.getFontName(), font.getStyle(), 14));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(textArea);
        textArea.setText(text);
        initTextArea(textArea);

        jTabbedPane1.addTab(name, scrollPane);
        map.put(name, text);
        textArea.addCaretListener((CaretEvent ce) -> {
            saveGui();
            caretPosition = textArea.getCaretPosition();
            selStart = textArea.getSelectionStart();
            selEnd = textArea.getSelectionEnd();
            jLabel1.setText((textArea.getCaretOffsetFromLineStart() + 1) + ":" + (textArea.getCaretLineNumber() + 1));
        });
        textArea.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                map.put(name, textArea.getText());
                saveGui();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                map.put(name, textArea.getText());
                saveGui();
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                map.put(name, textArea.getText());
                saveGui();
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            //UIManager.setLookAndFeel(new NimbusLookAndFeel());

            IntelliJTheme.install(new FileInputStream("theme.json"));
        } catch (Exception ex) {
            Ui.tools.err("Failed to initialize LaF");
        }

        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                TIcon.run(new Ui(), "icons/logo.png");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
