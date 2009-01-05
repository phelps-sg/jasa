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

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ViolinStrings.Strings;

public class FileWidget extends JPanel implements PropertyWidget,
  FocusListener
{

  private String propertyName = null;
  private JTextField txtField = new JTextField(10);
  private JButton btnBrowse = new JButton("Browse");
  private ArrayList listeners = new ArrayList();
  

  public FileWidget() {
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    add(txtField);
    add(Box.createRigidArea(new Dimension(5, 0)));
    add(btnBrowse);

    btnBrowse.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent evt) {
	  showDialog();
	}
      });
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String name) {
    propertyName = name;
  }

  public void setValue(Object val) {
    if (val == null) txtField.setText("Null");
    txtField.setText((String)val);
  }
  
  public Object getValue() {
    return txtField.getText();
  }

  public void focusLost(FocusEvent evt) {
    // tie the focus lost to actionEvents.
    ArrayList copy = null;
    synchronized(listeners) {
      copy = (ArrayList)listeners.clone();
    }

    ActionEvent e = new ActionEvent(this, evt.getID(), "");
    for (int i = 0; i < copy.size(); i++) {
      ((ActionListener)copy.get(i)).actionPerformed(e);
    }
  }

  private void showDialog() {
    FileDialog dialog = new FileDialog(new JFrame(), "Load network",
				       FileDialog.LOAD);
    
    String path = txtField.getText().trim();
    if (path.length() > 0) {
      path = Strings.change(path, "/", File.separator);
      path = Strings.change(path, "\\", File.separator);
      int index = path.lastIndexOf(File.separator);
      String curFile = path.substring(index + 1, path.length());
      String curDir = path.substring(0, index);
      File file = new File(path);
      if (file.exists()) {
	dialog.setDirectory(curDir);
	dialog.setFile(curFile);
      } else {
	file = new File(curDir);
	if (file.exists()) {
	  dialog.setDirectory(curDir);
	}
      }
    }

    dialog.setVisible(true);
   
    if (dialog.getFile() != null) {
      path = dialog.getDirectory() + dialog.getFile();
      txtField.setText(path);
    }
  }

  public void focusGained(FocusEvent evt) {}
  
  public void addActionListener(ActionListener l) {
    txtField.addActionListener(l);
    txtField.addFocusListener(this);
    btnBrowse.addFocusListener(this);
    listeners.add(l);
  }
  
  public void setEnabled(boolean b) {
    txtField.setEnabled(b);
    btnBrowse.setEnabled(b);
  }
}
