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

import javax.swing.UIManager;

import uchicago.src.sim.gui.FrameFactory;
import uchicago.src.sim.parameter.ParameterSetter;
import uchicago.src.sim.parameter.ParameterSetterFactory;
import uchicago.src.sim.util.SimUtilities;

/**
 * Creates and initializes a simulation. A RePast model (one that implements
 * the SimModel interface) can either use this class to load themselves, or
 * be loaded by this class and associated with the appropriate controller.
 * In the first case, a RePast model would create and instance of itself
 * and SimInit in a main method, and the load itself using SimInits.load
 * method. For example,
 * <code><pre>
 * public static void main(String[] args) {
 *   MyModel model = new MyModel();
 *   SimInit init = new SimInit();
 *   init.load(model, null, false);
 * }
 * </pre></code>
 * And the model name then passed as argument to "java" on the command line,
 * <code><pre>
 * java -cp ... mymodels.MyModel
 * </pre></code>
 *
 * Model's loaded this way can load parameter files and specify whether
 * they are batch models are not. For how to do this, see the load definition
 * below.<p>
 *
 * Using SimInit to load your model works as follows. SimInit is passed as
 * an argument on the command line to "java" and your fully qualified
 * model name should follow "uchicago.src.sim.engine.SimInit". Two option
 * switch can be passed to SimInit that determine how SimInit will treat your
 * model.<p>
 * <ul>
 * <li> -b indicates that this is a batch model and that full name of
 * a parameter file will follow the fully qualified model name.</li>
 * <li> -ng indicates that this is a gui model that you wish to run without
 * the gui Controller. SimInit will start your model for you, but it is up
 * to the model to stop itself. This switch is generally not useful except
 * for automated testing.</li>
 * <li> -v displays the current version number and then exits.</li>
 * </ul>
 *
 * With no switches, RePast assumes your model is a gui model (that is,
 * you want to start and stop it via the graphical toolbar). You can
 * pass your gui model the full name of a parameter file as well, and these
 * will be treated as default model parameters. For more on parameter files,
 * see {@link uchicago.src.sim.parameter.ParameterReader ParameterReader} and
 * the Repast how to documentation.<p>
 *
 * Some sample ways to start the model models.MyModel. The model loading
 * itself:<p>
 *
 * java -cp ... models.MyModel<p>
 *
 * Loading a models.MyModel as a batch model via SimInit<p>
 *
 * java -cp ...uchicago.src.sim.engine.SimInit -b models.MyModel my_params
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see Controller
 * @see BatchController
 * @see BaseController
 */
public class SimInit {

  public static final String VERSION = "3.0";

  private static boolean noGui = false;
  private boolean exitOnExit = true;

  private static final String USAGE =
          "\nUsage:\t [-ng -b] [fully_qualified_name_of_model_class] [parameter file | run count]\n" +
          "e.g java -cp .;sim.jar uchicago.src.sim.SimInit uchicago.src.sim.heatBugs.HeatBugModel\n";

  private int numRuns = 1;

  private void open() {
    Controller control = new Controller();
    control.setModel(null);
    control.display();
  }

  private void open(String modelClass) throws IllegalAccessException {
    open(modelClass, false, "");
  }

  /**
   * Sets the number of runs in a batch run. Note that is only applies
   * when doing a batch run with no parameter file. Otherwise, the
   * parameter space defined by the parameter file determines how many
   * runs will occur.
   *
   * @param numRuns the number of runs to perform
   */
  public void setNumRuns(int numRuns) {
    this.numRuns = numRuns;
  }

  private void open(String modelClass, boolean isBatch, String fileName)
          throws IllegalAccessException {
    SimModel model = null;
    try {
      Class c = Class.forName(modelClass);
      model = (SimModel) c.newInstance();
    } catch (ClassCastException ex) {
      throw new IllegalArgumentException("Model does not implement the SimModel interface");

    } catch (ClassNotFoundException ex1) {
      throw new IllegalArgumentException("Can't find " + modelClass);

    } catch (InstantiationException ex2) {
      throw new IllegalArgumentException("Can't instantiate " + modelClass);
    }

    load(model, isBatch, fileName);
  }

  private void load(SimModel model, boolean isBatch, String fileName) {
  	load(model, isBatch, fileName, true);
  }
  
