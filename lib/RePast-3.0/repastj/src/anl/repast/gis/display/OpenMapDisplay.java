/*$$
 * Copyright (c) 2004, Repast Organization for Architecture and Design (ROAD)
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
 * Neither the name of the ROAD nor the names of its
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
package anl.repast.gis.display;

import anl.repast.gis.OpenMapAgent;
import anl.repast.gis.data.OpenMapData;
import com.bbn.openmap.*;
import com.bbn.openmap.app.OpenMap;
import com.bbn.openmap.dataAccess.shape.*;
import com.bbn.openmap.event.DistanceMouseMode;
import com.bbn.openmap.event.NavMouseMode2;
import com.bbn.openmap.event.NullMouseMode;
import com.bbn.openmap.event.SelectMouseMode;
import com.bbn.openmap.gui.*;
import com.bbn.openmap.gui.MenuBar;
import com.bbn.openmap.layer.GraticuleLayer;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.layer.location.LocationLayer;
import com.bbn.openmap.layer.shape.ShapeLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoint;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.ProjectionStack;
import uchicago.src.sim.gui.MovieMaker;
import uchicago.src.sim.gui.MediaProducer;
import uchicago.src.sim.util.SimUtilities;
import uchicago.src.sim.engine.SimModel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.io.FileOutputStream;
import java.io.DataOutputStream;

/**
 * @author Robert Najlis
 */
public class OpenMapDisplay implements MediaProducer {

  OpenMapFrame frame;
  BasicMapPanel mapPanel;
  MapBean mapBean;
  MapHandler mapHandler;
  ShapeLayer shapeLayer;
  LayerHandler layerHandler;
  Layer[] layers;
  LayersMenu layersMenu;
  LayersPanel layersPanel;
  PropertyHandler propertyHandler;
  OpenMap openMap;
  java.awt.Color highlightColor;
  GoToMenu goTo;
  
  private MovieMaker movieMaker;
  private SimModel model;
  private String snapshotFile;

  /**
   * Constructor for OpenMapDisplay, no title
   */
  public OpenMapDisplay() {
    this("");
  }

  /**
   * Constructor for OpenMapDisplay, set title for OpenMap frame default background color
   */
  public OpenMapDisplay(String title) {
    this(title, new Color(137, 197, 249, 255));
  }

  /**
   * Constructor for OpenMapDisplay, set title for OpenMap frame, set background color
   *
   * @param title
   * @param backgroundColor
   */
  public OpenMapDisplay(String title, Color backgroundColor) {
    this(title, new Color(137, 197, 249, 255), false);
  }

