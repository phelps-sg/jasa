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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Jan 2, 2003
 * Time: 4:02:08 PM
 * To change this template use Options | File Templates.
 */
public class DataCallback {
  protected SocketChannel channel;
  protected BufferedWriter out;
  protected static int channelNum = 0;

  public DataCallback(SocketChannel channel, String fqModelName) {
    channelNum++;
    try{
      String homeDir = System.getProperty("user.home");
      String modelDir = homeDir + File.separator + ".repast" + File.separator +
              fqModelName.replace('.', File.separatorChar);
      File fModelDir = new File(modelDir);
      if (!fModelDir.exists()) {
        fModelDir.mkdirs();
      }
      String fileName = modelDir + File.separator + ".tmp." + channelNum + ".txt";
      out = new BufferedWriter(new FileWriter(fileName, true));
          }catch(Exception e){
      e.printStackTrace();
    }
    this.channel = channel;
  }

  public void execute() throws IOException {
    out.close();
  }

  public SocketChannel getChannel() {
    return this.channel;
  }

  public void record(String values) {
    if(values.indexOf("\007") >= 0){
      values = values.substring(0, values.indexOf("\007"));
    }
    try {
      out.write(values);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
