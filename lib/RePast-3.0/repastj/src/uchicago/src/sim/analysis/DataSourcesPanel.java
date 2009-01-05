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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import layout.TableLayout;

/**
 *
 * @version $Revision$ $Date$
 */

public class DataSourcesPanel extends JPanel implements ChartGuiPanel {

  JButton btnAdd, btnDelete;
  JComboBox cmbClass;
  JList lstClassSources, lstDataSources;
  AbstractChartModel model;

  public DataSourcesPanel() {
    Class clazz = getClass();
    btnDelete = new JButton(new ImageIcon(clazz.getResource("/uchicago/src/sim/images/Left.gif")));
    btnAdd = new JButton(new ImageIcon(clazz.getResource("/uchicago/src/sim/images/Right.gif")));
    setBorder(BorderFactory.createTitledBorder("Add Data Sources"));

    cmbClass = new JComboBox();
    lstClassSources = new JList();
    lstClassSources.setModel(new DefaultListModel());
    lstDataSources = new JList();
    lstDataSources.setModel(new DefaultListModel());

    int hborder = 5;
    int hiborder = 8;
    int vborder = 8;
    int btnHeight = btnAdd.getPreferredSize().height;

    double[][] sizes = {{hborder, .5, hiborder, 30, hiborder, .5, hborder},
                        {vborder, cmbClass.getPreferredSize().height, vborder, .33, btnHeight, vborder,
                         btnHeight, vborder, .33, .33, vborder}};
    setLayout(new TableLayout(sizes));
    add(cmbClass, "1, 1");
    add(new JScrollPane(lstClassSources), "1, 3, 1, 9");
    add(btnAdd, "3, 4");
    add(btnDelete, "3, 6");
    add(new JScrollPane(lstDataSources), "5, 1, 5, 9");

    lstClassSources.setCellRenderer(new DefaultListCellRenderer() {
      public Component getListCellRendererComponent(JList list, Object value,
              int index, boolean isSelected, boolean cellHasFocus) {
        JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                                                  cellHasFocus);
        l.setText(((GuiChartDataSource)value).getShortName());
        return l;
      }
    });

    lstDataSources.setCellRenderer(new DefaultListCellRenderer() {
      public Component getListCellRendererComponent(JList list, Object value,
              int index, boolean isSelected, boolean cellHasFocus) {
        JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                                                  cellHasFocus);
        l.setText(((GuiChartDataSource)value).getFullName());
        return l;
      }
    });

    addListeners();
  }

  private void addListeners() {
    btnAdd.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        int index = lstClassSources.getSelectedIndex();
        if (index != -1) {
          Object o = lstClassSources.getSelectedValue();
          DefaultListModel lm = (DefaultListModel)lstDataSources.getModel();
          lm.addElement(o);
        }
      }
    });

    btnDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        int index = lstDataSources.getSelectedIndex();
        if (index != -1) {
          DefaultListModel lm = (DefaultListModel)lstDataSources.getModel();
          lm.remove(index);
        }
      }
    });
  }

  public AbstractChartModel updateModel() {
    ArrayList list = new ArrayList();
    DefaultListModel lm = (DefaultListModel)lstDataSources.getModel();
    for (int i = 0, n = lm.size(); i < n; i++) {
      list.add(lm.get(i));
    }
    model.setDataSources(list);

    return model;
  }

  public void setModel(AbstractChartModel model) {
    this.model = model;
    fillLists();
  }

  private void fillLists() {
    DefaultComboBoxModel cmbModel = (DefaultComboBoxModel) cmbClass.getModel();
    String name = model.getSimModel().getClass().getName();
    int index = name.lastIndexOf('.');
    if (index != -1) name = name.substring(index + 1);
    cmbModel.addElement(name);

    DefaultListModel listModel = (DefaultListModel) lstClassSources.getModel();
    listModel.clear();
    ArrayList list = model.getModelDataSources();
    for (int i = 0; i < list.size(); i++) {
      listModel.addElement(list.get(i));
    }

    listModel = (DefaultListModel) lstDataSources.getModel();
    listModel.clear();
    list = model.getDataSources();
    for (int i = 0; i < list.size(); i++) {
      listModel.addElement(list.get(i));
    }
  }
}
