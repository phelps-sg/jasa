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
package uchicago.src.sim.adaptation.neural;

import org.joone.engine.DirectSynapse;
import org.joone.engine.InputPatternListener;
import org.joone.engine.Monitor;
import org.joone.engine.NeuralNetEvent;
import org.joone.engine.NeuralNetListener;
import org.joone.engine.Pattern;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.net.NeuralNet;

/**
 * A wrapper around a {@link org.joone.net.NeuralNet}.  This class adds
 * training and retrieval methods that wait until the training/retrieval is
 * finished before returning.  
 * 
 * @author Jerry Vos
 * @version $Revision$ $Date$
 */
public class RepastNeuralWrapper implements NeuralNetListener {
	/**
	 * The net being wrapped
	 */
	protected NeuralNet net;

	/**
	 * The number of times to apply the patterns to the network during the
	 * training
	 */
	protected int epochsPerIteration = 1;

	/** 
	 * Whether or not the network is running (training/retrieving).  This
	 * doesn't mean that the network's threads have all shut down, just that
	 * the network has signaled its start or stop.
	 */
	protected transient boolean netStopped = true;

	/** the monitor watching if the network is running or not **/
	private Object networkRunningMonitor = new Object();
	
	
	/**
	 * The default constructor for a network wrapper.  This creates a basic network,
	 * however this cannot be used until {@link org.joone.engine.Layer}s, 
	 * {@link org.joone.engine.Synapse}s, and so forth have been specified.<br/>
	 * Same as <c>new RepastNeuralWrapper(new NeuralNet());</c>
	 */
	public RepastNeuralWrapper() {
		this(new NeuralNet());
	}
	
	/**
	 * This creates a basic network wrapper, however this cannot be used until 
	 * {@link org.joone.engine.Layer}s, {@link org.joone.engine.Synapse}s, 
	 * and so forth have been specified.<br/>
	 */
	public RepastNeuralWrapper(NeuralNet net) {
		super();
		
		this.net = net;
		net.removeAllListeners();
		net.addNeuralNetListener(this);
	}

	/**
	 * This method will retrieve a result Pattern from a network based on the
	 * in parameter.<br/>
	 * This method will remove all outputs and inputs from the network, and then
	 * it will add a DirectSynapse as an output, and the in parameter
	 * as an input. THESE ARE NOT RESTORED BY THE CALL.<br/>
	 * 
	 * @param in	The input to the network (best as a {@link DirectSynapse})
	 * @return the Pattern resulting from running the network
	 * 
	 * @throws NeuralException when there is an error querying the network
	 */
	public synchronized Pattern retrieve(InputPatternListener in) throws NeuralException {
		//	    logger.debug("Retrieving from the network");
		// the call to removeAllOutputs will clear the teacher
		TeachingSynapse teacher = net.getTeacher();
		
		DirectSynapse out = new DirectSynapse();
		
		net.removeAllInputs();
		net.removeAllOutputs();
		net.addInputSynapse(in);
		net.addOutputSynapse(out);


		// setup the monitor for the retrieval
		Monitor monitor = net.getMonitor();

		monitor.setTrainingPatterns(1);
		monitor.setTotCicles(1);
		monitor.setLearning(false);

		net.start();
		// have to start the monitor if this isn't a direct synapse 
		if (!(in instanceof DirectSynapse))
			net.getMonitor().Go();
		// grab the pattern
		Pattern retPattern = out.fwdGet();
		
		net.stop(true);
		if (!(in instanceof DirectSynapse))
			net.getMonitor().Stop();
		
        try {
        	// wait around until the network signals its stop
            synchronized(networkRunningMonitor) {
	        	while (!netStopped) {
	        	    networkRunningMonitor.wait();
	        	}
            }
		} catch (InterruptedException ex) {
			throw new NeuralException("Error waiting for net to stop", ex);
		}
		
		waitTilNetTrulyStops();

		// restore the teacher
		net.setTeacher(teacher);
		
		return retPattern;
	}

