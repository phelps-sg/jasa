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


import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */


public class IntrospectFrame extends JFrame { //implements ActionListener {

  //private Introspector spector = new Introspector();
  private Object spectee;
  private String[] introProps = null;
  private JTextField firstField = null;


  public IntrospectFrame(Object o) {
    this(o, "", null);
  }

  public IntrospectFrame(Object o, String title) {
    this(o, title, null);
  }

  public IntrospectFrame(Object o, String title, String[] propsToIntrospect) {
    super(title);
    spectee = o;
    introProps = propsToIntrospect;
  }

  public IntrospectPanel display() throws IllegalAccessException,
    InvocationTargetException, IntrospectionException
  {
    IntrospectPanel p = init();
    this.pack();
    this.setVisible(true);
    return p;
  }

  private IntrospectPanel init() throws IllegalAccessException,
    InvocationTargetException, IntrospectionException
  {
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    IntrospectPanel p = new IntrospectPanel(spectee, introProps, true);
    p.setBorder(BorderFactory.createEtchedBorder());
    this.getContentPane().add(p);
    return p;
  }

  public void setFocus() {
    if (firstField != null) {
      firstField.requestFocus();
    }
  }
}

