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

import uchicago.src.reflector.Introspector;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.MediaProducer;
import uchicago.src.sim.gui.ProducerNamePair;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.SimUtilities;
import cern.jet.random.Uniform;

/**
 * A partial implementation of the SimModel interface. Most,
 * if not all, actual models will inherit from this class. By default SimModelImpl
 * initializes the random number generator in the Random class using the
 * current timestamp as a seed. Random can then be used for random number
 * generator. If you need to have two random number streams with different
 * seeds, you'll need to make your own. See the random number how to in
 * repast/docs/how_to/random.html for more information. When data is collected
 * from a model, it is this seed for this rng that is written out.
 *
 * @author Nick Collier (Modified by Michael J. North)
 * @version $Revision$ $Date$
 *
 * @see uchicago.src.sim.util.Random
 */
public abstract class SimModelImpl implements SimModel {

  private static final int NUMERIC = 1;
  private static final int STRING = 2;
  private static final int BOOLEAN = 3;
  private static final int OBJECT = 4;

  private Vector listenerList = new Vector();
  private Vector propertyListeners = new Vector();
  private IController controller;
  private Vector producers = new Vector();

  /**
   * Used to store property descriptors. The key should be the property or
   * parameter name and the value the descriptor associated with that property.
   *
   * @see uchicago.src.reflector.ListPropertyDescriptor
   * @see uchicago.src.reflector.BooleanPropertyDescriptor
   */
  protected Hashtable descriptors = new Hashtable(5);

  /**
   * A ModelManipulator that can be used to manipulate the model at
   * run time.
   */
  protected ModelManipulator modelManipulator = new ModelManipulator();

  /**
   * Constructs a SimModel and initializes the default random number generator
   * in uchicago.src.sim.util.Random with
   * the current timestamp (new java.util.Date()) as the seed.
   *
   * @see uchicago.src.sim.util.Random
   */
  public SimModelImpl() {
    generateNewSeed();
  }

  /**
   * Generates a new seed for the random number generator using the
   * current timestamp.
   */
  public void generateNewSeed() {
    // Uniform is here for backwards compatibility.
    Uniform.staticSetRandomEngine(Random.generateNewSeed());
    Random.createUniform();
    this.fireSimEvent(new SimEvent(this, SimEvent.RNG_SEED_EVENT));
  }

  /**
   * Sets the random number generator in Random to a new value.
   * Note this will invalidate any distributions created with Random. These
   * will need to be recreated.
   *
   * @param seed the new rng seed
   * @see uchicago.src.sim.util.Random
   */
  public void setRngSeed(long seed) {
    // hack to get around a problem that sometimes appears
    // see colt docs.
    Uniform.makeDefaultGenerator();
    // real setup
    //RandomElement generator = new MersenneTwister((int)seed);
    Uniform.staticSetRandomEngine(Random.getGenerator(seed));
    //rngSeed = seed;
    Random.createUniform();
    this.fireSimEvent(new SimEvent(this, SimEvent.RNG_SEED_EVENT));
  }

  /**
   * Gets the current random seed.
   */
  public long getRngSeed() {
    return Random.getSeed();
  }

  /**
   * Gets the current tick count
   */
  public double getTickCount() {
    return controller.getCurrentTime();
  }

  /**
   * Gets the current tick count
   *
  public double getTickCountDouble() {
    return controller.getCurrentTimeDouble();
  }
   */

  /**
   * Sets the controller associated with this model
   */
  public void setController(IController controller) {
    this.controller = controller;
  }

  /**
   * Gets the BaseController associated with this model.
   */
  public IController getController() {
    return controller;
  }

  /**
   * Gets a String listing the model parameters and values.
   * For example, <pre>
   * MaxVision: 3
   * MaxMetabolism: 4
   * NumAgents: 52
   * </pre>
   *
   * @return a list of the model parameters and values.
   */
  public String getPropertiesValues() {
    Introspector intro = new Introspector();
    String lineSep = System.getProperty("line.separator");
    try {
      intro.introspect(this, this.getInitParam());
      return "RngSeed: " + Random.getSeed() + lineSep + intro.getPropertiesValues();
    } catch (Exception ex) {
      SimUtilities.showError("Unable to return model parameters and values", ex);
      ex.printStackTrace();
      System.exit(0);
    }
    return "";
  }

  /**
   * Gets the ModelManipulator.
   */
  public ModelManipulator getModelManipulator() {
    return modelManipulator;
  }

  /**
   * Gets the mediaProducers registered with this model. The returned vector
   * will contain a ProducerNamePair objects.
   */
  public Vector getMediaProducers() {
    return producers;
  }

  /**
   * Registers a DisplaySurface with this model and associates it with a
   * particular name.
   */
  public void registerDisplaySurface(String name, DisplaySurface surface) {
    if (!listenerList.contains(surface)) {
      addSimEventListener(surface);
    }

    registerMediaProducer(name, surface);
  }

  /**
   * Registers a MediaProducer with this model and associates it with
   * the specified name.
   */
  public void registerMediaProducer(String name, MediaProducer media) {
    ProducerNamePair p = new ProducerNamePair(name, media);

    // ensures that the latest handle to a mediaproducer is in the list
    int index = 0;
    if ((index = producers.indexOf(p)) != -1) {
      producers.remove(index);
    }

     producers.add(p);
  }

