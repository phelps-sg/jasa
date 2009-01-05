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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import uchicago.src.sim.engine.SimEvent;
import uchicago.src.sim.engine.SimEventListener;
import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.util.ProbeUtilities;
import uchicago.src.sim.util.SimUtilities;

/**
 * Handles the drawing of Displayables and the probing of probeables.
 * Displayables are added to a DisplaySurface which is then responsible for
 * drawing them, and handling probing (single left-click) of them. Displayables
 * are drawn in the order they are added to the DisplaySurface. Typically,
 * a DisplaySurface is created by the model and has displayables added to it.
 * DisplaySurface also handles the creation of movies from and snapshots of
 * displays.<p>
 * <p/>
 * When drawing discrete (cell-based displays) the actual drawing surface
 * divided up into a number of cells equal to the DisplaySurface's width * the
 * DisplaySuface's height (and in the future, the DisplaySurface's depth).
 * Drawing at x, y coordinates via {@link uchicago.src.sim.gui.SimGraphics
 * SimGraphics} draws in the cell with these coordinates.<p>
 * <p/>
 * When drawing non-discrete displays(e.g Network2DDisplay), drawing is
 * done to actual screen coordinates, where x and y refer to a screen
 * coordinate.<p>
 *
 * @author Nick Collier (Modified by Michael J. North)
 * @version $Revision$ $Date$
 * @see Displayable
 * @see Probeable
 */
