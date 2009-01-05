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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import layout.TableLayout;
import uchicago.src.guiUtils.ColorChooserPopup;
import uchicago.src.guiUtils.ColorIcon;

/**
 *
 * @version $Revision$ $Date$
 */

public class DataSourcePropPanel extends JPanel implements ChartGuiPanel {

  private AbstractChartModel model;
  private int curListIndex = -1;
  private Color color;
  JTextField txtName = new JTextField("A Sequence");
  JComboBox cmbMarkStyle;
  JButton btnColor = new JButton();
  JList lstSources = new JList();
  private static final int ICON_SIZE = 14;

  public DataSourcePropPanel() {
    lstSources.setModel(new DefaultListModel());
    setBorder(BorderFactory.createTitledBorder("Sequence Display Properties"));
    btnColor.setIcon(new ColorIcon(Color.blue, ICON_SIZE));
    Object[] markTypes = {new Integer(0), new Integer(1), new Integer(2), new Integer(3),
                          new Integer(4), new Integer(5), new Integer(6),
                          new Integer(7), new Integer(8), new Integer(9)};
    cmbMarkStyle = new JComboBox(markTypes);
    cmbMarkStyle.setRenderer(new MarkListRenderer());

    int hborder = 5;
    int hiborder = 8;
    int vborder = 8;
    int height = txtName.getPreferredSize().height;

    double[][] sizes = {{hborder, .5, hiborder, .25, hiborder, .25, hborder},
                        {vborder, height, height, vborder, height,
                         btnColor.getPreferredSize().height, .5, .5, vborder}};
    setLayout(new TableLayout(sizes));

    add(new JScrollPane(lstSources), "1, 1, 1, 7");
    add(new JLabel("Name:"), "3, 1, 5, 1");
    add(txtName, "3, 2, 5, 2");
    add(new JLabel("Color:"), "3, 4");
    add(btnColor, "3, 5");
    add(new JLabel("Mark Style:"), "5, 4");
    add(cmbMarkStyle, "5, 5");

    lstSources.setCellRenderer(new DefaultListCellRenderer() {
      public Component getListCellRendererComponent(JList list, Object value,
                                                    int index, boolean isSelected, boolean cellHasFocus) {
        JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                                                               cellHasFocus);
        l.setText(((GuiChartDataSource) value).getFullName());
        return l;
      }
    });

    addListeners();
  }

  public void addListeners() {
    lstSources.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
          if (curListIndex != -1) {
            DefaultListModel listModel = (DefaultListModel) lstSources.getModel();
            SequenceSource source = (SequenceSource) listModel.getElementAt(curListIndex);
            source.setName(txtName.getText());

          }

          curListIndex = lstSources.getSelectedIndex();
          SequenceSource source = (SequenceSource) lstSources.getSelectedValue();
          if (source != null) {
            btnColor.setIcon(new ColorIcon(source.getColor(), ICON_SIZE));
            txtName.setText(source.getName());
            cmbMarkStyle.setSelectedItem(new Integer(source.getMarkStyle()));
          }

        }
      }
    });

    btnColor.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        final ColorChooserPopup popup = new ColorChooserPopup();
        popup.addColorChangeListener(new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            color = popup.getColor();
            btnColor.setIcon(new ColorIcon(color, ICON_SIZE));
            if (curListIndex != -1) {
              DefaultListModel listModel = (DefaultListModel) lstSources.getModel();
              SequenceSource source = (SequenceSource) listModel.getElementAt(curListIndex);
              source.setColor(color);
            }

            popup.setVisible(false);
          }
        });
        popup.show(btnColor, 0, 0);
      }
    });

    cmbMarkStyle.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        if (curListIndex != -1) {
          DefaultListModel listModel = (DefaultListModel) lstSources.getModel();
          SequenceSource source = (SequenceSource) listModel.getElementAt(curListIndex);
          Integer val = (Integer) cmbMarkStyle.getSelectedItem();
          source.setMarkStyle(val.intValue());
        }
      }
    });

    txtName.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        if (curListIndex != -1) {
          DefaultListModel listModel = (DefaultListModel) lstSources.getModel();
          SequenceSource source = (SequenceSource) listModel.getElementAt(curListIndex);
          source.setName(txtName.getText());
        }
      }
    });

  }

  public AbstractChartModel updateModel() {
    DefaultListModel listModel = (DefaultListModel) lstSources.getModel();
    ArrayList list = new ArrayList();
    for (int i = 0, n = listModel.size(); i < n; i++) {
      list.add(listModel.elementAt(i));
    }
    model.setDataSources(list);
    return model;
  }

  public void setModel(AbstractChartModel model) {
    this.model = model;
    curListIndex = -1;
    DefaultListModel listModel = (DefaultListModel) lstSources.getModel();
    listModel.clear();
    ArrayList list = model.getDataSources();
    for (int i = 0; i < list.size(); i++) {
      listModel.addElement(list.get(i));
    }

    if (listModel.size() > 0) {
      lstSources.setSelectedIndex(0);
    }
  }
}
