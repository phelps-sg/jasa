/*$$
 * Copyright (c) 2004, Repast Organization for Architecture and Design (ROAD)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with 
 * or without modification, are permitted provided that the following 
 * conditions are met:
 *
 *	 Redistributions of source code must retain the above copyright notice,
 *	 this list of conditions and the following disclaimer.
 *
 *	 Redistributions in binary form must reproduce the above copyright notice,
 *	 this list of conditions and the following disclaimer in the documentation
 *	 and/or other materials provided with the distribution.
 *
 * Neither the name of the ROAD nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE TRUSTEES OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *$$*/
package uchicago.src.sim.engine.gui.components;

import uchicago.src.sim.engine.gui.ParameterWizard;
import uchicago.src.sim.engine.gui.model.ConstantParameter;
import uchicago.src.sim.engine.gui.model.DataParameter;
import uchicago.src.sim.engine.gui.model.IncrementParameter;
import uchicago.src.sim.engine.gui.model.ListParameter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/**
 *
 * @author wes maciorowski
 */
public class ParameterWizardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final static String treeName = "Parameter Definitions:";
    private DefaultMutableTreeNode top = null;

    //private ArrayListListModel configuredParmListModel = null;
    private ArrayListListModel inputParmListModel = null;
    private javax.swing.ButtonGroup buttonGroup1;
    private EnhancedJTable allParameterTable;
    private JButton addNestParmButton = new JButton();
    private JButton addParmButton = new JButton();

    // Variables declaration 
    private JButton okButton;
    private JButton cancelButton;
    private JButton selectButton;
    private JButton removeParmButton = new JButton();
    private JLabel endLabel = new JLabel();
    private JLabel incrLabel = new JLabel();
    private JLabel fileNameLabel;
    private JLabel outputLocationLabel;
    private JLabel commaLabel;
    private JLabel runsLabel = new JLabel();
    private JLabel startLabel = new JLabel();
    private JList inputParameterList = new JList();
    private JPanel allParametersPanel;
    private JPanel buttonPanel;
    private JPanel inputParametersPanel;
    private JPanel outputLocationPanel;
    private JRadioButton listOfValuesRadioButton = new JRadioButton();
    private JRadioButton incrValueRadioButton = new JRadioButton();
    private JRadioButton constValueRadioButton = new JRadioButton();
    private JTabbedPane tabPane;
    private JTextField endTextField = new JTextField();
    private JTextField incrTextField = new JTextField();
    private JTextField runsTextField = new JTextField("1");
    private JTextField startTextField = new JTextField();

    //private javax.swing.JList configuredParmTree = new javax.swing.JList();
    private JTree configuredParmTree = null;
    private ParameterData aParameterData;
    private ParameterDataObjectTableModel aParameterDataObjectTableModel;
    private ParameterWizard aParameterWizard;
    private TreePath curPath;

    /** Creates new form DataWizardFrame */
    public ParameterWizardPanel(ParameterWizard aParameterWizard) {
        this.aParameterWizard = aParameterWizard;
        initComponents();
    }

    /**
     * @param inputParameterList The inputParameterList to set.
     */
    public void setInputParameterList(javax.swing.JList inputParameterList) {
        this.inputParameterList = inputParameterList;
    }

    /**
     * @return Returns the inputParameterList.
     */
    public JList getInputParameterList() {
        return inputParameterList;
    }

    /**
     * returns user selected output location
     * @return
     */
    public String getOutputLocation() {
        return outputLocationLabel.getText();
    }

    /**
     * returns user selected output location
     * @return
     */
    public void setOutputLocation(String aFileName) {
        outputLocationLabel.setText(aFileName);
    }

    public void setParameterData(ParameterData aParameterData) {
        this.aParameterData = aParameterData;
        aParameterDataObjectTableModel.setAParameterData(aParameterData);
        inputParmListModel.setSomeList(aParameterData.getInputParameterList());

        ArrayList rootNodes = aParameterData.getRootNodes();
        DefaultTreeModel model = (DefaultTreeModel) configuredParmTree.getModel();
        DefaultMutableTreeNode aNode = null;

        for (int i = 0; i < rootNodes.size(); i++) {
            aNode = (DefaultMutableTreeNode) rootNodes.get(i);
            model.insertNodeInto(aNode, top, top.getChildCount());
        }

        expandAll(configuredParmTree, true);
        setOutputLocation(aParameterData.getOutputLocation());
    }

    //  If expand is true, expands all nodes in the tree.
    // Otherwise, collapses all nodes in the tree.
    public void expandAll(JTree tree, boolean expand) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();

        // Traverse tree from root
        expandAll(tree, new TreePath(root), expand);
    }

    private void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode) parent.getLastPathComponent();

        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    public void configureOutputScreen() {
        GridBagConstraints gridBagConstraints;

        outputLocationPanel.setLayout(new GridBagLayout());
        fileNameLabel = new JLabel();
        outputLocationLabel = new JLabel();
        selectButton = new JButton();
        fileNameLabel.setFont(new Font("Microsoft Sans Serif", 1, 11));
        fileNameLabel.setText("File Name:");

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);

        //gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        outputLocationPanel.add(fileNameLabel, gridBagConstraints);

        outputLocationLabel.setText("File Name Not Specified.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        outputLocationPanel.add(outputLocationLabel, gridBagConstraints);

        selectButton.setText("Select");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        outputLocationPanel.add(selectButton, gridBagConstraints);
        selectButton.addActionListener(new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    selectButtonAction();
                }
            });
    }

    public void rePaintTree() {
        configuredParmTree.repaint();
        configuredParmTree.invalidate();

        DefaultTreeModel model = (DefaultTreeModel) configuredParmTree.getModel();
        model.nodeChanged((TreeNode) curPath.getLastPathComponent());
    }

    /**
     * Loads scenarios and runs into the tree menu.
     *
     */
    public void refreshTree() {
        top = new DefaultMutableTreeNode(treeName);

        //loadCases(top);
        configuredParmTree = new JTree(top);
        configuredParmTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        configuredParmTree.setRootVisible(false);

        //		  Listen for when the selection changes.
        configuredParmTree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) configuredParmTree.getLastSelectedPathComponent();

                    if (configuredParmTree.isSelectionEmpty()) {
                        removeParmButton.setEnabled(false);
                    } else {
                        removeParmButton.setEnabled(true);
                    }

                    if (node == null) {
                        return;
                    }

