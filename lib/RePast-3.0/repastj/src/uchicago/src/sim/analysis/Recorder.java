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

import uchicago.src.sim.engine.SimEvent;
import uchicago.src.sim.engine.SimEventListener;

/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Jan 2, 2003
 * Time: 12:23:55 PM
 * To change this template use Options | File Templates.
 */
public interface Recorder extends SimEventListener {
  /**
   * Records the data for the current tick in tabular format. Each tick is
   * a row, and each column is the name of the data source.
   */
  public void record();

  /**
  * Writes the recorded data out to a file in tabular format. This also
  * does a flush on the data itself (i.e. the data is no longer stored by
  * repast and exists only in the file). Identical to writeToFile().
  */
  public void write();

  /**
   * Writes any ending matter to the file. Used internally during a batch run
   * to write the ending time of the entire batch. A model would not
   * typically call this method.
   */

  public void writeEnd();

  // listener interface
  public void simEventPerformed(SimEvent evt);

  public void writeToFile();
}
