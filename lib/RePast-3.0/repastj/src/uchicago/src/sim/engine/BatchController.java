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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import uchicago.src.sim.analysis.DataFileHeader;
import uchicago.src.sim.analysis.DataRecorder;
import uchicago.src.sim.parameter.DefaultParameterSetter;
import uchicago.src.sim.parameter.ParameterSetter;
import uchicago.src.sim.parameter.ParameterUtility;
import uchicago.src.sim.util.SimUtilities;

/**
 * A controller for running a simulation over multiple runs.
 * See {@link uchicago.src.sim.engine.SimInit SimInit} for more on how to run a
 * simulation in batch mode. <code>BatchController</code> should not be
 * created by a user under normal circumstances.
 *
 * @author Nick Collier (Modified by Michael J. North)
 * @version $Revision$ $Date$
 * @see SimInit
 */
public class BatchController extends BaseController {
  protected String[] pArray;
  protected long batchCount = 0;

  protected ArrayList nameList;
  protected ArrayList dynNameList;
  protected Hashtable nameParam = new Hashtable(5);

  protected ArrayList batchListeners = new ArrayList();
  
  protected volatile boolean finished = false;

  protected boolean stopped;
  
  protected DataRecorder recorder;
  
  /**
   * whether or not the controller is automatically recording the parameters
   * of the model
   */
  protected boolean autoRecording = true;
  
  
  BatchController(){
    params = new DefaultParameterSetter();
  }

  /**
   * Creates a BatchController with the specified model parameters. The
   * actual model is (inelegantly) loaded via setModel().
   *
   * @param parameters the parameters with which to run the simulation
   */
  public BatchController(ParameterSetter parameters) {
    params = parameters;
    //getParameterNames();
    ParameterUtility.createInstance(params);
  }

  /**
   * Sets the model to be controlled by this BatchController.
   *
   * @param model the model to be controlled by this BatchController
   */
  public void setModel(SimModel model) {
    super.setModel(model);
    params.setModelParameters(model);
    model.addSimEventListener(this);
//    if(model != null) {
//    	anObjectDataRecorder = new ObjectDataRecorder(model.getName()+".out","Tick\t"+ParameterUtility.getInstance().getPropertyNamesHeader(model));
//    }
  }

  /**
   * Gets the number of the current batch run
   */
  public long getRunCount() {
    return batchCount;
  }

  /**
   * Should the header for the specified file be written. The BatchController
   * is persistent over the course of all the batch runs, and a non-persistent
   * object can query this BatchController to see if the file header
   * has already been written.
   *
   * @param fileName the name of the file whose header should or should not
   * be written.
   * @return true if the header should be written, otherwise false.
   * @deprecated now uses the persistent object mechanism.
   */
  public boolean doWriteHeader(String fileName) {
    fileName = fileName + DataFileHeader.WRITE_HEADER;
    Boolean b = (Boolean)persistentObj.get(fileName);
    if (b == null) {
      // hasn't been set yet, so not written yet, so true
      b = Boolean.TRUE;
      persistentObj.put(fileName, b);
    }
    return b.booleanValue();
  }

  /**
   * Sets whether the header for the specified file should be written.
   *
   * @param filename the name of the file whose header should or should not
   * be written
   * @param val true if the header should be written, otherwise false
   * @deprecated
   */
  public void setWriteHeader(String filename, boolean val) {
    persistentObj.put(filename + DataFileHeader.WRITE_HEADER,
                      val ? Boolean.TRUE : Boolean.FALSE);
  }

  /**
   * This fires a tick changed batch event every tick
   */
  public void onTickCountUpdate() {
  	fireBatchEvent(new BatchEvent(this, BatchEvent.TICK_CHANGED, this.getCurrentTime()));
  }

  /**
   * Begins the batch runs
   */

  /* All this threading is necessary if the model is allowed to stop itself
   * through the schedule. The problem arises because schedule.execute()
   * must be allowed to complete if runThread is to finish. Consequently,
   * stopping the current run and starting a new run in schedule.execute
   * never allows the current run to complete. This runnable avoids that
   * problem by starting the run inside a separate thread and monitoring
   * runFinished which goes to true when runThread terminates.
   */
  public void begin() {
     Runnable batchRunner = new Runnable() {
      public void run() {
        while (!finished && !stopped) {
          start();
          synchronized (monitor) {
            while (!runFinished) {
              try {
                monitor.wait();
              } catch (InterruptedException ex) {
              }
            }
          }
          stopRun();
        }
        synchronized (appMonitor) {
          appMonitor.notify();
        }
      }
    };

    stopped = false;
    
    Thread batchThread = new Thread(batchRunner);
    batchThread.start();
    synchronized (appMonitor) {
      while (!finished) {
        try {
          appMonitor.wait();
        } catch (InterruptedException ex) {
        }
      }
    }
    exitSim();
  }

  private Object appMonitor = new Object();


