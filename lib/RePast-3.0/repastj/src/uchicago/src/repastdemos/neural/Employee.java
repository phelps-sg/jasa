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
package uchicago.src.repastdemos.neural;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;

import org.joone.engine.DirectSynapse;
import org.joone.engine.FullSynapse;
import org.joone.engine.LinearLayer;
import org.joone.engine.Monitor;
import org.joone.engine.Pattern;
import org.joone.engine.SigmoidLayer;
import org.joone.io.MemoryInputSynapse;

import uchicago.src.sim.adaptation.neural.NeuralException;
import uchicago.src.sim.adaptation.neural.NeuralUtils;
import uchicago.src.sim.adaptation.neural.RepastNeuralWrapper;
import uchicago.src.sim.engine.AutoStepable;
import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.network.DefaultDrawableNode;
import uchicago.src.sim.util.RepastException;
import uchicago.src.sim.util.SimUtilities;

/**
 * This class represents the agents who are to perform an action for the 
 * Boss.  The agent learns to perform the correct action by training
 * a neural network based on the commands received, and whether they were
 * scolded for the action they did perform, or praised.
 * 
 * @author Jerry Vos
 * @version $Revision$ $Date$
 */
public class Employee extends DefaultDrawableNode implements AutoStepable {
	public static final int DO_NOTHING = 0;
	
	public static final int DO_SOMETHING = 1;

	
	public static final int BOSS_A = 0;

	public static final int BOSS_B = 1;
	
	private static Image employeePicture;
	

	private static final Color[] colors = new Color[] {
			Color.PINK,
			Color.BLUE,
			Color.GRAY,
			Color.GREEN,
			Color.MAGENTA,
			Color.YELLOW,
			Color.WHITE,
			Color.CYAN
	};
	private static int colorIndex = 0;
	
	private static int baseIdNumber;
	
	
	/**
	 * This is the neural network used by this employee
	 */
	protected RepastNeuralWrapper net;

	/**
	 * This is used to store the output that we want to train the network 
	 * towards
	 */
	protected MemoryInputSynapse desiredNetworkOutput;
	/**
	 * This is used to store the input that results in the desired output
	 */
	protected MemoryInputSynapse inputForTraining;
	
	/**
	 * This is the input synapse used to grab information from the network.
	 * DirectSynapses work the best for this type of usage, that is, when a
	 * single input pattern will be applied to the network.
	 */
	protected DirectSynapse inputForRetrieval;
	
	/**
	 * The action the network said to perform
	 */
	private int actionPerformedInStep = DO_NOTHING;

	private double retrievedValue = 0.0;

	private boolean wasScolded = false;

	private double[] bossCommands = 
		new double[] { DO_NOTHING, DO_NOTHING };

	private double[] prevStepCommands =
		new double[] { DO_NOTHING, DO_NOTHING };
	
	private double angst = 1;

	private double error = 0.0;

	
	public Employee(double x, double y) throws RepastException {
		super(new OvalNetworkItem(x, y));

		loadEmployeePicture();
		
		buildNeuralNetwork();
		
		this.setColor(getNextColor());
		this.setHeight(5);
		this.setWidth(2);
		this.setNodeLabel("Milton " + ++baseIdNumber);
	}

	public Employee() throws RepastException {
		this(0, 0);
	}
	
	private static void loadEmployeePicture() {
		if (employeePicture == null) {
			java.net.URL employeePicURL = Employee.class.getResource("person.gif");
			employeePicture = new ImageIcon(employeePicURL).getImage(); 
		}
	}
	
	private static Color getNextColor() {
		if (colorIndex == colors.length)
			colorIndex = 0;
		
		return colors[colorIndex++];
	}

	
	public static void resetIndices() {
		baseIdNumber = 0;
	}

