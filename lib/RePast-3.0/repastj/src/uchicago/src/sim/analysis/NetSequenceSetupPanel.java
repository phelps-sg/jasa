/*$$
 * Copyright (c) 1999, Trustees of the University of Chicago
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
 * Neither the name of the University of Chicago nor the names of its
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
package uchicago.src.sim.analysis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import uchicago.src.guiUtils.ColorCellEditor;
import uchicago.src.guiUtils.ColorIcon;

/**
 * The setup panel displayed on a tab in a NetSequencePlot. This is
 * not for the use of modelers.
 *
 * @version $Revision$ $Date$
 */

public class NetSequenceSetupPanel extends JPanel {

  private JTable table;

  public NetSequenceSetupPanel(ArrayList rows) {
    super(new BorderLayout());
    table = new JTable(new NetPlotTableModel(rows));
    TableColumn col = table.getColumnModel().getColumn(2);
    col.setCellRenderer(new ColorRenderer());
    col.setCellEditor(new ColorCellEditor());
    //table.setDefaultRenderer(Color.class, new ColorRenderer());


    col = table.getColumnModel().getColumn(3);
    col.setCellRenderer(new MarkRenderer());
    Object[] markTypes = {new Integer(1), new Integer(2), new Integer(3),
			  new Integer(4), new Integer(5), new Integer(6),
			  new Integer(7), new Integer(8), new Integer(9)};
    JComboBox box = new JComboBox(markTypes);
    box.setRenderer(new MarkListRenderer());
    col.setCellEditor(new DefaultCellEditor(box));
    JScrollPane p = new JScrollPane(table);
    add(p, BorderLayout.CENTER);
  }
}



class NetPlotTableModel extends AbstractTableModel {

  private ArrayList rows;
  private String[] names = {"Statistic", "Show", "Color", "Mark Type"};
  private int rowCount;
  public static final int COL_COUNT = 4;

  public NetPlotTableModel(ArrayList rows) {
    this.rows = rows;
    rowCount = rows.size();
  }

  public String getColumnName(int index) {
    return names[index];
  }

  public int getRowCount() {
    return rowCount;
  }

  public int getColumnCount() {
    return COL_COUNT;
  }

  public Class getColumnClass(int col) {
    return NetSeqTableRow.getClassAt(col);
  }

  public Object getValueAt(int row, int col) {
    NetSeqTableRow tRow = (NetSeqTableRow)rows.get(row);
    return tRow.getValueAt(col);
  }

  public boolean isCellEditable(int row, int col) {
    if (col != 0) {
      NetSeqTableRow tRow = (NetSeqTableRow)rows.get(row);
      return tRow.isEditable();
    }
    return false;
  }

  public void setValueAt(Object val, int row, int col) {
    NetSeqTableRow tRow = (NetSeqTableRow)rows.get(row);
    if (col == 1) tRow.setShow(((Boolean)val).booleanValue());
    else if (col == 2) tRow.setColor((Color)val);
    else if (col == 3) tRow.setMarkType(((Integer)val).intValue());
  }
}

class MarkRenderer extends DefaultTableCellRenderer {
  private MarkIcon icon = new MarkIcon(10);

  public MarkRenderer() {
    setIcon(icon);
    setHorizontalAlignment(CENTER);
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
						 boolean isSelected,
						 boolean isFocused, int row,
						 int col)
  {
    Component comp = super.getTableCellRendererComponent(table, value,
							 isSelected, isFocused,
							 row, col);
    icon.setType(((Integer)value).intValue());
    setIcon(icon);
    setText("");
    return comp;
  }
}

class ColorRenderer extends DefaultTableCellRenderer {
  private ColorIcon icon = new ColorIcon(Color.white, 10, 10, 1);

  public ColorRenderer() {
    setIcon(icon);
    setHorizontalAlignment(CENTER);
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
						 boolean isSelected,
						 boolean isFocused, int row,
						 int col)
  {
    Component comp = super.getTableCellRendererComponent(table, value,
							 isSelected, isFocused,
							 row, col);
    icon.setColor((Color)value);
    setIcon(icon);
    setText("");
    return comp;
  }
}

