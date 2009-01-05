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

import javax.media.Buffer;
import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.format.RGBFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;

/**
 * A DataSource for turning JMF Buffers into movies. See the Java Media
 * Framework documentation for details.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class BufferDataSource extends PullBufferDataSource {

  private BufferSourceStream streams[];

  public BufferDataSource(int width, int height, int frameRate, RGBFormat format) {
    streams = new BufferSourceStream[1];
    streams[0] = new BufferSourceStream(width, height, frameRate, format);
  }

  public void addBuffer(Buffer buf) {
    streams[0].addBuffer(buf);
  }

  public void cleanUp() {
    streams[0].waitForDone();
  }

  public void setLocator(MediaLocator source) {

  }

  public MediaLocator getLocator() {
    return null;
  }

  public String getContentType() {
    return ContentDescriptor.RAW;
  }

  public void connect() {}

  public void disconnect() {}

  public void start() {}

  public void stop() {}

  public PullBufferStream[] getStreams() {
    return streams;
  }

  public Time getDuration() {
    return DURATION_UNKNOWN;
  }

  public Object[] getControls() {
    return new Object[0];
  }

  public Object getControl(String type) {
    return null;
  }
}