	/**
	 * Trains a network.  The network is expected to be fully built before this
	 * method is called. <br/>
	 * This method will remove all outputs and inputs from the network, and then
	 * it will add the network's teacher as an output, and the in parameter
	 * as an input. THESE ARE NOT RESTORED BY THE CALL.<br/>
	 * 
	 * To use this method the network's teacher should have its desired output
	 * specified through a getTeacher().setDesired(..) call.  Also the in
	 * should be setup to deliver at least one pattern to the network (this
	 * training only feeds one pattern through).<br/>
	 * 
	 * Make sure before calling this method you specify the learning rate and
	 * momentum of the network's {@link Monitor}.
	 * 
	 * @param in	the input for the network.
	 * @throws NeuralException when there is an error training the network
	 * 
	 * @see NeuralNet#check()
	 * @see #retrieve(InputPatternListener)
	 */
	public synchronized void train(InputPatternListener in) throws NeuralException {
		// the .removeAllOutputs call will set the teacher to null
		TeachingSynapse teacher = net.getTeacher();
		
		net.removeAllInputs();
		net.removeAllOutputs();
		
		net.setTeacher(teacher);
		net.addInputSynapse(in);
		net.addOutputSynapse(teacher);
		
		net.getMonitor().setLearning(true);
		
		net.start();
		net.getMonitor().Go();

		try {
        	// wait around until the network signals its stop
			synchronized (networkRunningMonitor) {
				while (!netStopped) {
					networkRunningMonitor.wait();
				}
			}
		} catch (InterruptedException ex) {
			throw new NeuralException("Error waiting for net to stop", ex);
		}
		
		waitTilNetTrulyStops();
	}
	
	/**
	 * This waits around until the network really has stopped.  If it isn't 
	 * shutting down, this forcefully stops the network
	 *  
	 * @throws NeuralException when there is an error stopping the network 
	 * 							(only happens 
	 */
	private synchronized void waitTilNetTrulyStops() throws NeuralException {
		for (int i = 0; net.isRunning() && i < 100; i++){
			try {
				Thread.sleep(3);
				if (i == 50) {
//					System.out.println("WARNING****** Forcefully stopping net");
					net.stop();
				}
			}catch (Exception ex) { }
		}
		if (net.isRunning())
			throw new NeuralException("Couldn't stop network");
	}
	
	/**
	 * @return the number of times to feed a pattern through the network
	 */
	public synchronized int getEpochsPerIteration() {
		return epochsPerIteration;
	}

	/**
	 * @param epochsPerIteration the number of times to feed a pattern 
	 * 								through the network
	 */
	public synchronized void setEpochsPerIteration(int epochsPerIteration) {
		//	    logger.debug("Setting epochs to: " + epochsPerIteration);
		this.epochsPerIteration = epochsPerIteration;
	}

	public void netStarted(NeuralNetEvent e) {
		netStopped = false;
	}

	public void netStopped(NeuralNetEvent e) {
		//	    logger.debug("Net stopped");
		//		System.out.println("\t*Entering stop");
		if (!netStopped) {
			netStopped = true;
			synchronized (networkRunningMonitor) {
				networkRunningMonitor.notify();
			}
		}
		//		System.out.println("\t*Exiting stop");
	}

	public void netStoppedError(NeuralNetEvent e, String error) {
		//	    logger.error("Net stopped on error");
		System.out.println("STOPPED ON ERROR");
		if (!netStopped) {
			netStopped = true;
			synchronized (networkRunningMonitor) {
				networkRunningMonitor.notify();
			}
		}
	}

	public void cicleTerminated(NeuralNetEvent e) {
	}

	public void errorChanged(NeuralNetEvent e) {
	}
	
	/**
	 * @return the net this wrapper is wrapping
	 */
	public synchronized NeuralNet getNet() { return net; }
	/**
	 * @param net the new net to wrap
	 */
	public synchronized void setNet(NeuralNet net) { this.net = net; }
	
	/**
	 * Saves the neural network this is wrapping to the specified file.  This
	 * can be loaded through {@link #loadNetFromFile(String)}.
	 * 
	 * @param fileName	the name of the file to save the network to
	 * 
	 * @throws NeuralException when there is an error saving the network
	 * @see #loadNetFromFile(String)
	 * @see NeuralUtils#saveNetToFile(NeuralNet, String)
	 */
	public void saveNetToFile(String fileName) throws NeuralException {
		NeuralUtils.saveNetToFile(this.net, fileName);
	}
}