public class DisplaySurface
        extends JComponent
        implements SimEventListener, MediaProducer, ComponentListener {

  /**
   * The Painter used by this DisplaySurface to do the actual drawing.
   */
  protected Painter painter;

  /**
   * List of probeable displays.
   */
  protected ArrayList probeables = new ArrayList();

  /**
   * The JFrame in which this DisplaySurface is displayed.
   */
  protected JFrame frame;

  /**
   * Whether or not this DisplaySurface needs to be updated.
   */
  protected boolean needsUpdate = true;

  /**
   * Filename for snapshots taken of this display surface.
   */
  protected String snapshotFile = null;

  /**
   * The model associated with this display surface.
   */
  protected SimModel model;

  /**
   * Dictionary of the Displays contained by this DisplaySurface. The menu
   * text associated with the display is the key and the display itself
   * is the value.
   */
  protected Hashtable displays = new Hashtable();

  /**
   * The option menu.
   */
  protected JMenu menu = new JMenu("Options");

  /**
   * The menu bar for the DisplaySurface JFrame
   */
  protected JMenuBar bar;

  /**
   * The name of this display.
   */
  protected String name = "";

  protected MovieMaker movieMaker;
  protected Legend legend;

  /**
   * List of keyListeners for this DisplaySurface.
   */
  protected ArrayList keyListeners = new ArrayList();
  protected ArrayList zoomables = new ArrayList();
  protected Point location = null;
  protected Dimension size, defaultSize;

  /**
   * This is set to true via a mouse motion listener on the frame.
   */
  protected boolean mousePressed = false;

  class Rect {
    int x, y, width, height;

    public String toString() {
      return "x: "
              + x
              + ", y: "
              + y
              + ", width: "
              + width
              + ", height: "
              + height;
    }
  }

  protected Rect zoomRect = new Rect();

  protected WindowAdapter dsWindowAdapter = new WindowAdapter() {
    public void windowIconified(WindowEvent evt) {
      needsUpdate = false;
      ViewEvent event =
              new ViewEvent(this, Displayable.TOGGLE_UPDATE_LAYOUT, false);
      for (int i = 0; i < painter.displayables.size(); i++) {
        Displayable d = (Displayable) painter.displayables.get(i);
        d.viewEventPerformed(event);
      }
    }

    public void windowDeiconified(WindowEvent evt) {
      needsUpdate = true;
      ViewEvent event =
              new ViewEvent(this, Displayable.TOGGLE_UPDATE_LAYOUT, true);
      for (int i = 0; i < painter.displayables.size(); i++) {
        Displayable d = (Displayable) painter.displayables.get(i);
        d.viewEventPerformed(event);
      }
    }

    public void windowClosing(WindowEvent evt) {
      needsUpdate = false;
      frame.removeWindowListener(this);
      dispose();
      frame = null;
    }
  };

  protected MouseInputAdapter dsMouseAdapter = new MouseInputAdapter() {

    private boolean firstDrag = true;
    private Moveable moveable = null;
    private int origX, origY;
    private Probeable probeable = null;
    private int count = 0;
    //private int zoomX, zoomY;
    	
    // used to avoid the "jump" when first moving an object
    private int origXOffset, origYOffset;

    public void mouseClicked(MouseEvent evt) {
      int x = evt.getX();
      int y = evt.getY();

      // reset the zoomRect
      if (zoomRect.x != -1)
        eraseRect();
      zoomRect.x = -1;
      for (int i = 0; i < probeables.size(); i++) {
        Probeable p = (Probeable) probeables.get(i);
        ArrayList list = p.getObjectsAt(x, y);
        for (int j = 0; j < list.size(); j++) {
          Object o = list.get(j);
          if (o != null)
            ProbeUtilities.probe(o);
        }
      }
    }

    public void mouseDragged(MouseEvent evt) {
      if (evt.isControlDown()) {
        int x = evt.getX();
        int y = evt.getY();

        zoomRect.x = origX > x ? x : origX;
        zoomRect.y = origY > y ? y : origY;
        zoomRect.width = origX > x ? origX - x : x - origX;
        zoomRect.height = origY > y ? origY - y : y - origY;
        drawRect(zoomRect.x,
                zoomRect.y,
                zoomRect.width,
                zoomRect.height);

      } else {
        int x = evt.getX();
        int y = evt.getY();
        count++;
        if (firstDrag) {
          moveable = getMoveableAt(x, y);

          firstDrag = false;
        }

        if (moveable != null && count > 3) {
          if (x >= getWidth())
            x = getWidth() - 4;
          if (x <= 0)
            x = 4;
          if (y >= getHeight())
            y = getHeight() - 4;
          if (y <= 0)
            y = 4;
          probeable.setMoveableXY(moveable, x + origXOffset, y + origYOffset);
          //System.out.println("moveable: " + moveable);
          count = 0;
          updateDisplayDirect();

        }
      }
    }

    private Moveable getMoveableAt(int x, int y) {
      Moveable moveableAtXY = null;

      for (int i = probeables.size() - 1; i >= 0; i--) {
        probeable = (Probeable) probeables.get(i);
        ArrayList l = probeable.getObjectsAt(x, y);
        for (int j = l.size() - 1; j >= 0; j--) {
          if (l.get(j) instanceof Moveable) {
            moveableAtXY = (Moveable) l.get(j);
            //System.out.println("first drag moveable: " + moveable);
            break;
          }
        }

        if (moveableAtXY != null)
          break;
      }

      return moveableAtXY;
    }

    public void mousePressed(MouseEvent evt) {
      origX = evt.getX();
      origY = evt.getY();

      Moveable toMove = getMoveableAt(origX, origY);
            
      // account for both kinds of Drawables, storing the offset of
      // where the user clicked on the item (if it wasn't a drawble
      // it wouldn't be shown on the screen)
      if (toMove instanceof NonGridDrawable) {
        origXOffset = (int) (((NonGridDrawable) toMove).getX() * SimGraphics.getInstance().getXScale() - origX);
        origYOffset = (int) (((NonGridDrawable) toMove).getY() * SimGraphics.getInstance().getYScale() - origY);
      } else if (toMove instanceof Drawable) {
        origXOffset = (int) (((Drawable) toMove).getX() * SimGraphics.getInstance().getXScale()) - origX;
        origYOffset = (int) (((Drawable) toMove).getY() * SimGraphics.getInstance().getYScale()) - origY;
      } else {
        origXOffset = 0;
        origYOffset = 0;
      }

      firstDrag = true;
    }

    public void mouseReleased(MouseEvent evt) {
      if (moveable != null) {
        int x = evt.getX();
        int y = evt.getY();
        //System.out.println("released moveable: " + moveable);
        probeable.setMoveableXY(moveable, x + origXOffset, y + origYOffset);
        updateDisplayDirect();
        count = 0;
        moveable = null;
        probeable = null;
      }
    }
  };

  /**
   * Creates a DisplaySurface of the specified size and with the
   * specified model and the specified name. The size of the display surface
   * should be equal to
   * the size of the largest displayable is going to display. For example,
   * <code><br>Object2DDisplay display = new Object2DDisplay(someGrid);<br>
   * DisplaySurface ds = new DisplaySurface(display.getSize(), someModel,
   * "Display");<br></code>. The name appears in the title bar of the
   * actual screen window that contains the display.
   *
   * @param size  the size of the DisplaySurface
   * @param model the model associated with this display surface
   * @param name  the name that appears in the title bar of the physical
   *              display
   */

  public DisplaySurface(Dimension size, SimModel model, String name) {
    this(model, name);
    super.setSize(size);
  }

  /**
   * Creates a DisplaySurface for the specified model, with the specified
   * name and using the specified Painter. The name appears in the title bar of
   * the actual screen window that contains the display.<p>
   * <p/>
   * <b> Note</b> that specifying your own Painter is an advanced feature
   * and shouldn't be done unless you know what you are doing. Of course,
   * if you've written your own Painter then you probably do.
   *
   * @param model   the model associated with this display surface
   * @param name    the name that appears in the title bar of the physical
   *                display
   * @param painter the Painter object used to do the actual painting of
   *                the Displayables contained by this DisplaySurface.
   */
  public DisplaySurface(SimModel model, String name, Painter painter) {
    this(model, name);
    this.painter = painter;
  }

  /**
   * Creates a DisplaySurface for the specified model and with the specified
   * name. The name appears in the title bar of the actual screen window that
   * contains the display.
   *
   * @param model the model associated with this display surface
   * @param name  the name that appears in the title bar of the physical
   *              display
   */
  public DisplaySurface(SimModel model, String name) {
    this.model = model;
    this.name = name;
    addMouseListener(dsMouseAdapter);
    addMouseMotionListener(dsMouseAdapter);

    addKeyListener(new KeyAdapter() {
      boolean inZoom = false;

      public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_Z) {
          if (zoomRect.x != -1) {
            //System.out.println("zoom: " + zoomRect);
            for (int i = 0; i < zoomables.size(); i++) {
              Zoomable z = (Zoomable) zoomables.get(i);
              z.startZoom(zoomRect.x,
                      zoomRect.y,
                      zoomRect.width,
                      zoomRect.height);
            }

            inZoom = true;
            zoomRect.x = -1;
            updateDisplayDirect();
          }
        } else if (evt.getKeyCode() == KeyEvent.VK_R) {
          if (inZoom) {
            for (int i = 0; i < zoomables.size(); i++) {
              Zoomable z = (Zoomable) zoomables.get(i);
              z.endZoom();
            }

            inZoom = false;
            updateDisplayDirect();
          }
        }
      }
    });
  }

  /**
   * Sets the background color for this display
   *
   * @param c the background color
   */
  public void setBackground(Color c) {
    super.setBackground(c);
    painter.setBackgroundColor(c);
  }

  /**
   * Adds a Displayable to the list of displayables that are displayed when
   * {@link #updateDisplay() updateDisplay} is called.
   *
   * @param display the displayable to add
   * @param name    the name of the displayable (is shown under the view menu)
   */
  public void addDisplayable(Displayable display, String name) {
    buildPainter(display);
    addDisplay(display, name);
    painter.addDisplayable(display);
  }

  /**
   * Adds a Displayable to the list of displayables that are displayed when
   * {@link #updateDisplay() updateDisplay} is called.
   *
   * @param display the displayable to add
   * @param name    the name of the displayable (is shown under the view menu)
   * @param order   the integer specifying the display order of the displayable
   */
  public void addDisplayable(Displayable display, String name, int order) {
    if (painter == null) {
      Dimension size = display.getSize();
      super.setSize(size);
      painter = new LocalPainter(this, size.width, size.height);
    } else if (painter.surface == null) {
      Dimension size = display.getSize();
      super.setSize(size);
      painter.init(this, size.width, size.height);
    }
    addDisplay(display, name);
    painter.addDisplayable(new DisplayableOrder(display, order));
  }

  /**
   * Removes the specified Displayable from the list of displayables to
   * display
   *
   * @param display the displayable to remove
   */
  public void removeDisplayable(Displayable display) {
    painter.removeDisplayable(display);
    removeDisplay(display);
  }

  /**
   * Adds the specified Probeable to the list of probeables.
   *
   * @param probeable the probeable to add
   */
  public void addProbeable(Probeable probeable) {
    probeables.add(probeable);
  }

  /**
   * Removes the specified Probeable from the list of probeables.
   *
   * @param probeable the probeable to remove
   */
  public void removeProbeable(Probeable probeable) {
    probeables.remove(probeable);
  }

  /**
   * Removes the specified Displayable from the ist of displayables to
   * display. If this Displayable is also a Probeable it is removed from
   * that list as well.
   *
   * @param display the Displayable to remove
   */
  public void removeProbeableDisplayable(Displayable display) {
    removeDisplayable(display);
    if (display instanceof Probeable)
      removeProbeable((Probeable) display);
  }

  public void addZoomable(Zoomable zoomable) {
    zoomables.add(zoomable);
  }

  /**
   * Returns the default dimension of this DisplaySurface. The default
   * dimension is the size of the DisplaySurface before any persistent
   * or remembered size is applied.
   */
  public Dimension getDefaultSize() {
    return new Dimension(defaultSize);
  }

  /**
   * Adds the specified Displayable to the displayables list and the probeables
   * list.
   *
   * @param display the Displayable to add to this to DisplaySurface.
   * @param name    the name of the displayable to add. Name will be shown
   *                on the view menu
   * @throws java.lang.IllegalArgumentException
   *          if the specified object
   *          is not a probable and a displayable
   */
  public void addDisplayableProbeable(Displayable display, String name) {
    buildPainter(display);

    if (display instanceof Probeable) {
      probeables.add(display);

      painter.addDisplayable(display);
      addDisplay(display, name);
    } else {
      throw new IllegalArgumentException("Object is not a Probleable and a Displayable");
    }
  }

  class DUpdate implements Runnable {
    public void run() {
      painter.paint(getGraphics());
    }
  }

  DUpdate du = new DUpdate();

  /**
   * Updates the display. Painting all the displayables to the screen. This
   * method is typically added to the schedule with an interval of however
   * often the user wants the display to refresh.
   */
  public void updateDisplay() {

    if (needsUpdate) {
      try {
        SwingUtilities.invokeAndWait(du);
      } catch (InterruptedException ex) {
        // keep interuppting until the thread that called this method
        // i.e. BaseControler.runThread terminates. When that terminates
        // we are okay.
        Thread.currentThread().interrupt();
      } catch (InvocationTargetException ex) {
        ex.getTargetException().printStackTrace();
      }
    }
  }

  /**
   * Updates the display. Painting all the displayables to the screen. Use
   * this when you want to update the display in response to a gui event:
   * button press, key typed, etc. Use updateDisplay to update the display
   * from within your model.
   */
  public void updateDisplayDirect() {
    painter.paint(getGraphics());
  }

  /**
   * This method builds a painter if there isn't already one for this surface.
   * If there is one and it isn't initialized it initializes it.
   *
   * @param display the display to base the painter on (can be null in which
   *                case the default size of the painter is 200, 200)
   */
  private void buildPainter(Displayable display) {
    Dimension painterSize;
    if (display != null)
      painterSize = display.getSize();
    else
      painterSize = new Dimension(200, 200);

    if (painter == null) {
      super.setSize(painterSize);
      painter = new LocalPainter(this, painterSize.width, painterSize.height);
    } else if (painter.surface == null) {
      super.setSize(painterSize);
      painter.init(this, painterSize.width, painterSize.height);
    }
  }

  private void drawRect(int left, int top, int width, int height) {
    if (needsUpdate) {
      painter.drawRect(getGraphics(), left, top, width, height);
    }
  }

  private void eraseRect() {
    if (needsUpdate) {
      painter.eraseRect(getGraphics());
    }
  }

  /**
   * Paints this surface.
   */

  public void paint(Graphics g) {
    painter.paint(g);
    //System.out.println("Painting");
  }

  public void paintComponents(Graphics g) {
    painter.paint(g);
    //System.out.println("Painting C");
  }

  public void paintAll(Graphics g) {
    painter.paint(g);
    //System.out.println("Painting A");
  }

  public void update(Graphics g) {
    paint(g);
  }

  private void removeDisplay(Displayable d) {
    for (Iterator iter = displays.entrySet().iterator(); iter.hasNext();) {
      Map.Entry entry = (Map.Entry) iter.next();
      DisplayInfo info = (DisplayInfo) entry.getValue();
      if (info.getDisplayable().equals(d)) {
        String menuText = (String) entry.getKey();
        for (int i = 0, n = menu.getItemCount(); i < n; i++) {
          JMenuItem item = menu.getItem(i);
          if (item.getText().equals(menuText)) {
            menu.remove(i);
            break;
          }
        }
      }
    }
  }

  private void addDisplay(Displayable d, String name) {
    buildPainter(d);

    ArrayList infoList = d.getDisplayableInfo();
    for (int i = 0; i < infoList.size(); i++) {
      DisplayInfo info = (DisplayInfo) infoList.get(i);
      String menuText = info.getMenuText();
      if (menuText.length() == 0) {
        menuText = name;
      } else {
        menuText = "(" + name + ")" + " " + menuText;
      }

      if (displays.containsKey(menuText)) {
        throw new IllegalArgumentException("Display Surface already contains a menu item with this menu text");
        //System.exit(0);
      }

      JCheckBoxMenuItem item = new JCheckBoxMenuItem(menuText, true);
      item.addActionListener(viewAction);
      item.setActionCommand(menuText);
      menu.setMnemonic('o');
      menu.add(item);
      displays.put(menuText, info);
    }
  }

  private Action viewAction = new AbstractAction("") {
    public void actionPerformed(ActionEvent evt) {
      JCheckBoxMenuItem item = (JCheckBoxMenuItem) evt.getSource();
      DisplayInfo info =
              (DisplayInfo) displays.get(item.getActionCommand());
      ViewEvent event =
              new ViewEvent(this, info.getId(), item.isSelected());
      info.getDisplayable().viewEventPerformed(event);
      repaint();
    }
  };

  public boolean isFrameVisible() {
    if (frame == null)
      return false;
    return frame.isVisible();
  }

  /**
   * Sets the screen location for this OpenGraph.
   *
   * @param x the x screen coordinate
   * @param y the y screen coordinate
   */
  public void setLocation(int x, int y) {
    location = new Point(x, y);
    if (frame != null)
      frame.setLocation(location);
  }

  private Action defaultSizeAction =
          new AbstractAction("Resize to Default") {
            public void actionPerformed(ActionEvent evt) {
              if (frame != null) {
                setSize(defaultSize);
                componentResized(null);
                frame.pack();
              }
            }
          };

  /**
   * Displays this DisplaySurface, that is, makes it visible on the screen in
   * a JFrame etc.
   */
  public void display() {
    // make sure the painter is ready to paint (only necessary if there are 
    // no displayables yet for this display
    buildPainter(null);

    frame = FrameFactory.createFrame(name);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(this, BorderLayout.CENTER);
    frame.getRootPane().setDoubleBuffered(false);
    frame.addWindowListener(dsWindowAdapter);
    bar = new JMenuBar();
    menu.addSeparator();
    JMenuItem item = new JMenuItem(defaultSizeAction);
    menu.add(item);
    bar.add(menu);

    frame.addComponentListener(this);

    frame.addMouseMotionListener(new MouseInputAdapter() {
      // implements java.awt.event.MouseListener
      public void mousePressed(MouseEvent e) {
        mousePressed = true;

      }

      // implements java.awt.event.MouseListener
      public void mouseReleased(MouseEvent e) {
        mousePressed = false;
      }

    });

    frame.setJMenuBar(bar);
    frame.pack();
    defaultSize = new Dimension(getWidth(), getHeight());
    Rectangle bounds = FrameFactory.getBounds(name);
    if (location != null) {
      // user specified location overrides persistent location
      frame.setLocation(location);
      if (bounds != null)
        frame.setSize(bounds.getSize());
    } else if (bounds != null) {
      frame.setBounds(bounds);
    }

    frame.setVisible(true);

    if (legend != null) {
      legend.display();
    }

    if (movieMaker != null) {
      this.addMovieFrame();
    }

    if (keyListeners.size() > 0) {
      for (int i = 0; i < keyListeners.size(); i++) {
        frame.addKeyListener((KeyListener) keyListeners.get(i));
      }

      keyListeners.clear();
    }
  }

  /**
   * Dispose this DisplaySurface
   */
  public void dispose() {
    if (painter != null) {
      painter.dispose();
      painter = null;
    }
    if (frame != null) {
      frame.getContentPane().remove(this);
      frame.remove(menu);
      menu.removeAll();

      frame.dispose();
      frame = null;
    }

    if (legend != null) {
      legend.dispose();
    }
    
    if (probeables != null) probeables.clear();
    displays.clear();
    keyListeners.clear();
    zoomables.clear();

    removeAll();

    probeables = null;
    model = null;
    menu = null;

    System.gc();
  }

  public void createLegend(String title) {
    legend = new Legend(title);
  }

  public void addLegendLabel(String label,
                             int iconType,
                             Color color,
                             boolean hollow) {
    if (legend == null) {
      System.err.println("Must createLegend before adding labels to it");
      return;
    }

    legend.addLegend(label, iconType, color, hollow);
  }

  public void addLegendLabel(String label,
                             int iconType,
                             Color color,
                             boolean hollow,
                             int iconWidth,
                             int iconHeight) {
    if (legend == null) {
      System.err.println("Must createLegend before adding labels to it");
      return;
    }

    legend.addLegend(label, iconType, color, hollow, iconWidth, iconHeight);
  }

  /**
   * Sets the name and type of a movie. Currently type can only be
   * DisplaySurface.QUICK_TIME.
   *
   * @param fileName  the name of the movie
   * @param movieType the type of movie (e.g. DisplaySurface.QUICK_TIME)
   */
  public void setMovieName(String fileName, String movieType) {
    Dimension d = getSize();
    if (movieType.equals(QUICK_TIME)) {
      fileName = fileName + ".mov";
      movieMaker =
              new MovieMaker(d.width, d.height, 1, fileName, movieType);
    } else {
      SimUtilities.showMessage("Movie type " + movieType + " is unsupported");
    }
  }

  /**
   * Adds the currently displayed image as frame to a movie. setMovieName must
   * be called before this method is called.
   */
  public void addMovieFrame() {
    if (movieMaker == null) {
      System.err.println("Unable to create frame - use setMovieFileName first");
      return;
    }

    painter.paint(getGraphics());
    movieMaker.addImageAsFrame(painter.getCurrentImage());
  }

  /**
   * Closes the movie, writing any remaining frames to the file. This must
   * be called if making a movie.
   */
  public void closeMovie() {
    if (movieMaker != null) {
      movieMaker.cleanUp();
    }
  }

  /**
   * Returns the options menu for this DisplaySurface.
   */
  public JMenu getOptionsMenu() {
    return menu;
  }

  /**
   * Returns the JFrame for this DisplaySurface.
   */
  public JFrame getFrame() {
    return frame;
  }

  /**
   * Sets the snapshot file name.
   *
   * @param fileName then file name to record to image to.
   */
  public void setSnapshotFileName(String fileName) {
    snapshotFile = fileName;
  }

  /**
   * Takes a snapshot of the current displayed image and writes it to
   * the file specified by setSnapshotFileName(String fileName). The name of
   * the file has the tickcount appended to it, as well as an appropriate
   * extension. For example, a fileName of 'SugarScape', and calling
   * takeSnapShot every 25 ticks would generate files like the following:
   * SugarScape25.gif, SugarScape50.gif, and so on.
   */

  public void takeSnapshot() {
    if (snapshotFile == null) {
      System.err.println("file not defined - use setSnapshotFileName");
      return;
    }

    // repaint so most current
    painter.paint(getGraphics());
    //String file = snapshotFile + model.getTickCountDouble() + ".gif";
    String file = snapshotFile + model.getTickCount() + ".png";
    DataOutputStream os = null;
    try {
      os = new DataOutputStream(new FileOutputStream(file));
      painter.takeSnapshot(os);
    } catch (java.io.IOException ex) {
      SimUtilities.showError("Unable to create output stream for snapshot image",
              ex);
      ex.printStackTrace();
    } finally {
      try {
        if (os != null) os.close();
      } catch (Exception ex) {
        SimUtilities.showError("Unable to close output stream for snapshot image",
                ex);
        ex.printStackTrace();
      }
    }

  }

  /**
   * Gets the preferred size of this DisplaySurface.
   */

  public Dimension getPreferredSize() {
    return getSize();
  }

  public void addKeyListener(KeyListener listener) {
    if (frame != null)
      frame.addKeyListener(listener);
    else
      keyListeners.add(listener);
  }

  public void removeKeyListener(KeyListener listener) {
    if (frame != null)
      frame.removeKeyListener(listener);
    else
      keyListeners.remove(listener);
  }

  /**
   * Repaints the display on a pause or a stop event. Consequently, adding
   * a DisplaySurface as SimEventListener to a SimModel causes the display
   * to update whenever a simulation run is paused or stopped.
   */
  public void simEventPerformed(SimEvent evt) {
    int id = evt.getId();
    if (id == SimEvent.PAUSE_EVENT || id == SimEvent.STOP_EVENT) {
      // Updating the display will move the display out of synch with
      // the schedule, i.e. the displays gets updated in an order
      // different than that defined by the schedule. For this reason,
      // I've commented out the repaint. However, anything that is displayed
      // changes its state in the schedule before that schedule calls
      // updateDisplay(), probing won't work correctly. The current
      // state of the display will be out of synch with the state of what
      // it displays.
      //repaint();
    }
  }

  // ComponentListener interface.
  public void componentShown(ComponentEvent e) {
  }

  public void componentHidden(ComponentEvent e) {
  }

  public void componentMoved(ComponentEvent e) {
  }

  // !!! this is called by the defaultSizeAction with
  // a null parameter. So, if e is used here, then
  // defaultSizeAction will have to be changed !!!!
  public void componentResized(ComponentEvent e) {

    // we only want to do the resize after the drag to
    // make the frame smaller or larger is finished (i.e.
    // the mouse button has been released.
    if (!mousePressed) {

      int width = getWidth();
      int height = getHeight();

      painter.reSize(width, height);
      updateDisplayDirect();
    }
  }
  
  /**
   * @author Femke
   * @version $Revision$ $Date$
   */
  public class DisplayableOrder implements Comparable {

    int order;
    Displayable displayble;

    public DisplayableOrder(Displayable d, int o) {
      this.displayble = d;
      this.order = o;
    }

    public int compareTo(Object obj) {
      DisplayableOrder other = (DisplayableOrder) obj;
      return this.order - other.order;
    }

    public Displayable getDisplayable() {
      return displayble;
    }
  }
}
