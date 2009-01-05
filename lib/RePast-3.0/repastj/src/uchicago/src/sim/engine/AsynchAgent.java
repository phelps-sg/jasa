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
package uchicago.src.sim.engine;

// Import the needed support classes.
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

import uchicago.src.sim.network.DefaultDrawableNode;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;

/**
 * An asynchronously executed agent class.
 * 
 * @author Michael North
 * @version $Revision$ $Date$
 * @since 3.0
 */
public class AsynchAgent extends DefaultDrawableNode implements Serializable, IAsynchAgent {
	/**
	 * The accumulated utilized time.
	 */
	private double utilizedTime = 0;

	/**
	 * The default maximum time delay.
	 */
	private double delayMaximum = 0.1;

	/**
	 * The default minimum time delay.
	 */
	private double delayMinimum = 0.1;

	/**
	 * The model.
	 */
	private SimModel model = null;
	
	/**
	 * The default minimum time delay.
	 */
	private double nextAvailableTime = 0;

	/**
	 * The list of agents..
	 */
	private static ArrayList agentList = new ArrayList();

	/**
	 * ID counter
	 */
	private static int nextID = 0;

	/**
	 * This agent's ID
	 */
	private int ID = AsynchAgent.nextID++;
	
	/**
	 * The random distribution used to distribute delays
	 */
	private AbstractContinousDistribution randomDistribution;

	/**
	 * Default creator, sets up this agent with a random distribution
	 * of type Uniform running on a MersenneTwister.  You must call
	 * setModel before running the simulation or none of the schedule
	 * methods will work.
	 */
	public AsynchAgent() {
		this(null);
	}
	
	/**
	 * Default creator, sets up this agent with a random distribution
	 * of type Uniform running on a MersenneTwister and a model
	 * 
	 * @param model the model this agent uses when scheduling
	 */
	public AsynchAgent(SimModel model) {
		this.model = model;		
		
		randomDistribution = new Uniform(new MersenneTwister());
		// Note the new agent.
		AsynchAgent.agentList.add(this);
				
	}

	/**
	 * for serialization
	 */
	private void readObject(ObjectInputStream in)
		throws IOException, ClassNotFoundException {

		// Note the reborn agent.
		AsynchAgent.agentList.add(this);
		
		// Continue with the default work.
		in.defaultReadObject();
	}

	public double findNextTaskCompletionTime() {
		this.setNextAvailableTime(Math.max(Math.max(
			this.getNextAvailableTime(),
			this.getModel().getTickCount())
				+ getNextDoubleFromTo(
						this.getDelayMinimum(), 
						this.getDelayMaximum()), 0.0));
		
		return this.nextAvailableTime;
	}
	
	/**
	 * used to get random numbers in a range from any 
	 * AbstractContinuousDistribution.
	 * 
	 * @param from	least the number can be, inclusive
	 * @param to	most the number can be, exclusive
	 * 
	 * @return a random number in [from, to)
	 */
	private double getNextDoubleFromTo(double from, double to) {
		double randNumber = randomDistribution.nextDouble();
		
		return randNumber % (to - from) + from; 
	}

	public void initialize() {
		
		// Set the starting time.
		this.setNextAvailableTime(
			((AsynchSchedule) this.getModel().getSchedule()).getCurrentTime());

	}

