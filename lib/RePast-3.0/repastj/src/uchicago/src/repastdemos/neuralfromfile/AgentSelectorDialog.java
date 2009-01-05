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
package uchicago.src.repastdemos.neuralfromfile;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A frame that takes in a list of agents, displays them, and returns the
 * agent selected by the user.
 *
 * @author Jerry Vos
 * @version $Revision$ $Date$
 */
public class AgentSelectorDialog extends JDialog implements ActionListener {
	public static final String CANCELED	= "CANCELED";
	public static final String OK		= "OK";
	
	protected JButton okButton;
	protected JButton cancelButton;
	
	protected JComboBox box;
	
	protected ActionListener listener;
	
	protected Object selectedItem = null;
	
	
	/**
	 * constructor for this dialog 
	 * 
	 * @throws java.awt.HeadlessException
	 */
	public AgentSelectorDialog() throws HeadlessException {
		super();
		this.setTitle("Agent Selector");
	}
	
	/**
	 * shows the dialog asking the user to select an agent
	 * 
	 * @param agentCollection	the list of agents
	 * @param selectionListener	the method to call when the user has selected ok or cancel
	 */
	public void show(Collection agentCollection, ActionListener selectionListener) {
		this.listener = selectionListener;
		this.getContentPane().removeAll();
		
		getContentPane().add(new JLabel("Please select an agent"), BorderLayout.NORTH);
		
		box = new JComboBox();
		for (Iterator iter = agentCollection.iterator(); iter.hasNext(); ) 
			box.addItem(iter.next());
	
		getContentPane().add(box, BorderLayout.CENTER);
		
		JPanel btnPanel	= new JPanel(new BorderLayout());
		
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		
		btnPanel.add(okButton, BorderLayout.WEST);
		btnPanel.add(cancelButton, BorderLayout.EAST);
		
		getContentPane().add(btnPanel, BorderLayout.SOUTH);
		
		this.pack();
		this.show();
	}
	
	/**
	 * called when the ok or cancel button is clicked
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelButton) {
			this.listener.actionPerformed(new ActionEvent(this, 0, CANCELED));
		} else {
			this.selectedItem = box.getSelectedItem();
			
			this.listener.actionPerformed(new ActionEvent(this, 0, OK));			
		}
		
		this.dispose();
	}
	
	/**
	 * @return the agent selected by the user or null if none were selected
	 */
	public Object getSelectedItem() { return selectedItem; }
}