	private void buildNeuralNetwork() throws RepastException {
		/** this is how to build the network by hand **/
//		// First create the three layers
//		LinearLayer input = new LinearLayer();
//		SigmoidLayer hidden = new SigmoidLayer();
//		SigmoidLayer output = new SigmoidLayer();
//
//		// set the dimensions of the layers
//		input.setRows(2);
//		hidden.setRows(3);
//		output.setRows(1);
//
//		// Now create the two Synapses
//		FullSynapse synapse_IH = new FullSynapse(); /* input -> hidden conn. */
//		FullSynapse synapse_HO = new FullSynapse(); /* hidden -> output conn. */
//
//		// Connect the input layer whit the hidden layer
//		input.addOutputSynapse(synapse_IH);
//		hidden.addInputSynapse(synapse_IH);
//
//		// Connect the hidden layer whit the output layer
//		hidden.addOutputSynapse(synapse_HO);
//		output.addInputSynapse(synapse_HO);
//
//		// the input to the neural net
//		inputForTraining = new MemoryInputSynapse();
//
//		// The Trainer and its desired output
//		desiredNetworkOutput = new MemoryInputSynapse();
//
//		TeachingSynapse trainer = new TeachingSynapse();
//		trainer.setDesired(desiredNetworkOutput);
//
//		net = new RepastNeuralNet();
//
//		net.addLayer(input, NeuralNet.INPUT_LAYER);
//		net.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
//		net.addLayer(output, NeuralNet.OUTPUT_LAYER);
//		net.setTeacher(trainer);
		
		/** And this is how it is built through Repast **/
		this.net = NeuralUtils.buildNetwork(new int[]{2, 3, 1}, 
	    								new Class[] { 
	    										LinearLayer.class,
	    										SigmoidLayer.class,
	    										SigmoidLayer.class
	    									},
										new Class[] {
	    										FullSynapse.class,
												FullSynapse.class});
	    
	    // now we specify the learning parameters
		this.net.getNet().getMonitor().setLearningRate(.8);
		this.net.getNet().getMonitor().setMomentum(0.3);

	    
		this.inputForTraining = new MemoryInputSynapse();
	    // set the synapse to have two columns of input corresponding
	    // to the number of rows in the input layer
		this.inputForTraining.setAdvancedColumnSelector("1,2");

	    // this will hold the data we wish the network produced
		this.desiredNetworkOutput = new MemoryInputSynapse();
	    // set the synapse to have one columns of output corresponding
	    // to the number of rows in the output layer
		this.desiredNetworkOutput.setAdvancedColumnSelector("1");

	    
	    /** set up the rest of the network info **/
		this.inputForRetrieval = new DirectSynapse();
		this.inputForRetrieval.setName("RetrievingInput MemoryInputSynapse");
		
		// set the stream that the teacher will look to for computing 
		// errors and teaching the network
		this.net.getNet().getTeacher().setDesired(desiredNetworkOutput);
	}

	/**
	 * This trains the network based on the previous steps actions and what
	 * the network should've done the previous step
	 * 
	 * @throws NeuralException when there is an error training the network
	 */
	private synchronized void train() throws NeuralException {
		int actionShouldveBeen;

		// figure out what action the network should've performed, so we can
		// train it to perform that correct action.  Note, while the network
		// is supposed to be learning XOR, we don't have to put that logic into
		// this agent.  The agent just knows whether or not it was scolded in
		// the last step.  If the agent wasn't scolded then it will train the
		// network to produce the same behavior again; if it was scolded it will
		// train the network to perform a different action.
		if (wasScolded) {
			if (actionPerformedInStep == DO_NOTHING)
				actionShouldveBeen = DO_SOMETHING;
			else
				actionShouldveBeen = DO_NOTHING;
//			System.out.println("was scolded did:" + this.actionPerformedInStep + ", shoulda:"
//					+ actionShouldveBeen);
		} else {
			actionShouldveBeen = this.actionPerformedInStep;
		}
		this.net.getNet().getMonitor().setLearningRate(.8);
		this.net.getNet().getMonitor().setMomentum(0.3);

		// compute the network's error
		this.error = Math.abs(actionShouldveBeen - retrievedValue);
//		System.out.println("Error: " + error);

		// get the object that watches over the training
		Monitor monitor = net.getNet().getMonitor();

		// set the monitor parameters
		monitor.setTrainingPatterns(1);
		monitor.setTotCicles(1);
		
		// setup the inputs for the next round of training
		this.desiredNetworkOutput
				.setInputArray(new double[][] { { actionShouldveBeen } });
		synchronized(bossCommands) {
			this.inputForTraining
				.setInputArray(
					new double[][] { (double[]) this.prevStepCommands.clone() });
		}

		// now actually train the network
		try {
			this.net.train(inputForTraining);
		} catch (NeuralException ex) {
			SimUtilities.showError("Error training neural network for agent \""
					+ getNodeLabel() + "\"", ex);
			throw ex;
		}
	}
	
