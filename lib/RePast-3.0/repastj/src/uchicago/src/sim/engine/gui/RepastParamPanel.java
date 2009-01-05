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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;

import uchicago.src.sim.engine.ParameterFileListener;
import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.engine.SimpleModel;
import uchicago.src.sim.engine.gui.components.ParameterData;


/**
 * The panel used to select a parameter file on the Controller.
 *
 * @author Jerry Vos
 * @version $Revision$ $Date$
 */
public class RepastParamPanel extends JPanel implements ParameterFileListener {
	private final String BROWSE_ITEM = "<Browse...>";
	private final String CREATE_ITEM = "<Create>";
	
	/**
	 * the combo box holding the recent file names and browse/create options
	 */
	private JComboBox paramFileBox;
	private JLabel fileLabel;

	/**
	 * maps the short name showed in the paramFileBox -> the full file name
	 */
	private HashMap paramFiles = new HashMap();
	
	/**
	 * the character used in the directory tree
	 */
	private String fileSeparator;
	
	/**
	 * This is set by the setParameterFile method which is called by the
	 * parameter wizard when the parameter file is chosen.  This only holds
	 * a value temporarily so it should not be used outside of the 
	 * setParameterFile or createNewFile methods.
	 */
	private String newlyCreatedParamFileName = null;
	
	/**
	 * the model to use for creating the wizard
	 */
	private SimModel model;
	
	
	public RepastParamPanel(SimModel model) {
		super();
		
		this.model = model;
		
		fileSeparator = System.getProperty("file.separator");
		if (fileSeparator.equals(""))
			fileSeparator = "/";
		
		
		setupPanel();
	}

	/**
	 * creates the GUI part of the panel
	 */
	private void setupPanel() {
		fileLabel = new JLabel("Parameter file: ");

		this.add(fileLabel);

		paramFileBox = new JComboBox();

		paramFileBox.addItem(BROWSE_ITEM);
		paramFileBox.addItem(CREATE_ITEM);

		paramFileBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectedItemChanged();
				}
			});

		this.add(paramFileBox);
	}

	/**
	 * called when the user selects something in the combo box
	 */
	private void selectedItemChanged() {
		String selectedFile = null;
		Object selectedItem = paramFileBox.getSelectedItem();

		if (selectedItem == BROWSE_ITEM) {
			selectedFile = browseForFile();
			if (selectedFile != null)
				addToComboBox(selectedFile);
		} else if (selectedItem == CREATE_ITEM) {
			selectedFile = createNewFile();
			if (selectedFile != null)
				addToComboBox(selectedFile);
		} else {
			selectedFile = (String) paramFileBox.getSelectedItem();
		}
		
		// TODO FINISH THIS METHOD
	}

	private String browseForFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileFilter() {
				public boolean accept(File pathname) {
					String name = pathname.getName();

					return pathname.isDirectory() || name.endsWith(".pf")
						   || name.endsWith(".xml")
						   || name.endsWith(".txt");
				}

				public String getDescription() {
					return "Parameter files (*.pf, *.xml, *.txt)";
				}
			});

		chooser.setDialogType(JFileChooser.OPEN_DIALOG);

		int retVal = chooser.showOpenDialog(this);

		if (retVal == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();

			return f.getAbsolutePath();
		}

		return null;
	}
	
	/**
	 * Create a new file with the parameter wizard
	 * 
	 * @return the name of the file created
	 */
	private String createNewFile() {
		ParameterWizard paramWizard	= new ParameterWizard(this);
		ParameterData paramData		= new ParameterData(this.model);
		
		paramData.reloadInputOutput();
    	paramWizard.setAParameterData(paramData);
    	
		// shows the wizard modally
		paramWizard.display();
		
		// set by the parameter wizard
		String fileName = this.newlyCreatedParamFileName;
		
		// null this so a confusing value isn't sitting around
		newlyCreatedParamFileName = null;
		
		return fileName;
	}
	
	/**
	 * adds a file uniquely to the file listing combo box
	 * 
	 * @param fileName the full name (path and name) of the file
	 */
	private void addToComboBox(String fileName) {
		int shortNameStart = fileName.lastIndexOf(fileSeparator);
		shortNameStart++;
		
		String shortName = fileName.substring(shortNameStart);
		
		// get an unused name
		int i = 1;
		String tempName = shortName;
		while (paramFiles.containsKey(tempName)) {
			tempName = shortName + "-" + i;
		}
		shortName = tempName;
		
		// now store the parameter file name
		paramFiles.put(shortName, fileName);
		paramFileBox.addItem(shortName);
		
		paramFileBox.setSelectedItem(shortName);
	}
	
	/**
	 * used to get the name of the file created by the parameter wizard
	 */
	public void setParameterFile(String paramFileName) {
		this.newlyCreatedParamFileName = paramFileName;
	}
	
	/**
	 * @return the name of the parameter file selected by the user or
	 * 			null if none is selected
	 */
	public String getParameterFileName() {
		String selectedName = (String) paramFileBox.getSelectedItem();
		
		if (selectedName == BROWSE_ITEM || selectedName == CREATE_ITEM) {
			// call browse or create
			selectedItemChanged();
			
			selectedName = (String) paramFileBox.getSelectedItem();
		}
		
		if (!paramFiles.containsKey(selectedName)) {
			return null;
		}
		
		return (String) paramFiles.get(selectedName);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();

		RepastParamPanel panel = new RepastParamPanel(new SimpleModel());

		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		frame.getContentPane().add(panel);

		frame.show();
		frame.pack();

		frame.addWindowListener(new WindowAdapter() {
				public void windowClosed(WindowEvent e) {
					System.exit(0);
				}
			});
	}
}