  /**
   * Constructor for OpenMapDisplay, set title for OpenMap frame, set background color,
   * can also set true if you want to use the openmap.properties file.  Not recommended unless you
   * understand how openmap.properies files work
   *
   * @param title
   * @param backgroundColor
   */
  public OpenMapDisplay(String title, Color backgroundColor, boolean usePropertyFile) {


    this.setHighlightColor(Color.YELLOW);
    try {

      frame = new OpenMapFrame(title);
      // Size the frame appropriately
      frame.setSize(800, 600);
      frame = new OpenMapFrame(title);
	        
      // The BasicMapPanel automatically creates many
      // default components, including the MapBean and the
      // MapHandler.  You can extend the BasicMapPanel class if
      // you like to add different functionality or different
      // types of objects.
      //mapPanel = (BasicMapPanel) openMap.getMapPanel();
      mapPanel = new BasicMapPanel();
      //mapPanel.setPropertyHandler(propertyHandler);
      // Get the default MapHandler the BasicMapPanel created.
      mapHandler = mapPanel.getMapHandler();

      // Get the default MapHandler the BasicMapPanel created.
      //   mapHandler = mapPanel.getMapHandler();
      mapHandler.add(frame);

      // Get the default MapBean that the BasicMapPanel created.
      mapBean = mapPanel.getMapBean();
      mapBean.setBackgroundColor(backgroundColor);
				 
//				 Create and add a LayerHandler to the MapHandler.  The
      // LayerHandler manages Layers, whether they are part of the
      // map or not.  layer.setVisible(true) will add it to the map.
      // The LayerHandler has methods to do this, too.  The
      // LayerHandler will find the MapBean in the MapHandler.
      layerHandler = new LayerHandler();
      layerHandler.addLayerListener(mapBean);
      layerHandler.removeAll();
				 
				
				 
      //PropertyHandler ph = mapPanel.getPropertyHandler();
				
	            
      mapHandler.add(new MouseDelegator(this.mapBean));

      // Add MouseMode.  The MouseDelegator will find it via the
      // MapHandler.
      //  Adding NavMouseMode first makes it active.
      mapHandler.add(new NavMouseMode2());
      mapHandler.add(new DistanceMouseMode());
      mapHandler.add(new SelectMouseMode("Gestures", true));
      mapHandler.add(new MouseModePanel());
      mapHandler.add(new MouseModeButtonPanel());
      mapHandler.add(new NullMouseMode());

      mapHandler.add(new ScaleTextPanel());
      //menus
      mapHandler.add(new ControlMenu());
      mapHandler.add(new NavigateMenu());
      mapHandler.add(new NavigatePanel());
      mapHandler.add(new ZoomPanel());
      mapHandler.add(new MenuBar());
      mapHandler.add(new InformationDelegator());
      mapHandler.add(new ToolPanel());
      mapHandler.add(new ProjectionStack());
      mapHandler.add(new ProjectionStackTool());
      goTo = new GoToMenu();
      goTo.setMap(mapBean);
      goTo.addDefaultLocations();
      mapHandler.add(goTo);
      layersMenu = new LayersMenu(layerHandler, "Layers", LayersMenu.LAYERS_ON_OFF);
      mapHandler.add(layersMenu);

      layersPanel = new LayersPanel(layerHandler);

      LayerControlButtonPanel lcbp = new LayerControlButtonPanel(layersPanel);
      lcbp.removeLayersPanel(layersPanel);
      layersMenu.setupEditLayersButton(layersPanel);
      layersPanel.setControls(lcbp);
      layersPanel.updateLayerLabels();
      mapHandler.add(layersPanel);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the SimModel, if any, associated with this OpenMapDisplay.
   * @return
   * 
   */ 
  public SimModel getModel() {
    return model;
  }

  /**
   * Sets the SimModel to associate with this OpenMapDisplay.
   * 
   * @param model the model to associate with this OpenMapDisplay
   * 
   */ 
  public void setModel(SimModel model) {
    this.model = model;
  }

  /**
   * add the Graticule layer to the map (shows lat/lon lines over the map)
   */
  public void addGraticuleLayer() {
    GraticuleLayer graticuleLayer = new GraticuleLayer();
    graticuleLayer.setShowRuler(true);
    graticuleLayer.setConsumeEvents(false);
    Layer[] layers = layerHandler.getLayers();
    layerHandler.addLayer(graticuleLayer, layers.length);
  }

  /**
   * center the map over a given LatLonPoint
   *
   * @param center
   */
  public void centerMap(LatLonPoint center) {
    mapBean.setCenter(center);
  }

  /**
   * set the scale of the map display
   *
   * @param scale
   */
  public void setMapScale(float scale) {
    mapBean.setScale(scale);
  }

  /**
   * add a Layer of GisAgents to the map display
   *
   * @param gisAgents
   * @return ShapeLayer
   */
  public RepastOMLayer addLayer(List gisAgents, String title) {
    return this.addLayer(gisAgents, title, this.getHighlightColor());
  }

  /**
   * add a Layer of GisAgents to the map display
   * set the highlight color for use when the mouse moves over a an agent on the map
   *
   * @param gisAgents
   * @return ShapeLayer
   */
  public RepastOMLayer addLayer(List gisAgents, String title, java.awt.Color highlightColor) {
    RepastOMLayer layer = new RepastOMLayer(this, title);

    try {

      OMGraphicList omList = new OMGraphicList();
      for (Iterator iter = gisAgents.iterator(); iter.hasNext();) {
        OpenMapAgent omd = (OpenMapAgent) iter.next();
        OMGraphic omg = omd.getOMGraphic();
        omg.setFillPaint(((OpenMapAgent) omd).getFillPaint());
        omList.add(omg);
      }
      Properties layerProps = new Properties();
      layerProps.put("prettyName", title);
      layerProps.put("lineColor", "000000");
      layerProps.put("fillColor", "BDDE83");
      layer.setProperties(layerProps);
      layerHandler.addLayer(layer, 0);
      layerHandler.setLayers();
      mapHandler.add(layer);

      String[] mmi = new String[1];
      mmi[0] = "Gestures";
      layer.setMouseModeIDsForEvents(mmi);
      layer.setName(title);
      layersMenu.updateLayerLabels();
      layer.setList(omList);
      layer.setHighlightFillPaint(highlightColor);
      layer.setVisible(true);
      layer.setConsumeEvents(true);
      layer.setAgentList(gisAgents);
      layer.doPrepare();

      OpenMapData gisData = OpenMapData.getInstance();

      LatLonPoint center = gisData.getCenter(gisData.buildEsriGraphicList(gisAgents));

      Properties layerGoTo = new Properties();
      layerGoTo.put("name", title);
      layerGoTo.put("latitude", "" + center.getLatitude());
      layerGoTo.put("longitiude", "" + center.getLongitude());

      layerGoTo.put("projection", "" + this.getProjection());
      layerGoTo.put("scale", "5f");
      goTo.addLocationItem(title, layerGoTo);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return layer;
  }


  /**
   * update all layers in the OpenMapDisplay
   */
  public void updateDisplay() {
    Layer[] layers = layerHandler.getLayers();
    for (int i = 0; i < layers.length; i++) {
      if (layers[i] instanceof OMGraphicHandlerLayer) {
        ((OMGraphicHandlerLayer) layers[i]).doPrepare();
      } else if (layers[i] instanceof LocationLayer) {
        ((LocationLayer) layers[i]).doPrepare();
      } else {
        layers[i].repaint();
      }
    }
  }

  /**
   * update the Layer
   * returns true if able to update
   *
 
   * @param layerName
   * @return
   */
  public boolean updateLayer(List gisAgents, String layerName) {
    Layer layer = this.getLayer(layerName);
    if (layer instanceof RepastOMLayer) {
      ((RepastOMLayer) layer).setAgentList(gisAgents);
      ((RepastOMLayer) layer).doPrepare();
      return true;
    }
    return false;
  }


  /**
   * return the specified layer
   *
   * @param layerName
   * @return Layer
   */
  public Layer getLayer(String layerName) {
    Layer[] layers = layerHandler.getLayers();
    for (int i = 0; i < layers.length; i++) {
      if (layers[i].getName().equalsIgnoreCase(layerName)) {
        return layers[i];
      }
    }
    return null;
  }

  /**
   * Takes a Collection of GisAgents, gets the OMGraphic from each, and attempts to build
   * an EsriGraphicList from them.
   * <p/>
   * Requires that the GisAgents OMGraphics can be casted to EsriGraphic
   *
   * @param gisAgents
   * @return EsriGraphicList
   */
  public EsriGraphicList buildEsriGraphicList(Collection gisAgents) {
    EsriGraphicList egList = null;
    OMGraphic omg = ((OpenMapAgent) gisAgents.iterator().next()).getOMGraphic();
    if (omg instanceof OMPoint) { // point
      egList = new EsriPointList();
    } else if (omg instanceof EsriPolyline) { // polyline
      egList = new EsriPolylineList();
    } else { // shpType == 5 (polygon)
      egList = new EsriPolygonList();
    }

    // add esri graphics from agents
    Iterator omIterator = gisAgents.iterator();
    while (omIterator.hasNext()) {

      OpenMapAgent om = (OpenMapAgent) omIterator.next();
      egList.add(om.getOMGraphic());
    }
    return egList;
  }

  /**
   * get the current OpenMapProjection
   *
   * @return Projection
   */
  public Projection getProjection() {
    return mapBean.getProjection();
  }

  /**
   * get the current projection for the specified layer
   *
   * @param layerName
   * @return Projection
   */
  public Projection getProjection(String layerName) {
    Layer layer = getLayer(layerName);
    return layer.getProjection();
  }

  /**
   * get the current projection for the specified layer
   *
   * @param layer
   * @return Projection
   */
  public Projection getProjection(Layer layer) {
    return layer.getProjection();
  }


  /**
   * convert an OpenMap LatLonPoint to a point object
   * this is really a conversion from the LatLonPoint on a map
   * to the x,y location used to display that LatLonPoint on the display
   *
   * @param llp
   * @return Point
   */
  public java.awt.Point latLonToPoint(LatLonPoint llp) {
    Projection proj = mapBean.getProjection();
    return proj.forward(llp);
  }

  /**
   * convert a Point object to a LatLonPoint
   * this is really a conversion from the x,y location used to display a point on the display
   * to the that LatLonPoint on the display
   *
   * @param p
   * @return Point
   */
  public LatLonPoint pointToLatLon(Point p) {
    Projection proj = mapBean.getProjection();
    return proj.inverse(p);
  }

  /**
   * convert x, y points  to a LatLonPoint
   * this is really a conversion from the x,y location used to display a point on the display
   * to the that LatLonPoint on the display
   *
   * @param x
   * @param y
   * @return Point
   */
  public LatLonPoint xyToLatLon(int x, int y) {
    Projection proj = mapBean.getProjection();
    return proj.inverse(x, y);
  }

  /**
   * @param x
   * @param y
   * @return an ArrayList of all closest agents from all RepastOMLayers
   */
  public ArrayList findClosestAgents(int x, int y) {

    ArrayList agents = new ArrayList();
    Layer[] layers = layerHandler.getLayers();
    for (int i = 0; i < layers.length; i++) {
      if (layers[i] instanceof RepastOMLayer) {
        agents.add(((RepastOMLayer) layers[i]).findClosestAgent(x, y));
      }
    }
    return agents;
  }

  /**
   * given an x,y location returns list of closet agents from each layer
   *
   * @param x
   * @param y
   * @param limit
   * @return list of GisAgents
   */
  public ArrayList findClosestAgents(int x, int y, float limit) {

    ArrayList agents = new ArrayList();
    Layer[] layers = layerHandler.getLayers();
    for (int i = 0; i < layers.length; i++) {
      if (layers[i] instanceof RepastOMLayer) {
        agents.add(((RepastOMLayer) layers[i]).findClosestAgent(x, y, limit));
      }
    }
    return agents;
  }


  /**
   * adds a shapelayer using default settings
   * <p/>
   * this is just for display of the shapefile, it does not set any agent attributes
   *
   * @param datasource
   * @param title
   * @return
   */
  public ShapeLayer addShapeLayer(String datasource, String title) {
    return this.addShapeLayer(datasource, title, "000000", "BDDE83");
  }

  /**
   * adds a shapelayer.  Set line color and fillColor for the Features
   * <p/>
   * this is just for display of the shapefile, it does not set any agent attributes
   *
   * @param datasource
   * @param title
   * @return
   */
  public ShapeLayer addShapeLayer(String datasource, String title, String lineColor, String fillColor) {
    try {
      ShapeLayer layer = new ShapeLayer(datasource);
      Properties layerProps = new Properties();
      layerProps.put("prettyName", title);
      layerProps.put("lineColor", lineColor);
      layerProps.put("fillColor", fillColor);
      layer.setProperties(layerProps);
      mapHandler.add(layer);
      layerHandler.addLayer(layer);
      layersMenu.updateLayerLabels();
      layer.doPrepare();


    } catch (Exception e) {
      e.printStackTrace();
    }
    return shapeLayer;
  }

  /**
   * dispose of the OpenMap frame
   */
  public void dispose() {
    frame.dispose();
  }

  public OpenMapFrame getFrame() {
    return frame;
  }

  public void setFrame(OpenMapFrame frame) {
    this.frame = frame;
  }

  public LayerHandler getLayerHandler() {
    return layerHandler;
  }

  public void setLayerHandler(LayerHandler layerHandler) {
    this.layerHandler = layerHandler;
  }

  public Layer[] getLayers() {
    return layers;
  }

  public void setLayers(Layer[] layers) {
    this.layers = layers;
  }

  public MapBean getMapBean() {
    return mapBean;
  }

  public void setMapBean(MapBean mapBean) {
    this.mapBean = mapBean;
  }

  public MapHandler getMapHandler() {
    return mapHandler;
  }

  public void setMapHandler(MapHandler mapHandler) {
    this.mapHandler = mapHandler;
  }

  public BasicMapPanel getMapPanel() {
    return mapPanel;
  }

  public void setMapPanel(BasicMapPanel mapPanel) {
    this.mapPanel = mapPanel;
  }

  public java.awt.Color getHighlightColor() {
    return highlightColor;
  }

  public void setHighlightColor(java.awt.Color highlightColor) {
    this.highlightColor = highlightColor;
  }

  public OpenMap getOpenMap() {
    return openMap;
  }

  public void setOpenMap(OpenMap openMap) {
    this.openMap = openMap;
  }

  public PropertyHandler getPropertyHandler() {
    return propertyHandler;
  }

  public void setPropertyHandler(PropertyHandler propertyHandler) {
    this.propertyHandler = propertyHandler;
  }

  // implements MediaProducer
  /**
   * Sets the name and type of a movie. Currently type can only be
   * DisplaySurface.QUICK_TIME.
   *
   * @param fileName  the name of the movie
   * @param movieType the type of movie (e.g. DisplaySurface.QUICK_TIME)
   */
  public void setMovieName(String fileName, String movieType) {
    Dimension d = mapBean.getSize();
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

    updateDisplay();
    BufferedImage buffImage = mapBean.getGraphicsConfiguration().createCompatibleImage(mapBean.getWidth(),
            mapBean.getHeight());
    Graphics g = buffImage.getGraphics();
    mapBean.paint(g);
    g.dispose();
    movieMaker.addImageAsFrame(buffImage);
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

  public void setSnapshotFileName(String fileName) {
    snapshotFile = fileName;
  }

  public void takeSnapshot() {
    if (snapshotFile == null) {
      System.err.println("file not defined - use setSnapshotFileName");
      return;
    }

    // repaint so most current
    updateDisplay();
    
    String file;
    if (model != null) file = snapshotFile + model.getTickCount() + ".png";
    else file = snapshotFile + ".png";
    
    DataOutputStream os = null;
    try {
      os = new DataOutputStream(new FileOutputStream(file));
      try {
        BufferedImage buffImage = mapBean.getGraphicsConfiguration().createCompatibleImage(mapBean.getWidth(),
                mapBean.getHeight());
        Graphics g = buffImage.getGraphics();
        mapBean.paint(g);
        ImageIO.write(buffImage, "png", os);
        g.dispose();
      } catch (java.io.IOException ex) {
        SimUtilities.showError("Unable to write snapshot image to file", ex);
        //ex.printStackTrace();
      }
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
}