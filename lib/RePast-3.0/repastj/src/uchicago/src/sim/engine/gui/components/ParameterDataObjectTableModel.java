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

import java.awt.event.ActionEvent;

import uchicago.src.sim.engine.gui.model.DataParameter;


/**
 * @author maciorowski
 *
 */
public class ParameterDataObjectTableModel extends EnhancedTableModel {
    /** Source code revision. */
    public final static String revision = "$Revision$";
    ParameterData aParameterData = null;
    Class[] colClasses = { String.class, String.class, Integer.class };
    private GUIControllerAbstract aGUIControllerAbstract = null;

    /**
     *
     * Jun 6, 2003
     *
     */
    public ParameterDataObjectTableModel(
        GUIControllerAbstract aGUIControllerAbstract,
        ParameterData aParameterData) {
        super();
        this.aGUIControllerAbstract = aGUIControllerAbstract;
        this.aParameterData = aParameterData;
    }

    /**
     * @param parameterData The aParameterData to set.
     */
    public void setAParameterData(ParameterData parameterData) {
        aParameterData = parameterData;
        fireTableDataChanged();
    }

    /**
     * @return Returns the aParameterData.
     */
    public ParameterData getAParameterData() {
        return aParameterData;
    }

    public boolean isCellEditable(int rowIndex, int col) {
        if (col == 2) {
            return true;
        }

        return false;
    }

    /**
     *
     * @param col
     * @return
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class getColumnClass(int col) {
        return colClasses[col];
    }

    /**
     * @return
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 3;
    }

    public String getColumnName(int col) {
        switch (col) {
        case 0:
            return "Parameter";

        case 1:
            return "Data Type";

        case 2:
            return "Function";

        default:
            return null;
        }
    }

    /**
     * @return
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        if (aParameterData == null) {
            return 0;
        }

        return aParameterData.getParameterList().size();
    }

    public void setValueAt(Object aValue, int row, int col) {
        super.fixColumnSizing(aValue, row, col);

        if (aParameterData != null) {
            DataParameter aParameter = (DataParameter) aParameterData.getParameterList()
                                                             .get(row);

            switch (col) {
            case 0:
                aParameter.setName(aValue.toString());

                break;

            case 1:
                aParameter.setDataType(aValue.toString());

                break;

            case 2:

                if (((Integer) aValue).intValue() == 0) {
                    aParameter.setInput(true);
                } else {
                    aParameter.setInput(false);
                }

                break;

            default:
                break;
            }

            if ((aGUIControllerAbstract != null)) {
                aGUIControllerAbstract.actionPerformed(new ActionEvent(this,
                        ActionEvent.ACTION_PERFORMED,
                        GUIControllerAbstract.INPUT_OUTPUT_CHANGED));
            }
        }
    }

    /**
     * @param row
     * @param col
     * @return
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col) {
        if (aParameterData == null) {
            return null;
        }

        DataParameter aParameter = (DataParameter) aParameterData.getParameterList()
                                                         .get(row);

        switch (col) {
        case 0:
            super.fixColumnSizing(aParameter.getName(), row, col);

            return aParameter.getName();

        case 1:
            return aParameter.getDataType();

        case 2:
            return (aParameter.isInput()) ? new Integer(0) : new Integer(1);

        default:
            return null;
        }
    }

    /**
     *
     * @see com.pg.ecmo.gui.components.jtable.EnhancedTableModel#insertRow()
     */
    public void insertRow() {
        DataParameter aParameter = new DataParameter("", "", true);
        aParameterData.getParameterList().add(aParameter);
        fireTableDataChanged();

        if ((aGUIControllerAbstract != null)) {
            aGUIControllerAbstract.actionPerformed(new ActionEvent(this,
                    ActionEvent.ACTION_PERFORMED,
                    GUIControllerAbstract.INPUT_OUTPUT_CHANGED));
        }
    }

    /**
     * @param i
     * @see com.pg.ecmo.gui.components.jtable.EnhancedTableModel#removeRow(int)
     */
    public void removeRow(int i) {
        aParameterData.getParameterList().remove(i);

        if ((aGUIControllerAbstract != null)) {
            aGUIControllerAbstract.actionPerformed(new ActionEvent(this,
                    ActionEvent.ACTION_PERFORMED,
                    GUIControllerAbstract.INPUT_OUTPUT_CHANGED));
        }
    }
}
