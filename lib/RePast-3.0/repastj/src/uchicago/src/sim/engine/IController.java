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

/**
 * Defines methods for controlling the execution of a SimModel. Implementors
 * will add code to work with loading and saving parameters and perhaps
 * a GUI for controlling the execution.
 *
 * @version $Revision$ $Date$
 */
public interface IController extends TickCounter {

  /**
   * Starts a simulation run.
   */
  public void startSim();

  /**
   * Returns the current tick value.
   */
  public double getCurrentTime();

  /**
   * Stops a simulation run.
   */
  public void stopSim();

  /**
   * Pauses a simulation run.
   */
  public void pauseSim();

  /**
   * Exits the entire simulation. No more runs can be executed after
   * a simulation has exited.
   */
  public void exitSim();

  /**
   * Returns the SimModel executed by this IController. This may be null.
   */
  public SimModel getModel();

  /**
   * Sets the SimModel to be executed by this IController. This may be null.
   * @param model the SimModel to be executed by this IController
   */
  public void setModel(SimModel model);

  /**
   * Returns the Schedule of execution for the SimModel for this IController.
   */
  public Schedule getSchedule();

  /**
   * Sets he Schedule of execution for the SimModel for this IController.
   *
   * @param schedule the Schedule of exectution
   */
  public void setSchedule(Schedule schedule);

  /**
   * Adds a listener for SimEvents fired by this IController.
   *
   * @param listener listens for SimEvents fired by this IController
   */
  public void addSimEventListener(SimEventListener listener);

  /**
   * Removes the specified SimEventListener from this IController.
   *
   * @param listener the SimEventListener to remove
   */
  public void removeSimEventListener(SimEventListener listener);

  /**
   * Returns whether or not this Controller is a GUI controller. A GUI
   * controller controls (starts, stops, etc.) a simulation run in response
   * to GUI input from a user.
   */
  public boolean isGUI();

  /**
   * Returns whether or not this Controller is a batch controller. A batch
   * controller controls (starts, stops, etc.) a simulation run
   * programmatically.
   */
  public boolean isBatch();


  /**
   * Allows for the storage of objects to persist beyond the life of a
   * single run by objects that do not so persist.
   *
   * @param key a unique identifier for the object to store
   * @param val the actual object to store
   */
  public void putPersistentObj(Object key, Object val);

  /**
   * Gets a stored persistent (over the life of many runs) object.
   *
   * @param key the unique identifier for the persistent object
   */
  public Object getPersistentObj(Object key);


  /**
   * Gets the number of the run that this controller is running. For a
   * "batch" controller this may be greater than 1. For others it will be
   * 1.
   */
  public long getRunCount();

  /**
   * Sets whether the simulation application should exit with a
   * System.exit(0) call on exit (pressing the exit button), or
   * dispose of all windows, release the model without killing this
   * instance of the virtual machine.
   */
  void setExitOnExit(boolean val);

  /**
  * Gets whether the simulation application should exit with a
  * System.exit(0) call on exit (pressing the exit button), or
  * dispose of all windows, release the model without killing this
  * instance of the virtual machine.
  */
  boolean getExitOnExit();
}
