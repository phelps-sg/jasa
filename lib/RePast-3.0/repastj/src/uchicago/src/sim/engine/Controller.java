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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import uchicago.src.reflector.IntrospectPanel;
import uchicago.src.sim.engine.gui.RepastActionPanel;
import uchicago.src.sim.engine.gui.RepastParamPanel;
import uchicago.src.sim.gui.RepastConsole;
import uchicago.src.sim.parameter.ParameterSetter;
import uchicago.src.sim.parameter.ParameterSetterFactory;
import uchicago.src.sim.util.ProbeUtilities;
import uchicago.src.sim.util.SimUtilities;

/**
 * Default GUI control of the simulation. Allows start stop, step, exit, as well as
 * setup to re-run the simulation after a stop. A user will interact with
 * a <code>Controller</code> through the GUI, but, under normal circumstances,
 * should not create one. See {@link uchicago.src.sim.engine.SimInit SimInit}
 * for more on how to run a simulation.<p>
 *
 * A list of the buttons displayed by the controller and what they do follows:
 * <ul>
 * <li>Setup: sets up the simulation for another run</li>
 * <li>Start: starts the simulation when paused or not yet running</li>
 * <li>Step: run one execution cycle of the simulation</li>
 * <li>Stop: stops the simulation</li>
 * <li>Pause: pauses the simulation</li>
 * <li>Exit: stops and exits the simulation</li>
 * </ul>
 *
 * @author Nick Collier 
 * @version $Revision$ $Date$
 */
public class Controller extends AbstractGUIController {

    protected JToolBar toolBar = new JToolBar();
    protected JFrame tbFrame;
    protected JButton btnSetup;
    protected JButton btnStart;
    protected JButton btnStep;
    protected JButton btnStop;
    protected JButton btnPause;
    protected JButton btnExit;
    protected JButton btnLoad;
    protected JButton btnSettings;
    protected JButton btnBegin;
    // used to run all runs as specified in a parameter file.
    protected JButton btnMultRunsStart;
    
    protected RepastParamPanel parameterFilePanel;
    /** 
     * used to do the multi-runs
     */
    protected BatchController batchController;
    
    
    protected JLabel tickCount = new JLabel(" Tick Count: 0.0                ");
    // used to display the current run
    protected JLabel runCountLabel = new JLabel(" Run: 1       ");

    protected JFrame settingsFrame = null;
    protected JTabbedPane tabPane = null;

    protected IntrospectPanel modelPanel = null;
    protected IntrospectPanel simPanel = null;

    protected RepastActionPanel repastPanel = new RepastActionPanel();

    protected ArrayList userButtons = new ArrayList();

    protected ArrayList listeners = new ArrayList();
    protected ArrayList keyListeners = new ArrayList();
    
    /**
     * Constructs a Controller with no associated model, or parameters.
     */
    public Controller() {
        // just to be explicit.
        super();
    }

    /**
     * Constructs a controller whose model has the specified parameters. The
     * model itself is set via setModel().
     *
     * @param parameters default values for the model's parameters
     */

    public Controller(ParameterSetter parameters) {
        super(parameters);
    }

    private void setSettingsEnabled(boolean val) {
        if (isGui) {
            if (modelPanel != null) modelPanel.setEnabled(val);
            simPanel.setEnabled(val);
        }
    }

    /**
     * Sets the model that is controlled by this Controller.
     */
    public void setModel(SimModel model) {
        String title = "Repast";
        super.setModel(model);
        tbFrame = new JFrame(title);
        
        tbFrame.setIconImage(new ImageIcon(
                Controller.class.getResource(
                    "/uchicago/src/sim/images/RepastSmall.gif")).getImage());
        
        setupToolBar();
        setButtonsState(model == null);
        addListeners();
        tickCount.setForeground(Color.blue);
        runCountLabel.setForeground(Color.blue);
    }

    /**
     * Adds a user defined button to the toolbar. This must be called
     * in the model's setup method or the button will not be added.
     *
     * @param label the label for the new JButton
     * @param listener the ActionListener fired when the button is clicked
     */

