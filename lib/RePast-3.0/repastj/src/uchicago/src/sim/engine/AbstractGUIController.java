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
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import uchicago.src.reflector.IntrospectPanel;
import uchicago.src.sim.analysis.AbstractChartModel;
import uchicago.src.sim.analysis.ChartArchiver;
import uchicago.src.sim.analysis.CustomChartGui;
import uchicago.src.sim.analysis.plot.OpenGraph;
import uchicago.src.sim.engine.gui.MakeMovieDialog;
import uchicago.src.sim.engine.gui.SnapshotDialog;
import uchicago.src.sim.gui.DisplayConstants;
import uchicago.src.sim.gui.FrameFactory;
import uchicago.src.sim.gui.ProducerNamePair;
import uchicago.src.sim.gui.RepastConsole;
import uchicago.src.sim.parameter.DefaultParameterSetter;
import uchicago.src.sim.parameter.ParameterSetter;
import uchicago.src.sim.parameter.ParameterSetterFactory;
import uchicago.src.sim.parameter.ParameterUtility;
import uchicago.src.sim.util.ProbeUtilities;
import uchicago.src.sim.util.SimUtilities;

/**
 * Base class for GUI controllers. This provides access to various dialogs
 * and JPanels that can be used subclassses creating full-blown GUI controllers.
 * 
 * @version $Revision$ $Date$
 */

public abstract class AbstractGUIController extends BaseController {

  public static boolean SHOW_CHARTS = true;
  public static boolean ALPHA_ORDER = true;
  public static boolean CONSOLE_OUT = true;
  public static boolean CONSOLE_ERR = true;
  public static boolean DEFAULTS_INCLUDE_RNGSEED = false;
  public static boolean UPDATE_PROBES = false;

  /**
   * Repast parameters.
   */
  protected String[] myParams = {"PauseAt", "RandomSeed", "CellWidth",
                                 "CellHeight", "CellDepth"};

  /**
   * Lists of MediaSchedulers used to schedule media actions (taking
   * snapshots etc.)
   */
  protected Vector mediaSchedulers = new Vector();

  protected ArrayList chartModels = new ArrayList();
  protected ArrayList customCharts = new ArrayList();
  protected RepastConsole console = null;

  /**
   * Constructs an AbstractGUIController with no associated model, or parameters.
   */
  public AbstractGUIController() {
    ParameterUtility.createInstance(new DefaultParameterSetter());
  }

  /**
   * Constructs a AbstractGUIController whose model has the specified parameters. The
   * model itself is set via setModel().
   * 
   * @param parameters default values for the model's parameters
   */

  public AbstractGUIController(ParameterSetter parameters) {

    ParameterUtility.createInstance(parameters);
    params = parameters;

    /*
    if (parameters != null) {
      params = new Vector();
      flattenParameters(parameters);
    }
    */
  }


  public void setConsoleOut(boolean out) {
    AbstractGUIController.CONSOLE_OUT = out;
  }

  public boolean getConsoleOut() {
    return AbstractGUIController.CONSOLE_OUT;
  }

  public void setConsoleErr(boolean err) {
    AbstractGUIController.CONSOLE_ERR = err;
  }

  public boolean getConsoleErr() {
    return AbstractGUIController.CONSOLE_ERR;
  }


  /**
   * Sets the model that is controlled by this Controller.
   */
  public void setModel(SimModel model) {
    if (model != null) {
      chartModels = ChartArchiver.loadCharts(model);
    }
    super.setModel(model);
  }

  /**
   * Displays the LoadModelDialog and loads the selected SimModel.
   * 
   * @param frame the parent frame for the LoadModelDialog
   */
  public void showLoadModelDialog(JFrame frame) {

    LoadModelDialog dialog = new LoadModelDialog();
    dialog.display(frame);

    if (dialog.getModel() != null) {
      stopSimulation();


      if (model != null) {
        model.setup();
        model = null;
      }


      // we need to set these to their default values because
      // the load model dialog calls the constructors of any SimModel's
      // it finds. Those constructors may have screwed up these values.
      DisplayConstants.CELL_WIDTH = 5;
      DisplayConstants.CELL_HEIGHT = 5;
      AbstractGUIController.SHOW_CHARTS = true;
      AbstractGUIController.ALPHA_ORDER = true;
      AbstractGUIController.DEFAULTS_INCLUDE_RNGSEED = false;
      AbstractGUIController.UPDATE_PROBES = false;

      model = dialog.getModel();
      params = null;
      try {
        String parameterFile = dialog.getParameterFile();
        if (parameterFile.trim().length() > 0) {
          params = ParameterSetterFactory.createParameterSetter(dialog.getParameterFile());
          ParameterUtility.createInstance(params);
        }
      } catch (Exception ex) {
        SimUtilities.showError("Error reading parameter file", ex);
        System.exit(0);
      }

      FrameFactory.load(model.getClass().getName());
      model.setController(AbstractGUIController.this);
      model.addSimEventListener(AbstractGUIController.this);

      frame.setTitle(model.getName());
      chartModels = ChartArchiver.loadCharts(model);
      model.setup();
      setup();
    }
  }

