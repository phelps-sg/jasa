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
package uchicago.src.sim.engine.gui;

import uchicago.src.guiUtils.GuiUtilities;

import uchicago.src.sim.engine.ParameterFileListener;
import uchicago.src.sim.engine.gui.components.GUIControllerAbstract;
import uchicago.src.sim.engine.gui.components.ParameterData;
import uchicago.src.sim.engine.gui.components.ParameterWizardPanel;
import uchicago.src.sim.parameter.XMLParameterFileWriter;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * GUI controller.
 *
 * @author wes maciorowski
 *
 */
public class ParameterWizard extends GUIControllerAbstract {
	//view
	private ParameterWizardPanel paramWizardPanel;

	//using dialog
	private JDialog dialog;

	//data
	private ParameterData aParameterData;

	//parent dialog
	private ParameterFileListener parameterListener;

	public ParameterWizard(ParameterFileListener parameterListener) {
		paramWizardPanel = new ParameterWizardPanel(this);
		this.parameterListener = parameterListener;
	}

	/**
	 * Shows this wizard.  The wizard is shown modally so this method will not
	 * return until the user has had time to create their file.
	 */
	public void display() {
		// have to have it shown modally and don't want to have to store 
		// references to the caller (hence the new JFrame())....
		dialog = new JDialog(new JFrame(), true);
		dialog.setTitle("Parameter Wizard");

		Container c = dialog.getContentPane();

		//c.setLayout(new BorderLayout());
		c.add(paramWizardPanel); //, BorderLayout.CENTER);

		dialog.pack();
		GuiUtilities.centerComponentOnScreen(dialog);
		dialog.setVisible(true);
	}

	/**
	 * @param e
	 * @see uchicago.src.sim.engine.gui.components.GUIControllerAbstract#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (cmd.equals(GUIControllerAbstract.INPUT_OUTPUT_CHANGED)) {
			aParameterData.reloadInputOutput();
			setAParameterData(aParameterData);
		} else if (cmd.equals(GUIControllerAbstract.CHANGED_OUTPUT_LOCATION)) {
			aParameterData.setOutputLocation(paramWizardPanel.getOutputLocation());
		} else if (cmd.equals(GUIControllerAbstract.EXIT)) {
			dialog.setVisible(false);
		} else if (cmd.equals(GUIControllerAbstract.RUN_SIMULATION)) {
			parameterListener.setParameterFile(aParameterData.getOutputLocation());

			//TODO create parameter file DefaultMutableTreeNode getTreeTop
			XMLParameterFileWriter anXMLParameterFileWriter = new XMLParameterFileWriter();
			anXMLParameterFileWriter.write(aParameterData.getOutputLocation(),
										   paramWizardPanel.getTreeTop(),
										   aParameterData.getOutputParameterList());
			dialog.setVisible(false);
		}
	}

	/**
	 * @return Returns the aParameterData.
	 */
	public ParameterData getAParameterData() {
		return aParameterData;
	}

	/**
	 * @param parameterData The aParameterData to set.
	 */
	public void setAParameterData(ParameterData parameterData) {
		aParameterData = parameterData;
		paramWizardPanel.setParameterData(aParameterData);
	}

	/**
	 * @return Returns the aParameterWizardFrame.
	 */
	public ParameterWizardPanel getParamWizardPanel() {
		return paramWizardPanel;
	}

	/**
	 * @param parameterWizardFrame The aParameterWizardFrame to set.
	 */
	public void setParamWizardPanel(ParameterWizardPanel parameterWizardFrame) {
		paramWizardPanel = parameterWizardFrame;
	}

	//    /**
	//     * @param args the command line arguments
	//     */
	//    public static void main(String[] args) {
	//    	ParameterWizard aParameterWizard = new ParameterWizard();
	//    	
	//    	ParameterData aParameterData = new ParameterData();
	//    	DataParameter aParameter = new DataParameter("parm1","string",true);
	//        aParameterData.getParameterList().add(aParameter);
	//        aParameter = new DataParameter("parm2","int",true);
	//        aParameterData.getParameterList().add(aParameter);
	//        aParameter = new DataParameter("parm3","double",true);
	//        aParameterData.getParameterList().add(aParameter);
	//        aParameter = new DataParameter("parm4","double",false);
	//        aParameterData.getParameterList().add(aParameter);
	//        aParameter = new DataParameter("parm5","boolean",true);
	//        aParameterData.getParameterList().add(aParameter);
	//		
	//        aParameterData.reloadInputOutput();
	//		aParameterWizard.setAParameterData(aParameterData);
	//		aParameterWizard.display();
	//    }
	//

	/**
	 * @return Returns the dialog.
	 */
	public JDialog getDialog() {
		return dialog;
	}

	/**
	 * @param dialog The dialog to set.
	 */
	public void setDialog(JDialog dialog) {
		this.dialog = dialog;
	}
}