	/**
	 * Schedules a method to be called on this agent after a cerain waiting
	 * period.  
	 * This method must have 1 parameter. 
	 * <c>methodName(param0, param1, param2)</c>
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object
	 * @param param0		a parameter passed to the method.
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleNow(final String methodName) {

		// Schedule a call back.
		return ((AsynchSchedule) this.getModel().getSchedule()).scheduleNow(
			this,
			methodName,
			null,
			null,
			null);

	}

	public BasicAction scheduleNow(
		final String methodName,
		final Object param0) {

		// Schedule a call back.
		return ((AsynchSchedule) this.getModel().getSchedule()).scheduleNow(
			this,
			methodName,
			param0,
			null,
			null);

	}

	public BasicAction scheduleNow(
		final String methodName,
		final Object param0,
		final Object param1) {

		// Schedule a call back.
		return ((AsynchSchedule) this.getModel().getSchedule()).scheduleNow(
			this,
			methodName,
			param0,
			param1,
			null);

	}

	public BasicAction scheduleNow(
		final String methodName,
		final Object param0,
		final Object param1,
		final Object param2) {

		// Schedule a call back.
		return ((AsynchSchedule) this.getModel().getSchedule()).scheduleNow(
			this,
			methodName,
			param0,
			param1,
			param2);

	}

	/**
	 * Schedules a method to be called on this agent after a certain waiting
	 * period.  
	 * This method must have no parameters. 
	 * <c>methodName()</c>
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleAfterWaiting(final String methodName) {

		// Schedule a call back.
		return ((AsynchSchedule) this.getModel().getSchedule()).scheduleAfterWaiting(
			this,
			methodName);

	}

	/**
	 * Schedules a method to be called on this agent after a certain waiting
	 * period.  
	 * This method must have 1 parameter. 
	 * <c>methodName(param0)</c>
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleAfterWaiting(
		final String methodName,
		final Object param0) {

		// Schedule a call back.
		return ((AsynchSchedule) this.getModel().getSchedule()).scheduleAfterWaiting(
			this,
			methodName,
			param0);

	}

	/**
	 * Schedules a method to be called on this agent after a certain waiting
	 * period.  
	 * This method must have 2 parameters. 
	 * <c>methodName(param0, param1)</c>
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object
	 * @param param0		a parameter passed to the method. 
	 * @param param1		a parameter passed to the method.
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleAfterWaiting(
		final String methodName,
		final Object param0,
		final Object param1) {

		// Schedule a call back.
		return ((AsynchSchedule) this.getModel().getSchedule()).scheduleAfterWaiting(
			this,
			methodName,
			param0,
			param1);

	}

	/**
	 * Schedules a method to be called on this agent after a certain waiting
	 * period.  
	 * This method must have 3 parameters. 
	 * <c>methodName(param0, param1, param2)</c>
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object
	 * @param param0		a parameter passed to the method. 
	 * @param param1		a parameter passed to the method.
	 * @param param2		a parameter passed to the method.
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleAfterWaiting(
		final String methodName,
		final Object param0,
		final Object param1,
		final Object param2) {

		// Schedule a call back.
		return ((AsynchSchedule) this.getModel().getSchedule()).scheduleAfterWaiting(
			this,
			methodName,
			param0,
			param1,
			param2);

	}


	/**
	 * Schedules a method to be called when the agent is next available.  
	 * This method must have no parameters. 
	 * <c>methodName(param0)</c>
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleWhenAvailable(final String methodName) {

		// Schedule a call back.
		return ((AsynchSchedule) this.getModel().getSchedule()).scheduleWhenAvailable(
			this,
			methodName);

	}

	/**
	 * Schedules a method to be called when the agent is next available.  
	 * This method must have 1 parameter. 
	 * <c>methodName(param0)</c>
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object
	 * @param param0		a parameter passed to the method. 
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleWhenAvailable(
		final String methodName,
		final Object param0) {

		// Schedule a call back.
		return ((AsynchSchedule) this.getModel().getSchedule()).scheduleWhenAvailable(
			this,
			methodName,
			param0);

	}

	/**
	 * Schedules a method to be called when the agent is next available.  
	 * This method must have 2 parameters. 
	 * <c>methodName(param0, param1)</c>
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object
	 * @param param0		a parameter passed to the method. 
	 * @param param1		a parameter passed to the method.
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleWhenAvailable(
		final String methodName,
		final Object param0,
		final Object param1) {

		// Schedule a call back.
		return ((AsynchSchedule) this.getModel().getSchedule()).scheduleWhenAvailable(
			this,
			methodName,
			param0,
			param1);

	}

	/**
	 * Schedules a method to be called when the agent is next available.  
	 * This method must have 3 parameters. 
	 * <c>methodName(param0, param1, param2)</c>
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object
	 * @param param0		a parameter passed to the method. 
	 * @param param1		a parameter passed to the method.
	 * @param param2		a parameter passed to the method.
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleWhenAvailable(
		final String methodName,
		final Object param0,
		final Object param1,
		final Object param2) {

		// Schedule a call back.
		return ((AsynchSchedule) this.getModel().getSchedule()).scheduleWhenAvailable(
			this,
			methodName,
			param0,
			param1,
			param2);

	}

	/**
	 * Schedules a method to be called on this agent at a certain time.  
	 * This method must have no parameters. 
	 * <c>methodName()</c>
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object.
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleAt(final double time, final String methodName) {

		// Schedule a call back.
		return ((AsynchSchedule) this.getModel().getSchedule()).scheduleAt(
			time,
			this,
			methodName);

	}

	/**
	 * Schedules a method to be called on this agent at a certain time.  
	 * This method must have 1 parameter. 
	 * <c>methodName(param0)</c>
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object
	 * 
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleAt(
		final double time,
		final String methodName,
		final Object param0) {

		// Schedule a call back.
		return ((AsynchSchedule) this.getModel().getSchedule()).scheduleAt(
			time,
			this,
			methodName,
			param0);

	}

	/**
	 * Schedules a method to be called on this agent at a certain time.  
	 * This method must have 2 parameters. 
	 * <c>methodName(param0, param1)</c>
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object
	 * @param param0		a parameter passed to the method. 
	 * @param param1		a parameter passed to the method.
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleAt(
		final double time,
		final String methodName,
		final Object param0,
		final Object param1) {

		// Schedule a call back.
		return ((AsynchSchedule) this.getModel().getSchedule()).scheduleAt(
			time,
			this,
			methodName,
			param0,
			param1);

	}

	/**
	 * Schedules a method to be called on this agent at a certain time.  
	 * This method must have 3 parameters. 
	 * <c>methodName(param0, param1, param2)</c>
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object
	 * @param param0		a parameter passed to the method. 
	 * @param param1		a parameter passed to the method.
	 * @param param2		a parameter passed to the method.
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleAt(
		final double time,
		final String methodName,
		final Object param0,
		final Object param1,
		final Object param2) {

		// Schedule a call back.
		return ((AsynchSchedule) this.getModel().getSchedule()).scheduleAt(
			time,
			this,
			methodName,
			param0,
			param1,
			param2);

	}
	
	public double getDelayMaximum() {
		return delayMaximum;
	}

	public double getDelayMinimum() {
		return delayMinimum;
	}

	/**
	 * The model this agent is working with, used to get the schedule
	 * for the scheduling methods
	 */
	public SimModel getModel() {
		return this.model;
	}
	
