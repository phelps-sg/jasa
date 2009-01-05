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

import java.awt.event.KeyEvent;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;


/**
 * This table, unlike the regular JTable out-of-the-box will:
 *         - support multiple row deletion with delete key
 *  - support cell's content deletion with delete key
 *  - end cell editing with Enter key
 *  - stop cell editing on loss of focus
 *
 * @author wes maciorowski
 *  Created on Sep 25, 2003
 *
 */
public class EnhancedJTable extends JTable {
    /** Source code revision. */
    public final static String revision = "$Revision$";
    EnhancedTableModel anEnhancedTableModel;

    /**
     * Default constructor
     *
     * Sep 2, 2004
     * @param anEnhancedTableModel
     * @param selectedColumnWidth
     */
    public EnhancedJTable(final EnhancedTableModel anEnhancedTableModel,
        int selectedColumnWidth) {
        super(anEnhancedTableModel);
        this.anEnhancedTableModel = anEnhancedTableModel;
        this.anEnhancedTableModel.setEnhancedJTable(this);
        setRowSelectionAllowed(true);
        setColumnSelectionAllowed(false);
        addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                        int[] selRow = getSelectedRows();

                        if (selRow.length >= 1) {
                            //deleting rows
                            for (int i = selRow.length - 1; i >= 0; i--) {
                                anEnhancedTableModel.removeRow(i);
                            }
                        } else if (selRow.length == 1) {
                            //deleting cell
                            anEnhancedTableModel.setValueAt(null, selRow[0],
                                getSelectedColumn());
                        }

                        anEnhancedTableModel.fireTableDataChanged();
                    } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                        TableCellEditor editor = getCellEditor();

                        if (isEditing() && (editor != null)) {
                            editor.stopCellEditing();
                            anEnhancedTableModel.fireTableDataChanged();
                        }
                    } else {
                        super.keyPressed(evt);
                    }
                }
            });

        this.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        /*
                     addFocusListener(new java.awt.event.FocusAdapter()
                                               {
                                                     public void focusLost(FocusEvent e)
                                                     {
                                                             TableCellEditor editor = getCellEditor();
                                                             if(isEditing() && editor!= null) {
                                                                     editor.stopCellEditing();
                                                                     System.out.println(editor.getCellEditorValue());
                                                                     aECMOTableModel.fireTableDataChanged();
                                                             }
                                                     }
                                               });
                 */
        fixColumnSizing(this, selectedColumnWidth);
    }

    /**
     * Sets all columns of aTable to provided colWidth width.
     * @param aTable
     * @param colWidth
     */
    protected void fixColumnSizing(int col, int colWidth) {
        TableColumn aTableColumn2 = null;
        aTableColumn2 = getColumnModel().getColumn(col);

        if (aTableColumn2.getPreferredWidth() < (colWidth + 10)) {
            aTableColumn2.setPreferredWidth(colWidth + 10);
        }
    }

    /**
     * Sets all columns of aTable to provided colWidth width.
     * @param aTable
     * @param colWidth
     */
    private void fixColumnSizing(JTable aTable, int colWidth) {
        aTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumn aTableColumn2 = null;

        for (int l = 0; l < aTable.getColumnModel().getColumnCount(); l++) {
            aTableColumn2 = aTable.getColumnModel().getColumn(l);
            aTableColumn2.setPreferredWidth(colWidth);
        }
    }
}
