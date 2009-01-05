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
 * Provides minimal (thin) controller support for running a simulation.
 * ThinController is intended to provide batch-like non-gui programmatic control over
 * a simulation for those who want to do batch type runs without using
 * RePast's batch run mechansim. Thin controller thus provides methods to
 * start, stop, pause and exit a simulation, but virtually nothing else. In
 * particular loading parameters into a model is not done here. Loading
 * parameters using some sort of custom parameter file or mechanism will have
 * to be done elsewhere.<p>
 *
 * ThinController can be used in conjuntion with a tool like Drone (drone.sf.net)
 * where an instance of the model and a controller are created for each run
 * of the model. The main method of a model might then look like:
 * <pre><code>
 *  public static void main( String[] args ) {
 *    MyModel model = new MyModel();
 *    ThinController controller = new ThinController();
 *    model.setController(controller);
 *    controller.setExitOnExit(true);
 *    controller.setModel(model);
 *    model.addSimEventListener(controller);
 *    // custom parameter loading here ...
 *    ...
 *    control.startSimulation();
 * }
 * </code></pre>
 *
 * @version $Revision$ $Date$
 *
 */
public class ThinController extends BaseController {

  protected boolean isGui;

  /**
   * Creates a ThinController, specifying whether this is a graphical controller
   * or not. The isGui parameter only determines what value is returned by the
   * isGUI method, nothing more.
   *
   * @param isGui whether or not this ThinController will identify itself as
   * a gui controller or not
   */
  public ThinController(boolean isGui) {
    super();
    this.isGui = isGui;
  }

  /**
   * Exits a simulation.
   */
  public void exitSim() {
    stopSim();
    if (runThread != null) {
      runThread.interrupt();
      try {
        runThread.join();
      } catch (InterruptedException ex) {
        System.out.println("Interrupted");
      }
    }

    fireSimEvent(new SimEvent(this, SimEvent.END_EVENT));
    if (exitOnExit) System.exit(0);
  }

  protected void onTickCountUpdate() {}

  /**
   * Listens for SimEvents and responds accordingly.
   * @param evt
   */
  public void simEventPerformed(SimEvent evt) {
    if (evt.getId() == SimEvent.STOP_EVENT) {
      stopSim();
    } else if (evt.getId() == SimEvent.END_EVENT) {
      exitSim();
    } else if (evt.getId() == SimEvent.PAUSE_EVENT) {
      pauseSim();
    }
  }

  /**
   * Returns true if this ThinController is a gui controller. The actual
   * value returned depends the value passed to the contructor.
   */
  public boolean isGUI() {
    return isGui;
  }

  /**
   * Returns true if this ThinController is a batch controller. The actual
   * value returned depends the value passed to the contructor.
   */
  public boolean isBatch() {
    return !isGui;
  }

  /**
   * Always returns 1.
   */
  public long getRunCount() {
    return 1;
  }
}