  private void load(SimModel model, boolean isBatch, String fileName, boolean autoRecordParameters) {
	// Load the system specific look and feel
	try {
		String nativeLF = UIManager.getSystemLookAndFeelClassName();
		UIManager.setLookAndFeel(nativeLF);
	} catch (Exception e) {
		// oh well, couldn't load another look and feel
	}
  	if (!isBatch) {
     FrameFactory.load(model.getClass().getName());
      Controller control = null;
      if (fileName.length() == 0) {
        control = new Controller();
      } else {
        ParameterSetter setter = null;
        try {
          setter = ParameterSetterFactory.createParameterSetter(fileName);
        } catch (Exception ex) {
          SimUtilities.showError("Error reading parameter file", ex);
          if (exitOnExit) System.exit(0);
          else throw new RuntimeException("Error reading parameter file");
        }

        control = new Controller(setter);
      }
      model.setController(control);
      control.setExitOnExit(exitOnExit);
      control.setModel(model);
      model.addSimEventListener(control);
      //model.setup();
      if (noGui) {
        control.loadAndStart();
      } else {
        control.display();
      }
    } else {
      ParameterSetter setter = null;
      if (fileName != null && fileName.trim().length() > 0) {
        try {
          setter = ParameterSetterFactory.createParameterSetter(fileName);
          //reader = new ParameterReader(fileName);
        } catch (Exception ex) {
          ex.printStackTrace();
          SimUtilities.showError("Initialization error", ex);
          if (exitOnExit) System.exit(0);
          else throw new RuntimeException("Error reading parameter file");
        }
      } else {
        // batch mode but no batch file, so run once with current parameters
        setter = ParameterSetterFactory.createSingleSetParameterSetter(numRuns);
      }

      BatchController control = new BatchController(setter);
      model.setController(control);
      control.setModel(model);
      control.begin();
    }
  }

  /**
   * Loads the specified model as if it was given on the command line.
   * RePast models can use this to load themselves in their own main
   * methods. The fileName is optional if this is not a batch simulation. If
   * specified the parameters and their values specified in the parameter file
   * will become the default parameters for the model.
   *
   * @param model the model to load
   * @param fileName the name of a parameter file. This can be null, or an
   * empty String if no parameter file is desired.
   * @param isBatch whether this is a batch simulation or not.
   */
  public void loadModel(SimModel model, String fileName, boolean isBatch) {
    if (fileName == null) {
      fileName = "";
    }
    load(model, isBatch, fileName);
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

  public static void main(String[] args) {
	// Load the system specific look and feel
	try {
		String nativeLF = UIManager.getSystemLookAndFeelClassName();
		UIManager.setLookAndFeel(nativeLF);
	} catch (Exception e) {
		// oh well, couldn't load another look and feel
	}
  	SimInit si = new SimInit();
    try {

      

      if (args.length == 0) {
        si.open();
        //System.out.println(USAGE);
        //System.exit(0);
      } else if (args[0].startsWith("-")) {
        if (args[0].equals("-ng")) {
          noGui = true;
          si.open(args[1]);
        } else if (args[0].equals("-v")) {
          System.out.println("RePast Version: " + SimInit.VERSION);
          System.exit(0);
        } else if (args[0].equals("-b")) {
          // batch mode
          if (args.length < 2) {
            System.out.println("Model argument is missing.\n" +
                               "Specify a model class name");
            System.exit(0);
          } else if (args.length == 2) {
            si.open(args[1], true, "");

          } else {
            // is the 3rd argument a number or a string representing a
            // parameter file
            try {
              si.numRuns = Integer.parseInt(args[2]);
              si.open(args[1], true, "");
            } catch (NumberFormatException ex) {
              si.open(args[1], true, args[2]);
            }

          }

        } else {
          System.out.println("Invalid option");
          System.out.println(USAGE);
          if (si.exitOnExit) System.exit(0);
        }
      } else if (args.length > 1) {
        si.open(args[0], false, args[1]);
      } else {
        si.open(args[0]);
      }
    } catch (Exception ex) {
      SimUtilities.showError("Initialization error", ex);
      ex.printStackTrace();
      if (si.exitOnExit) System.exit(0);
    }
  }
}