  /**
   * Clears the vector of media producers.
   */
  public void clearMediaProducers() {
    //System.out.println("Clearing media producers");

    for (int i = 0; i < producers.size(); i++) {
      ProducerNamePair p = (ProducerNamePair)producers.get(i);
      if (p.getProducer() instanceof DisplaySurface) {
        //for (int j = 0; j < listenerList.size(); j++) {
        //  System.out.println(listenerList.get(i));
        //}
        //System.out.println("removing listener");
        this.removeSimEventListener((SimEventListener)p.getProducer());
        //System.out.println("removed");
        //for (int j = 0; j < listenerList.size(); j++) {
        //  System.out.println(listenerList.get(i));
        //}
      }
    }
    producers.clear();
  }

  /**
   * Clears the list of PropertyListeners.
   */
  public void clearPropertyListeners() {
    propertyListeners.clear();
  }

  /**
   * Gets a hashtable of ParameterDescriptors where key is parameter
   * name, and value is the ParameterDescriptor.
   */
  public Hashtable getParameterDescriptors() {
    return descriptors;
  }

  /**
   * Adds a SimEventListener to listen for SimEvents broadcast from
   * this model.
   *
   * @param l the SimEventListener to add
   * @see SimEventListener
   */
  public void addSimEventListener(SimEventListener l) {
    if (!listenerList.contains(l)) {
      listenerList.add(l);
    }
  }

  /**
   * Removes a SimEventListener from the list of listeners listening for
   * SimEvents broadcast from this model.
   *
   * @param l the SimEventListener to remove.
   * @see SimEventListener
   */
  public void removeSimEventListener(SimEventListener l) {
    listenerList.remove(l);
  }

  /**
   * Broadcast the specified SimEvent to all this model's SimEventListeners
   *
   * @param evt the SimEvent to broadcast
   * @see SimEvent
   */
  public void fireSimEvent(SimEvent evt) {
    Vector copy;
    synchronized(listenerList) {
      copy = (Vector)listenerList.clone();
    }

    for (int i = 0; i < copy.size(); i++) {
      SimEventListener l = (SimEventListener)copy.elementAt(i);
      l.simEventPerformed(evt);
    }
  }

  /**
   * Adds a PropertyListener to this model.
   *
   * @param listener the PropertyListener to add.
   */
  protected void addPropertyListener(PropertyListener listener) {
    propertyListeners.add(listener);
  }

  /**
   * Removes a PropertyListener from this model.
   *
   * @param listener the PropertyListener to remove.
   */
  protected void removePropertyListener(PropertyListener listener) {
    propertyListeners.remove(listener);
  }

  private void firePropertyEvent(PropertyEvent evt, int type) {
    Vector copy;

    synchronized(propertyListeners) {
      copy = (Vector)propertyListeners.clone();
    }

    switch (type) {

      case NUMERIC:
        for (int i = 0; i < copy.size(); i++) {
          PropertyListener l = (PropertyListener)copy.get(i);
          l.numericPropertyChanged(evt);
        }
        break;

      case STRING:
        for (int i = 0; i < copy.size(); i++) {
          PropertyListener l = (PropertyListener)copy.get(i);
          l.stringPropertyChanged(evt);
        }
        break;

      case BOOLEAN:
        for (int i = 0; i < copy.size(); i++) {
          PropertyListener l = (PropertyListener)copy.get(i);
          l.booleanPropertyChanged(evt);
        }
        break;

      case OBJECT:
        for (int i = 0; i < copy.size(); i++) {
          PropertyListener l = (PropertyListener)copy.get(i);
          l.objectPropertyChanged(evt);
        }
        break;

      default:
        break;
    }
  }

  /**
   * Pause the simulation.
   */
  public void pause() {
    firePauseSim();
  }

  /**
   * Stops the simulation.
   */
  public void stop() {
    fireStopSim();
  }

  /**
   * Broadcasts a numeric property event to all this model's
   * PropertyEventListeners.
   *
   * @param evt the PropertyEvent to broadcast
   */
  public void fireNumericPropertyEvent(PropertyEvent evt) {
    firePropertyEvent(evt, NUMERIC);
  }

  /**
   * Broadcasts a string property event to all this model's
   * PropertyEventListeners.
   *
   * @param evt the PropertyEvent to broadcast
   */
  public void fireStringPropertyEvent(PropertyEvent evt) {
    firePropertyEvent(evt, STRING);
  }

  /**
   * Broadcasts a boolean property event to all this model's
   * PropertyEventListeners.
   *
   * @param evt the PropertyEvent to broadcast
   */
  public void fireBooleanPropertyEvent(PropertyEvent evt) {
    firePropertyEvent(evt, BOOLEAN);
  }

  /**
   * Broadcasts an object property event to all this model's
   * PropertyEventListeners.
   *
   * @param evt the PropertyEvent to broadcast
   */
  public void fireObjectPropertyEvent(PropertyEvent evt) {
    firePropertyEvent(evt, OBJECT);
  }

  /**
   * Broadcasts a SimEvent with an id of SimEvent.STOP_EVENT
   * to all this model's listeners.
   */
  protected void fireStopSim() {
    fireSimEvent(new SimEvent(this, SimEvent.STOP_EVENT));
  }

  /**
   * Broadcasts a SimEvent with an id of SimEvent.PAUSE_EVENT
   * to all this model's listeners.
   */
  protected void firePauseSim() {
    fireSimEvent(new SimEvent(this, SimEvent.PAUSE_EVENT));
  }

  /**
   * Broadcasts a SimEvent with an id of SimEvent.END_EVENT
   * to all this model's listeners.
   */
  protected void fireEndSim() {
    fireSimEvent(new SimEvent(this, SimEvent.END_EVENT));
  }
}
