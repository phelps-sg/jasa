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
package uchicago.src.sim.analysis;

import java.awt.Color;

/**
 * Represents a row in the model contained by the table in
 * NetSequenceSetPanel. This should not be used by modelers.
 *
 * @version $Revision$ $Date$
 */
public class NetSeqTableRow {
  
  protected String name;
  protected Sequence sequence;
  protected Color color;
  protected boolean show = false;
  protected boolean hasRun = false;
  protected int markType = 9;

  public NetSeqTableRow(String name, Sequence seq, Color color) {
    this.name = name;
    this.sequence = seq;
    this.color = color;
  }

  public void setColor(Color c) {
    color = c;
  }

  public void setShow(boolean val) {
    show = val;
  }

  public void setMarkType(int type) {
    markType = type;
  }

  public void setHasRun(boolean val) {
    hasRun = val;
  }

  public Object getValueAt(int col) {
    switch (col) {
    case 0:
      return name;
    case 1:
      return new Boolean(show);
    case 2:
      return color;
    case 3:
      return new Integer(markType);
    default:
      throw new IllegalArgumentException("invalid column index");
    }
  }

  public static Class getClassAt(int col) {
    switch (col) {
    case 0:
      return String.class;
    case 1:
      return Boolean.class;
    case 2:
      return Color.class;
    case 3:
      return Integer.class;
    default:
      throw new IllegalArgumentException("invalid column index");
    }
  }

  public boolean isEditable() {
    return !hasRun;
  }

  public void addSequence(NetSequenceGraph plot) {
    if ((!hasRun) && show) {
      plot.addSequence(name, sequence, color, markType);
      hasRun = true;
    }
  }

  public void addSequence(NetSequenceGraph plot, String name) {
    if ((!hasRun) && show) {
      this.name = name;
      plot.addSequence(name, sequence, color, markType);
      hasRun = true;
    }
  }

  public void addSequence(NetSequenceGraph plot, String name, Color color) {
    if ((!hasRun) && show) {
      this.name = name;
      this.color = color;
      plot.addSequence(name, sequence, color, markType);
      hasRun = true;
    }
  }

  public void addSequence(NetSequenceGraph plot, String name, int markType) {
    if ((!hasRun) && show) {
      this.name = name;
      this.markType = markType;
      plot.addSequence(name, sequence, color, markType);
      hasRun = true;
    }
  }

  public void addSequence(NetSequenceGraph plot, String name, Color color,
			  int markType)
  {
    if ((!hasRun) && show) {
      this.name = name;
      this.color = color;
      this.markType = markType;
      plot.addSequence(name, sequence, color, markType);
      hasRun = true;
    }
  }
}