  /**
   * Executes begin() on the SimModel associated with this
   * AbstractGUIController. This is useful for displaying and recording
   * the state of the model before any scheduled actions are executed. In
   * addition this will displays any custom charts associated with this model
   * if that option is selected.
   */
  public void beginModel() {
    
    if (AbstractGUIController.CONSOLE_ERR | AbstractGUIController.CONSOLE_OUT) {
      console = null;
      console = new RepastConsole(AbstractGUIController.CONSOLE_OUT, AbstractGUIController.CONSOLE_ERR);
      console.display();
    }
    
    super.beginModel();
    if (AbstractGUIController.SHOW_CHARTS) {
      for (int i = 0, n = chartModels.size(); i < n; i++) {
        AbstractChartModel chartModel = (AbstractChartModel) chartModels.get(i);
        OpenGraph graph = chartModel.createChart();
        Schedule sch = model.getSchedule();
        sch.scheduleActionAtInterval(sch.repeatInterval, graph, "step",
                Schedule.LAST);
        customCharts.add(graph);
        graph.display();
      }
    }    
    executeBegin = false;
  }

  /**
   * Steps the simulation through one iteration of execution.
   */
  public void stepSim() {
    doStep = true;
    pauseSim = false;
    go = true;
    enableManipulation(true);

    if (runThread == null) {
      if (executeBegin) beginModel();
      schedule = model.getSchedule();
      setupSchedule();
      runThread = new Thread(simRun);
      runThread.start();
    }

    notifyMonitor();
  }

  /**
   * Loads any default parameters and starts the simulation. This is a
   * convenient way to start a "gui" simulation without waiting for
   * user input or even showing the gui.
   */
  public void loadAndStart() {
    if (params != null) {
      setParameters();
    }

    startSim();
  }


  /**
   * Sets up this controller and is associated SimModel prior to a
   * model run.
   */
  public void setup() {
    if (runThread != null) {
      runThread.interrupt();
      try {
        runThread.join();
      } catch (InterruptedException ex) {
        System.out.println("Interrupted");
      }
    }

    model.clearMediaProducers();
    model.generateNewSeed();
    model.setup();
    for (int i = 0, n = customCharts.size(); i < n; i++) {
      ((OpenGraph) customCharts.get(i)).dispose();
    }

    if (params != null) {
      setParameters();
    }

    runThread = null;
    executeBegin = true;
    setMediaSchedulers();
    if (console != null) {
      console.dispose();
      console = null;
    }
  }

  /*
  * Ensures that any buttons created for media capture are updated with
  * the current handle to the new MediaProducer objects
  */
  private void setMediaSchedulers() {
    if (mediaSchedulers.size() > 0) {
      Vector displays = model.getMediaProducers();
      for (int i = 0; i < mediaSchedulers.size(); i++) {
        MediaScheduler s = (MediaScheduler) mediaSchedulers.get(i);
        for (int j = 0; j < displays.size(); j++) {
          ProducerNamePair pnp = (ProducerNamePair) displays.get(j);
          if (pnp.getName() == s.getName()) {
            s.setProducer(pnp.getProducer());
            break;
          }
        }
      }
    }
  }


  /**
   * Exits the simulation. This saves chart and display locations and size,
   * as well as any custom models.
   */
  public void exitSim() {
    if (exitOnExit) {
      stopSim();
      if (runThread != null) {
        runThread.interrupt();
        try {
          runThread.join();
        } catch (InterruptedException ex) {
        }
      }

      if (model != null) {
        String modelName = model.getClass().getName();
        FrameFactory.saveFrameData(modelName);
        ChartArchiver.saveCharts(modelName, chartModels);
      }
      if (console != null) {
        console.dispose();
        console = null;
      }

      System.exit(0);
    } else {
      shutdown();
    }
  }

  /**
   * Exits the simulation but does not exit the jvm. This is useful if you
   * run a simulation in from some other application within the same jvm
   * instance.
   */
  public void shutdown() {
    stopSim();
    model.setup();

    if (model != null) {
      String modelName = model.getClass().getName();
      FrameFactory.saveFrameData(modelName);
      ChartArchiver.saveCharts(modelName, chartModels);
    }

    model = null;
    System.gc();
    if (console != null) {
      console.dispose();
      console = null;
    }

  }


