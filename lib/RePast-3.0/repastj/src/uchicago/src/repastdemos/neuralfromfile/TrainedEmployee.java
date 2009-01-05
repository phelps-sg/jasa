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

import org.joone.io.MemoryInputSynapse;

import uchicago.src.repastdemos.neural.Employee;
import uchicago.src.sim.adaptation.neural.NeuralException;
import uchicago.src.sim.adaptation.neural.NeuralUtils;
import uchicago.src.sim.util.RepastException;

/**
 * An employee that performs actions based on a neural network stored in a file.
 *
 * @author Jerry Vos
 * @version $Revision$ $Date$
 */
public class TrainedEmployee extends Employee {

	public TrainedEmployee(double x, double y) throws RepastException {
		super(x, y);
	}

	public TrainedEmployee() throws RepastException {
		super();
	}

	/**
	 * This constructor creates an employee who's decisions are made by the
	 * neural network stored in a file.
	 * 
	 * @param x					the agent's x coordinate
	 * @param y					the agent's y coordinate
	 * @param neuralNetFileName	the name of the file to load the neural network
	 * 							from
	 * @throws RepastException	when there is an error constructing the network
	 */
	public TrainedEmployee(double x, double y, String neuralNetFileName) throws RepastException {
		super(x, y);
		
		loadBrainFromFile(neuralNetFileName);
	}

	/**
	 * loads a neural network as this employee's <i>brain</i> from a specified
	 * file
	 * 
	 * @param neuralNetFileName	the name of the file to load the neural net from
	 * @throws NeuralException when there is an error loading the network
	 */
	public void loadBrainFromFile(String neuralNetFileName) throws NeuralException {
		super.net = NeuralUtils.loadNetFromFile(neuralNetFileName);

		// You must reset the desired network output if you want the network
		// training to work correctly.
		
	    // this will hold the data we wish the network produced
		this.desiredNetworkOutput = new MemoryInputSynapse();
	    // set the synapse to have one columns of output corresponding
	    // to the number of rows in the output layer
		this.desiredNetworkOutput.setAdvancedColumnSelector("1");
		
		// set the stream that the teacher will look to for computing 
		// errors and teaching the network
		this.net.getNet().getTeacher().setDesired(desiredNetworkOutput);
	}
	
	/**
	 * saves this agent's neural network to a file
	 * 
	 * @param neuralNetFileName	the name of the file to save the neural net to
	 * @throws NeuralException when there is an error loading the network
	 */
	public void saveBrainToFile(String neuralNetFileName) throws NeuralException {
		super.net.saveNetToFile(neuralNetFileName);	
	}
	
	public String toString() {
		return getNodeLabel();
	}
}
