/*$$
* Copyright (c) 2004, Repast Organization for Architecture and Design (ROAD)
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
* Neither the name of the ROAD nor the names of its
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

import com.netbreeze.bbowl.gui.BeanBowlGUI;
import com.netbreeze.swing.SwingEnvironment;

/**
 * Utilities for working with the bean bowl library. For more information
 * on the BeanBowl library go to <a href="http://beanbowl.sourceforge.net/">
 * http://beanbowl.sourceforge.net/</a>. 
 *
 * @author Mike North modified by Jerry Vos
 * @version $Revision$ $Date$
 */
public class BeanBowlUtils {
	/**
	 * the static BeanBowlGUI shared by the current RePast instance
	 */
	private static BeanBowlGUI beanBowlGUI = null;

	private BeanBowlUtils() {
		super();
	}

	/**
	 * probes an object through the bean bowl gui.
	 */
	public static void probe(Object object) {
		// Show the object.
		// Add the current item.
		BeanBowlUtils.getBeanBowlGUI().getBowl().addBean(object);
		BeanBowlUtils.beanBowlGUI.show();
	}


	/**
	 * @return the static BeanBowlGUI shared by the current RePast instance  
	 */
	private static BeanBowlGUI getBeanBowlGUI() {
		// Wash the dishes.		
		if ((BeanBowlUtils.beanBowlGUI != null) &&
			(!BeanBowlUtils.beanBowlGUI.isShowing())) {
			BeanBowlUtils.beanBowlGUI.dispose();
			BeanBowlUtils.beanBowlGUI = null;
		}

		// Check the bowl.
		if (BeanBowlUtils.beanBowlGUI == null) {

			// Create and configure a new display panel.
			BeanBowlUtils.beanBowlGUI = new BeanBowlGUI();
			SwingEnvironment.setBeansContext(
					BeanBowlUtils.beanBowlGUI.getContext());
			BeanBowlUtils.beanBowlGUI.setTitle("Repast Properties Editor");
			BeanBowlUtils.beanBowlGUI.setSize(800, 600);
			com.netbreeze.util.Utility.centerWindow(
					BeanBowlUtils.beanBowlGUI);

		}

		// Return the results.
		return beanBowlGUI;
	}


	/**
	 * probes an object through the bean bowl gui.
	 * 
	 * @param object the object to probe
	 */
	public static void remove(Object object) {
		// Spot clean the dishes.		
		if (BeanBowlUtils.beanBowlGUI != null) {
			if (BeanBowlUtils.beanBowlGUI.isShowing()) {
				BeanBowlUtils.beanBowlGUI.getBowl().removeBean(object);
			} else {
				BeanBowlUtils.beanBowlGUI.dispose();
				BeanBowlUtils.beanBowlGUI = null;
			}
		}
	}
}
