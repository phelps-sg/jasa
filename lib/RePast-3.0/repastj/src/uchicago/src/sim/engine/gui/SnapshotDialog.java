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
package uchicago.src.sim.engine.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import uchicago.src.guiUtils.Wizard;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SnapshotScheduler;
import uchicago.src.sim.gui.ProducerNamePair;
import uchicago.src.sim.util.SimUtilities;

/**
 * Dialog for taking snapshots.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class SnapshotDialog extends Wizard {

  private static final int FIRST_PANEL = 0;
  private static final int SECOND_PANEL = 1;
  private static final int CAPTURE_NONE = 2;
  private static final int CAPTURE_PAUSE = 3;
  private static final int CAPTURE_INTERVAL = 4;

  private int state = FIRST_PANEL;
  private int capture = CAPTURE_NONE;

  private JPanel firstPanel = new JPanel(new BorderLayout());
  private JPanel secondPanel = new JPanel(new BorderLayout());
  private JComboBox displays;
  private JTextField fldName = new JTextField();
  private JButton btnBrowse = new JButton("Browse");
  private JRadioButton btnPauseAndEnd = new JRadioButton("At Pause and End");
  private JRadioButton btnInterval = new JRadioButton("At Every nth Tick");
  private JRadioButton btnNone = new JRadioButton("At Button Click");
  private JTextField fldInterval = new JTextField(3);
  private JLabel buttonLabel = new JLabel("", SwingConstants.LEFT);

  private JFrame frame = null;
  private SnapshotScheduler snapshotScheduler;
  private int interval = 0;
  private String dispName = "";

  private static final String NONE_HELP = "<html><font color=black size=-1><b>" +
                          "Take snapshot whenever the 'Take X Snapshot' button " +
                          "on the Repast Actions panel is clicked where X " +
                          "is replaced by the DisplaySurface name. This will " +
                          "add a new button to the RePast Actions panel" +
                          "</b></font></html>";

  private static final String PAUSE_HELP = "<html><font color=black size=-1><b>" +
                          "Take snapshot whenever the simulation pauses, and " +
                          "ends.</b></font></html>";

  private static final String INTERVAL_HELP = "<html><font color=black size=-1><b>" +
                          "Take snapshot at the interval given in the text field " +
                          "below, e.g. every 100th tick.</b></font></html>";

  public SnapshotDialog(Vector surfaces) {
    super(false);
    super.setTopPanel(firstPanel);
    guiInit(surfaces);
    btnBack.setEnabled(false);
    addListeners();
  }

  private void addListeners() {
    btnBrowse.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        FileDialog fd = new FileDialog(frame, "Snapshot File", FileDialog.LOAD);
        fd.show();
        String file = fd.getFile();

        if (file == null)
          return;

        file = fd.getDirectory() + file;
        fd.dispose();


        fldName.setText(file);
      }
    });

    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        cancel = true;
        close();
      }
    });

    btnNext.addActionListener(next);
    btnBack.addActionListener(back);
    btnNone.setActionCommand("none");
    btnInterval.setActionCommand("interval");
    btnPauseAndEnd.setActionCommand("pause");

    btnNone.addActionListener(captureAt);
    btnPauseAndEnd.addActionListener(captureAt);
    btnInterval.addActionListener(captureAt);

  }

  private Action captureAt = new AbstractAction() {
    public void actionPerformed(ActionEvent evt) {
      JRadioButton source = (JRadioButton)evt.getSource();

      if (source.getActionCommand().equals("none")) {
        capture = CAPTURE_NONE;
        fldInterval.setEnabled(false);
        buttonLabel.setText(NONE_HELP);
      } else if (source.getActionCommand().equals("interval")) {
        capture = CAPTURE_INTERVAL;
        fldInterval.setEnabled(true);
        buttonLabel.setText(INTERVAL_HELP);
      } else {
        capture = CAPTURE_PAUSE;
        buttonLabel.setText(PAUSE_HELP);
        fldInterval.setEnabled(false);
      }
    }
  };


  public void display(JFrame owner, String title) {
    frame = owner;
    super.setSize(337, 286);
    super.display(owner, title, false);
  }

  private void guiInit(Vector v) {
    super.guiInit();
    displays = new JComboBox(v);

    JLabel label = new JLabel();
    String help = "<html><font color=black size=-1><b>" +
                  "Enter a base file name for the snapshot, and choose a " +
                  "display surface as the source of the snapshot. " +
                  "RePast will append the tick count at which the snapshot was " +
                  "taken as well as the '.png' extension, creating a new file " +
                  "each time a snapshot is taken.</b></font></html>";
    label.setText(help);
    firstPanel.add(label, BorderLayout.NORTH);

    JPanel subP = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.insets = new Insets(2, 2, 2, 2);
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 0.0;
    c.weighty = 0.0;

    c.gridwidth = 1;
    subP.add(new JLabel("File Name: "), c);
    c.gridy = GridBagConstraints.RELATIVE;
    subP.add(new JLabel("Display: "), c);

    c.weightx = 1.0;
    c.gridy = 0;
    c.gridx = 1;

    subP.add(fldName, c);
    c.gridy = 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    subP.add(displays, c);

    c.weightx = 0.0;
    c.gridx = 2;
    c.gridy = 0;
    c.gridwidth = 1;
    subP.add(btnBrowse, c);

    firstPanel.add(subP, BorderLayout.SOUTH);
    firstPanel.setBorder(BorderFactory.createEtchedBorder());

    // secondPanel
    buttonLabel.setText(NONE_HELP);
    secondPanel.add(buttonLabel, BorderLayout.NORTH);


    JPanel subP1 = new JPanel(new GridBagLayout());
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1.0;
    c.fill = GridBagConstraints.HORIZONTAL;

    subP1.add(btnNone, c);
    c.gridy = GridBagConstraints.RELATIVE;
    subP1.add(btnPauseAndEnd, c);
    subP1.add(btnInterval, c);

    c.gridy = 2;
    c.gridx = 1;
    c.weightx = 1.0;
    subP1.add(fldInterval, c);
    fldInterval.setEnabled(false);

    ButtonGroup bg = new ButtonGroup();
    bg.add(btnPauseAndEnd);
    bg.add(btnNone);
    bg.add(btnInterval);

    btnNone.setSelected(true);
    secondPanel.add(subP1, BorderLayout.SOUTH);
    secondPanel.setBorder(BorderFactory.createTitledBorder("Take Snapshot"));

    Dimension browseSize = btnBrowse.getPreferredSize();
    Dimension betterSize = new Dimension(browseSize.width, fldName.getPreferredSize().height);
    btnBrowse.setPreferredSize(betterSize);

  }

  private Action next = new AbstractAction() {
    public void actionPerformed(ActionEvent evt) {
      if (state == FIRST_PANEL) {
        if (fldName.getText().trim().length() == 0) {
          SimUtilities.showMessage("You must provide a snapshot file name");
          return;
        }
        setTopPanel(secondPanel);
        state = SECOND_PANEL;
        btnNext.setText("Finished");
        btnBack.setEnabled(true);
      } else {
        if (capture == CAPTURE_INTERVAL) {
          String num = fldInterval.getText().trim();
          if (num.length() == 0) {
            SimUtilities.showMessage("You must provide a numeric interval");
            return;
          }

          try {
            interval = Integer.parseInt(num);
            if (interval <= 0) {
              SimUtilities.showMessage("Interval must be a positive whole number");
              return;
            }
          } catch (NumberFormatException ex) {
            SimUtilities.showMessage("Interval must be a positive whole number");
            return;
          }
        }
        makeRetVal();
        close();
      }
    }
  };

  private Action back = new AbstractAction() {
    public void actionPerformed(ActionEvent evt) {
      btnBack.setEnabled(false);
      setTopPanel(firstPanel);
      btnNext.setText("Next");
      state = FIRST_PANEL;
    }
  };

  private void makeRetVal() {
    String fileName = fldName.getText();
    ProducerNamePair pnp = (ProducerNamePair)displays.getSelectedItem();
    snapshotScheduler = new SnapshotScheduler(fileName, pnp.getProducer(),
      pnp.getName());
    dispName = pnp.getName();
  }

  public SnapshotScheduler scheduleSnapshot(Schedule schedule) {

    if (!cancel) {
      if (capture == CAPTURE_NONE) {
        snapshotScheduler.onButtonClick("Take " + dispName + " Snapshot");
        return snapshotScheduler;
      } else if (capture == CAPTURE_PAUSE) {
        snapshotScheduler.scheduleAtPauseAndEnd(schedule);
      } else {
        snapshotScheduler.scheduleAtInterval(schedule, interval);
      }
    }

    // for now don't return snapshotScheduler if not associated with a button
    return null;
  }

  /*
  public static void main(String[] args) {
    Vector v = new Vector();
    v.add("Sam");
    v.add("Bill");

    SnapshotDialog d = new SnapshotDialog(v);
    JFrame frame = new JFrame();
    d.display(frame, "Test");
  }
  */
}



