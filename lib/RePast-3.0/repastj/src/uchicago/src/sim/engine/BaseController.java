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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import uchicago.src.sim.gui.DisplayConstants;
import uchicago.src.sim.parameter.ParameterSetter;
import uchicago.src.sim.parameter.ParameterUtility;
import uchicago.src.sim.util.SimUtilities;

/**
 * Abstract base class implementing IController. This provides methods for
 * controlling the execution of a SimModel, firing SimEvents and minimal
 * parameter handling abilities. Child classes are expected to add more
 * sophisticated parameter handling.
 *
 * @author Nick Collier (Modified by Michael J. North)
 * @version $Revision$ $Date: 2002/11/25 16:37:33
 */
public abstract class BaseController implements SimEventListener, IController {

  /**
   * The master schedule
   */
  protected Schedule schedule = null;
  /**
   * The runnable that encapsulates the schedule and allows for
   * threaded execution of the schedule
   */
  protected Runnable simRun = null;

  /**
   * the tick count
   */
  protected double time = 0.0;

  /**
   * The actual run thread that runs the Runnable simRun
   */
  protected volatile Thread runThread;

  /**
   * when true the simulation runs, when false it stops
   */
  protected volatile boolean go = false;

  /**
   * when true the sim pauses, when false it runs
   */
  protected volatile boolean pauseSim = false;

  /**
   * when true the simulation has stopped
   */
  protected boolean done = false;

  /**
   * when true the simulation should execute all actions for the next tick
   * and then pause.
   */
  protected volatile boolean doStep = false;

  /**
   * when true a a single run of simulation has finished. Used by the
   * <code>BatchController</code>
   */
  protected volatile boolean runFinished = false;

  /**
   * The amount to pause while simRun is running, allowing other processing to
   * occur.
   */
  protected int sleepValue = 5;

  /**
   * the model (simulation) to run with this <code>BaseController</code>
   */
  protected SimModel model;

  /**
   * true if the actions scheduled to be run when the simulation is
   * paused have been run, false if not
   */
  protected boolean pauseActionsExecuted = false;

  /**
   * true if this is a gui controller - i.e. toolbar is displayed
   */
  protected boolean isGui = false;

  /**
   * Persistent objects store.
   * For anything that might need to persist over the life of
   * a batch run, although the object that created it may
   * disappear.
   */
  protected Hashtable persistentObj = new Hashtable();

  /**
   * Default model parameters read from a parameter file
   */
  protected ParameterSetter params = null;

  protected int pauseAt = -1;

  /**
   * Tracks whether model.begin() has been called and so whether startSim
   * should call model.begin().
   */
  protected boolean executeBegin = true;

  /**
   * Monitor object used in guarded suspension pause check.
   */
  protected Object monitor = new Object();

  /**
   * List of listeners listening for SimEvents broadcast by this
   * BaseController.
   */
  protected ArrayList listenerList = new ArrayList();

  /**
   * Flag for whether the simulation should exit the JVM on exiting
   * the simulation.
   */
  protected boolean exitOnExit = true;


  /**
   * Constructs a BaseController.
   */
  public BaseController() {
    ParameterUtility.createInstance();

  }

  /**
   * Sets the model associated with this controller.
   *
   * @param model the model associated with this BaseController
   */
  public void setModel(SimModel model) {
    this.model = model;
    if (model != null) {
      model.setup();
    }
  }

  /**
   * Returns the SimModel currently associated with this BaseController.
   */
  public SimModel getModel() {
    return this.model;
  }


  /**
   * Gets the display cell width. The display cell size is the size in pixels
   * of the cells in which agents, environments and so forth are drawn.
   *
   * @return the display cell size
   */
  public int getCellWidth() {
    return DisplayConstants.CELL_WIDTH;
  }

  /**
   * Sets the display cell width. The display cell size is the size in pixels
   * of the cells in which agents, environments and so forth are drawn. Values
   * between 5 and 50 work well depending on the size of the "space"
   * being displayed.
   *
   * @param cellSize the new display cell size
   * @see #getCellWidth()
   */
  public void setCellWidth(int cellSize) {
    if (runThread == null)
      DisplayConstants.CELL_WIDTH = cellSize;
  }

  /**
   * Gets the display cell height. The display cell size is the size in pixels
   * of the cells in which agents, environments and so forth are drawn.
   *
   * @return the display cell size
   */
  public int getCellHeight() {
    return DisplayConstants.CELL_HEIGHT;
  }

  /**
   * Sets the display cell height. The display cell size is the size in pixels
   * of the cells in which agents, environments and so forth are drawn. Values
   * between 5 and 50 work well depending on the size of the "space"
   * being displayed.
   *
   * @param cellSize the new display cell size
   * @see #getCellHeight()
   */
  public void setCellHeight(int cellSize) {
    if (runThread == null)
      DisplayConstants.CELL_HEIGHT = cellSize;
  }

