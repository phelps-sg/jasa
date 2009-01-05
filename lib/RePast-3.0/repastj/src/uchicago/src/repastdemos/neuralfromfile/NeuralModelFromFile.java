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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import uchicago.src.repastdemos.neural.Employee;
import uchicago.src.repastdemos.neural.NeuralModel;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.RepastException;
import uchicago.src.sim.util.SimUtilities;


/**
 * This demo model is the same as the {@link NeuralModel} except that
 * it allows for the neural network the {@link Employee}s use to be loaded 
 * from a file.
 * 
 * @author Jerry Vos
 * @version $Revision$ $Date$
 */
public class NeuralModelFromFile extends NeuralModel {
	/**
	 * the file to load the employees' neural network from
	 */
	private String neuralNetFileName = "EmployeeNet.ser";
	
	public NeuralModelFromFile() {
		super();
	}

	public String[] getInitParam() {
		// copy the super's parameters and add "neuralNetFileName"
		String[] initParams		= super.getInitParam();
		String[] finalParams	= new String[initParams.length + 1];
		
		for (int i = 0; i < initParams.length; i++)
			finalParams[i] = initParams[i];
		
		finalParams[finalParams.length - 1] = "neuralNetFileName";
		
		return finalParams;
	}

	public void begin() {
		try {
			super.begin();
			
		    buildModel();
		    buildSchedule();
			
			/*
			 * This is the difference between this OfficeSpaceModel and the 
			 * other OfficeSpaceModel.  This model loads the network from a 
			 * file.
			 */
			int numHired = 0;
			try {
				// attempt to hire employees using the serialized network
				
				for (; numHired < super.getEmployeeCount(); numHired++) {
					TrainedEmployee emp = 
						new TrainedEmployee(
								Random.uniform.nextDoubleFromTo(0, office.getWidth()),
								Random.uniform.nextDoubleFromTo(0, office.getHeight()),
								neuralNetFileName);
					office.hireEmployee(emp);
				}
			} catch (Exception ex) {
				// there was an error loading the serialized network, so now
				// load default employees
				SimUtilities.showError("Error loading a neural network from \"" + neuralNetFileName + "\".\n" +
						"Will attempt to hire untrained employees.", ex);
				
				for (; numHired < super.getEmployeeCount(); numHired++) {
					TrainedEmployee emp = new TrainedEmployee();
					office.hireEmployee(emp);
				}
			}
			office.hireBosses();
			office.hireConsultant();

			// add them to the list of 
			super.agentList.addAll(office.getEmployees());
			super.agentList.addAll(office.getBosses());
			super.agentList.add(office.getConsultant());

			// create all the displays
			this.buildDisplay();
			this.buildGraphs();

			// Schedule some pictures of the displays if you'd like
//			new SnapshotScheduler("display", officeDisplaySurface, "display").scheduleAtInterval(schedule, 200);
//			new SnapshotScheduler("individual", individualGraph, "individual").scheduleAtInterval(schedule, 400);
//			new SnapshotScheduler("office", officeErrorGraph, "office").scheduleAtInterval(schedule, 400);
		} catch (RepastException ex) {
			SimUtilities.showError("Error readying the model", ex);
			super.stop();
		}
	}
	
	public void setup() {
		super.setup();
		
		getModelManipulator().addButton("Store a selected agent's brain (neural network)", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final AgentSelectorDialog dialog = new AgentSelectorDialog();
				dialog.show(office.getEmployees(), new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (ae.getActionCommand() == AgentSelectorDialog.CANCELED)
							return;
						
						TrainedEmployee toSave = (TrainedEmployee) dialog.getSelectedItem();
						
						saveEmployeesNet(toSave);
					}
				});
			}
		});
	}
	
	/**
	 * this saves an employees neural network to a file so it can be reloaded later
	 * 
	 * @param toSave	the employee whose network to save
	 */
	private void saveEmployeesNet(TrainedEmployee toSave) {
		String fileName = toSave.getNodeLabel() + System.currentTimeMillis() + ".ser";
		try {
			toSave.saveBrainToFile(fileName);
			SimUtilities.showMessage("Successfully saved " + toSave.getNodeLabel() + "'s neural net to \"" + fileName + "\"");
		} catch (Exception ex) {
			SimUtilities.showError("Error saving " + toSave.getNodeLabel() + "'s neural net to file", ex);
		}
	}
	
	
	public String getNeuralNetFileName() { 
		return neuralNetFileName;
	}
	
	public void setNeuralNetFileName(String neuralNetFileName) {
		this.neuralNetFileName = neuralNetFileName;
	}
	
	
	public static void main(String[] args) {
		uchicago.src.sim.engine.SimInit init = new uchicago.src.sim.engine.SimInit();
		NeuralModelFromFile model = new NeuralModelFromFile();
		if (args.length > 0)
			init.loadModel(model, args[0], false);
		else
			init.loadModel(model, null, false);
	}
}