  /**
   * Updates the tick clock whenever the tick count is incremented
   */
  protected void onTickCountUpdate() {
    if (UPDATE_PROBES) ProbeUtilities.updateProbePanels();
  }

  /**
   * Enables the user customized actions.
   * 
   * @param enable 
   */
  public void enableManipulation(boolean enable) {
    ModelManipulator mm = model.getModelManipulator();
    mm.setEnabled(enable);
  }

  /**
   * Stops the simulation. Note that the simulation will continue until the
   * next time the looping condition is checked. Consequently, if the running
   * loop begins its next iteration before the stop condition is received then
   * an additional iteration of the simulation will occur.
   */
  public void stopSimulation() {
    stopSim();
  }

  /**
   * Pauses the simulation.
   */
  public void pauseSimulation() {
    pauseSim();
  }

  /**
   * Exits the simulation.
   */
  public void exitSimulation() {
    exitSim();
  }


  private String[] makeModelParams(boolean includeRngSeed) {
    // assumes parameters is not null

    String[] mParams = model.getInitParam();
    if (includeRngSeed) {
      String[] allParams = new String[mParams.length + myParams.length + 1];

      System.arraycopy(mParams, 0, allParams, 0, mParams.length);
      System.arraycopy(myParams, 0, allParams, mParams.length, myParams.length);
      allParams[allParams.length - 1] = "RngSeed";

      return allParams;
    }

    return mParams;
  }

  /**
   * Sets the parameters of this AbstractGUIController's SimModel to their
   * default values, if any. These default values are the values specified
   * in a parameter file when repast was loaded or the values recorded
   * when the makeCurParamsDefault method is called.
   */
  protected void setParameters() {
    params.setModelParameters(model);
    /*
    String[] allParams = makeModelParams(true);
    Introspector i = new Introspector();
    try {
      i.introspect(model, allParams);
    } catch (IntrospectionException ex) {
      String msg = "Fatal Error setting model parameters";
      System.out.println(msg);
      SimUtilities.logException(msg, ex);
      ex.printStackTrace();
      System.exit(0);
    }

    for (int j = 0; j < params.size(); j++) {
      Parameter p = (Parameter) params.get(j);
      try {
        i.invokeSetMethod(p.name, p.getValue());
      } catch (InvocationTargetException ex) {
        SimUtilities.showError("Unable to set model parameter " + p.getName(), ex);
        System.exit(0);
      } catch (IllegalAccessException ex) {
        SimUtilities.showError("Unable to set model parameter " + p.getName(), ex);
        System.exit(0);
      } catch (NullPointerException ex) {
        SimUtilities.showError("Unable to set model parameter " + p.getName(), ex);
        System.exit(0);
      }
    }
    */
  }

  /**
   * Creates an IntrospectPanel that contains the parameters this AbstractGUIController's
   * SimModel. This is a gui representation of a SimModel's parameters.
   */
  public IntrospectPanel getModelParameterPanel() {
    IntrospectPanel modelPanel = null;
    try {

      modelPanel = new IntrospectPanel(model, model.getInitParam(), ALPHA_ORDER);
      ProbeUtilities.addModelProbePanel(modelPanel);
    } catch (Exception ex) {
      SimUtilities.showError("Unable to create model parameter panel", ex);
      ex.printStackTrace();
      System.exit(0);

    }

    return modelPanel;
  }

  /**
   * Creates an IntrospectPanel that contains the parameters for all repast
   * models.
   */
  public IntrospectPanel getRepastParameterPanel() {
    IntrospectPanel simPanel = null;
    try {
      simPanel = new IntrospectPanel(this, myParams, true);
      if (simPanel.getBeanBowlButton() != null)
      	simPanel.remove(simPanel.getBeanBowlButton());
    } catch (Exception ex) {
      SimUtilities.showError("Unable to display repast parameters", ex);
      ex.printStackTrace();
      System.exit(0);
    }

    return simPanel;
  }

  /**
   * Displays the dialog for taking snapshot images. Also deals appropriately
   * with the results of the dialog, scheduling image capture appropriately.
   * <b>Note</b> it is the caller's responsiblity to deal with the returned
   * Action if necessary. For example, by creating a JButton.
   * 
   * @param f the parent frame for the dialog
   * @return An Action that when executed takes a snapshot. This will be null
   *         if the button snapshot option in the dialog is not selected.
   */

