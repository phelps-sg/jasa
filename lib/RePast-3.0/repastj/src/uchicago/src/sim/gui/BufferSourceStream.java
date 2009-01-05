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

import java.util.Vector;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.RGBFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferStream;

/**
 * A DataSourceStream for turning JMF Buffers into movies. The JMF Buffers
 * are created by DisplaySurface. See the Java Media Framework documentation
 * for more details on BufferStreams etc.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class BufferSourceStream implements PullBufferStream {

  private Vector buffers = new Vector(17);
  //private int width;
  //private int height;
  //private VideoFormat vFormat;
  private RGBFormat vFormat;
  private boolean notDone = true;
  private boolean finished = false;

  //private Object waitSync = new Object();

  public BufferSourceStream(int width, int height, int frameRate, RGBFormat aFormat) {
    //this.width = width;
    //this.height = height;
    vFormat = aFormat;
    /*
    vFormat = new RGBFormat(new Dimension(width, height),
                            aFormat.getMaxDataLength(),
                            aFormat.getDataType(),
                            (float)frameRate,
                            aFormat.getBitsPerPixel(),
                            aFormat.getRedMask(),
                            aFormat.getGreenMask(),
                            aFormat.getBlueMask());
    */
  }

  public void addBuffer(Buffer buf) {
    synchronized(buffers) {
      buffers.add(buf);
      //System.out.println("Adding buffer: size = " + buffers.size());
      buffers.notify();
    }
  }

  public void waitForDone() {

    synchronized (buffers) {
      notDone = false;
      while (buffers.size() != 0) {
        try {
          buffers.wait();
        } catch (InterruptedException ex) {}
      }
      buffers.notify();
    }
    finished = true;
  }

  public boolean willReadBlock() {
    return buffers.size() == 0;
  }

  public void read(Buffer buf) {
    synchronized (buffers) {
      while (buffers.size() == 0 && notDone) {
        try {
          buffers.wait();
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
      }

      if (buffers.size() > 0) {
        Buffer newBuf = (Buffer)buffers.get(0);
        int[] newData = (int[])newBuf.getData();
        buf.setData(newData);
        buf.setLength(newBuf.getLength());
        buf.setOffset(0);
        buf.setFormat(vFormat);
        buf.setFlags(Buffer.FLAG_KEY_FRAME | Buffer.FLAG_NO_DROP);
        buffers.remove(0);
        //System.out.println("Removing buffer: size = " + buffers.size());
      } else {
        buf.setEOM(true);
        buf.setOffset(0);
        buf.setLength(0);
        synchronized (buffers) {
          buffers.notify();
        }
      }
    }
  }

  public Format getFormat() {
    return vFormat;
  }

  public ContentDescriptor getContentDescriptor() {
    return new ContentDescriptor(ContentDescriptor.RAW);
  }

  public long getContentLength() {
    return 0;
  }

  public boolean endOfStream() {
    return finished;
  }

  public Object[] getControls() {
    return new Object[0];
  }

  public Object getControl(String type) {
    return null;
  }
}


