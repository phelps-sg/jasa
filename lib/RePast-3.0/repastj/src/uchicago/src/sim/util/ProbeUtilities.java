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
package uchicago.src.sim.util;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import uchicago.src.reflector.IntrospectFrame;
import uchicago.src.reflector.IntrospectPanel;
import uchicago.src.sim.engine.CustomProbeable;
import uchicago.src.sim.gui.Named;

/**
 * Static utility methods for creating and working with Probes.
 *
 * @version $Revision$ $Date$
 */
public class ProbeUtilities {

  private static HashMap probePanelMap = new HashMap();
  
  private static IntrospectPanel modelPanel;
  
  private static ArrayList probedList	= new ArrayList();
  private static ArrayList listeners	= new ArrayList();
  
  private static int newWindowXOffset	= 0;
  private static int newWindowYOffset	= 0;
  
  /**
   * Probes an object, where probing means to display an Object's
   * properties via get and set methods. The property name and value
   * will be displayed in a window. The values can be edited when
   * appropriate.
   *
   * @param o the Object to be probed.
   */
  public static void probe(final Object o) {
	// bring the panel to front if the object is already being probed
	if (probePanelMap.containsKey(o)) {
		IntrospectPanel panel = (IntrospectPanel) probePanelMap.get(o);
		
		updateProbePanel(o);
		
		panel.grabFocus();
		
		return;
	}
	
	final IntrospectFrame spector;
	String objName = "";
	if (o instanceof Named) {
		Named n = (Named) o;
		objName = n.getName();
	}

	if (o instanceof CustomProbeable) {
		CustomProbeable cp = (CustomProbeable) o;
		spector = new IntrospectFrame(o, objName, cp.getProbedProperties());
	} else {
		spector = new IntrospectFrame(o, objName);
	}

	spector.addWindowListener(new IntroFrameAdapter(o, listeners));
	Runnable specDisplay = new Runnable() {
		public void run() {
			try {
				ProbeUtilities.addProbe(o, spector);
			} catch (Exception ex) {
				SimUtilities.showError("Probing error", ex);
				ex.printStackTrace();
				System.exit(0);
			}
		}
	};
	SwingUtilities.invokeLater(specDisplay);
  }

  /**
   * Returns a list of the objects being probed.
   */
  public static List getProbedObjects() {
    return Collections.unmodifiableList(probedList);
  }

  /**
   * Adds the IntrospectPanel for a simulation model. <b>This should
   * not be called by modelers</b>
   *
   * @param panel the panel displaying the model's properties
   */
  public static void addModelProbePanel(IntrospectPanel panel) {
    modelPanel = panel;
  }

  /**
   * Removes the IntrospectPanel associated with the simulation model.
   */
  public static void removeModelProbePanel(){
    modelPanel = null;
  }

  /**
   * Updates the IntrospectPanel for the simulation model.
   */
  public static void updateModelProbePanel() {
    if (modelPanel == null) throw new RuntimeException("ModelPanel is null");
    modelPanel.reset();
  }

  /**
   * Adds and associates the specified IntrospectPanel with the
   * specified object. An IntrospectPanel is what a probed agent's
   * properties are displayed in. <b>This should not be called by
   * modelers</b>
   *
   * @param o the probed object
   * @param panel the panel displaying the object's properties
   */
  public static void addProbePanel(Object o, IntrospectPanel panel) {
    probedList.add(o);
    probePanelMap.put(o, panel);
  }

	/**
	 * Removes the IntrospectPanel associated with the specified Object.
	 *
	 * @param o the probed object whose panel should be removed
	 */
	public static void removeProbePanel(Object o) {
		probedList.remove(o);
		probePanelMap.remove(o);
		BeanBowlUtils.remove(o);
	}
	
  /**
   * Clears the map of Objects and their probe panels.
   */
  public static void clearProbePanels() {
    modelPanel = null;
    probedList.clear();
    probePanelMap.clear();
    listeners.clear();
  }