  public Action showSnapshotDialog(JFrame f) {
    Vector displays = model.getMediaProducers();
    if (displays.size() == 0) {
      SimUtilities.showMessage("<html><b><font color=black size=-1>No DisplaySurface or MediaProducers registered.<p>" +
              "Use registerDisplaySurface(...)  or registerMediaProducer(...) in model's setup method</font></b></html>");
      return null;
    }

    SnapshotDialog dialog = new SnapshotDialog(displays);
    dialog.display(f, "Snapshot Setup");
    SnapshotScheduler ss = dialog.scheduleSnapshot(model.getSchedule());

    if (ss != null) {
      mediaSchedulers.add(ss);
      return ss.getButtonAction();
    }

    return null;
  }

  /**
   * Displays a dialog for creating movies of the simulation. This also
   * deals with the results of that dialog appropriately, scheduling frame
   * capture and so forth.
   * 
   * @param frame the parent frame for the dialog
   */
  public void showMakeMovieDialog(JFrame frame) {
    Vector displays = model.getMediaProducers();
    if (displays.size() == 0) {
      SimUtilities.showMessage("<html><b><font color=black size=-1>No DisplaySurface registered.<p>" +
              "Use registerDisplaySurface(...) in model's setup method</font></b></html>");
      return;
    }

    MakeMovieDialog dialog = new MakeMovieDialog(displays);
    dialog.display(frame, "Make Movie");
    dialog.scheduleMovie(model.getSchedule());
  }


  /**
   * Displays a message box showing the version number of repast.
   */
  public void showVersion() {
    String version = "Repast J Version: " + SimInit.VERSION;
    JFrame f = new JFrame();
    JOptionPane.showMessageDialog(f, version);
    f.dispose();
  }


  /**
   * Makes the SimModel's current parameters the default parameters. These
   * parameters will then be used as the default parameters when setup
   * is performed.
   */
  public void makeCurrentParamsDefault() {
    try {
      ParameterUtility pu = ParameterUtility.getInstance();
      String[] ps = makeModelParams(AbstractGUIController.DEFAULTS_INCLUDE_RNGSEED);
      params = pu.createParameters(model, ps);
    } catch (Exception ex) {
      SimUtilities.showError("Error setting default parameters", ex);
      System.exit(0);
    }
  }


  /**
   * Displays a dialog for writing the SimModel's parameters to a file.
   * 
   * @param f the parent frame for this dialog
   */
  public void showWriteParamsDialog(JFrame f) {
    try {
      JFileChooser fd = new JFileChooser();
      fd.setDialogTitle("Save Parameters");
      int res = fd.showSaveDialog(f);
      if (res == JFileChooser.APPROVE_OPTION) {
        String file = fd.getSelectedFile().getAbsolutePath();
        ParameterUtility.getInstance().makeParameterFileFromCurVals(model,
                makeModelParams(AbstractGUIController.DEFAULTS_INCLUDE_RNGSEED), file);
      }
    } catch (Exception ex) {
      SimUtilities.showError("Error writing parameters to file", ex);
      System.exit(0);
    }
  }

  /**
   * Displays a dialog for creating custom charts. This also deals with the
   * results of that dialog, creating the actual charts and so forth.
   * 
   * @param f 
   */
  public void showChartDialog(JFrame f) {
    if (model != null) {
      CustomChartGui g = new CustomChartGui(model, chartModels);
      g.display(f);
      if (g.getModels() != null) {
        chartModels = g.getModels();
      }
    } else {
      SimUtilities.showMessage("Cannot create a custom chart without a model");
    }
  }


// SimEventListener interface
  /**
   * Invoked by a source on which this is a listener when a
   * <code>SimEvent</code> is fired. <code>Controller</code> listens
   * for stop, end, and rng_seed events. Stop will stop the
   * simulation, end will end the simluation and an rng_seed event
   * will update the model panel with the new rng seed value.
   */
  public void simEventPerformed(SimEvent evt) {
    if (evt.getId() == SimEvent.STOP_EVENT) {
      stopSimulation();
    } else if (evt.getId() == SimEvent.END_EVENT) {
      exitSimulation();
    } else if (evt.getId() == SimEvent.PAUSE_EVENT) {
      pauseSimulation();
    }
  }

  /**
   * Returns the current run count. GUI Controllers do not count runs and thus
   * this always returns 1.
   * 
   * @return 
   */
  public long getRunCount() {
    return 1;
  }


  /**
   * Returns true;
   */
  public boolean isGUI() {
    return true;
  }

  public boolean isBatch() {
    return false;
  }
}