	public double getNextAvailableTime() {
		this.nextAvailableTime = 
			Math.max(this.nextAvailableTime, 
				Math.max(
						this.nextAvailableTime, 
						this.getModel().getTickCount())
						 + getNextDelay());
		
		return this.nextAvailableTime;
	}

	public double getNextDelay() {
		return getNextDoubleFromTo(
			this.getDelayMinimum(),
			this.getDelayMaximum());
	}

	public void setDelayMaximum(double d) {
		delayMaximum = d;
	}

	public void setDelayMinimum(double d) {
		delayMinimum = d;
	}

	public void setModel(SimModel model) {
		this.model = model;
	}

	public void setNextAvailableTime(double d) {
		this.utilizedTime = this.utilizedTime
				+ Math.max(d - Math.max(this.nextAvailableTime, 
									this.getModel().getTickCount()), 
							0.0);

		this.nextAvailableTime = Math.max(this.nextAvailableTime, d);
	}

	public double getUtilization() {
		// Find the time.
		if (this.model != null) {
			
			// Check the time.
			double totalTime = ((AsynchSchedule) this.getModel().getSchedule()).getTotalTime();
			if (totalTime == 0.0) {
				
				// Return the default value.
				return 0.0;
				
			}
			
			// Account for future commitments.
			return Math.min(this.utilizedTime / totalTime, 1.0);
			
		}
		
		// Return the default value.
		return 0.0;
	}

	public void clearUtilization() {
		this.utilizedTime = 0;
	}

	public static ArrayList getAgentList() {
		return agentList;
	}

	public static void setAgentList(ArrayList list) {
		agentList = list;
	}

	public int getID() {
		return ID;
	}

	public double getUtilizedTime() {
		return Math.min(this.utilizedTime,
			this.getModel().getTickCount());
	}

	public void setID(int i) {
		ID = i;
	}

	public void setUtilizedTime(double d) {
		utilizedTime = d;
	}

	/**
	 * @return the distribution used to compute delays
	 */
	public AbstractContinousDistribution getRandomDistribution() {
		return randomDistribution;
	}
	
	/**
	 * @param randomDistribution the distribution used to compute delays
	 */
	public void setRandomDistribution(
			AbstractContinousDistribution randomDistribution) {
		this.randomDistribution = randomDistribution;
	}
}