  /**
   * Gets the display cell depth. The display cell size is the size in pixels
   * of the cells in which agents, environments and so forth are drawn.
   *
   * @return the display cell size
   */
  public int getCellDepth() {
    return DisplayConstants.CELL_DEPTH;
  }

  /**
   * Sets the display cell depth. The display cell size is the size in pixels
   * of the cells in which agents, environments and so forth are drawn. Values
   * between 5 and 50 work well depending on the size of the "space"
   * being displayed.
   *
   * @param cellSize the new display cell size
   * @see #getCellDepth()
   */
  public void setCellDepth(int cellSize) {
    if (runThread == null)
      DisplayConstants.CELL_DEPTH = cellSize;
  }

  /**
   * Sets the schedule to be run by the controller.
   *
   * @param schedule the schedule to be run by this BaseController.
   * @see Schedule
   */
  public void setSchedule(Schedule schedule) {
    this.schedule = schedule;
    setupSchedule();
  }

  /**
   * Returns the Schedule associated with BaseController
   */
  public Schedule getSchedule() {
    return schedule;
  }

  /*
   * Gets the current simulation time (tick count) - the current number of
   * execution cycles completed by main schedule.
   *
   * @return the current simulation time
   * @deprecated replaced by getCurrentTimeDouble
   *
  public long getCurrentTime() {

    return (long) time; // + timeMod);
  }
   */

  /**
   * Gets the current simulation time (tick count) - the current number of
   * execution cycles completed by main schedule.
   *
   * @return the current simulation time
   */
  //public double getCurrentTimeDouble() {
  public double getCurrentTime() {

    return time; // + timeMod;
  }

  /**
   * Called whenever the tick count is incremented. Allows subclasses to update
   * a display, data point etc. when the tick count is updated.
   */
  protected abstract void onTickCountUpdate();

  /**
   * Sets the random seed for the current model. The random number generator
   * will be reset each time the model is run.
   *
   * @param seed the new random seed
   * @see SimModelImpl#setRngSeed(long)
   */
  public void setRandomSeed(long seed) {
    model.setRngSeed(seed);
  }

  /**
   * Gets the current random seed for the current model
   */
  public long getRandomSeed() {
    return model.getRngSeed();
  }

  protected void beginModel() {
    model.begin();
  }

  /**
   * Starts the simulation. Fires a START_EVENT before the simulation
   * thread is actually started.
   */
  public void startSim() {
    // necessary to make pauseAt persist over the course of
    // several runs.
    setPauseAt(pauseAt);

    fireSimEvent(new SimEvent(this, SimEvent.START_EVENT));

    if (runThread == null) {
      if (executeBegin) beginModel();
      schedule = model.getSchedule();
      setupSchedule();
    }
    if (schedule == null) {
      SimUtilities.showMessage("No schedule to run");
      System.exit(0);
    } else if (!go) {
      go = true;
      pauseSim = false;
      doStep = false;
      if (runThread == null) {
        runThread = new Thread(simRun);
        runThread.start();
      }
    } else if (go) {
      pauseSim = false;
      doStep = false;
      notifyMonitor();
    }
  }

  /**
   * Notifies object monitor that controlls pausing.
   */
  protected void notifyMonitor() {
    synchronized (monitor) {
      monitor.notify();
    }
  }

  /**
   * Sets up the schedule thread.
   */
  protected void setupSchedule() {
    simRun = new Runnable() {

      public void run() {

        java.awt.Toolkit tk = null;
        java.awt.EventQueue eq = null;
        if (isGui) {
          tk = java.awt.Toolkit.getDefaultToolkit();
          eq = tk.getSystemEventQueue();
        }

        while (go) {
          //timeMod = 1;

          schedule.preExecute();
          time = schedule.getCurrentTime();
          schedule.execute();

          onTickCountUpdate();


          if (doStep) {
            doStep = false;
            pauseSim = true;
          }


          if (pauseSim) {
            try {
              if (!pauseActionsExecuted) {
                Vector v = schedule.getPauseActions();
                for (int i = 0; i < v.size(); i++) {
                  BasicAction ba = (BasicAction) v.elementAt(i);
                  ba.execute();
                }

                pauseActionsExecuted = true;
                model.fireSimEvent(new SimEvent(this, SimEvent.PAUSE_EVENT));
              }

              synchronized (monitor) {
                while (pauseSim) {
                  monitor.wait();
                }
              }


            } catch (InterruptedException ex3) {
              // should stop the simulation in here.
              go = false;
              pauseSim = false;
              doStep = false;
            } finally {
              pauseActionsExecuted = true;
            }

          }

          pauseActionsExecuted = false;


          // process gui events like mouse clicks etc.
          try {
            if (isGui) {
              while (eq.peekEvent() != null && go) {
                //runThread.sleep(BaseController.this.sleepValue);
                Thread.sleep(BaseController.this.sleepValue);
              }
            }
          } catch (InterruptedException ex4) {
            go = false;
            pauseSim = false;
            doStep = false;
          }
        }

        // if reach here simulation has ended
        //timeMod = 0;
        schedule.executeEndActions();
        model.fireSimEvent(new SimEvent(this, SimEvent.STOP_EVENT));
        time = 0;
        runFinished = true;
        notifyMonitor();
        model.clearPropertyListeners();
      }
    };

    runThread = null;
  }

