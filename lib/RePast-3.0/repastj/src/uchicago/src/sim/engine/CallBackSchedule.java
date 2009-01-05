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
package uchicago.src.sim.engine;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * The CallBackSchedule class.
 * Created on Dec 11, 2003 9:17:33 PM
 * @author MichaelNorth
 *
 */
public class CallBackSchedule extends Schedule implements Serializable {

	/**
	 * The step size for "now".
	 */
	private double delta = 0.00000001;


	/**
	 * The caller checking flag.
	 */
	private boolean notingCaller = true;
	
	
	/**
	 * 
	 */
	public CallBackSchedule() {
		super();
	}

	/**
	 * @param executionInterval
	 */
	public CallBackSchedule(double executionInterval) {
		super(executionInterval);
	}

	/**
	 * 
	 */
	public BasicAction scheduleNow(
		final Object o,
		final String methodName) {

		// Schedule a call back.
		return this.scheduleNow(o, methodName, null, null, null);

	}

	/**
	 * 
	 */
	public BasicAction scheduleNow(
		final Object o,
		final String methodName,
		final Object param0) {

		// Schedule a call back.
		return this.scheduleNow(o, methodName, param0, null, null);

	}

	/**
	 * 
	 */
	public BasicAction scheduleNow(
		final Object o,
		final String methodName,
		final Object param0,
		final Object param1) {

		// Schedule a call back.
		return this.scheduleNow(o, methodName, param0, param1, null);

	}

	/**
	 * 
	 */
	public BasicAction scheduleNow(
		final Object o,
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
	 * 
	 */
	public BasicAction scheduleAt(
		final double time,
		final Object o,
		final String methodName) {

		// Schedule a call back.
		return this.scheduleAt(time, o, methodName, null, null, null);

	}

	/**
	 * 
	 */
	public BasicAction scheduleAt(
		final double time,
		final Object o,
		final String methodName,
		final Object param0) {

		// Schedule a call back.
		return this.scheduleAt(time, o, methodName, param0, null, null);

	}

	/**
	 * 
	 */
	public BasicAction scheduleAt(
		final double time,
		final Object o,
		final String methodName,
		final Object param0,
		final Object param1) {

		// Schedule a call back.
		return this.scheduleAt(time, o, methodName, param0, param1, null);

	}

	/**
	 * 
	 */
	public BasicAction scheduleAt(
		final double time,
		final Object o,
		final String methodName,
		final Object param0,
		final Object param1,
		final Object param2) {

		// Schedule the action when the agent is next available.
		return this.schedule(time, o, methodName, param0, param1, param2);

	}
		
	/**
	 * 
	 */
	public BasicAction schedule(
		final double time,
		final Object o,
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
					methods = o.getClass().getMethods();
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
						methods[m].invoke(o, params);
						
					} else {

						// Signal an error the hard way.
						System.err.println(
							"Manual method resolution to work around Java Bug Database entries 4287725, 4301875, 4401287, and 4651775 failed to find a match.");
						System.err.println("Tried to setup a call for:");
						System.err.print("    " + o + "." + methodName + "(");
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
							System.err.print("    " + o + "." +
								methods[m].getName() + "(");
							if (parameters != null) {
								for (p = 0; p < params.length; p++) {
									if (p > 0) System.err.print(", "); 
									System.err.print("" + params[p]);
								}
							}
							System.err.println(")");
						} else {
							System.err.println("    " + o + ".?(...)");
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
	 * @return
	 */
	public double getDelta() {
		return delta;
	}

	/**
	 * @param d
	 */
	public void setDelta(double d) {
		delta = d;
	}

	/**
	 * @return
	 */
	public boolean isNotingCaller() {
		return notingCaller;
	}

	/**
	 * @param b
	 */
	public void setNotingCaller(boolean b) {
		notingCaller = b;
	}

}
