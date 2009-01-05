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

import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import uchicago.src.sim.engine.Controller;
import uchicago.src.sim.util.SimUtilities;

/**
 * A Factory for JFrames that adds support for persistent location and size.
 * Individual JFrames are identified by their title. So, two or more JFrames
 * with the same title will be treated has having the same location and
 * size.<p>
 *
 * Any JFrame created with <code>createFrame</code> will have its size and
 * location (its bounds property) saved and indexed to that JFrame's
 * title when the JFrame is closed. When a JFrame with this title is created
 * its bounds are set to the saved value. The saved bounds information can
 * also be accessed via the <code>getBounds</code> method.<p>
 *
 * The size and location data is persisted between application instances
 * via an xml file. The file is stored in $HOME/fully_qualified_model_name_as_dir/frame_props.xml.
 * So, for example, if the home directory is /home/nick and the fully qualified
 * model name is uchicago.src.sim.heatBugs, then the file path is <br>
 * /home/nick/.repast/uchicago/src/sim/heatBugs/frame_props.xml. <br>
 * The xml itself is human readable, but is liable to change. An example follows:
 * <p>
 * <pre><code>
 * <?xml version="1.0"?>
 * <!-- RePast Frame Properties File -->
 * <RePast:FrameProps xmlns:RePast="http://src.uchicago.edu/repast/" >
 *    <frame_property name="Mouse Trap Display" x="0" y="0" width="411" height="443" />
 *    <frame_property name="Trigger Data vs. Time" x="19" y="473" width="697" height="428" />
 * </RePast:FrameProps>
 * </code></pre>
 *
 * @version $Revision$ $Date$
 */
public class FrameFactory {

  // stores the name and bounds of a particular JFrame.
  static class FrameData {
    String name;
    Rectangle bounds = new Rectangle();
    JFrame frame;

    public FrameData(String name, Rectangle bounds, JFrame frame) {
      this.bounds = bounds;
      this.name = name;
      this.frame = frame;
    }

    public void resetBounds() {
      if (frame != null) bounds = frame.getBounds();
    }

    public String toXML() {
      StringBuffer b = new StringBuffer("<frame_property name=\"");
      //to account for the presence of these characters in an xml file.
      StringBuffer buf = new StringBuffer(name);
      int start = 0;
      while((buf.indexOf("&", start) > 0)){
        start = buf.indexOf("&",start) + 1;
        buf.replace(start - 1,start,"&amp;");
      }
      start = 0;
      while((buf.indexOf(">", start) > 0)){
        start = buf.indexOf(">",start) + 1;
        buf.replace(start - 1,start,"&gt;");
      }
      start = 0;
      while((buf.indexOf("<", start) > 0)){
        start = buf.indexOf("<",start) + 1;
        buf.replace(start - 1,start,"&lt;");
      }
      name = buf.toString();
      b.append(name);
      b.append("\" x=\"");
      b.append(bounds.x);
      b.append("\" y=\"");
      b.append(bounds.y);
      b.append("\" width=\"");
      b.append(bounds.width);
      b.append("\" height=\"");
      b.append(bounds.height);
      b.append("\" />\n");

      return b.toString();
    }
  }

  private static HashMap dataMap = new HashMap();

  private FrameFactory() {
  }

  /**
   * Loads the frame data for the specified model.
   * @param fqModelName the name of the model to load the data for
   */
  public static void load(String fqModelName) {
    String homeDir = System.getProperty("user.home");
    String modelDir = homeDir + File.separator + ".repast" + File.separator +
            fqModelName.replace('.', File.separatorChar);
    String file = modelDir + File.separator + "frame_props.xml";
    File propsFile = new File(file);
    if (propsFile.exists()) {
      loadXML(file);
    }
  }