  /**
   * Gets the parameters of the current loaded model.
   * @deprecated replaced by ParameterUtilities.#getModelProperties(SimModel)
   */
  public Hashtable getModelParameters() {
    Hashtable props = new Hashtable(23);
    if (model != null) {
      try {
        props = ParameterUtility.getInstance().getModelProperties(model);
      } catch (Exception ex) {
        SimUtilities.showError("Error retrieving model properties", ex);
        System.exit(0);
      }
    }

    return props;
  }

  /**
   * Gets a Hashtable of the default model parameters. The key is the parameter
   * name, and the value is the default value. This is done in the
   * BaseController heirarchy as parameters loaded from a file might
   * effect this. Returns an empty <code>Hashtable</code> if no model
   * has been loaded. Note that this creates no distinction between
   * batch parameters that are dynamic and those that are constant.<p>
   *
   * If any default values have been loaded from a file this will return
   * those values. To get the current parameter values, use
   * getModelParameters();
   */
  public Hashtable getDefaultModelParameters() {
    return params.getDefaultModelParameters(model);
  }


  /**
   * Pauses the simulation
   */
  public void pauseSim() {
    pauseSim = true;
  }

  /**
   * Stops the simulation.
   */
  public void stopSim() {
    go = false;
    doStep = false;
    pauseSim = false;
    notifyMonitor();
  }

  /**
   * Is this a BatchController. Returns false.
   */
  public boolean isBatch() {
    return false;
  }

   /**
   * Adds a SimEventListener to listen for SimEvents broadcast from
   * this BaseController.
   *
   * @param l the SimEventListener to add
   * @see SimEventListener
   */
  public void addSimEventListener(SimEventListener l) {
    listenerList.add(l);
  }

  /**
   * Removes a SimEventListener from the list of listeners listening for
   * SimEvents broadcast from this BaseController.
   *
   * @param l the SimEventListener to remove.
   * @see SimEventListener
   */
  public void removeSimEventListener(SimEventListener l) {
    listenerList.remove(l);
  }

   /**
   * Fires a SimEvent to the registered listeners.
   *
   * @param evt the SimEvent to fire
   */
  public void fireSimEvent(SimEvent evt) {
    ArrayList copy;
    synchronized(listenerList) {
      copy = (ArrayList)listenerList.clone();
    }

    for (int i = 0, n = copy.size(); i < n; i++) {
      SimEventListener l = (SimEventListener)copy.get(i);
      l.simEventPerformed(evt);
    }
  }

  /**
   * Allows for the storage of objects to persist beyond the life of a
   * single run by objects that do not so persist.
   *
   * @param key a unique identifier for the object to store
   * @param val the actual object to store
   */
  public void putPersistentObj(Object key, Object val) {
    persistentObj.put(key, val);
  }

  /**
   * Gets a stored persistent (over the life of many runs) object.
   *
   * @param key the unique identifier for the persistent object
   */
  public Object getPersistentObj(Object key) {
    return persistentObj.get(key);
  }

  class PauseAction extends BasicAction {
    public void execute() {
      pauseSim();
    }
  }

  protected BasicAction pauseAtAction = new PauseAction();

  public void setPauseAt(int val) {
    pauseAt = val;
    if (val > 0) {
      if (model != null) {
        if (pauseAtAction != null)
          model.getSchedule().removeAction(pauseAtAction);
        pauseAtAction = model.getSchedule().scheduleActionAt(pauseAt,
                                                             pauseAtAction);
      }
    } else {
      if (model != null && model.getSchedule() != null) {
        if (pauseAtAction != null)
          model.getSchedule().removeAction(pauseAtAction);
      }
    }
  }

  public int getPauseAt() {
    return pauseAt;
  }

  /**
   * Sets whether the simulation application should exit with a
   * System.exit(0) call on exit (pressing the exit button), or
   * dispose of all windows, release the model without killing this
   * instance of the virtual machine.
   */
  public void setExitOnExit(boolean val) {
    exitOnExit = val;
  }

   /**
   * Gets whether the simulation application should exit with a
   * System.exit(0) call on exit (pressing the exit button), or
   * dispose of all windows, release the model without killing this
   * instance of the virtual machine.
   */
  public boolean getExitOnExit() {
    return exitOnExit;
  }
}
