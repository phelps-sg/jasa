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
package uchicago.src.reflector;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import uchicago.src.sim.util.BeanBowlUtils;

/**
 * The panel that is shown on an IntrospectFrame.  This holds the actual
 * introspective controls.
 * 
 * @author Nick Collier modified by Jerry Vos
 * @version $Revision$ $Date$
 */
public class IntrospectPanel extends JPanel implements ActionListener {

  private Introspector spector = new Introspector();
  private Object spectee;
  private String[] introProps = null;
  private PropertyWidget firstWidget = null;
  private ArrayList valueFields = new ArrayList(11);
  private boolean alphaOrder = true;
  private boolean first = true;
  private boolean showBeanBowlButton = true;

  /**
   * the button used to pull up the bean bowl gui
   */
  private JButton beanBowlButton = null;
  
  
  public IntrospectPanel(Object o)
          throws InvocationTargetException, IllegalAccessException,
          IntrospectionException {
    this(o, null, true);
  }
  
  public IntrospectPanel(Object o, String[] propsToIntrospect, boolean alphaOrder, boolean showBeanBowlButton)
          throws InvocationTargetException, IllegalAccessException,
          IntrospectionException 
  {
    spectee = o;
    //if (propsToIntrospect == null) introProps = new String[]{};
    //else
    introProps = propsToIntrospect;
    this.alphaOrder = alphaOrder;
    this.showBeanBowlButton = showBeanBowlButton;
    init();
    
  }

  public IntrospectPanel(Object o, String[] propsToIntrospect, boolean alphaOrder)
          throws InvocationTargetException, IllegalAccessException,
          IntrospectionException 
  {
    this(o, propsToIntrospect, alphaOrder, true);
  }

  public void setAlphaOrder(boolean alphaOrder) {
    this.alphaOrder = alphaOrder;
  }

  private Iterator getProps() {
    Iterator props = null;

    if (alphaOrder) {
      props = spector.getPropertyNames();
    } else {
      ArrayList list = new ArrayList();
      int length = 0;
      if (introProps != null) {
        if (introProps.length > 0) length = introProps.length;
      }

      for (int i = 0; i < length; i++) {
        // ensure that the first letter of the property is upper case
        StringBuffer key = new StringBuffer(introProps[i].trim());
        if (key.length() > 0) {
          char ch = Character.toUpperCase(key.charAt(0));
          key.setCharAt(0, ch);
          String propName = key.toString();
          if (spector.hasProperty(propName)) {
            list.add(propName);
          }
        }
      }
      props = list.iterator();
    }

    return props;
  }

  private void init() throws IllegalAccessException, InvocationTargetException,
          IntrospectionException {
    Hashtable descriptors = new Hashtable();
    ArrayList actionDescriptors = new ArrayList();

    if (spectee instanceof DescriptorContainer) {
      descriptors = ((DescriptorContainer) spectee).getParameterDescriptors();
      Enumeration e = descriptors.keys();
      while (e.hasMoreElements()) {
        Object key = e.nextElement();

        // this is unfortunately necessary if want clients to only implement
        // DescriptorContainer and have the returned Hashtable contain
        // both PropertyDescriptor-s and ActionDescriptor-s. So, life is
        // simpler for the client, but inelegant here.
        Object desc = descriptors.get(key);
        if (desc instanceof ActionDescriptor) {
          actionDescriptors.add(desc);
          descriptors.remove(key);
        }
      }
    }

    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0.0;
    c.weighty = 0.0;
    c.fill = GridBagConstraints.HORIZONTAL;

    spector.introspect(spectee, introProps);
    Iterator props = getProps();
    
    //spector.printProperties();

    WidgetFactory factory = new WidgetFactory(spector, valueFields, descriptors);
    while (props.hasNext()) {
      // iterate through each property and create the label and the appropriate
      // type of widget for that property
      String propertyName = (String) props.next();
      JLabel label = new JLabel(propertyName + ": ");
      add(label, c);
      c.gridx = 1;
      c.weightx = 1.0;

      PropertyWidget widget = factory.getWidget(propertyName);
      widget.addActionListener(this);
      widget.setPropertyName(propertyName);
      add((Component) widget, c);
      
      if (first) {
        firstWidget = widget;
        first = false;
      }
      
      c.gridy++;
      c.gridx = 0;
      c.weightx = 0.0;
    }

    addActionDescriptors(actionDescriptors, c);
    if (showBeanBowlButton) addBeanBowl(c);
  }

  private void addActionDescriptors(ArrayList descriptors,
                                    GridBagConstraints c) {

    c.gridwidth = GridBagConstraints.REMAINDER;
    for (int i = 0; i < descriptors.size(); i++) {
      ActionDescriptor d = (ActionDescriptor) descriptors.get(i);
      add(d.getComponent(), c);
      c.gridy++;
    }
  }
    
  /**
   * adds the button to start probing this object in the bean bowl, this button
   * is <c>IntrospectPanel.beanBowlButton</c>.
   */
  private void addBeanBowl(GridBagConstraints c) {
  	c.gridwidth = GridBagConstraints.REMAINDER;
  	
  	beanBowlButton = new JButton("Inspect Agent");
  	beanBowlButton.setMnemonic('i');
  	
  	beanBowlButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			BeanBowlUtils.probe(spectee);
		}
  	});
  	
  	add(beanBowlButton, c);
  	
  	c.gridy++;
  }


  public void redraw() throws IllegalAccessException,
          InvocationTargetException, IntrospectionException {
    this.removeAll();
    init();
    this.invalidate();
  }

  public void reset() {
    Iterator iter = valueFields.iterator();

    while (iter.hasNext()) {
      Object o = iter.next();
      try {
        PropertyWidget widget = (PropertyWidget) o;
        widget.setValue(spector.getPropertyValue(widget.getPropertyName()));
      } catch (Exception ex) {
        ex.printStackTrace();
        System.exit(0);
      }
    }
  }

  public void setFocus() {
    if (firstWidget != null) {
      firstWidget.requestFocus();
    }
  }

  // actionListener Interface
  public void actionPerformed(ActionEvent evt) {
    //System.out.println("Action Performed by " + evt.getActionCommand());
    PropertyWidget widget = (PropertyWidget) evt.getSource();
    /*
    String param = widget.getValue().trim();
    if (param.length() == 0)
      return;
    */

    Object param = widget.getValue();
    try {
      spector.invokeSetMethod(widget.getPropertyName(), param);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setEnabled(boolean enabled) {
    for (int i = 0; i < valueFields.size(); i++) {
      PropertyWidget w = (PropertyWidget) valueFields.get(i);
      w.setEnabled(enabled);
    }
  }
  
  /**
   * @return the button that starts the bean bowl probing
   */
  public JButton getBeanBowlButton() {
    return beanBowlButton;
  }
  
}