	/**
	 * This method retrieves from the network the best action to perform based
	 * on the boss commands.
	 * 
	 * @return the value the network returns
	 * 
	 * @throws NeuralException when there is an error querying the network
	 */
	private synchronized double retrieve() throws NeuralException {
		// setup the network input for the current state
		synchronized(bossCommands) {
			inputForRetrieval.fwdPut(new Pattern((double[]) (bossCommands.clone())));
		}
		// query the network
		Pattern retrievedPattern = net.retrieve(inputForRetrieval);
		
		return retrievedPattern.getValues()[0];
	}
	
	public void preStep() throws NeuralException {
		// train the network based on the previous step
		train();

		// reset for this step
		this.wasScolded = false;
	}

	public void step() throws NeuralException {
		try {
//		    System.out.println("bossCommand: " + bossCommands[0] + "," + bossCommands[1]);
			
			synchronized(bossCommands) {
				prevStepCommands = (double[]) bossCommands.clone();
			}
			
			this.retrievedValue = retrieve();

//			System.out.println("retrievedValue: " + retrievedValue);

			// act based on what the network said to do
			if (Math.round(retrievedValue) == DO_NOTHING) {
				doNothing();
			} else {
				doSomething();
			}

		} catch (NeuralException ex) {
			SimUtilities.showError(
					"Error computing the next action to perform. \n"
							+ "Agent \"" + getNodeLabel() + "\".", ex);
			throw ex;
		}
	}

	public void postStep() {
	}

	public void draw(SimGraphics g) {
		// draw the employee's picture
		g.drawImage(employeePicture);
		
		// grab the width of the picture
		int width = employeePicture.getWidth(null);
		
		g.setFont(super.getFont());
		
		// get the size of the node's text
		Rectangle2D bounds = g.getStringBounds(this.getNodeLabel());
		
		// set the graphics to draw the text above the label
		// the x coordinate is relative to the upper left corner of the image
		// so the coordinates are shifted to account for that
		g.setDrawingCoordinates((float) (this.getX() + width / 2.0 - bounds.getWidth() / 2.0),
								(float) (this.getY() - bounds.getHeight() - 2),
								0f);
		
		// draw the label
		g.drawString(getNodeLabel(), Color.BLACK);
	}
	
	public void scold(Boss boss) {
		this.angst += .1;
		this.wasScolded = true;
	}

	public void praise(Boss boss) {
		this.angst -= .05;
	}

	public void receiveCommand(int bossID, int command) {
		synchronized(bossCommands) {
			bossCommands[bossID] = command;
		}
	}

	protected void doNothing() {
		actionPerformedInStep = DO_NOTHING;
	}

	protected void doSomething() {
		actionPerformedInStep = DO_SOMETHING;
	}

	public double getActionPerformed() {
		return actionPerformedInStep;
	}

	public double[] getCommands() {
		synchronized(bossCommands) {
			return bossCommands;
		}
	}

	/**
	 * @return returns the neural network's error
	 */
	public double getError() {
		return error;
	}
}