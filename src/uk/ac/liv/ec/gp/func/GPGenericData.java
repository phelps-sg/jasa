/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package uk.ac.liv.ec.gp.func;

import ec.gp.*;

import huyd.poolit.*;

import uk.ac.liv.util.Pooled;


public class GPGenericData extends GPData {

  public Object data;

  public GPGenericData() {
  }

  public GPData copyTo( GPData other ) {
    ((GPGenericData) other).data = this.data;
    //TODO What is the exact semantics of copyTo?  Is this safe for ADF calls?
    return other;
  }

  public GPGenericData safeCopy() {
    GPGenericData copy = new GPGenericData();
    if ( data instanceof Pooled ) {
      copy.data = ((Pooled) data).newCopy();
    } else {
      copyTo(copy);
    }
    return copy;
  }

  public String toString() {
    return "(" + getClass() + " data:" + data + ")";
  }

}