  private static void loadXML(String file) {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder parser = dbf.newDocumentBuilder();
      Document doc = parser.parse(new FileInputStream(file), file);
      Element root = doc.getDocumentElement();
      NodeList list = root.getElementsByTagName("frame_property");
      for (int i = 0; i < list.getLength(); i++) {
        Element child = (Element) list.item(i);
        String name = child.getAttribute("name");
        int x = Integer.parseInt(child.getAttribute("x"));
        int y = Integer.parseInt(child.getAttribute("y"));
        int width = Integer.parseInt(child.getAttribute("width"));
        int height = Integer.parseInt(child.getAttribute("height"));
        FrameData data = new FrameData(name, new Rectangle(x, y, width, height),
                null);
        dataMap.put(name, data);
      }
    } catch (Exception ex) {
      SimUtilities.showError("Error loading persistent frame size and positions", ex);
      ex.printStackTrace();
    }
  }

  /**
   * Returns a new JFrame with specified title as its title and size and location
   * if any. If persistent location and size information is found for this
   * title then the created JFrame will have that size and location.
   *
   * @param title the title of the JFrame to create
   * @return a new JFrame with persistent size and location if any
   */
  public static JFrame createFrame(String title) {
    JFrame frame = new JFrame(title);
    FrameData data = (FrameData) dataMap.get(title);
    if (data != null) {
      frame.setBounds(data.bounds);
      data.frame = frame;
    } else {
      data = new FrameData(title, null, frame);
      dataMap.put(title, data);
    }

    frame.setIconImage(new ImageIcon(
            Controller.class.getResource(
                "/uchicago/src/sim/images/RepastSmall.gif")).getImage());

    frame.addWindowListener(new WindowAdapter() {
      public void windowClosed(WindowEvent e) {
        JFrame f = (JFrame) e.getSource();
        String fTitle = f.getTitle();
        Rectangle bounds = f.getBounds();
        if (fTitle.length() > 0) {
          FrameData data = (FrameData) dataMap.get(fTitle);
          if (data == null) {
            data = new FrameData(fTitle, bounds, f);
            dataMap.put(fTitle, data);
          } else {
            data.bounds = bounds;
          }
        }
      }
    });

    return frame;
  }

  /**
   * Returns the bounds, if any, associated with this frameName.
   *
   * @param frameTitle the title of the JFrame whose bounds data this returns
   * @return the bounds associated wit the specified title, or null if no bounds
   * are found for that title
   */
  public static Rectangle getBounds(String frameTitle) {
    FrameData data = (FrameData) dataMap.get(frameTitle);
    if (data != null) return data.bounds;
    return null;
  }

  /**
   * Saves the frame data for the specified model.
   * The size and location data is persisted between application instances
   * via an xml file. The file is stored in $HOME/fully_qualified_model_name_as_dir/frame_props.xml.
   * So, for example, if the home directory is /home/nick and the fully qualified
   * model name is uchicago.src.sim.heatBugs, then the file path is <br>
   * /home/nick/.repast/uchicago/src/sim/heatBugs/frame_props.xml. <br>
   * The xml itself is human readable, but is liable to change. An example follows:
   * <p>
   * <pre><code>
   * <?xml version="1.0"?>
   * <!-- RePast Frame Properties File -->
   * <RePast:FrameProps xmlns:RePast="http://src.uchicago.edu/repast/" >
   *    <frame_property name="Mouse Trap Display" x="0" y="0" width="411" height="443" />
   *    <frame_property name="Trigger Data vs. Time" x="19" y="473" width="697" height="428" />
   * </RePast:FrameProps>
   * </code></pre>
   *
   * @param fqModelName the fully qualified name of the model whose frame data
   * we want to save
   */
  public static void saveFrameData(String fqModelName) {
    String homeDir = System.getProperty("user.home");
    String modelDir = homeDir + File.separator + ".repast" + File.separator +
            fqModelName.replace('.', File.separatorChar);
    File fModelDir = new File(modelDir);
    if (!fModelDir.exists()) {
      fModelDir.mkdirs();
    }

    String file = modelDir + File.separator + "frame_props.xml";
    try {
      BufferedWriter out =
              new BufferedWriter(new FileWriter(file));
      out.write("<?xml version=\"1.0\"?>");
      out.newLine();
      out.write("<!-- RePast Frame Properties File -->");
      out.newLine();
      out.write("<RePast:FrameProps xmlns:RePast=\"http://src.uchicago.edu/repast/\" >");
      out.newLine();

      for (Iterator iter = dataMap.values().iterator(); iter.hasNext();) {
        FrameData data = (FrameData) iter.next();
        data.resetBounds();
        out.write("  ");
        out.write(data.toXML());
        out.newLine();
      }

      out.write("</RePast:FrameProps>");

      out.flush();
      out.close();

    } catch (IOException ex) {
      SimUtilities.showError("Error storing persistent frame size and positions", ex);
      ex.printStackTrace();
    }
  }
}
