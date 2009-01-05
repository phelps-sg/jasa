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
package uchicago.src.repastdemos.neural;

import java.awt.Image;

import javax.swing.ImageIcon;

import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.network.DefaultDrawableNode;

/**
 * The class used to draw the goal of the agents (the stapler).
 *
 * @author Jerry Vos
 * @version $Revision$ $Date$
 */
public class Stapler extends DefaultDrawableNode {
	private static Image staplerPicture;
	
	public Stapler(Office office) {
		super();
		
		loadStaplerPicture();
		
		// the minus 34 and 15 are taken from the size of the image
		this.setX(office.getWidth() / 2.0 - 34);
		this.setY(office.getHeight() / 2.0 - 15);
	}
	
	private static void loadStaplerPicture() {
		if (staplerPicture == null) {
			java.net.URL staplerURL = Stapler.class.getResource("stapler.gif");
			
			staplerPicture = new ImageIcon(staplerURL).getImage();			
		}
	}

	/* (non-Javadoc)
	 * @see uchicago.src.sim.network.DefaultDrawableNode#draw(uchicago.src.sim.gui.SimGraphics)
	 */
	public void draw(SimGraphics g) {
		g.drawImage(staplerPicture);
	}
}
