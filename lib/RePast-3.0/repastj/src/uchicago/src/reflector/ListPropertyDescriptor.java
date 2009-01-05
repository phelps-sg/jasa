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

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Descriptor for model properties/parameters that can be represented as a
 * closed list. A ListPropertyDescriptor can be used to present the user
 * with a drop-down combo box containing a list parameter values of which
 * he or she can choose one. It can also be used to represent a list of one
 * type as a list of another type - a list of ints as a list of strings for
 * example.<p>
 *
 * There are several ways to set up a list of parameter values to be displayed
 * by the combo box. A vector of Objects, an array of Objects or a hashtable
 * can be passed when the ListPropertyDescriptor when it is constructed. In
 * the first two cases combo box displays the vector or the array, and
 * passes the chosen object to the appropriate set method when combo-box item
 * is selected. In the case of the hashtable, the values of the hashtable
 * are displayed by the combox box and it is the key of the selected value
 * that is passed to the appropriate set method. This allows for
 * the display of natural language words representing numeric constants.<p>
 *
 * A ListPropertyDescriptor is typically setup in the constructor of your
 * model. For example,
 * <code><pre>
 *  Hashtable h1 = new Hashtable();
 *  h1.put(new Integer(0), "VON NEUMANN");
 *  h1.put(new Integer(1), "MOORE");
 *  ListPropertyDescriptor pd = new ListPropertyDescriptor("NType", h1);
 *  descriptors.put("NType", pd);
 * </pre></code>
 * A ListPropertyDescriptor is created with the name of the property, and a
 * hashtable as an argument. The property name must reflect the standard
 * get/set accessor method coding style. In above case, the model has two
 * methods getNType and setNType. Given the name of the property a
 * ListPropertyDescriptor will call the appropriate set method when an
 * item in the combo box is seleted. The second argument is a hashtable
 * whose keys are the actual objects that can be selected and passed to the
 * set method as well as a string representations of those objects.
 * In the above case, the hashtable contains two keys: an Integer with a value
 * of 0 and an Integer with a value of 1. The value of these to keys' are
 * the Strings "VON NEUMANN" and "MOORE" respectively. What the user will see
 * then is a combo box labeled as "NType" that contains two Strings "VON
 * NEUMANN" and "MOORE". When the user selects one of these strings, the
 * corresponding Integer is passed as an argument to the setNType method.
 * Descriptors is an instance variable (a hashtable) of the SimModelImpl class
 * and can be used by sub classes.<p/>
 * 
 * This class's widget is a PairComboBox or a PropertyComboBox.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class ListPropertyDescriptor extends PropertyDescriptor {

  private Vector values = new Vector();
  private boolean isPaired = false;
  private ComboPairComparator comparator = new ComboPairComparator();

  /**
   * Creates a ListPropertyDescriptor for the specified property and
   * the specified Vector of values.
   *
   * @param name the name of the property
   * @param values the objects to be displayed in the combo box and
   * passed to the appropriate set method.
   */

  public ListPropertyDescriptor(String name, Vector values) {
    super(name);
    this.values = values;
    
    super.widget = this.createWidget();
  }

  /**
   * Creates a ListPropertyDescriptor for the specified property and
   * the specified array of values.
   *
   * @param name the name of the property
   * @param values the objects to be displayed in the combo box and
   * passed to the appropriate set method.
   */
  public ListPropertyDescriptor(String name, Object[] values) {
    super(name);
    for (int i = 0; i < values.length; i++) {
      this.values.add(values[i]);
    }
    
    super.widget = this.createWidget();
  }

  /**
   * Creates a ListPropertyDescriptor for the specified property and
   * the specified Hashtable where the objects to be sent to the set
   * method are the keys, and their String representations are the
   * values.
   *
   * @param name the name of the property
   * @param hash the objects to be sent to the appropriate set method -
   * the hashtable's keys, and their string representations - the hashtable's
   * values.
   */
  public ListPropertyDescriptor(String name, Hashtable hash) {
    super(name);
    isPaired = true;

    Enumeration e = hash.keys();
    while (e.hasMoreElements()) {
      Object key = e.nextElement();
      ComboPair p = new ComboPair(key, (String)hash.get(key));
      //ComboPair p = new ComboPair((Integer)key, (String)hash.get(key));
      values.add(p);
    }

    Collections.sort(values, comparator);
    
    super.widget = this.createWidget();
  }

  /**
   * Gets the objects to be put in the combo box.
   */
  public Vector getValues() {
    return values;
  }
  
  /**
   * Gets the gui widget associated with this ListPropertyDescriptor. In
   * this case a combobox.
   */
  private PropertyWidget createWidget() {
    if (isPaired) {
      return new PairComboBox(values);
    }

    return new PropertyComboBox(values);
  }
}

class ComboPairComparator implements Comparator {

  public int compare(Object o1, Object o2) {
    String s1 = o1.toString();
    String s2 = o2.toString();

    return s1.compareTo(s2);
  }

  public boolean equals(Object obj) {
    return super.equals(obj);
  }
}