    public JButton addButton(String label, ActionListener listener) {
        JButton b = new JButton(label);
        int width = b.getPreferredSize().width - 10;
        b.setMinimumSize(new Dimension(width, 31));
        b.setMaximumSize(b.getMinimumSize());
        b.setPreferredSize(b.getMinimumSize());
        b.addActionListener(listener);

        userButtons.add(b);
        return b;
    }

    /**
     * Adds a user defined button to the toolbar. This must be called
     * in the model's setup method or the button will not be added.
     *
     * @param b the JButton to add
     */
    public JButton addButton(JButton b) {
        userButtons.add(b);
        return b;
    }

    /**
     * Adds a user defined button to the toolbar. This must be called
     * in the model's setup method or the button will not be added. The
     * location of the image to create the icon from is given the specified
     * path.
     *
     * @param path the path to image to use as an ImageIcon for the created
     * JButton
     * @param l ActionListener fired when the button is clicked
     */
    public JButton addIconButton(String path, ActionListener l) {
        JButton b = new JButton(new ImageIcon(path));
        b.addActionListener(l);

        userButtons.add(b);
        return b;
    }

    private void setupToolBar() {
        toolBar.setFloatable(false);
    	
        btnLoad =
            new JButton(
                new ImageIcon(
                    Controller.class.getResource(
                        "/uchicago/src/sim/images/Open24.gif")));
        btnLoad.setActionCommand("load");

        btnBegin =
            new JButton(
                new ImageIcon(
                    Controller.class.getResource(
                        "/uchicago/src/sim/images/Redo24.gif")));
        btnBegin.setActionCommand("initialize");

        btnSetup =
            new JButton(
                new ImageIcon(
                    Controller.class.getResource(
                        "/uchicago/src/sim/images/Refresh24.gif")));
        btnSetup.setActionCommand("setup");

        btnStart =
            new JButton(
                new ImageIcon(
                    Controller.class.getResource(
                        "/uchicago/src/sim/images/Play24.gif")));
        btnStart.setActionCommand("start");

        btnStep =
            new JButton(
                new ImageIcon(
                    Controller.class.getResource(
                        "/uchicago/src/sim/images/StepForward24.gif")));
        btnStep.setActionCommand("step");

        btnStop =
            new JButton(
                new ImageIcon(
                    Controller.class.getResource(
                        "/uchicago/src/sim/images/StopSquare24.gif")));
        btnStop.setActionCommand("stop");

        btnPause =
            new JButton(
                new ImageIcon(
                    Controller.class.getResource(
                        "/uchicago/src/sim/images/Pause24.gif")));
        btnPause.setActionCommand("pause");

        btnExit =
            new JButton(
                new ImageIcon(
                    Controller.class.getResource(
                        "/uchicago/src/sim/images/Delete.gif")));
        btnExit.setActionCommand("exit");

        btnSettings =
            new JButton(
                new ImageIcon(
                    Controller.class.getResource(
                        "/uchicago/src/sim/images/TipOfTheDay24.gif")));
        btnSettings.setActionCommand("settings");

        btnMultRunsStart =
            new JButton(
                new ImageIcon(
                    Controller.class.getResource(
                        "/uchicago/src/sim/images/ParameterSweep.gif")));
        btnMultRunsStart.setActionCommand("MultRunsStart");

        
        toolBar.add(btnLoad);
        btnLoad.setToolTipText("Load Model");
        toolBar.addSeparator();
        toolBar.add(btnMultRunsStart);
        btnMultRunsStart.setToolTipText("Multiple Runs Start");
        toolBar.add(btnStart);
        btnStart.setToolTipText("Start");
        toolBar.add(btnStep);
        btnStep.setToolTipText("Step");
        toolBar.add(btnBegin);
        toolBar.add(btnStop);
        btnBegin.setToolTipText("Initialize");
        btnStop.setToolTipText("Stop");
        toolBar.add(btnPause);
        btnPause.setToolTipText("Pause");
        toolBar.addSeparator();

        toolBar.add(btnSetup);
        btnSetup.setToolTipText("Setup Model");

        toolBar.add(btnSettings);
        btnSettings.setToolTipText("View Parameters");

        toolBar.addSeparator();
        btnExit.setMinimumSize(btnPause.getPreferredSize());
        btnExit.setMaximumSize(btnExit.getMinimumSize());
        btnExit.setPreferredSize(btnExit.getMinimumSize());
        toolBar.add(btnExit);
        btnExit.setToolTipText("Exit");

        for (int i = 0; i < userButtons.size(); i++) {
            if (i == 0)
                toolBar.addSeparator();
            toolBar.add((JButton) userButtons.get(i));
        }

        //toolBar.addSeparator();
        //toolBar.add(help);
        //help.setToolTipText("Help");

        toolBar.addSeparator();
        toolBar.add(tickCount);
        toolBar.addSeparator();
        toolBar.add(runCountLabel);

        Dimension d = btnExit.getPreferredSize();
        Dimension td = tickCount.getPreferredSize();
        tickCount.setPreferredSize(new Dimension(td.width + 4, d.height));
        tickCount.setMaximumSize(tickCount.getPreferredSize());
        tickCount.setMinimumSize(tickCount.getPreferredSize());
        Border b = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
        tickCount.setBorder(b);
        tickCount.setText(" Tick Count: 0.0                ");

        td = runCountLabel.getPreferredSize();
        runCountLabel.setPreferredSize(new Dimension(td.width + 4, d.height));
        runCountLabel.setMaximumSize(tickCount.getPreferredSize());
        runCountLabel.setMinimumSize(tickCount.getPreferredSize());
        runCountLabel.setBorder(b);
        tbFrame.getContentPane().add(toolBar);
    }

