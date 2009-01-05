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

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * A subclass of schedule useful for asynchronous scheduling.
 * 
 * @author Michael North
 * @version $Revision$ $Date$
 * @since 3.0
 */
public class AsynchSchedule extends Schedule implements Serializable {

	/**
	 * The step size for "now".
	 */
	private double delta = 0.00000001;

	/**
	 * The time of the first simulation event.
	 */
	private double startTime = Double.NaN;

	/**
	 * The caller checking flag.
	 */
	private boolean notingCaller = true;

	public AsynchSchedule() {
		super();
	}

	/**
	 * Constructs a schedule that executes at the specified interval. (i.e a
	 * schedule with an interval of 2 executes all its BasicActions every other
	 * clock tick. The master Schedule built in a model and used to execute all
	 * the actions in the simulation will typicaly have an interval of 1. Any
	 * sub schedules added to this "master" schedule might have other intervals.
	 * 
	 * @param executionInterval the execution interval.
	 */
	public AsynchSchedule(double executionInterval) {
		super(executionInterval);
	}
	
	/**
	 * Schedules a method to be called now.  This method must have no
	 * parameters.
	 * 
	 * @param o				the object to call the method on
	 * @param methodName	the method to call on the object
	 * 
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleNow(
		final IAsynchAgent o,
		final String methodName) {

		// Schedule a call back.
		return this.scheduleNow(o, methodName, null, null, null);

	}

	
	/**
	 * Schedules a method to be called now.  This method must have 1
	 * parameter. <c>methodName(param0)</c>
	 * 
	 * @param o				the object to call the method on
	 * @param methodName	the method to call on the object
	 * @param param0		a parameter passed to the method. 
	 * 
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleNow(
		final IAsynchAgent o,
		final String methodName,
		final Object param0) {

		// Schedule a call back.
		return this.scheduleNow(o, methodName, param0, null, null);

	}

	/**
	 * Schedules a method to be called now.  This method must have 2
	 * parameters. <c>methodName(param0, param1)</c>
	 * 
	 * @param o				the object to call the method on
	 * @param methodName	the method to call on the object
	 * @param param0		a parameter passed to the method. 
	 * @param param1		a parameter passed to the method.
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleNow(
		final IAsynchAgent o,
		final String methodName,
		final Object param0,
		final Object param1) {

		// Schedule a call back.
		return this.scheduleNow(o, methodName, param0, param1, null);

	}

	/**
	 * Schedules a method to be called now.  This method must have 3
	 * parameters. <c>methodName(param0, param1, param2)</c>
	 * 
	 * @param o				the object to call the method on
	 * @param methodName	the method to call on the object
	 * @param param0		a parameter passed to the method. 
	 * @param param1		a parameter passed to the method.
	 * @param param2		a parameter passed to the method.
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleNow(
		final IAsynchAgent o,
		final String methodName,
		final Object param0,
		final Object param1,
		final Object param2) {

		// Find the current time.
		final double time = this.getCurrentTime() + this.getDelta();
			
		// Schedule the action when the agent is next available.
		return this.schedule(time, o, methodName, param0, param1, param2);

	}

	/**
	 * Schedules a method to be called when the agent is next available.  
	 * This method must have 0 parameters. 
	 * <c>methodName()</c>
	 * 
	 * @param o				the object to call the method on
	 * @param methodName	the method to call on the object
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleWhenAvailable(
		final IAsynchAgent o,
		final String methodName) {

		// Schedule a call back.
		return this.scheduleWhenAvailable(o, methodName, null, null, null);

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
		final IAsynchAgent agent,
		final String methodName,
		final Object param0) {

		// Schedule a call back.
		return this.scheduleWhenAvailable(agent, methodName, param0, null, null);

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
		final IAsynchAgent agent,
		final String methodName,
		final Object param0,
		final Object param1) {

		// Schedule a call back.
		return this.scheduleWhenAvailable(agent, methodName, param0, param1, null);

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
		final IAsynchAgent agent,
		final String methodName,
		final Object param0,
		final Object param1,
		final Object param2) {

		// Find the next available time.
		final double time = agent.getNextAvailableTime();
			
		// Schedule the action when the agent is next available.
		return this.schedule(time, agent, methodName, param0, param1, param2);

	}
	
	/**
	 * Schedules a method to be called on an agent at a certain time.  
	 * This method must have no parameters. 
	 * <c>methodName()</c>
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleAt(
		final double time,
		final IAsynchAgent agent,
		final String methodName) {

		// Schedule a call back.
		return this.scheduleAt(time, agent, methodName, null, null, null);

	}

	/**
	 * Schedules a method to be called on an agent at a certain time.  
	 * This method must have 1 parameters. 
	 * <c>methodName(param0)</c>
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object
	 * @param param0		a parameter passed to the method. 
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleAt(
		final double time,
		final IAsynchAgent agent,
		final String methodName,
		final Object param0) {

		// Schedule a call back.
		return this.scheduleAt(time, agent, methodName, param0, null, null);

	}

	/**
	 * Schedules a method to be called on an agent at a certain time.  
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
		final IAsynchAgent agent,
		final String methodName,
		final Object param0,
		final Object param1) {

		// Schedule a call back.
		return this.scheduleAt(time, agent, methodName, param0, param1, null);

	}

	/**
	 * Schedules a method to be called on an agent at a certain time.  
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
		final IAsynchAgent agent,
		final String methodName,
		final Object param0,
		final Object param1,
		final Object param2) {

		// Schedule the action when the agent is next available.
		return this.schedule(time, agent, methodName, param0, param1, param2);

	}
	
	/**
	 * Schedules a method to be called on an agent after a certain waiting
	 * period.  
	 * This method must have no parameters. 
	 * <c>methodName(param0)</c>
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction scheduleAfterWaiting(
		final IAsynchAgent agent,
		final String methodName) {

		// Schedule a call back.
		return this.scheduleAfterWaiting(agent, methodName, null, null, null);

	}

	/**
	 * Schedules a method to be called on an agent after a certain waiting
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
	public BasicAction scheduleAfterWaiting(
		final IAsynchAgent agent,
		final String methodName,
		final Object param0) {

		// Schedule a call back.
		return this.scheduleAfterWaiting(agent, methodName, param0, null, null);

	}

	/**
	 * Schedules a method to be called on an agent after a certain waiting
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
		final IAsynchAgent agent,
		final String methodName,
		final Object param0,
		final Object param1) {

		// Schedule a call back.
		return this.scheduleAfterWaiting(agent, methodName, param0, param1, null);

	}

	/**
	 * Schedules a method to be called on an agent after a certain waiting
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
		final IAsynchAgent agent,
		final String methodName,
		final Object param0,
		final Object param1,
		final Object param2) {

		// Find the next available time.
		final double time = agent.findNextTaskCompletionTime();
			
		// Schedule the action when the agent is next available.
		return this.schedule(time, agent, methodName, param0, param1, param2);

	}

	/**
	 * Schedules a method to be called on an agent.  If the method takes
	 * parameters, then the cooresponding param0, param1, param2 must be
	 * set, otherwise they must be null. <br/>
	 * 
	 * Note: this determines how many parameters the method has by how
	 * many of param{0, 1, 2} are null, therefore if you inadvertently 
	 * pass a null parameter to this method it may not be able to find the
	 * method you are attempting to call.
	 * 
	 * @param o				the agent to call the method on
	 * @param methodName	the method to call on the object
	 * @param param0		a parameter passed to the method. 
	 * @param param1		a parameter passed to the method.
	 * @param param2		a parameter passed to the method.
	 *  
	 * @return the BasicAction that was scheduled
	 */
	public BasicAction schedule(
		final double time,
		final IAsynchAgent agent,
		final String methodName,
		final Object param0,
		final Object param1,
		final Object param2) {

		// Setup the arguments.
		final Object params[];
		final Class paramsClasses[];
		if (param0 == null) {
			
			// Setup the paramaters.
			params = new Object[0];
				
			// Setup the paramater classes.
			paramsClasses = new Class[0];
	
		} else if (param1 == null) {
			
			// Setup the paramaters.
			params = new Object[1];
			params[0] = param0;
			
			// Setup the paramater classes.
			paramsClasses = new Class[1];
			paramsClasses[0] = param0.getClass();
			
		} else if (param2 == null) {
			
			// Setup the paramaters.
			params = new Object[2];
			params[0] = param0;
			params[1] = param1; 
			
			// Setup the paramater classes.
			paramsClasses = new Class[2];
			paramsClasses[0] = param0.getClass();
			paramsClasses[1] = param1.getClass();
			
		} else {

			// Setup the paramaters.
			params = new Object[3];
			params[0] = param0; 
			params[0] = param1; 
			params[0] = param2; 
			
			// Setup the paramater classes.
			paramsClasses = new Class[3];
			paramsClasses[0] = param0.getClass();
			paramsClasses[1] = param1.getClass();
			paramsClasses[2] = param2.getClass();
			
		}
		
		// Note the caller.
		final Exception context;
		if (this.isNotingCaller()) {
			try {
				throw new Exception();
			} catch (Exception e) {
				context = e;
			}
		} else {
			context = null;
		}

		// Check the start time.
		if (Double.isNaN(this.getStartTime()) || time < this.getStartTime()) {
			this.setStartTime(time);
		}

		// Setup a callback.
		return this.scheduleActionAt(time, new BasicAction() {
			public void execute() {

				// Attempt to make the call.				
				int p;
				int m = 0;
				Method[] methods = null;
				Class[] parameters = null;
				try {
					
					// Find the methods to work around Java Bug Database entries
					// 4287725, 4301875, 4401287, and 4651775.
					methods = agent.getClass().getMethods();
					boolean found = false;
					while (m < methods.length) {

						// Find the next method.
						parameters = methods[m].getParameterTypes();
						
						// Check the next method.
						if ((methodName.equals(methods[m].getName())) &&
							(parameters.length == params.length)) {
							found = true;
							for (p = 0; p < parameters.length; p++) {
								if (!parameters[p].isInstance(params[p])) {
									found = false;
									break;
								}
							}
						}
						
						// Move on.
						if (found) break;
						else m++;
						
					}
						
					// Make the call, if possible.
					if (found) {
						
						// Make the call.	
						methods[m].invoke(agent, params);
						
					} else {

						// Signal an error the hard way.
						System.err.println(
							"Manual method resolution to work around Java Bug Database entries 4287725, 4301875, 4401287, and 4651775 failed to find a match.");
						System.err.println("Tried to setup a call for:");
						System.err.print("    " + agent + "." + methodName + "(");
						for (p = 0; p < params.length; p++) {
							if (p > 0) System.err.print(", "); 
							System.err.print("" + params[p]);
						}
						System.err.println(")");
						if (context == null) {
							System.err.println(
								"No context available.  Please set CallbackScheduler.setNotingCaller(true) for context.");
						} else {
							System.err.print("The context was:");
							context.printStackTrace();
						}

						// Note the available methods.
						System.err.println("The available methods where:");
						for (m = 0; m < methods.length; m++) {
							parameters = methods[m].getParameterTypes();
							System.err.print(methods[m].getName() + "(");
							for (p = 0; p < parameters.length; p++) {
								if (p > 0) System.err.print(", ");
								System.err.print(parameters[p].getName());
							}
							System.err.println(")");
						
						}


					}
					
				} catch (Exception e1) {
					
					// Send out a message that is as clear as possible.
					try {
						System.err.println("Tried to call:");
						if ((methods != null) && (methods.length > m)) {
							System.err.print("    " + agent + "." +
								methods[m].getName() + "(");
							if (parameters != null) {
								for (p = 0; p < params.length; p++) {
									if (p > 0) System.err.print(", "); 
									System.err.print("" + params[p]);
								}
							}
							System.err.println(")");
						} else {
							System.err.println("    " + agent + ".?(...)");
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					e1.printStackTrace();
					
				}
				
			}
		});

	}

	/**
	 * @return The step size for "now".
	 */
	public double getDelta() {
		return delta;
	}

	/**
	 * @param d The step size for "now".
	 */
	public void setDelta(double d) {
		delta = d;
	}

	/**
	 * @return currentTime - startTime
	 */
	public double getTotalTime() {
		if (Double.isNaN(this.getStartTime())) return 0.0;
		return this.getCurrentTime() - this.getStartTime();
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double d) {
		startTime = d;
	}

	public boolean isNotingCaller() {
		return notingCaller;
	}

	public void setNotingCaller(boolean b) {
		notingCaller = b;
	}
}
