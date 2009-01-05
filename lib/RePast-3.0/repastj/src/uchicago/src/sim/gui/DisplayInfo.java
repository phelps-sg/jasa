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
package uchicago.src.sim.gui;

//import javax.swing.*;

/**
 * Encapsulates information used by a display surface to create the view menu.
 * The View menu makes it possible to turn off display(s) or parts of displays
 * (e.g. the nodes in a network display).
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class DisplayInfo {

  public static final int DRAW = 0;
  public static final int NO_DRAW = 1;

  private String menuText = "";
  private int id;
  private Displayable displayable;

  /**
   * Creates a new DisplayInfo with the specified menu text, id,
   * and displayble.
   *
   * @param menuText the text to display on the view menu
   * @param id the id associated with this menuText, used to create
   * a ViewEvent when this menu item is selected.
   * @param d the displayable associated with this DisplayInfo
   */
  public DisplayInfo(String menuText, int id, Displayable d) {
    this.menuText = menuText;
    this.id = id;
    displayable = d;
  }

  /**
   * Gets the menu text.
   */
  public String getMenuText() {
    return menuText;
  }

  /**
   * Gets the id.
   */
  public int getId() {
    return id;
  }

  /**
   * Gets the displayble.
   */
  public Displayable getDisplayable() {
    return displayable;
  }
}