  /**
   * Starts an individual run of the simulation
   */
  public void start() {
    incrementBatchCount();
    System.out.println("Run: " + getRunCount());
    listenerList.clear();

    if (autoRecording)
    	setupRecorder();
    
    startSim();
  }

  /**
   * This determines if the BatchController will automatically record the
   * parameters from the model at the end of a simulation run. These parameters
   * will be recorded to a file called ModelName.out.
   * 
   * @return If the BatchController is automatically recording the model's parameters.
   */
  public boolean isAutoRecording() {
    return autoRecording;
  }
  
  /**
   * This determines if the BatchController will automatically record the
   * parameters from the model at the end of a simulation run. These parameters
   * will be recorded to a file called ModelName.out.<br/>
   * This must be set before calling BatchController.begin for it to be effective.
   * The parameters that are recorded are based on those returned by the
   * getInitParams() method.  If a get method cannot be found, it ignores that
   * parameter.
   * 
   * @param autoRecording whether or not to automatically record the parameters
   */
  public void setAutoRecording(boolean autoRecording) {
    this.autoRecording = autoRecording;
  }
  
  private void setupRecorder() {
    recorder = new DataRecorder(model.getName() + ".out", this.getModel());
    
    addSourcesFromInitParams(recorder, model);
        
    model.getSchedule().scheduleActionAtEnd(recorder, "record");
    model.getSchedule().scheduleActionAtEnd(recorder, "writeToFile");
  }
  
  private void addSourcesFromInitParams(DataRecorder record, SimModel modelSrc) {
  	if (model == null || record == null)
  		return;
  	
  	String[] modelParams = modelSrc.getInitParam();
  	for (int i = 0; i < modelParams.length; i++) {
  		addSourceFromInitParam(record, modelSrc, modelParams[i]);
  	}
  }
  
  private void addSourceFromInitParam(DataRecorder record, SimModel modelSrc,
			String param)
  {
  	if (model == null || record == null || param == null)
  		return;
  	
  	String methodName = "get" + SimUtilities.capitalize(param);
  	try {
		Method method = ActionUtilities.getNoArgMethod(modelSrc, methodName);
		
		if (method.getReturnType().isPrimitive())
			record.createNumericDataSource(param, modelSrc, methodName);
		else
			record.createObjectDataSource(param, modelSrc, methodName);
		
	} catch (NoSuchMethodException e) {
		// do nothing, just don't record this
	}  	
  }
  
  /**
   * Stops an individual run of the simulation, increments the parameters
   * appropriately, and if necessary starts another run.
   */
  public void stopRun() {
    model.generateNewSeed();
    model.setup();
    runThread = null;
    params.setNextModelParameters(model);
    finished = !params.hasNext();
    runFinished = false;
    
    fireBatchEvent(new BatchEvent(this, BatchEvent.RUN_ENDED));
  }

  /**
   * Called by a source on which this is a listener when a
   * <code>SimEvent</code> is performed. This only listens for stop
   * events and stops the sim when it receives the event.
   */
  public void simEventPerformed(SimEvent evt) {
    if (evt.getId() == SimEvent.STOP_EVENT)
      this.stopSim();
  }

  /**
   * shuts down a simulation even if it is in the middle of a run
   */
  public void endSim() {
  	// set the stop message
  	stopped = true;
  	super.stopSim();
  	
  	exitSim();
  }
  
  public void exitSim() {
    System.out.println("Batch Done");
    fireSimEvent(new SimEvent(this, SimEvent.END_EVENT));
    if (exitOnExit) System.exit(0);
    fireBatchEvent(new BatchEvent(this, BatchEvent.BATCH_FINISHED));
  }
  
  protected void fireBatchEvent(BatchEvent evt) {
  	synchronized (batchListeners) {
  		ArrayList tmpList = (ArrayList) batchListeners.clone();
  		for (Iterator iter = tmpList.iterator(); iter.hasNext();) {
	    	BatchListener listener = (BatchListener) iter.next();
	    	
	    	listener.batchEventPerformed(evt);
	    }
  	}
  }

  /**
   * Increment the batchCount.  This is added for parallel models.
   */

  public void incrementBatchCount(){
    batchCount++;
  }

  /**
   * adds a batch listener to this batch controller
   * 
   * @param listener the listener to remove
   */
  public void addBatchListener(BatchListener listener) {
  	synchronized (batchListeners) {
  		this.batchListeners.add(listener);
  	}
  }
  
  /**
   * removes a batch listener from this controller
   * 
   * @param listener the listener to remove
   */
  public void removeBatchListener(BatchListener listener) {
  	synchronized (batchListeners) {
  		this.batchListeners.remove(listener);
  	}
  }
  
  /**
   * Is this a batch run. Always returns true as this is a BatchController.
   *
   * @return true
   */
  public boolean isBatch() {
    return true;
  }

  /**
   * If this is a GUI controller, always false.
   * 
   * @return false
   */
  public boolean isGUI() {
    return false;
  }
}
