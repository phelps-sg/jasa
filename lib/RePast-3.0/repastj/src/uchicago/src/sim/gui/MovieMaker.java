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

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.media.Buffer;
import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.format.RGBFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.util.ImageToBuffer;

import uchicago.src.sim.util.SimUtilities;

/**
 * Takes DisplaySurface images and makes movies out of them. All messages sent
 * to this class should be done through DisplaySurface.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class MovieMaker implements ControllerListener, DataSinkListener {

  private Processor p;
  private BufferDataSource source;

  private Object waitSync = new Object();
  private Object waitFileSync = new Object();
  private boolean fileDone = false;
  boolean stateTransitionOK = true;
  private MediaLocator outML;
  private DataSink sink;
  private String movieType;
  private int frameRate;
  private boolean init = false;
  private int width, height;

  public MovieMaker(int width, int height, int frameRate,
                    String fileName, String movieType)
  {
    this.movieType = movieType;
    this.frameRate = frameRate;
    this.width = width;
    this.height = height;
    try {
      File f = new File(fileName);
      fileName = f.getCanonicalPath();
      System.out.println(fileName);
    } catch (IOException ex) {
      SimUtilities.showError("Unable to create file for movie", ex);
      ex.printStackTrace();
      System.exit(0);
    }
    String url = "file:/" + fileName;
    outML = new MediaLocator(url);
  }

  public boolean init(RGBFormat format) {
    source = new BufferDataSource(width, height, frameRate, format);
    try {
      p = Manager.createProcessor(source);
    } catch (Exception ex) {
      SimUtilities.showError("Failed to create processor for movie", ex);
      ex. printStackTrace();
      return false;
    }

    p.addControllerListener(this);

    // put the processor into configured state so can set some options
    // on the processor
    p.configure();
    if (!waitForState(p, Processor.Configured)) {
      System.err.println("Failed to configure the processor");
      return false;
    }

    // set the output content descriptor to the movie type
    p.setContentDescriptor(new ContentDescriptor(movieType));

    TrackControl tcs[] = p.getTrackControls();
    Format f[] = tcs[0].getSupportedFormats();

    if (f == null || f.length <= 0) {
      System.err.println("The mux does not support the input format: " +
      tcs[0].getFormat());
      return false;
    }

    tcs[0].setFormat(f[0]);

    // realize the processor
    p.realize();
    if (!waitForState(p, Processor.Realized)) {
      System.err.println("Failed to Realize processor");
      return false;
    }

    boolean result = createDataSink();

    if (!result) {
      return false;
    }

    sink.addDataSinkListener(this);

    try {
      p.start();
      sink.start();
    } catch (IOException ex) {
      SimUtilities.showError("Movie error", ex);
      ex.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * Adds an image to a movie as a frame with a default sleep value of 40
   *
   * @param image the image to add as a frame
   */
  public void addImageAsFrame(Image image) {
    addImageAsFrame(image, 40);
  }

  /**
   * Adds an image to a movie as a frame. This method pause the main
   * simulation thread for the specified amount of time. This pause
   * is necessary to allow the images to be written to disk in a background
   * thread. Without the pauses the images are still written to disk, but
   * are added much faster than they can be written, resulting in increased
   * memory use and eventual OutOfMemoryErrors. This method is called by
   * DisplaySurface and shouldn't be called by a user in the course of
   * writing a model.
   *
   * @param image the image to add as a frame
   * @param sleepCount the amount to pause for writing images to disk
   */
  public void addImageAsFrame(Image image, int sleepCount) {
    Buffer b = ImageToBuffer.createBuffer(image, frameRate);

    if (!init) {
      boolean result = init((RGBFormat)b.getFormat());
      if (!result) {
        System.err.println("Failed to setup movie capture");
      }
      init = true;
    }
    source.addBuffer(b);
    try {
      Thread.sleep(sleepCount);
    } catch (InterruptedException ex) {}
    System.gc();
  }


  private boolean waitForState(Processor p, int state) {
    synchronized(waitSync) {
      try {
        while (p.getState() < state && stateTransitionOK) {
          waitSync.wait();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return stateTransitionOK;
  }

  public void cleanUp() {
    source.cleanUp();
    waitForFileDone();
    try {
      sink.close();
    } catch (Exception ex) {}


    p.removeControllerListener(this);

    SimUtilities.showMessage("Movie capture has finished");
    System.out.println("Movie done");
  }

  private void waitForFileDone() {
    synchronized (waitFileSync) {
      try {
        while (!fileDone) {
          waitFileSync.wait();
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        sink.close();
      }
    }
  }


  private boolean createDataSink() {
    DataSource ds = p.getDataOutput();
    try {
      sink = Manager.createDataSink(ds, outML);
      sink.open();
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }

    return true;
  }

  // controller listener
  public void controllerUpdate(ControllerEvent evt) {
    if (evt instanceof ConfigureCompleteEvent ||
        evt instanceof RealizeCompleteEvent ||
        evt instanceof PrefetchCompleteEvent)
    {
      synchronized(waitSync) {
        stateTransitionOK = true;
        waitSync.notifyAll();
      }
    } else if (evt instanceof ResourceUnavailableEvent) {
      synchronized(waitSync) {
        stateTransitionOK = false;
        waitSync.notifyAll();
      }
    } else if (evt instanceof EndOfMediaEvent) {
      System.out.println("End of Media Event");
      evt.getSourceController().stop();
      evt.getSourceController().close();
    }

  }

  // datasink listener
  public void dataSinkUpdate(DataSinkEvent evt) {
    if (evt instanceof EndOfStreamEvent) {
      synchronized (waitFileSync) {
        fileDone = true;
        waitFileSync.notifyAll();
      }
    } else if (evt instanceof DataSinkErrorEvent) {
      synchronized (waitFileSync) {
        fileDone = true;
        waitFileSync.notifyAll();
      }
    }
  }
}