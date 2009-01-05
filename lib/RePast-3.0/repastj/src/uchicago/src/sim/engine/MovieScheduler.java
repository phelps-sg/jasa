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

import uchicago.src.sim.gui.MediaProducer;

public class MovieScheduler {

  private MediaProducer producer;
  private BasicAction frameCapture;
  private BasicAction cleanUp;
  private String fileName;

  class FrameCapture extends BasicAction {
    String fileName;
    boolean nameSet = false;

    public FrameCapture(String fname) {
      fileName = fname;
    }

    public void execute() {
     // if (!nameSet) {
      //  producer.setMovieName(fileName, MediaProducer.QUICK_TIME);
     //   nameSet = true;
     // }

      producer.addMovieFrame();
    }
  }

  public MovieScheduler(String fileName, MediaProducer producer) {
    this.producer = producer;
    frameCapture = new FrameCapture(fileName);
    this.fileName = fileName;

    cleanUp = new BasicAction() {
      public void execute() {
        MovieScheduler.this.producer.closeMovie();
      }
    };
  }

  private void initProducer() {
    producer.setMovieName(fileName, MediaProducer.QUICK_TIME);
  }

  public void scheduleAtPauseAndEnd(Schedule schedule) {
    initProducer();
    schedule.scheduleActionAtPause(frameCapture);
    schedule.scheduleActionAtEnd(frameCapture);
    schedule.scheduleActionAtEnd(cleanUp);
  }

  public void scheduleAtInterval(Schedule schedule, int interval) {
    initProducer();
    schedule.scheduleActionAtInterval(interval, frameCapture, Schedule.LAST);
    schedule.scheduleActionAtEnd(cleanUp);
  }

  public void scheduleAtEveryTick(Schedule schedule) {
    initProducer();
    schedule.scheduleActionAt(0, frameCapture, Schedule.LAST);
    schedule.scheduleActionAtInterval(1, frameCapture, Schedule.LAST);
    schedule.scheduleActionAtEnd(cleanUp);
  }
}
