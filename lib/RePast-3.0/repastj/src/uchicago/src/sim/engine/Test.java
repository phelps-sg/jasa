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
/*
 * Modified by Michael J. North
*/
//import uchicago.src.sim.gui.DisplaySurface;


public class Test {

  public static void main(String[] args) {

    final Schedule s = new Schedule(1);

    final BasicAction ba1 = new BasicAction() {
      public void execute() {
        System.out.println("BA1 executing every tick");
      }
    };

    final BasicAction ba2 = new BasicAction() {
      public void execute() {
        System.out.println("BA2 executing at interval of 3");
      }
    };

    final BasicAction ba3 = new BasicAction() {
      public void execute() {
        System.out.println("BA3 executing at 10th tick");
      }
    };

    final BasicAction ba4 = new BasicAction() {
      public void execute() {
        System.out.println("Removing ba3");
        s.removeAction(ba3);
      }
    };

    final BasicAction ba5 = new BasicAction() {
      public void execute() {
        System.out.println("Sub schedule execution");
      }
    };


    Schedule ss = new Schedule(3);
    ss.scheduleActionBeginning(0, ba5);

    //ActionGroup ag = new ActionGroup(ActionGroup.SEQUENTIAL);
    //ag.addAction(ba1);
    //ag.addAction(ss);

    s.scheduleActionBeginning(0, ba1);
    s.scheduleActionBeginning(1, ss);

    s.scheduleActionAtInterval(3, ba2, Schedule.LAST);
    s.scheduleActionAt(10, ba3);
    s.scheduleActionAt(9, ba4, Schedule.LAST);

    for (int i = 0; i < 25; i++) {
      System.out.println("-------------");
      System.out.println("Tick: " + s.getCurrentTime());
      try {
        s.execute();
      } catch (Exception ex) {
        ex.printStackTrace();
        System.exit(0);
      }
    }
  }
}

    