//                    Object nodeInfo = node.getUserObject();
//                    if (nodeInfo instanceof uchicago.src.sim.engine.gui.model.Parameter) {
//                    } else if (nodeInfo instanceof com.pg.ecmo.meta.Scenario) {
//                    }
//                        curParameter = (uchicago.src.sim.engine.gui.model.Parameter) nodeInfo;
//                        aParameterWizard.actionPerformed(new ActionEvent(configuredParmTree,
//                                ActionEvent.ACTION_PERFORMED, VIEW_CASE));
                }
            });

        DefaultTreeModel model = (DefaultTreeModel) configuredParmTree.getModel();
        model.addTreeModelListener(new TreeModelListener() {
                public void treeNodesChanged(TreeModelEvent e) {
                    DefaultMutableTreeNode node;
                    node = (DefaultMutableTreeNode) (e.getTreePath()
                                                      .getLastPathComponent());

                    /*
                     * If the event lists children, then the changed
                     * node is the child of the node we've already
                     * gotten.  Otherwise, the changed node and the
                     * specified node are the same.
                     */
                    try {
                        int index = e.getChildIndices()[0];
                        node = (DefaultMutableTreeNode) (node.getChildAt(index));
                    } catch (NullPointerException exc) {
                    }

//					System.out.println("The user has finished editing the node.");
//					System.out.println("New value: " + node.getUserObject());
                }

                public void treeNodesInserted(TreeModelEvent arg0) {
                }

                public void treeNodesRemoved(TreeModelEvent arg0) {
                }

                public void treeStructureChanged(TreeModelEvent arg0) {
                }
            });
    }

    /**
     * @param e
     */
    protected void CancelActionPerformed(ActionEvent e) {
        aParameterWizard.actionPerformed(new ActionEvent(outputLocationLabel,
                ActionEvent.ACTION_PERFORMED, ParameterWizard.EXIT));
    }

    protected void OKActionPerformed(ActionEvent e) {
    	boolean dataOK = false;
    	    	
        if (outputLocationLabel.getText() == null ||
    			outputLocationLabel.getText().equals(""))
        {
            selectButtonAction();
         
            if (outputLocationLabel.getText() == null ||
        			outputLocationLabel.getText().equals(""))
            {	
				JOptionPane.showMessageDialog(this.aParameterWizard.getDialog(),
		                "You must select an output parameter file to run in Multi-Run mode.", 
						"Alert", 
						JOptionPane.ERROR_MESSAGE);
            }
            
            if (outputLocationLabel.getText() == null ||
        			outputLocationLabel.getText().equals("")) {
            	dataOK = false;
            } else {
            	dataOK = true;
            }
    	} else {
    		dataOK = true;
    	}

    	if (dataOK) {
    		aParameterWizard.actionPerformed(new ActionEvent(outputLocationLabel,
                ActionEvent.ACTION_PERFORMED, ParameterWizard.RUN_SIMULATION));
    	}
    }

    /**
     * @param e
     */
    protected void addNestActionPerformed(ActionEvent e) {
        DataParameter aParameter = null;

        try {
            aParameter = createParameterNode();
        } catch (DataTypeMismatchException e1) {
            JOptionPane.showMessageDialog(this.aParameterWizard.getDialog(),
                e1.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);

            return;
        }

        DefaultMutableTreeNode aNode = new DefaultMutableTreeNode(aParameter);
        DefaultTreeModel model = (DefaultTreeModel) configuredParmTree.getModel();
        DefaultMutableTreeNode parentNode = null;

        if (configuredParmTree.getSelectionPath() != null) {
            parentNode = 
            	(DefaultMutableTreeNode) configuredParmTree.getSelectionPath()
												.getLastPathComponent();
        } else {
            parentNode = top;
        }

        model.insertNodeInto(aNode, parentNode, parentNode.getChildCount());

        TreePath aTreePath = new TreePath(aNode.getPath());
        configuredParmTree.scrollPathToVisible(aTreePath);
        configuredParmTree.setSelectionPath(aTreePath);
    }

    /**
     * @param e
     */
    protected void addParmActionPerformed(ActionEvent e) {
        DataParameter aParameter = null;

        try {
            aParameter = createParameterNode();
        } catch (DataTypeMismatchException e1) {
            JOptionPane.showMessageDialog(this.aParameterWizard.getDialog(),
                e1.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);

            return;
        }

        DefaultMutableTreeNode aNode = new DefaultMutableTreeNode(aParameter);
        DefaultTreeModel model = (DefaultTreeModel) configuredParmTree.getModel();
        DefaultMutableTreeNode parentNode = null;

        parentNode = top;
        model.insertNodeInto(aNode, parentNode, parentNode.getChildCount());

        TreePath aTreePath = new TreePath(aNode.getPath());
        configuredParmTree.scrollPathToVisible(aTreePath);
        configuredParmTree.setSelectionPath(aTreePath);
    }

    /**
     * @param evt
     */
    protected void inputParameterListChanged(ListSelectionEvent evt) {
        if (inputParameterList.isSelectionEmpty()) {
            addNestParmButton.setEnabled(false);
            addParmButton.setEnabled(false);
        } else {
            addParmButton.setEnabled(true);

            if (configuredParmTree.getModel().getChildCount(top) > 0) {
                addNestParmButton.setEnabled(true);
            }
        }
    }

    /**
     * @param e
     */
    protected void removeParmActionPerformed(ActionEvent e) {
        DefaultMutableTreeNode parentNode = null;

        if (configuredParmTree.getSelectionPath() != null) {
            parentNode = (DefaultMutableTreeNode) configuredParmTree.getSelectionPath()
                                                                    .getLastPathComponent();
        }

        if (!parentNode.getUserObject().equals(treeName)) {
            ((DefaultTreeModel) configuredParmTree.getModel()).removeNodeFromParent(parentNode);
        }
    }

    /**
     * @param evt
     */
    protected void valueTypeSpecificationPerformed(ActionEvent evt) {
        if (listOfValuesRadioButton.isSelected()) {
            endTextField.setVisible(false);
            incrTextField.setVisible(false);
            endLabel.setVisible(false);
            incrLabel.setVisible(false);
            startLabel.setText("List of Values:");
            commaLabel.setText("(space separated)");
        } else if (constValueRadioButton.isSelected()) {
            endTextField.setVisible(false);
            incrTextField.setVisible(false);
            endLabel.setVisible(false);
            incrLabel.setVisible(false);
            startLabel.setText("Const Value:");
            commaLabel.setText("");
        } else {
            endTextField.setVisible(true);
            incrTextField.setVisible(true);
            endLabel.setVisible(true);
            incrLabel.setVisible(true);
            startLabel.setText("Start Value:");
            commaLabel.setText("");
        }
    }

    /**
     *
     */
    private void configureInputParametersPanel() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();

        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();

        incrValueRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    valueTypeSpecificationPerformed(evt);
                }
            });

        listOfValuesRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    valueTypeSpecificationPerformed(evt);
                }
            });

        constValueRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    valueTypeSpecificationPerformed(evt);
                }
            });

        buttonGroup1.add(incrValueRadioButton);
        buttonGroup1.add(listOfValuesRadioButton);
        buttonGroup1.add(constValueRadioButton);

        addParmButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addParmActionPerformed(e);
                }
            });
        addNestParmButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addNestActionPerformed(e);
                }
            });

        removeParmButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    removeParmActionPerformed(e);
                }
            });

        refreshTree();

        inputParmListModel = new ArrayListListModel(new ArrayList());
        inputParameterList = new JList(inputParmListModel);

        inputParametersPanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        javax.swing.JLabel jLabel11 = new javax.swing.JLabel();
        jLabel11.setText(" Parameter Definition: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);

        //gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPanel1.add(jLabel11, gridBagConstraints);

        incrValueRadioButton.setText("Increment");
        incrValueRadioButton.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(incrValueRadioButton, gridBagConstraints);

        listOfValuesRadioButton.setText("List");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(listOfValuesRadioButton, gridBagConstraints);

        constValueRadioButton.setText("Constant");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(constValueRadioButton, gridBagConstraints);

        runsLabel.setText("Runs:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(runsLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(runsTextField, gridBagConstraints);

        startLabel.setText("Start Value:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(startLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(startTextField, gridBagConstraints);

        endLabel.setText("End Value:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(endLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(endTextField, gridBagConstraints);

        incrLabel.setText("Step:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(incrLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(incrTextField, gridBagConstraints);

        commaLabel = new JLabel();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel1.add(commaLabel, gridBagConstraints);

        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        addParmButton.setText("Add");
        addParmButton.setEnabled(false);
        jPanel3.add(addParmButton);

        addNestParmButton.setText("Add/Nest");
        jPanel3.add(addNestParmButton);
        addNestParmButton.setEnabled(false);

        removeParmButton.setText("Remove");
        removeParmButton.setEnabled(false);
        jPanel3.add(removeParmButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jPanel3, gridBagConstraints);

        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        jLabel5.setText("List of parameter definitions:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jLabel5, gridBagConstraints);

        JScrollPane listScroller = new JScrollPane(configuredParmTree);
        listScroller.setPreferredSize(new Dimension(250, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(listScroller, gridBagConstraints);

        inputParametersPanel.add(jPanel1, java.awt.BorderLayout.CENTER);

        javax.swing.JPanel jPanel44 = new javax.swing.JPanel();
        jPanel44.setLayout(new java.awt.GridBagLayout());

        //jLabel6.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 11));
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        jLabel6.setText("Available parameters:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPanel44.add(jLabel6, gridBagConstraints);

        inputParameterList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        inputParameterList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(
                    javax.swing.event.ListSelectionEvent evt) {
                    inputParameterListChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        listScroller = new JScrollPane(inputParameterList);
        jPanel44.add(listScroller, gridBagConstraints);

        inputParametersPanel.add(jPanel44, java.awt.BorderLayout.WEST);

        inputParametersPanel.setBorder(BorderFactory.createTitledBorder(
                " Specify Input Parameters Values "));
    }

    private Object parseValue(String stringValue, String className)
        throws DataTypeMismatchException {
        try {
            if (className.equalsIgnoreCase("java.lang.Integer")) {
                Integer anInteger = new Integer(stringValue);

                return anInteger;
            } else if (className.equalsIgnoreCase("java.lang.Long")) {
                Long aLong = new Long(stringValue);

                return aLong;
            } else if (className.equalsIgnoreCase("java.lang.String")) {
                return stringValue;
            } else if (className.equalsIgnoreCase("java.lang.Float")) {
                Float aFloat = new Float(stringValue);

                return aFloat;
            } else if (className.equalsIgnoreCase("java.lang.Byte")) {
                Byte aByte = new Byte(stringValue);

                return aByte;
            } else if (className.equalsIgnoreCase("java.lang.Double")) {
                Double aDouble = new Double(stringValue);

                return aDouble;
            } else if (className.equalsIgnoreCase("java.lang.Boolean")) {
                Boolean aBoolean = new Boolean(stringValue);

                return aBoolean;
            }
        } catch (NumberFormatException e) {
            throw new DataTypeMismatchException(stringValue +
                " is not of type " + className);
        }

        return null;
    }

    /**
     * @return
     */
    private DataParameter createParameterNode()
        throws DataTypeMismatchException {
        String name = null;
        String dataType = null;
        boolean input = true;
        DataParameter aParameter = (DataParameter) inputParameterList.getSelectedValue();

        String currLabelName = "";
        
        try {
        
        if (aParameter != null) {
            name = aParameter.getName();
            dataType = aParameter.getDataType();
        }

        int runs;

        try {
            runs = Integer.parseInt(runsTextField.getText());
        } catch (NumberFormatException e) {
            runs = 0;
        }

        if (incrValueRadioButton.isSelected()) {
        	currLabelName = "Start";
            Object start = parseValue(startTextField.getText(), dataType);
            currLabelName = "End";
            Object end = parseValue(endTextField.getText(), dataType);
            currLabelName = "Step";
            Object increment = parseValue(incrTextField.getText(), dataType);

            IncrementParameter aValueParameter = null;
            aValueParameter = new IncrementParameter(runs, name, dataType,
                    input, start, end, increment);

            return aValueParameter;
        }

        if (constValueRadioButton.isSelected()) {
        	currLabelName = "Start";
            Object value = parseValue(startTextField.getText(), dataType);

            ConstantParameter aConstParameter = null;
            aConstParameter = new ConstantParameter(runs, name, dataType,
                    input, value);

            return aConstParameter;
        }

        if (listOfValuesRadioButton.isSelected()) {
            Object[] value = null;
            currLabelName = "List value";
            StringTokenizer st = new StringTokenizer(startTextField.getText()
                                                                   .trim());

            if (st.countTokens() > 0) {
                value = new Object[st.countTokens()];

                int curTok = 0;

                while (st.hasMoreTokens()) {
                    value[curTok] = parseValue(st.nextToken(), dataType);
                    curTok++;
                }
            }

            ListParameter aListParameter = null;
            aListParameter = new ListParameter(runs, name, dataType, input,
                    value);

            return aListParameter;
        }

        return null;
        } catch (DataTypeMismatchException ex) {
        	throw new DataTypeMismatchException(currLabelName + " " + ex.getMessage(), ex);
        }
    }

//    /** Exit the Application */
//    private void exitForm(java.awt.event.WindowEvent evt) {
//        aParameterWizard.actionPerformed(new ActionEvent(jLabel2, ActionEvent.ACTION_PERFORMED,
//                ParameterWizard.EXIT));
//    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        //setTitle("Parameter Wizard");
        buttonPanel		= new javax.swing.JPanel();
        okButton		= new javax.swing.JButton();
        cancelButton	= new javax.swing.JButton();
        tabPane			= new javax.swing.JTabbedPane();
        inputParametersPanel	= new javax.swing.JPanel();
        outputLocationPanel		= new javax.swing.JPanel();

//        addWindowListener(new java.awt.event.WindowAdapter() {
//                public void windowClosing(java.awt.event.WindowEvent evt) {
//                    exitForm(evt);
//                }
//            });
        //buttonPanel.setLayout(new javax.swing.BoxLayout(buttonPanel, javax.swing.BoxLayout.X_AXIS));
        okButton.setText("OK");
        buttonPanel.add(okButton);

        cancelButton.setText("Cancel");
        buttonPanel.add(cancelButton);
        okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    OKActionPerformed(e);
                }
            });
        cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    CancelActionPerformed(e);
                }
            });

        allParametersPanel = new javax.swing.JPanel();
        allParametersPanel.setLayout(new GridLayout(1, 1));
        aParameterDataObjectTableModel = new ParameterDataObjectTableModel(aParameterWizard,
                aParameterData);
        allParameterTable = new EnhancedJTable(aParameterDataObjectTableModel,
                90);

        String[] tmpLabels = new String[2];
        tmpLabels[0] = "Input";
        tmpLabels[1] = "Output";

        int[] tmpVals = new int[2];
        tmpVals[0] = 0;
        tmpVals[1] = 1;
        allParameterTable.setDefaultRenderer(Integer.class,
            new RadioBarPanel(aParameterWizard, tmpLabels, tmpVals));
        allParameterTable.setDefaultEditor(Integer.class,
            new RadioBarPanel(aParameterWizard, tmpLabels, tmpVals));

        allParametersPanel.add(new JScrollPane(allParameterTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

        allParametersPanel.setBorder(BorderFactory.createTitledBorder(
                " Specify Parameters Function "));

        tabPane.addTab("All Parameters", allParametersPanel);
        configureInputParametersPanel();
        tabPane.addTab("Input Parameters", inputParametersPanel);

        configureOutputScreen();
        tabPane.addTab("Parameter File Location", outputLocationPanel);
        setLayout(new BorderLayout());
        add(tabPane, java.awt.BorderLayout.CENTER);
        add(buttonPanel, java.awt.BorderLayout.SOUTH);

        //pack();
    }

    private void selectButtonAction() { //GEN-FIRST:event_jButton2ActionPerformed

        //Create a file chooser
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setDialogTitle("Choose file to store parameters");

        //In response to a button click:
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	String storeFile = cleanupFileName(fc.getSelectedFile().getPath());
        	
            outputLocationLabel.setText(storeFile);
            aParameterWizard.actionPerformed(new ActionEvent(
                    outputLocationLabel, ActionEvent.ACTION_PERFORMED,
                    ParameterWizard.CHANGED_OUTPUT_LOCATION));
        }
    }

    private String cleanupFileName(String originalName) {
    	if (!originalName.endsWith(".xml"))
    		return originalName + ".xml";
    	else
    		return originalName;
    }

    /**
     * Provides access to specified parameters data model
     * @return Returns the top.
     */
    public DefaultMutableTreeNode getTreeTop() {
        return top;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ParameterWizardPanel aParameterWizardFrame = new ParameterWizardPanel(null);
        JFrame aFrame = new JFrame();
        aFrame.getContentPane().add(aParameterWizardFrame);
        aFrame.pack();
        aFrame.setVisible(true);
    }
}
