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

import java.util.Hashtable;
import java.util.Vector;

import uchicago.src.reflector.DescriptorContainer;

/**
 * Interface for all RePast models.
 *
 * @author Nick Collier (Modified by Michael J. North)
 * @version $Revision$ $Date$
 */
public interface SimModel extends SimEventProducer, DescriptorContainer {

  /**
   * Gets the names of the initial model parameters to set. These names must
   * have get and set methods supplied by the model. For example, for the
   * parameter maxAge, the model must have getMaxAge and setMaxAge methods.
   *
   * @return a String[] of the initial parameter names
   */
  public String[] getInitParam();

  /**
   * Begins a simulation run. All initialization, building the model, diplay,
   * etc. should take place here. This method is called whenever the start
   * button (or the step button if the run has not yet begun) is clicked. If
   * running in batch mode this is called to kick off a new simulation run.
   */
  public void begin();

  /**
   * Prepares a model for a new run, typically by deallocating objects or
   * setting them to some reasonable default. Called whenever the setup button
   * is clicked or if operating in batch mode whenever a single run has ended in
   * preparation for the next. Also called when the model is first loaded.
   */
  public void setup();

  /**
   * Gets the Schedule created by the model.
   *
   * @return the schedule created by the model
   */
  public Schedule getSchedule();

  /**
   * Gets the name of the model/simulation.
   *
   * @return the name of the model/simulation
   */
  public String getName();

  /**
   * Gets a list of the simulation properties and values. For example, <pre>
   * MaxVision: 3
   * MaxMetabolism: 4
   * NumAgents: 52
   * </pre>
   *@return the String of properties and values
   */
  public String getPropertiesValues();

  /**
   * Sets the random number seed. This sets the seed for the generator for
   *  random number distributions in the Random class.
   *
   * @param seed the random number seed.
   * @see uchicago.src.sim.util.Random
   */
  public void setRngSeed(long seed);


  /**
   * Gets the default random number seed.
   */
  public long getRngSeed();

  /**
   * Generates a new random number seed and makes it the default.
   */
  public void generateNewSeed();

  /**
   * Gets the current tick count for the execution of this model
   *
   * @return the current tick count
   */
  public double getTickCount();

  /**
   * Gets the current tick count for the execution of this model
   *
   * @return the current tick count
   *
  public double getTickCountDouble();
   */

  /**
   * Sets the controller for this simulation model
   * @param controller the controller to associate with this model
   */
  public void setController(IController controller);

  /**
   * Gets the BaseController associated with this model.
   */
  public IController getController();

  /**
   * Gets the ModelManipulator.
   */
  public ModelManipulator getModelManipulator();

  /**
   * Gets a Vector of the MediaProducers registered with this model.
   */
  public Vector getMediaProducers();

  /**
   * Clears the vector of displaySurface
   */
  public void clearMediaProducers();

  /**
   * Clears the list of property listeners.
   */
  public void clearPropertyListeners();

  /**
   * Gets a hashtable of ParameterDescriptors where key is parameter
   * name, and value is the ParameterDescriptor.
   */
  public Hashtable getParameterDescriptors();
}
