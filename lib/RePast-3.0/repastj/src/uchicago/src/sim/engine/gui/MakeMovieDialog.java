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

import uchicago.src.guiUtils.Wizard;
import uchicago.src.sim.engine.MovieScheduler;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.gui.ProducerNamePair;
import uchicago.src.sim.util.SimUtilities;

/**
 * Dialog for making movies. Used by Controller via the Repast Action tab.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class MakeMovieDialog extends Wizard {

  private static final int FIRST_PANEL = 0;
  private static final int SECOND_PANEL = 1;
  private static final int CAPTURE_EVERY = 2;
  private static final int CAPTURE_PAUSE = 3;
  private static final int CAPTURE_INTERVAL = 4;

  private static final String EVERY_HELP = "<html><font color=black size=-1><b>" +
                              "Captures the displayed image as a movie frame " +
                              "every iteration of the simulation." +
                              "</b></font></html>";

  private static final String PAUSE_HELP = "<html><font color=black size=-1><b>" +
                              "Captures the displayed image as movie frame " +
                              "at a pause in or at the end of a simulation run." +
                              "</b></font></html>";

  private static final String INTERVAL_HELP = "<html><font color=black size=-1><b>" +
                              "Captures the displayed image as movie frame " +
                              "at the interval specified in the field below, " +
                              "e.g. every 10th iteration of the simulation." +
                              "</b></font></html>";

  private int state = FIRST_PANEL;
  private int capture = CAPTURE_EVERY;

  private JPanel firstPanel = new JPanel(new BorderLayout());
  private JPanel secondPanel = new JPanel(new BorderLayout());
  private JComboBox displays;
  private JTextField fldName = new JTextField();
  private JButton btnBrowse = new JButton("Browse");
  private JRadioButton btnPauseAndEnd = new JRadioButton("At Pause and End");
  private JRadioButton btnEveryTick = new JRadioButton("At Every Tick", true);
  private JRadioButton btnInterval = new JRadioButton("At Every nth Tick");
  private JTextField fldInterval = new JTextField(3);

  private JLabel buttonHelp = new JLabel();

  private JFrame frame = null;
  private MovieScheduler movieScheduler;
  private int interval = 0;

  public MakeMovieDialog(Vector surfaces) {
    super(false);
    super.setTopPanel(firstPanel);
    guiInit(surfaces);
    btnBack.setEnabled(false);
    addListeners();
  }

  private void addListeners() {
    btnBrowse.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        FileDialog fd = new FileDialog(frame, "Movie File", FileDialog.LOAD);
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
    btnEveryTick.setActionCommand("every");
    btnInterval.setActionCommand("interval");
    btnPauseAndEnd.setActionCommand("pause");

    btnEveryTick.addActionListener(captureAt);
    btnPauseAndEnd.addActionListener(captureAt);
    btnInterval.addActionListener(captureAt);

  }

  private Action captureAt = new AbstractAction() {
    public void actionPerformed(ActionEvent evt) {
      JRadioButton source = (JRadioButton)evt.getSource();

      if (source.getActionCommand().equals("every")) {
        capture = CAPTURE_EVERY;
        fldInterval.setEnabled(false);
        buttonHelp.setText(EVERY_HELP);
      } else if (source.getActionCommand().equals("interval")) {
        capture = CAPTURE_INTERVAL;
        buttonHelp.setText(INTERVAL_HELP);
        fldInterval.setEnabled(true);
      } else {
        capture = CAPTURE_PAUSE;
        buttonHelp.setText(PAUSE_HELP);
        fldInterval.setEnabled(false);
      }
    }
  };


  public void display(JFrame owner, String title) {
    frame = owner;
    super.setSize(337, 254);
    super.display(owner, title, false);
  }

  private void guiInit(Vector v) {
    super.guiInit();
    displays = new JComboBox(v);

    String help = "<html><font color=black size=-1><b>" +
                  "Enter a file name as the name of the movie, and choose " +
                  "a DisplaySurface as the source for the movie. Repast will " +
                  "append the '.mov' extension to the file name." +
                  "</b></font></html>";
    JLabel label = new JLabel(help);
    firstPanel.add(label, BorderLayout.NORTH);


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

    JPanel subP = new JPanel(new GridBagLayout());

    subP.add(new JLabel("File Name: "), c);
    c.gridy = 1;
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


    buttonHelp.setText(EVERY_HELP);
    secondPanel.add(buttonHelp, BorderLayout.NORTH);

    JPanel subP1 = new JPanel(new GridBagLayout());
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1.0;
    c.fill = GridBagConstraints.HORIZONTAL;

    subP1.add(btnPauseAndEnd, c);
    c.gridy = GridBagConstraints.RELATIVE;
    subP1.add(btnEveryTick, c);
    subP1.add(btnInterval, c);

    c.gridy = 2;
    c.gridx = 1;
    subP1.add(fldInterval, c);
    fldInterval.setEnabled(false);

    ButtonGroup bg = new ButtonGroup();
    bg.add(btnPauseAndEnd);
    bg.add(btnEveryTick);
    bg.add(btnInterval);

    btnEveryTick.setEnabled(true);
    secondPanel.add(subP1, BorderLayout.SOUTH);
    secondPanel.setBorder(BorderFactory.createTitledBorder("Capture Frame"));

  }

  private Action next = new AbstractAction() {
    public void actionPerformed(ActionEvent evt) {
      if (state == FIRST_PANEL) {
        if (fldName.getText().trim().length() == 0) {
          SimUtilities.showMessage("You must provide a movie file name");
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
    movieScheduler = new MovieScheduler(fileName, pnp.getProducer());
  }

  public void scheduleMovie(Schedule schedule) {
    if (!cancel) {
      if (capture == CAPTURE_EVERY) {
        movieScheduler.scheduleAtEveryTick(schedule);
      } else if (capture == CAPTURE_PAUSE) {
        movieScheduler.scheduleAtPauseAndEnd(schedule);
      } else {
        movieScheduler.scheduleAtInterval(schedule, interval);
      }
    }
  }

  /*
  public static void main(String[] args) {
    Vector v = new Vector();
    v.add("Sam");
    v.add("Bill");

    MakeMovieDialog d = new MakeMovieDialog(v);
    JFrame frame = new JFrame();
    d.display(frame, "Test");
  }
  */
}




