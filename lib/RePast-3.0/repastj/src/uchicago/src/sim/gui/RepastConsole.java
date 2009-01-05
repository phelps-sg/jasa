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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import uchicago.src.sim.util.SimUtilities;

public class RepastConsole extends WindowAdapter implements WindowListener, Runnable{
  private JFrame frame;
  private JTextArea textArea;
  private Thread reader;
  private Thread reader2;
  private boolean quit;
  private boolean stdoutOn = true;
  private boolean stderrOn = true;
  private final PipedInputStream stdout = new PipedInputStream();
  private final PipedInputStream stderr = new PipedInputStream();
  protected final static PrintStream oldStdout = System.out;
  protected final static PrintStream oldStderr = System.err;
  protected static PrintStream newStdout;
  protected static PrintStream newStderr;

  public RepastConsole(boolean stdout, boolean stderr){
    frame = FrameFactory.createFrame("RePast Output");
    Rectangle rect = FrameFactory.getBounds("RePast Output");
    if (rect == null) {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

      Dimension frameSize = new Dimension((screenSize.width - 40),
                                          180);
      int x = (20);
      int y = (screenSize.height - 220);

      frame.setBounds(x, y, frameSize.width, frameSize.height);
    }

    textArea = new JTextArea();
    textArea.setEditable(false);

    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
    frame.addWindowListener(this);
    try{
      newStdout = new PrintStream(new PipedOutputStream(this.stdout));
    }  catch (java.io.IOException io){
      SimUtilities.showError("Couldn't attach stdout to console", io);
    }
    catch (SecurityException se){
      SimUtilities.showError("Couldn't attach stdout to console", se);
    }
    try{
      newStderr = new PrintStream(new PipedOutputStream(this.stderr));
    }
    catch (java.io.IOException io){
      SimUtilities.showError("Couldn't attach stderr to console", io);
    }
    catch (SecurityException se){
      SimUtilities.showError("Coudldn't attach stderr to console", se);
    }
    setStdoutOn(stdout);
    setStderrOn(stderr);
    quit = false;
  }

  public void display(){
    frame.setVisible(true);
    if(stdoutOn){
      reader = new Thread(this);
      reader.setDaemon(true);
      reader.start();
    }
    if(stderrOn){
      reader2 = new Thread(this);
      reader2.setDaemon(true);
      reader2.start();
    }
  }

  public boolean isStdoutOn() {
    return stdoutOn;
  }

  public void setStdoutOn(boolean stdoutOn) {
    this.stdoutOn = stdoutOn;
    if(stdoutOn){
      System.setOut(newStdout);
    }else{
      System.setOut(oldStdout);
    }
  }

  public boolean isStderrOn() {
    return stderrOn;
  }

  public void setStderrOn(boolean stderrOn) {
    this.stderrOn = stderrOn;
    if(stderrOn){
      System.setErr(newStderr);
    }else{
      System.setErr(oldStderr);
    }
  }

  public synchronized void dispose(){
    quit = true;
    //this.notifyAll();
    frame.dispose();
  }

  public synchronized void windowClosed(WindowEvent evt){
    quit=true;
    this.notifyAll();
    if(stdoutOn){
      try {
        reader.join(1000);
        stdout.close();
      } catch (Exception e){
        SimUtilities.showError("Error closing console", e);
      }
    }
    if(stderrOn){
      try {
        reader2.join(1000);
        stderr.close();
      } catch (Exception e){
        SimUtilities.showError("Error closing console", e);
      }
    }

    System.setErr(oldStderr);
    System.setOut(oldStdout);
    //System.exit(0);
  }

  public synchronized void windowClosing(WindowEvent evt){
    frame.setVisible(false);
    frame.dispose();
  }

  public synchronized void run(){
    try{
      if(stdoutOn){
        while (Thread.currentThread() == reader){
          try {
            this.wait(100);
          }catch(InterruptedException ie) {

          }
          if (stdout.available() != 0){
            String input = this.readLine(stdout);
            textArea.append(input);
          }
          if (quit){
            return;
          }
        }
      }
      if(stderrOn){
        while (Thread.currentThread() == reader2)	{
          try {
            this.wait(100);
          }catch(InterruptedException ie) {

          }
          if(stderr.available()!=0)	{
            String input = this.readLine(stderr);
            textArea.append(input);
          }
          if (quit) return;
        }
      }
    } catch (Exception e){
      SimUtilities.showError("Error displaying message", e);
    }
  }

  public synchronized String readLine(PipedInputStream in) throws IOException{
    String input="";
    do{
      int available = in.available();
      if (available == 0) break;
      byte b[] = new byte[available];
      in.read(b);
      input = input + new String(b,0,b.length);
    }while(!input.endsWith("\n") && !input.endsWith("\r\n") && !quit);
    return input;
  }

  public static void main(String[] arg){
    new RepastConsole(true, true);
    System.out.println("This is my message");
    throw new NullPointerException("This is a test of err");
  }
}
