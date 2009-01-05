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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.BoxLayout;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;


/**
 *
 * @author  Wes Maciorowski
 */
public class RadioBarPanel extends javax.swing.JPanel
    implements TableCellRenderer, TableCellEditor {
    /** Source code revision. */
    public final static String revision = "$Revision$";
    transient protected ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();
    int[] dataValues = { 0, 1 };
    JRadioButton[] jRadioButtonArray = null;
    String[] labelValues = { "Input", "Output" };
    int selectedValue;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private GUIControllerAbstract anGUIControllerAbstract = null;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;

    /** Creates new form RadioBarPanel */
    public RadioBarPanel(GUIControllerAbstract anGUIControllerAbstract) {
        this.anGUIControllerAbstract = anGUIControllerAbstract;
        initComponents();
    }

    /** Creates new form RadioBarPanel */
    public RadioBarPanel(GUIControllerAbstract anGUIControllerAbstract,
        String[] labelValues, int[] loggingLevels) {
        this.anGUIControllerAbstract = anGUIControllerAbstract;
        this.labelValues = labelValues;
        this.dataValues = loggingLevels;
        initComponents();
    }

    /**
     * @param arg0
     * @return
     * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
     */
    public boolean isCellEditable(EventObject arg0) {
        return true;
    }

    /**
     * @return
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        return new Integer(saveData());
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
        this.setValue(((Integer) value).intValue());

        return this;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        this.setValue(((Integer) value).intValue());

        return this;
    }

    public void setValue(int value) {
        loadData(value);
    }

    public int getValue() {
        int retVal = 0;

        for (int i = 0; i < jRadioButtonArray.length; i++) {
            if (jRadioButtonArray[i].isSelected()) {
                return i;
            }
        }

        return retVal;
    }

    /**
     * Adds a <code>CellEditorListener</code> to the listener list.
     * @param l  the new listener to be added
     */
    public void addCellEditorListener(CellEditorListener l) {
        listenerList.add(CellEditorListener.class, l);
    }

    /**
     *
     * @see javax.swing.CellEditor#cancelCellEditing()
     */
    public void cancelCellEditing() {
        fireEditingStopped();
    }

    public void loadData(int i) {
        for (int j = 0; j < dataValues.length; j++) {
            if (dataValues[j] == i) {
                if (j < jRadioButtonArray.length) {
                    jRadioButtonArray[j].setSelected(true);
                    jLabel1.setText(labelValues[j]);
                    selectedValue = i;
                }
            }
        }
    }

    /**
     * Removes a <code>CellEditorListener</code> from the listener list.
     * @param l  the listener to be removed
     */
    public void removeCellEditorListener(CellEditorListener l) {
        listenerList.remove(CellEditorListener.class, l);
    }

    public int saveData() {
        return selectedValue;
    }

    /**
     * @param arg0
     * @return
     * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
     */
    public boolean shouldSelectCell(EventObject arg0) {
        return true;
    }

    /**
     * @return
     * @see javax.swing.CellEditor#stopCellEditing()
     */
    public boolean stopCellEditing() {
        fireEditingStopped();

        return true;
    }

    /**
     * @return
     */
    protected boolean isEditable() {
        return true;
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is created lazily.
     *
     * @see EventListenerList
     */
    protected void fireEditingStopped() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CellEditorListener.class) {
                // Lazily create the event:
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }

                ((CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
            }
        }
    }

    /**
     * Notifies the controller that the scenario has been changed.
     * @param e
     */
    protected void notify_controller() {
        // has been changed
        if (anGUIControllerAbstract != null) {
            anGUIControllerAbstract.actionPerformed(new ActionEvent(this,
                    ActionEvent.ACTION_PERFORMED,
                    GUIControllerAbstract.INPUT_OUTPUT_CHANGED));
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() { //GEN-BEGIN:initComponents

        javax.swing.JRadioButton aRadioButton = null;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.X_AXIS));
        setLayout(new java.awt.BorderLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(jLabel1, java.awt.BorderLayout.CENTER);
        jRadioButtonArray = new JRadioButton[labelValues.length];

        for (int i = 0; i < labelValues.length; i++) {
            aRadioButton = new javax.swing.JRadioButton();
            buttonGroup1.add(aRadioButton);

            if (i == 0) {
                aRadioButton.setSelected(true);
            }

            aRadioButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jRadioButtonActionPerformed(evt);
                    }
                });

            jPanel1.add(aRadioButton);
            jRadioButtonArray[i] = aRadioButton;
        }

        add(jPanel1, java.awt.BorderLayout.EAST);
    } //GEN-END:initComponents

    private void jRadioButtonActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jRadioButtonActionPerformed

        // Add your handling code here:
        for (int i = 0; i < jRadioButtonArray.length; i++) {
            if (jRadioButtonArray[i].equals(evt.getSource())) {
                jLabel1.setText(labelValues[i]);
                selectedValue = dataValues[i];
                stopCellEditing();
                notify_controller();

                return;
            }
        }
    } //GEN-LAST:event_jRadioButtonActionPerformed
}