    private void setButtonsState(boolean modelIsNull) {
        if (!modelIsNull) {
        	btnMultRunsStart.setEnabled(true);
            btnStart.setEnabled(true);
            btnBegin.setEnabled(true);
            btnSetup.setEnabled(false);
            btnStep.setEnabled(true);
            btnPause.setEnabled(false);
            btnStop.setEnabled(false);
            btnSettings.setEnabled(true);
        } else {
        	btnMultRunsStart.setEnabled(false);
            btnBegin.setEnabled(false);
            btnStart.setEnabled(false);
            btnSetup.setEnabled(false);
            btnStep.setEnabled(false);
            btnPause.setEnabled(false);
            btnStop.setEnabled(false);
            btnLoad.setEnabled(true);
            btnSettings.setEnabled(false);
        }
    }

    private void addListeners() {
    	btnMultRunsStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	// build 
            	if (parameterFilePanel == null) {
            		initParamFileOptions();
            	}
            	
           		startMultRunsSim();
            }
        });

    	
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                startSim();
            }
        });

        btnBegin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                beginModel();
            }
        });

        btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                loadModel();
            }
        });

        btnPause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                pauseSim();
            }
        });

        btnSetup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setup();
                setSettingsEnabled(true);
            }
        });

        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                stopSim();
            }
        });

        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exitSim();
            }
        });

        btnStep.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                stepSim();
            }
        });

        btnSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showSettings();
            }
        });

        tbFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                if (exitOnExit)
                    exitSim();
                else
                    shutdown();
            }
        });
    }

    /**
     * loads up the parameter file chooser panel
     */
    protected void initParamFileOptions() {
    	if (parameterFilePanel == null) {
    		parameterFilePanel = new RepastParamPanel(this.model);
    		
    		this.toolBar.add(parameterFilePanel, 3);
    		
    		int originalWidth = this.tbFrame.getWidth();
    		    		
    		tbFrame.pack();
    		
    		tbFrame.setLocation(
    				tbFrame.getX() - (tbFrame.getWidth() - originalWidth),
					tbFrame.getY());
    	}
    }
    
    /**
	 * method called when the triple play button is pushed
	 */
	protected void startMultRunsSim() {
        btnBegin.setEnabled(false);
        btnStart.setEnabled(false);
        btnStep.setEnabled(false);
        btnSetup.setEnabled(false);
        btnPause.setEnabled(false);
        btnSettings.setEnabled(false);
        btnStop.setEnabled(true);
        btnLoad.setEnabled(false);
        setSettingsEnabled(false);
		
		String paramFileName = parameterFilePanel.getParameterFileName();
		
		if (paramFileName != null) {
	        tickCount.setText(" Tick Count: 0");
//	        tickCount.setEnabled(false);
	        
	        if (settingsFrame != null)
	        	settingsFrame.dispose();
	        
	        runCountLabel.setText(" Run: 1       ");
	        
	        runBatch(paramFileName);
		} else {
			this.stopSim();
			SimUtilities.showMessage("You must select a parameter file to perform a Multi-Run\nChoose a parameter file, or use the parameter wizard to build one, or press setup to reset.");
		}
	}
	
	/**
	 * This class handles the multi-runs BatchController's batch messages.
	 * This updates the run label and handle the stopping of the model from the
	 * stop button.
	 */
	class BatchRunHandler implements BatchListener, ActionListener {
		int runCount = 1;
		
		public void batchEventPerformed(BatchEvent evt) {
			if (evt.getType() == BatchEvent.RUN_ENDED) {
				runCount++;
				Controller.this.runCountLabel.setText(" Run: " + runCount);
			} else if (evt.getType() == BatchEvent.BATCH_FINISHED) {
				model.addSimEventListener(Controller.this);
				batchController.removeBatchListener(this);
				model.setController(Controller.this);
				
				Controller.this.stopSim();
				
				Controller.this.btnStop.removeActionListener(this);
			} else if (evt.getType() == BatchEvent.TICK_CHANGED) {
				tickCount.setText("Tick Count:" + evt.getTick());
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == Controller.this.btnStop) {
				Controller.this.batchController.endSim();
			}
		}
	}
	
	private void runBatch(String paramFileName) {
		try {
			if (AbstractGUIController.CONSOLE_ERR
					| AbstractGUIController.CONSOLE_OUT) {
				
				try {
					if (console != null) console.dispose();
				} catch (Exception ex) {
					// oh well
				}
				
				console = null;
				console = new RepastConsole(AbstractGUIController.CONSOLE_OUT,
						AbstractGUIController.CONSOLE_ERR);
				console.display();
			}
		    
			// create a temporary batch controller to handle the batch runs
		    ParameterSetter setter = ParameterSetterFactory.createParameterSetter(paramFileName);
			batchController = new BatchController(setter);

			batchController.setModel(this.getModel());
			model.setController(batchController);
		
			model.removeSimEventListener(this);
			
			// this updates the Run: label
			BatchRunHandler runLabelUpdater = new BatchRunHandler();
			batchController.setExitOnExit(false);
			batchController.addBatchListener(runLabelUpdater);

			Thread batchThread = new Thread() {
				public void run() {
					batchController.begin();
				}
			};
			batchThread.start();
			
			model.setup();
			
			this.btnStop.addActionListener(runLabelUpdater);
			
		} catch (IOException ex) {
			SimUtilities.showError("Error loading parameter file", ex);
		}
	}

	private void loadModel() {
        super.showLoadModelDialog(tbFrame);
        resetParamPanel();
    }

    public void startSim() {
    	enableManipulation(true);
    	btnMultRunsStart.setEnabled(false);
        btnBegin.setEnabled(false);
        btnStart.setEnabled(false);
        btnStep.setEnabled(false);
        btnSetup.setEnabled(false);
        btnPause.setEnabled(true);
        btnStop.setEnabled(true);
        btnLoad.setEnabled(false);
        setSettingsEnabled(false);
        super.startSim();
    }

    public void stopSim() {
    	btnMultRunsStart.setEnabled(true);
        btnSetup.setEnabled(true);
        btnStep.setEnabled(false);
        btnStart.setEnabled(false);
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
        btnLoad.setEnabled(true);
        super.stopSim();
    }

    public void shutdown() {
        super.shutdown();
        settingsFrame.dispose();
        tbFrame.dispose();
    }

    public void showSettings() {
        if (settingsFrame == null) {
            setupParamFrame();
        } else if (!settingsFrame.isVisible()) {
            settingsFrame.setVisible(true);
        } else if (settingsFrame.getState() == JFrame.ICONIFIED) {
            settingsFrame.setState(JFrame.NORMAL);
        } else {
            settingsFrame.requestFocus();
        }
    }

    public void stepSim() {
        super.stepSim();
        setSettingsEnabled(false);
        btnStop.setEnabled(true);
        btnSetup.setEnabled(false);
        btnLoad.setEnabled(false);
    }

    public void beginModel() {
        btnMultRunsStart.setEnabled(false);
        btnBegin.setEnabled(false);
        if (model != null) {
            super.beginModel();
            executeBegin = false;
        }
    }

    public void pauseSim() {
        super.pauseSim();
        btnStart.setEnabled(true);
        btnPause.setEnabled(false);
        btnStep.setEnabled(true);
    }

    private void removeCustomListeners() {
        for (int i = 0; i < listeners.size(); i++) {
            btnStart.removeActionListener((ActionListener) listeners.get(i));
            btnStop.removeActionListener((ActionListener) listeners.get(i));
            btnStep.removeActionListener((ActionListener) listeners.get(i));
            btnPause.removeActionListener((ActionListener) listeners.get(i));
            btnExit.removeActionListener((ActionListener) listeners.get(i));
        }
        listeners.clear();

        for (int i = 0; i < keyListeners.size(); i++) {
            tbFrame.removeKeyListener((KeyListener) keyListeners.get(i));
        }
        keyListeners.clear();
    }

    public void setup() {
        removeCustomListeners();
        super.setup();
        btnMultRunsStart.setEnabled(false);
        btnStart.setEnabled(true);
        btnStep.setEnabled(true);
        tickCount.setText(" Tick Count: 0.0                ");
        runCountLabel.setText(" Run: 1       ");
		tickCount.setEnabled(true);
        btnBegin.setEnabled(true);
        setupParamFrame();
        tabPane.setSelectedIndex(0);
      ProbeUtilities.closeAllProbeWindows();
    }

    /**
     * Updates the tick clock whenever the tick count is incremented
     */
    protected void onTickCountUpdate() {
        tickCount.setText(" Tick Count: " + String.valueOf(time));
        if (UPDATE_PROBES)
            ProbeUtilities.updateProbePanels();
    }

    /**
     * Displays the controller
     */
    public void display() {
        isGui = true;
        tbFrame.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int myWidth = tbFrame.getWidth();
        int x = screenSize.width - myWidth;
        tbFrame.setLocation(x, 10);
        tbFrame.setVisible(true);

        if (model != null) {
            if (params != null) {
                setParameters();
            }
            setupParamFrame();
        }
    }

    private void setupParamFrame() {
        if (settingsFrame == null) {
            //p = settingsFrame.getLocation();
            //settingsFrame.dispose();
            //settingsFrame = null;

            settingsFrame = new JFrame();
            settingsFrame.setIconImage(new ImageIcon(
                    Controller.class.getResource(
                        "/uchicago/src/sim/images/RepastSmall.gif")).getImage());
            
            tabPane = new JTabbedPane();
            Container c = settingsFrame.getContentPane();
            c.setLayout(new BorderLayout());
            c.add(tabPane, BorderLayout.CENTER);
            settingsFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    //System.out.println(settingsFrame.getSize());
                    settingsFrame.dispose();
                }
            });

            addParamPanel();
            addActionPanel();
            addRepastPanel();

            settingsFrame.pack();
            Dimension sfSize = settingsFrame.getSize();
            if (sfSize.width < 240) {
                settingsFrame.setSize(240, sfSize.height);
            }

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int myWidth = settingsFrame.getWidth();
            int toolBarHeight = tbFrame.getHeight();
            int x = screenSize.width - myWidth;
            int toolBarY = tbFrame.getLocation().y;
            int y = toolBarY + toolBarHeight + 10;

            if (sfSize.height > screenSize.height - 150) {
                settingsFrame.setSize(
                    settingsFrame.getSize().width,
                    screenSize.height - 150);
            }

            settingsFrame.setLocation(x, y);
            //}
        } else {
            //settings frame not null;
            modelPanel.reset();
            simPanel.reset();
            addActionPanel();
            //addRepastPanel();
        }
        settingsFrame.setVisible(true);
        settingsFrame.setTitle(model.getName() + " Settings");
    }

    private void addParamPanel() {
        try {
            JPanel panel = new JPanel(new BorderLayout());
            JPanel p = new JPanel(new BorderLayout());
            Border b = BorderFactory.createEtchedBorder();
            JScrollPane sp = new JScrollPane(p);
            ProbeUtilities.clearProbePanels();
            panel.add(sp, BorderLayout.CENTER);

            if (model.getInitParam() != null) {
                modelPanel = super.getModelParameterPanel();
                modelPanel.setBorder(
                    BorderFactory.createTitledBorder(b, "Model Parameters"));
                p.add(modelPanel, BorderLayout.NORTH);
            }
            
            modelPanel.getBeanBowlButton().setText("Inspect Model");

            simPanel = super.getRepastParameterPanel();
            simPanel.setBorder(
                BorderFactory.createTitledBorder(b, "RePast Parameters"));

            p.add(simPanel, BorderLayout.SOUTH);
            tabPane.insertTab("Parameters", null, panel, null, 0);

        } catch (Exception ex) {
            SimUtilities.showError("Unable to display model parameters", ex);
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private void resetParamPanel() {
        if (modelPanel != null) {
            tabPane.remove(0);
            addParamPanel();
            settingsFrame.pack();
        }
    }

    private void addRepastPanel() {
        repastPanel.clear();

        repastPanel.addButton("Make Movie", new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showMakeMovieDialog(tbFrame);
            }
        });

        repastPanel.addButton("Take Snapshot", new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Action buttonAction = showSnapshotDialog(tbFrame);
                if (buttonAction != null) {
                    repastPanel.addButton(buttonAction);
                    repastPanel.revalidate();
                    settingsFrame.invalidate();
                    settingsFrame.pack();
                    settingsFrame.repaint();
                }
            }
        });

        repastPanel.addButton("Create / Edit Charts", new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showChartDialog(tbFrame);
            }
        });

        repastPanel.addCheckBox("In Alpha Order", new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JCheckBox box = (JCheckBox) evt.getSource();
                Controller.ALPHA_ORDER = box.isSelected();
                modelPanel.setAlphaOrder(Controller.ALPHA_ORDER);
                try {
                    modelPanel.redraw();
                } catch (Exception ex) {
                    SimUtilities.showError("Error querying parameters", ex);
                    System.exit(0);
                }
            }
        }, Controller.ALPHA_ORDER);

        repastPanel.addCheckBox("Stdout to Console", new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JCheckBox box = (JCheckBox) evt.getSource();
                AbstractGUIController.CONSOLE_OUT = box.isSelected();
            }
        }, Controller.CONSOLE_OUT);

        repastPanel.addCheckBox("Stderr to Console", new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JCheckBox box = (JCheckBox) evt.getSource();
                AbstractGUIController.CONSOLE_ERR = box.isSelected();
            }
        }, Controller.CONSOLE_ERR);

        if (tabPane.getTabCount() == 2) {
            tabPane.addTab("Repast Actions", repastPanel);
        }

        repastPanel.addCheckBox("Rng Seed in Defaults", new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JCheckBox box = (JCheckBox) evt.getSource();
                Controller.DEFAULTS_INCLUDE_RNGSEED = box.isSelected();
            }
        }, Controller.DEFAULTS_INCLUDE_RNGSEED);

        repastPanel.addCheckBox("Update Probes", new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JCheckBox box = (JCheckBox) evt.getSource();
                Controller.UPDATE_PROBES = box.isSelected();
            }
        }, Controller.UPDATE_PROBES);

        repastPanel.addCheckBox("Show Custom Charts", new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JCheckBox box = (JCheckBox) evt.getSource();
                Controller.SHOW_CHARTS = box.isSelected();
            }
        }, Controller.SHOW_CHARTS);

        repastPanel.addButton("Set As Default", new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                makeCurrentParamsDefault();
            }
        });

        repastPanel.addButton("Write Parameters", new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showWriteParamsDialog(tbFrame);
            }
        });

        repastPanel.addButton("About", new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showVersion();
            }
        });

    }

    /**
     * Returns the JFrame for controller tool bar.
     */
    public JFrame getFrame() {
        return tbFrame;
    }

    /**
     * Returns the tool bar containing the stop, start etc. buttons.
     */
    public JToolBar getToolBar() {
        return toolBar;
    }

    /**
     * Returns the JTabbedPane that contains the parameter panel etc.
     */
    public JTabbedPane getTabPane() {
        return tabPane;
    }

    private void addActionPanel() {
        if (tabPane.getTabCount() > 1) {
            // bug in java 1.2.2 - remove(int index) doesn't work
            //tabPane.remove(ACTION_TAB_INDEX);
            tabPane.remove(tabPane.getComponentAt(1));
        }
        JPanel p = new JPanel(new BorderLayout());
        tabPane.insertTab("Custom Actions", null, p, null, 1);
        ModelManipulator mm = model.getModelManipulator();
        mm.setEnabled(false);
        p.add(mm.getPanel(), BorderLayout.CENTER);

    }

    /**
     * Adds an ActionListener listening for start button presses. The
     * source for the event fired here will be the start button with an
     * actionCommand of "start".
     *
     * @param listener the listener to listen for button clicks.
     */
    public void addStartListener(ActionListener listener) {
        listeners.add(listener);
        btnStart.addActionListener(listener);
    }

    /**
     * Adds an ActionListener listening for stop button presses. The
     * source for the event fired here will be the stop button with an
     * actionCommand of "stop".
     *
     * @param listener the listener to listen for button clicks.
     */
    public void addStopListener(ActionListener listener) {
        listeners.add(listener);
        btnStop.addActionListener(listener);
    }

    /**
     * Adds an ActionListener listening for pause button presses. The
     * source for the event fired here will be the pause button with an
     * actionCommand of "pause".
     *
     * @param listener the listener to listen for button clicks.
     */
    public void addPauseListener(ActionListener listener) {
        listeners.add(listener);
        btnPause.addActionListener(listener);
    }

    /**
     * Adds an ActionListener listening for exit button presses. The
     * source for the event fired here will be the exit button with an
     * actionCommand of "exit".
     *
     * @param listener the listener to listen for button clicks.
     */
    public void addExitListener(ActionListener listener) {
        listeners.add(listener);
        btnExit.addActionListener(listener);
    }

    /**
     * Adds an ActionListener listening for step button presses. The
     * source for the event fired here will be the step button with an
     * actionCommand of "step".
     *
     * @param listener the listener to listen for button clicks.
     */
    public void addStepListener(ActionListener listener) {
        listeners.add(listener);
        btnStep.addActionListener(listener);
    }

    /**
     * Adds a KeyListener listening for any keyboard action when the
     * control toolbar has focus.
     *
     * @param listener the listener to listen for keyboard events
     */
    public void addKeyListener(KeyListener listener) {
        keyListeners.add(listener);
        tbFrame.addKeyListener(listener);
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
            setSettingsEnabled(true);
        } else if (evt.getId() == SimEvent.END_EVENT) {
            exitSimulation();
        } else if (evt.getId() == SimEvent.RNG_SEED_EVENT) {
            if (simPanel != null) {
                simPanel.reset();
            }
        } else if (evt.getId() == SimEvent.PAUSE_EVENT) {
            pauseSimulation();
            setSettingsEnabled(true);
        }
    }
}
