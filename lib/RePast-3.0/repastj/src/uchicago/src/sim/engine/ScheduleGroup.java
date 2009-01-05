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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * A specialized BasicAction containing other BasicActions. This is used
 * by the scheduling mechanism and should not be used by modelers.
 *
 * @version $Revision$ $Date$
 */
public class ScheduleGroup extends BasicAction {

  protected ArrayList actions = new ArrayList();
  protected ActionQueue queue;

  static class IndexComparator implements Comparator {

    public int compare(Object o1, Object o2) {

      BasicAction a1 = (BasicAction)o1;
      BasicAction a2 = (BasicAction)o2;

      return a1.index < a2.index ? -1 : a1.index == a2.index ? 0 : 1;
    }
  }

  private IndexComparator iComp = new IndexComparator();

  public ScheduleGroup(ActionQueue queue) {
    this.queue = queue;
  }

  public void addBasicAction(BasicAction action) {
    actions.add(action);
  }

  public void clear() {
    actions.clear();
  }

  public int size() {
    return actions.size();
  }

  public void execute() {
    for (int i = 0, n = actions.size(); i < n; i++) {
      ((BasicAction)actions.get(i)).execute();
    }
  }

  /**
   * Reschedules all the BasicAction contained by this ScheduleGroup. Note
   * that this ignores the aQueue parameter and uses the ActionQueue passed
   * in as part the constructor.
   */
  public void reSchedule(ActionQueue aQueue) {
    for (int i = 0, n = actions.size(); i < n; i++) {
      ((BasicAction)actions.get(i)).reSchedule(queue);
    }
  }

  /**
   * Sorts the BasicActions in this ScheduleGroup according to their
   * indices.
   */
  public void indexSort() {
    Collections.sort(actions, iComp);
  }
  
  public void removeAction(BasicAction action) {
    for (Iterator iter = actions.iterator(); iter.hasNext(); ) {
      if (iter.next().equals(action)) iter.remove();
    }
  }
}
