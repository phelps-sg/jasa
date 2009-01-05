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
package anl.repast.gis.display;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import anl.repast.gis.GisAgent;

/*
 *  @author Robert Najlis
 *  Created on Sep 24, 2004
 *
 *
 */
public class PropertyWindow  extends JFrame implements ActionListener {

    Container container;
    GisAgent gisAgent;
    String [] props;
    
    public PropertyWindow() {
        
    }
    public PropertyWindow(GisAgent agent) {
        super("GisAgent: ");// + agent.getGisAgentIndex());
        this.gisAgent = agent;
        
        
        props = gisAgent.gisPropertyList();
        container = this.getContentPane();
        //set layout
        //BorderLayout borderLayout = new BorderLayout();
        if (props != null && props.length > 0) {
            container.setLayout(new GridLayout((gisAgent.gisPropertyList().length/2),2));//BoxLayout(container,BoxLayout.Y_AXIS));
            this.setSize(300,gisAgent.gisPropertyList().length*60);
        }
        else { // property list is null
            container.setLayout(new GridLayout((3),2));//BoxLayout(container,BoxLayout.Y_AXIS));
            this.setSize(300,3*60);
        }
        addAgentInfo();
        //show();
    }
    
    public void addAgentInfo() {
    
        
        Class clazz = gisAgent.getClass();
        //Object o = new Object();
        
        JPanel generalPanel = new JPanel();
        try {
            
            if (props != null && props.length > 0) {
                // panel to hold this all
                
                generalPanel.setLayout(new BoxLayout(generalPanel,BoxLayout.Y_AXIS));
                
                // first check that the user has not added gisAgentIndex to props list (is so don't display twice
                boolean indexCalled = false;
                for (int i=0;i<props.length;i+=2) {
                    if (props[i+1].equalsIgnoreCase("getGisAgentIndex")) {
                        indexCalled = true;
                    }
                }
                
                if (!indexCalled) {
                    // Agent ID: JTextArea
                    JPanel idPanel = new JPanel();
                    JLabel idLabel = new JLabel("Agent ID:");
                    idPanel.add(idLabel);
                    Method indexMethod = clazz.getMethod("getGisAgentIndex", null);
                    JTextField idField = new JTextField("" + indexMethod.invoke(gisAgent, null));
                    idPanel.add(idField);
                    generalPanel.add(idPanel);
                }
                
                for (int i=0;i<props.length;i+=2) {
                    //    Method method;
                    
                    
                    
                    JPanel propsPanel = new JPanel();
                    JLabel propsLabel = new JLabel("" + props[i] + ":");
                    propsPanel.add(propsLabel);
                    Method propsMethod = clazz.getMethod(props[i+1], null);
                    JTextField propsField = new JTextField("" + propsMethod.invoke(gisAgent, null));
                    propsPanel.add(propsField);
                    generalPanel.add(propsPanel);
                    
                }
                
            }
            else { // props is null or length is zero  - at least show agent id
                JPanel idPanel = new JPanel();
                JLabel idLabel = new JLabel("Agent ID:");
                idPanel.add(idLabel);
                Method indexMethod = clazz.getMethod("getGisAgentIndex", null);
                JTextField idField = new JTextField("" + indexMethod.invoke(gisAgent, null));
                idPanel.add(idField);
                generalPanel.add(idPanel);
            }
            //  }catch (Exception e) {
       //    e.printStackTrace();
      // }
       } catch (SecurityException e1) {
                 e1.printStackTrace();
       } catch (NoSuchMethodException e1) {
                 e1.printStackTrace();
       } catch (IllegalArgumentException e2) {
               e2.printStackTrace();
     } catch (IllegalAccessException e3) {
               e3.printStackTrace();
     } catch (InvocationTargetException e4) {
               e4.printStackTrace();
     }
          
            container.add(generalPanel);
    }
      public static void main(String[] args) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        
    }
}
