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
package uchicago.src.sim.engine;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;

import uchicago.src.collection.Pair;
import uchicago.src.sim.event.CheckBoxListener;
import uchicago.src.sim.event.SliderListener;


/**
 * Creates sliders and buttons for the manipulation of models.
 * ModelManipulator can be used by a modeler to create buttons, sliders and
 * checkboxes that can alter the state of the model while its running.
 * These buttons, sliders, and checkboxes are displayed in gui run of
 * a model on the Custom Actions tab in the settings window.<p>
 *
 * SimModelImpl contains a protected ivar (modelManipulator) of this
 * type that can be used by all models extending SimModelImpl. See
 * Heat Bugs and Hypercycles demonstration simulations for examples
 * of its use.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class ModelManipulator {

  private ArrayList sliders = new ArrayList();
  private ArrayList buttons = new ArrayList();
  private ArrayList components = new ArrayList();

  private boolean enabled = false;
  private JPanel panel;

  /**
   * Initializes the manipulator by clearing any previously added
   * sliders, buttons, or checkboxes. This should be done at the
   * start of each simulation run. Either in the the setup() method
   * of a model, or in a method that the setup() method calls (e.g.
   * setupCustomActions().
   */
  public void init() {
    sliders.clear();
    buttons.clear();
    components.clear();
    panel = null;
  }

  /**
   * Adds a slider with the specified label, min and max values,
   * tick interval and SliderListener to this manipulator. This slider
   * will then show up on the Custom Actions tab in the settings window.
   *
   * @param label the label for this slider
   * @param min the minimum value for this slider
   * @param max the maximum value for this slider
   * @param tickInterval the interval at which to draw label ticks for
   * this slider.
   * @param listener a listener that listens for and responds to events
   * fired by this slider. The action that should occur when a user
   * moves the slider is defined in this listener.
   *
   * @see uchicago.src.sim.event.SliderListener
   */
  public void addSlider(String label, int min, int max, int tickInterval,
        SliderListener listener)
  {

    JSlider slider = new JSlider();
    slider.setMaximum(max);
    slider.setMinimum(min);
    slider.setMajorTickSpacing(tickInterval);
    slider.createStandardLabels(tickInterval);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    slider.addChangeListener(listener);
    slider.setEnabled(enabled);
    slider.setValue(min);
    listener.setFirstVal(min);
    JLabel l = new JLabel(label);
    sliders.add(new Pair(l, slider));
  }

  /**
   * Adds a button with the specified label and listener to this
   * ModelManipulator. This button will appear on the Custom Actions
   * tab in the settings window. The behavoir that should occur when
   * the button is pressed is defined in the listener.
   *
   * @param label the label displayed by the button
   * @param listener the listener that listens for and responds to
   * clicks of the button.
   *
   * @see java.awt.event.ActionListener
   */
  public void addButton(String label, ActionListener listener) {
    JButton button = new JButton(label);
    button.addActionListener(listener);
    button.setEnabled(false);
    buttons.add(button);
  }

  /**
   * Adds the specified Component to this ModelManipulation. The Component
   * itself will appear on the Custom Actions tabs in the settings window.<p>
   *
   * This Component could be, for example, a JPanel containing a JTextField
   * and JButton such that whenever the button is clicked some data from
   * the model is saved to a file named in the JTextField.
   *
   * @param comp the component to addd
   */
  public void addComponent(Component comp) {
    components.add(comp);
  }

	/**
	 * Adds a checkbox to this ModelManipulator with the specified label,
	 * CheckBoxListener, and whether the box should be selected initially.
	 * This button will appear on the Custom Actions
	 * tab in the settings window.
	 * The behavoir that should occur when the box is checked is defined
	 * in the listener.
	 *
	 * @param label the label for this check box
	 * @param listener the listener for this checkbox
	 * @param isSelected if true the box will initially display as checked
	 * other as not checked.
	 */
  public void addCheckBox(String label, CheckBoxListener listener,
    boolean isSelected)
  {
    JCheckBox box = new JCheckBox(label, isSelected);
    box.addActionListener(listener);
    listener.setSelected(isSelected);
    buttons.add(box);
  }

	/**
	 * Enables or disables all the sliders, buttons, and checkboxes
	 * associated with this ModelManipulator.
	 *
	 * @param enabled if true all the sliders, buttons, and checkboxes
	 * associated with this ModelManipulator with be enabled. If false, they
	 * will be disabled.
	 */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    for (int i = 0; i < sliders.size(); i++) {
      Pair p = (Pair)sliders.get(i);
      JSlider slider = (JSlider)p.second;
      slider.setEnabled(enabled);
    }

    for (int i = 0; i < buttons.size(); i++) {
      AbstractButton button = (AbstractButton)buttons.get(i);
      button.setEnabled(enabled);
    }
  }

	/**
	 * Is this ModelManipulator's sliders, buttons, and checkboxes enabled
	 */
  public boolean isEnabled() {
    return enabled;
  }

	/**
	 * Gets a JPanel with the sliders, buttons, and checkboxes defined in the
	 * add* methods laid out and ready for display.
	 *
	 * @return a JPanel containing sliders, buttons, and checkboxes.
	 */
  public JPanel getPanel() {
    if (panel == null) {
      panel = new JPanel ();
      panel.setLayout (new BoxLayout (panel, BoxLayout.Y_AXIS));
      JPanel sPanel = new JPanel (new BorderLayout ());
      JPanel bPanel = new JPanel (new BorderLayout ());

      GridBagConstraints c = new GridBagConstraints ();
      c.gridx = 0;
      c.gridy = 0;
      c.weightx = 1.0;
      // c.anchor will not work unless weighty > 0. Bug in Swing.
      c.weighty = 0.0;
      c.fill = GridBagConstraints.BOTH;
      c.gridheight = 1;
      c.anchor = GridBagConstraints.NORTH;

      JPanel sSubPanel = new JPanel (new GridBagLayout ());

      for (int i = 0; i < sliders.size (); i++) {
        c.gridy = i;
        JPanel p = new JPanel (new GridLayout (3, 1, 0, 0));
        Pair pair = (Pair) sliders.get (i);
        p.add ((JLabel) pair.first);
        p.add ((JSlider) pair.second);
        sSubPanel.add (p, c);
      }

      Border b = BorderFactory.createEtchedBorder ();
      sPanel.setBorder (b); //BorderFactory.createTitledBorder(b, "Sliders"));
      sPanel.add (sSubPanel, BorderLayout.NORTH);

      JPanel bSubPanel = new JPanel (new GridBagLayout ());

      for (int i = 0; i < buttons.size (); i++) {
        c.gridy = i;
        bSubPanel.add ((AbstractButton) buttons.get (i), c);
      }

      Border b1 = BorderFactory.createEtchedBorder ();
      bPanel.setBorder (b1); //BorderFactory.createTitledBorder(b1, "Buttons"));
      bPanel.add (bSubPanel, BorderLayout.NORTH);

      JPanel compPanel = new JPanel(new GridBagLayout());
      compPanel.setBorder(BorderFactory.createEtchedBorder());

      for (int i = 0; i < components.size (); i++) {
        c.gridy = i;
        compPanel.add((Component)components.get (i), c);
      }

      panel.add (sPanel);
      panel.add (bPanel);
      panel.add(compPanel);
    }

    return panel;
  }
}