  /**
   * Updates the IntrospectPanel (the Probe window) for the specified
   * object. Use this method to update the probe display for an agent
   * or model. The agent or model should be the object passed to the method.
   *
   * @param o the object whose probe panel is to be updated
   */
  public static void updateProbePanel(Object o) {
    IntrospectPanel p = (IntrospectPanel)probePanelMap.get(o);
    if (p != null) p.reset();
  }

  /**
   * The probe panels for any objects that are currently being probed.
   * Note that this does not update a simulation Model's probePanel.
   * To update your model's probe panel, use
   * <code>updateModelProbePanel</code>.
   */
  public static void updateProbePanels() {
    Iterator iter = probePanelMap.values().iterator();
    while (iter.hasNext()) {
      IntrospectPanel p = (IntrospectPanel)iter.next();
      p.reset();
    }
  }

  /**
   * Adds the specified ProbeListener to receive ProbeEvents from
   * any probed objects.
   *
   * @param probeListener the ProbeListener
   */
  public static void addProbeListener(ProbeListener probeListener) {
    listeners.add(probeListener);
  }

  
	private static void addProbe(Object o, IntrospectFrame frame)
			throws InvocationTargetException, IllegalAccessException,
			IntrospectionException {

		addProbePanel(o, frame.display());

		frame.setLocation(newWindowXOffset, newWindowYOffset);

		updateOffsets();

	}
	
	private static void updateOffsets() {
		newWindowXOffset += 10;
		newWindowYOffset += 5;
		
		if (newWindowYOffset > 400) {
			newWindowYOffset = 0;
		}
		if (newWindowXOffset > 600) {
			newWindowXOffset = 0;
		}
	}
  
  public static void closeAllProbeWindows() {
    List windows = new ArrayList(probePanelMap.values());
    for (Iterator iter = windows.iterator(); iter.hasNext();) {
      IntrospectFrame frame = (IntrospectFrame) ((IntrospectPanel) iter.next()).getTopLevelAncestor();
      frame.setVisible(false);
      frame.dispose();
    }
  }
	
	/**
	 * Closes all the probing windows for the objects in the collection.  If
	 * an object is in the collection and isn't being probed it is ignored.
	 * 
	 * @param c the collection containing objects that may be being probed
	 */
	public static void closeProbeWindows(Collection c) {
		Iterator iter = c.iterator();
		
		while (iter.hasNext()) {
			IntrospectFrame objectsFrame = getObjectsFrame(iter.next());

			if (objectsFrame != null) {
				objectsFrame.setVisible(false);
				objectsFrame.dispose();
			}
		}
	}
	
	/**
	 * retrieves the IntrospectFrame that an object is being probed under
	 * 
	 * @param o the object being probed
	 * @return the frame holding the IntrospectPanel
	 */
	public static IntrospectFrame getObjectsFrame(Object o) {
		if (probePanelMap.containsKey(o)) {
			return (IntrospectFrame) ((IntrospectPanel) probePanelMap.get(o)).getTopLevelAncestor();
		} else {
			return null;
		}
	}
}

/**
 * A window adapter that fires probe related events when probe
 * windows are opened and closed.
 */
class IntroFrameAdapter extends WindowAdapter {
  Object spectee;
  List listeners;

  public IntroFrameAdapter(Object o, List listeners) {
    this.listeners = listeners;
    spectee = o;
  }

  public void windowOpened(WindowEvent evt) {
    ArrayList l;
    synchronized (listeners) {
      l = new ArrayList(listeners);
      
    }

    for (int i = 0; i < l.size(); i++) {
      ProbeListener pl = (ProbeListener)l.get(i);
      pl.objectProbed(new ProbeEvent(spectee));
    }

  }

  public void windowClosing(WindowEvent evt) {
    ArrayList l;
    synchronized (listeners) {
      l = new ArrayList(listeners);
      
    }

    for (int i = 0; i < l.size(); i++) {
      ProbeListener pl = (ProbeListener)l.get(i);
      pl.objectUnprobed(new ProbeEvent(spectee));
    }
    ProbeUtilities.removeProbePanel(spectee);
  }
}
