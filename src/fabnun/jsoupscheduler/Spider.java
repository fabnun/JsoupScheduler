package fabnun.jsoupscheduler;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class Spider extends JDialog {

    final static Tools tools = Ui.tools;
    TreeMap<String, String[]> filters = new TreeMap();
    File fcfg = new File("spider.cfg");
    DefaultComboBoxModel<String> model = new DefaultComboBoxModel();
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton4;
    private JButton jButton5;
    private JButton jButton6;
    private JButton jButton7;
    private JCheckBox jCheckBox1;
    private JComboBox<String> jComboBox1;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;

    public Spider() throws FileNotFoundException, IOException, ClassNotFoundException {
        setModal(true);
        if (this.fcfg.exists()) {
            try (FileInputStream fis = new FileInputStream(this.fcfg); ObjectInputStream ois = new ObjectInputStream(fis)) {
                this.filters = (TreeMap) ois.readObject();
            }
        }
        initComponents();
        for (String key : this.filters.keySet()) {
            this.model.addElement(key);
        }
        this.jComboBox1.setModel(this.model);
        if (this.model.getSize() > 0) {
            jComboBox1ActionPerformed(null);
        }

        setLocationRelativeTo(null);
    }
    private JLabel jLabel4;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JTextArea jTextArea2;
    private JTextField jTextFieldURL;
    private JTextField jTextFieldSELECT;
    private JTree jTree1;

    private void initComponents() throws FileNotFoundException, IOException, ClassNotFoundException {
        this.jPanel1 = new JPanel();
        this.jTextFieldURL = new JTextField();
        this.jScrollPane1 = new JScrollPane();
        this.jTree1 = new JTree();
        this.jLabel1 = new JLabel();
        this.jLabel2 = new JLabel();
        this.jTextFieldSELECT = new JTextField();
        this.jComboBox1 = new JComboBox();
        this.jLabel3 = new JLabel();
        this.jLabel4 = new JLabel();
        this.jButton1 = new JButton();
        this.jButton2 = new JButton();
        this.jButton3 = new JButton();
        this.jButton4 = new JButton();
        this.jButton5 = new JButton();
        this.jCheckBox1 = new JCheckBox();
        this.jButton6 = new JButton();
        this.jButton7 = new JButton();
        this.jScrollPane2 = new JScrollPane();
        this.jTextArea2 = new JTextArea();


        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setTitle("Spider Browser");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                Spider.this.formWindowClosing(evt);
            }
        });

        this.jTextFieldURL.addActionListener((ActionEvent evt) -> {
            try {
                doit();
            } catch (IOException | ClassNotFoundException e) {
            }
        });

        this.jScrollPane1.setFocusable(false);

        this.jTree1.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("")));

        this.jTree1.setFocusable(false);
        this.jTree1.setRootVisible(false);
        this.jScrollPane1.setViewportView(this.jTree1);

        this.jLabel1.setHorizontalAlignment(4);
        this.jLabel1.setText("URL");
        this.jLabel1.setHorizontalTextPosition(0);

        this.jLabel2.setHorizontalAlignment(4);
        this.jLabel2.setText("FILTER");

        this.jTextFieldSELECT.addActionListener((ActionEvent evt) -> {
            try {
                doit();
            } catch (IOException | ClassNotFoundException e) {
            }
        });

        this.jComboBox1.setCursor(new Cursor(12));
        this.jComboBox1.setFocusable(false);
        this.jComboBox1.addActionListener((ActionEvent evt) -> {
            Spider.this.jComboBox1ActionPerformed(evt);
        });

        this.jLabel4.setHorizontalAlignment(4);
        this.jLabel4.setText("SAVED");

        this.jButton1.setText("+");
        this.jButton1.setToolTipText("Add to Saved");
        this.jButton1.setCursor(new Cursor(12));
        this.jButton1.setFocusable(false);
        this.jButton1.setPreferredSize(new Dimension(40, 26));
        this.jButton1.addActionListener((ActionEvent evt) -> {
            Spider.this.jButton1ActionPerformed(evt);
        });

        this.jButton2.setText("R");
        this.jButton2.setToolTipText("Rename Saved");
        this.jButton2.setCursor(new Cursor(12));
        this.jButton2.setFocusable(false);
        this.jButton2.setPreferredSize(new Dimension(40, 26));

        this.jButton3.setText("-");
        this.jButton3.setToolTipText("Remove Saved");
        this.jButton3.setCursor(new Cursor(12));
        this.jButton3.setFocusable(false);
        this.jButton3.setPreferredSize(new Dimension(40, 26));
        this.jButton3.addActionListener((ActionEvent evt) -> {
            Spider.this.jButton3ActionPerformed(evt);
        });

        this.jButton4.setText("B");
        this.jButton4.setToolTipText("Browse URL");
        this.jButton4.setCursor(new Cursor(12));
        this.jButton4.setFocusable(false);
        this.jButton4.setPreferredSize(new Dimension(40, 26));
        this.jButton4.addActionListener((ActionEvent evt) -> {
            Spider.this.jButton4ActionPerformed(evt);
        });

        this.jButton5.setText("DO IT");
        this.jButton5.setToolTipText("Browse URL");
        this.jButton5.setCursor(new Cursor(12));
        this.jButton5.setFocusable(false);
        this.jButton5.addActionListener((ActionEvent evt) -> {
            Spider.this.jButton5ActionPerformed(evt);
        });

        this.jCheckBox1.setText("JSON");

        this.jButton6.setText("EXPAND");
        this.jButton6.addActionListener((ActionEvent evt) -> {
            Spider.this.jButton6ActionPerformed(evt);
        });

        this.jButton7.setText("COLLAPSE");
        this.jButton7.addActionListener((ActionEvent evt) -> {
            Spider.this.jButton7ActionPerformed(evt);
        });

        this.jTextArea2.setColumns(20);
        this.jTextArea2.setRows(3);
        this.jTextArea2.setBorder(BorderFactory.createTitledBorder("DESCRIPCION"));
        this.jScrollPane2.setViewportView(this.jTextArea2);

        GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
        this.jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(this.jScrollPane2)
                                .addComponent(this.jScrollPane1)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                .addComponent(this.jLabel2, -1, -1, 32767)
                                                .addComponent(this.jLabel1, -1, 36, 32767)
                                                .addComponent(this.jLabel3)
                                                .addComponent(this.jLabel4, -1, -1, 32767))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                        .addComponent(this.jComboBox1, 0, -1, 32767)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(this.jButton3, -2, -1, -2)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(this.jButton2, -2, -1, -2))
                                                .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(this.jTextFieldSELECT, -1, 716, 32767)
                                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(this.jCheckBox1)
                                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED))
                                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(this.jTextFieldURL)
                                                                        .addGap(6, 6, 6)))
                                                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(this.jButton4, -2, -1, -2)
                                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(this.jButton1, -2, -1, -2))
                                                                .addComponent(this.jButton5, -1, -1, 32767)))))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(this.jButton7)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(this.jButton6)
                                ))
                        .addContainerGap()));

        jPanel1Layout.setVerticalGroup(jPanel1Layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(this.jComboBox1, -2, -1, -2)
                                .addComponent(this.jLabel3)
                                .addComponent(this.jLabel4)
                                .addComponent(this.jButton2, -2, -1, -2)
                                .addComponent(this.jButton3, -2, -1, -2))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(this.jLabel1)
                                .addComponent(this.jTextFieldURL, -2, -1, -2)
                                .addComponent(this.jButton1, -2, -1, -2)
                                .addComponent(this.jButton4, -2, -1, -2))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(this.jLabel2)
                                        .addComponent(this.jTextFieldSELECT, -2, -1, -2))
                                .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(this.jButton5)
                                        .addComponent(this.jCheckBox1)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(this.jScrollPane2, -2, -1, -2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(this.jScrollPane1, -1, 192, 32767)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(this.jButton6)
                                        .addComponent(this.jButton7))
                        )
                        .addGap(2, 2, 2)));



        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.jPanel1, -1, 922, 32767));

        layout.setVerticalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.jPanel1, GroupLayout.Alignment.TRAILING, -1, 442, 32767));

        pack();
    }

    private void jButton4ActionPerformed(ActionEvent evt) {
        try {
            Desktop.getDesktop().browse(new URI(this.jTextFieldURL.getText()));
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    private void jButton1ActionPerformed(ActionEvent evt) {
        String select = (String) this.jComboBox1.getSelectedItem();
        select = (select == null) ? "" : select;
        select = JOptionPane.showInputDialog(this, "Nombre del filtro", select);
        if (select != null) {
            select = select.trim().toUpperCase();
            if (this.filters.containsKey(select)
                    && JOptionPane.showConfirmDialog(this, "Sobrescribir Filtro", "Sobrescribir\n" + select, 0) != 0) {
                select = null;
            }
        }

        if (select != null && !select.isEmpty()) {
            this.filters.put(select, new String[]{this.jTextFieldURL.getText(), this.jTextFieldSELECT.getText(), this.jTextArea2.getText(), "" + this.jCheckBox1.isSelected()});
            if (this.model.getIndexOf(select) == -1) {
                this.model.addElement(select);
                this.jComboBox1.setSelectedItem(select);
            }
        }
    }

    private void jButton5ActionPerformed(ActionEvent evt) {
        try {
            doit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jComboBox1ActionPerformed(ActionEvent evt) {
        String[] sel = (String[]) this.filters.get((String) this.jComboBox1.getSelectedItem());
        this.jTextFieldURL.setText(sel[0]);
        this.jTextFieldSELECT.setText(sel[1]);
        if (sel.length > 2) {
            this.jTextArea2.setText(sel[2]);
            this.jCheckBox1.setSelected(sel[3].equals("true"));
        } else {
            this.jTextArea2.setText("");
            this.jCheckBox1.setSelected(false);
        }
    }

    private void formWindowClosing(WindowEvent evt) {
        try (FileOutputStream fos = new FileOutputStream(this.fcfg); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this.filters);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void jButton3ActionPerformed(ActionEvent evt) {
        String sel = (String) this.jComboBox1.getSelectedItem();
        if (sel != null && JOptionPane.showConfirmDialog(this, "Eliminar", "Desea eliminar " + sel, 0) == 0) {
            this.filters.remove(sel);
            this.jComboBox1.removeItem(sel);
        }
    }

    private void jButton7ActionPerformed(ActionEvent evt) {
        collapseAllNodes(this.jTree1, 0, this.jTree1.getRowCount());
    }

    private void jButton6ActionPerformed(ActionEvent evt) {
        expandAllNodes(this.jTree1, 0, this.jTree1.getRowCount());
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Spider.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            (new Spider()).setVisible(true);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void doit() throws FileNotFoundException, IOException, ClassNotFoundException {
        try {
            String url = this.jTextFieldURL.getText();
            if (url.startsWith("http:\\\\")) {
                url = "http://" + url.substring(7);
            } else if (url.startsWith("https:\\\\")) {
                url = "https://" + url.substring(8);
            }
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            this.jTextFieldURL.setText(url);
            this.jTextFieldURL.repaint();
            this.jTextFieldURL.updateUI();
            String filter = this.jTextFieldSELECT.getText();
            run(url, filter, jCheckBox1.isSelected());
            //collapseAllNodes(this.jTree1, 0, this.jTree1.getRowCount());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void collapseAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; i++) {
            tree.collapseRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            collapseAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; i++) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }
    

    public DefaultMutableTreeNode JsonTree(JSONObject obj, boolean root) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(root ? "" : obj.toString());
        for (String key : obj.keySet()) {
            Object val=obj.get(key);
            if (val instanceof JSONObject) {
                node.add(JsonTree((JSONObject) obj.get(key), false));
            } else if (val instanceof JSONArray) {
                JSONArray arr=(JSONArray)val;
                for(int i=0;i<arr.length();i++){
                    node.add(JsonTree(arr.getJSONObject(i), false));
                }
            } else {
                node.add(new DefaultMutableTreeNode(key + ": " + obj.get(key)));
            }
        }
        return node;
    }

    private void run(String url, String selector, boolean json) throws IOException, MalformedURLException, URISyntaxException {
        Node doc;
        if (json) {
            JSONObject obj = tools.domLoadJSON(url);
            this.jTree1.setModel(new DefaultTreeModel(JsonTree(obj, json)));
        } else {
            if (!selector.trim().isEmpty()) {
                doc = tools.domLoad(url, selector);
            } else {
                doc = tools.domLoad(url);
            }
            SoupTreeModel soupModel = new SoupTreeModel(doc);
            this.jTree1.setModel(soupModel);
        }

    }

    public static class SoupTreeModel
            extends DefaultTreeModel {

        public SoupTreeModel(Node doc) {
            super(Spider.SoupTreeNode.forNode(doc));
        }

        private Node objectToNode(Object o) {
            if (o instanceof Node) {
                return (Node) o;
            }
            if (o instanceof SoupTreeModel) {
                return objectToNode(((SoupTreeModel) o).root);
            }
            if (o instanceof Spider.SoupTreeNode) {
                return (Node) ((Spider.SoupTreeNode) o).getUserObject();
            }
            throw new IllegalArgumentException(String.format("Quoi?<%s>", new Object[]{(null == o) ? "NULL" : o.getClass().getName()}));
        }

        public boolean isLeaf(Object node) {
            Node n = objectToNode(node);
            return Spider.TreeHelpers.isLeaf(n);
        }

        public int getChildCount(Object parent) {
            Node n = objectToNode(parent);
            return Spider.TreeHelpers.getChildCount(n);
        }

        public Object getChild(Object parent, int index) {
            Node n = objectToNode(parent);
            return new SoupTreeModel(Spider.TreeHelpers.getChild(n, index));
        }

        public int getIndexOfChild(Object parent, Object child) {
            Node n = objectToNode(parent);
            Node ch = objectToNode(child);
            return Spider.TreeHelpers.getIndexOfChild(n, ch);
        }

        public String toString() {
            Node self = objectToNode(this.root);
            if (self instanceof TextNode) {
                return ((TextNode) self).getWholeText();
            }
            String text = tools.domGetText(self);
            if (text.length() > 48) {
                text = text.substring(0, 48) + "...";
            }
            return "<" + self.nodeName() + self.attributes() + ">" + (text.isEmpty() ? "" : (" " + text));
        }
    }

    public static class SoupTreeNode
            extends DefaultMutableTreeNode {

        private static final Map<Node, SoupTreeNode> cache = new WeakHashMap();

        public static SoupTreeNode forNode(Node n) {
            SoupTreeNode result;
            synchronized (SoupTreeNode.class) {
                if (cache.containsKey(n)) {
                    result = (SoupTreeNode) cache.get(n);
                } else {
                    result = new SoupTreeNode(n);
                    cache.put(n, result);
                }
            }
            return result;
        }

        private SoupTreeNode(Node userObject) {
            super(userObject);
        }

        private Node node() {
            return (Node) this.userObject;
        }

        public TreeNode getParent() {
            Node parente = node().parent();
            if (null == parente) {
                return null;
            }
            return forNode(parente);
        }

        public boolean isLeaf() {
            return Spider.TreeHelpers.isLeaf(node());
        }

        public int getChildCount() {
            return Spider.TreeHelpers.getChildCount(node());
        }

        public TreeNode getChildAt(int index) {
            return forNode(Spider.TreeHelpers.getChild(node(), index));
        }

        public int getIndex(TreeNode aChild) {
            return Spider.TreeHelpers.getIndexOfChild(node(), ((SoupTreeNode) aChild).node());
        }

        public String toString() {
            return node().nodeName();
        }
    }

    public static class TreeHelpers {

        public static TreePath[] selectorToPath(SoupTreeNode[] leaves) {
            if (null == leaves) {
                return new TreePath[0];
            }
            TreePath[] results = new TreePath[leaves.length];
            for (int i = 0; i < leaves.length; i++) {
                results[i] = new TreePath(leaves[i].getPath());
            }
            return results;
        }

        public static boolean isLeaf(Node node) {
            return (0 == node.childNodeSize());
        }

        public static int getChildCount(Node parent) {
            return parent.childNodeSize();
        }

        public static Node getChild(Node parent, int index) {
            return parent.childNode(index);
        }

        public static int getIndexOfChild(Node parent, Object child) {
            for (int i = 0, len = parent.childNodeSize(); i < len; i++) {
                Node n = parent.childNode(i);
                if (n.equals(child)) {
                    return i;
                }
            }
            return -1;
        }
    }
}


/* Location:              /home/dev/Descargas/webinfo (2).jar!/com/kreadi/webinfo/Spider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
