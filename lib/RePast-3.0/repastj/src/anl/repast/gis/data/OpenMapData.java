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
package anl.repast.gis.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureResults;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.geometry.Geometry;

import anl.repast.gis.GisAgent;
import anl.repast.gis.OpenMapAgent;
import anl.repast.gis.data.dbf.DBFReader;
import anl.repast.gis.data.dbf.DBFWriter;
import anl.repast.gis.data.dbf.JDBFException;
import anl.repast.gis.data.dbf.JDBField;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.MapBean;
import com.bbn.openmap.MapHandler;
import com.bbn.openmap.dataAccess.shape.DbfTableModel;
import com.bbn.openmap.dataAccess.shape.EsriGraphic;
import com.bbn.openmap.dataAccess.shape.EsriGraphicList;
import com.bbn.openmap.dataAccess.shape.EsriPoint;
import com.bbn.openmap.dataAccess.shape.EsriPointList;
import com.bbn.openmap.dataAccess.shape.EsriPolygon;
import com.bbn.openmap.dataAccess.shape.EsriPolygonList;
import com.bbn.openmap.dataAccess.shape.EsriPolyline;
import com.bbn.openmap.dataAccess.shape.EsriPolylineList;
import com.bbn.openmap.dataAccess.shape.EsriShapeExport;
import com.bbn.openmap.dataAccess.shape.input.ShpInputStream;
import com.bbn.openmap.dataAccess.shape.input.ShxInputStream;
import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.gui.BasicMapPanel;
import com.bbn.openmap.gui.OpenMapFrame;
import com.bbn.openmap.layer.shape.ShapeFile;
import com.bbn.openmap.layer.shape.ShapeLayer;
import com.bbn.openmap.omGraphics.DrawingAttributes;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoint;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.plugin.esri.EsriLayer;
import com.bbn.openmap.proj.Projection;

/**
 * @author Robert Najlis
 * 
 *  
 */
public class OpenMapData {

    OpenMapFrame frame;

    BasicMapPanel mapPanel;

    MapBean mapBean;

    MapHandler mapHandler;

    ShapeLayer shapeLayer;

    public static final int SHP_POINT = 0;

    public static final int SHP_POLYLINE = 3;

    public static final int SHP_POLYGON = 5;

    // 0 (point), 3 (polyline), or 5(polygon)

    private static OpenMapData instance = new OpenMapData();

    public OpenMapData() {
    }

    public static OpenMapData getInstance() {
        return instance;
    }

    /**
     * Reads in a file in the .GAL format created from GEODA
     * Geoda can give a list of neighbors for GIS data
     * <http://sal.agecon.uiuc.edu/geoda_main.php>
     * 
     * @param neighborhoodFile
     * @param gisAgents - a list of agents that implement the GisAgetn interface
     */
    public void readNeighborhoodInfo(String neighborhoodFile, ArrayList gisAgents) {
        BufferedReader in = null;
        try {
            Pattern p = Pattern.compile(" ");
            String[] data;
            //int lineCount = 0;
            String s = new String();

            in = new BufferedReader(new FileReader(neighborhoodFile));
            int agentNum = 0;
            in.readLine(); // deal with the header line -- just read it and do nothing
            while ((s = in.readLine()) != null) {
                data = p.split(s);
                agentNum = (Integer.parseInt(data[0]) - 1);

                if (Integer.parseInt(data[1]) > 0) {
                    // there are neighbors
                    // read the next line, parse it, and that to agent neighbor list
                    s = in.readLine();
                    data = p.split(s);

                    int[] neighbors = new int[data.length];
                    for (int i = 0; i < data.length; i++) {
                        neighbors[i] = (Integer.parseInt(data[i]) - 1); // have  to subtract  to acccount for  id #
                    }
                    ((GisAgent) gisAgents.get(agentNum)).setNeighbors(neighbors);
                } else {

                    s = in.readLine(); // read line but do nothing
                    int[] noNeighbors = new int[0];

                    ((GisAgent) gisAgents.get(agentNum)).setNeighbors(noNeighbors);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 
     * Create a list of GisAgents given a class type that implements the GisInterface, 
     * and a Shapefile datasource
     * 
     * @param clazz
     * @param datasource
     * @return ArrayList of GisAgents
     */
    public ArrayList createAgents(Class clazz, String datasource) {
        ArrayList agentList = new ArrayList();
        FileChannel in = null;
        DbaseFileReader dbfFileReader = null;
        try {

            String datasourceNoSHP = datasource.substring(0, datasource
                    .length() - 3);
            String dbfString = datasourceNoSHP + "dbf";

            in = new FileInputStream(dbfString).getChannel();
            dbfFileReader = new DbaseFileReader(in);
            DbaseFileHeader header = dbfFileReader.getHeader();
            int numRecords = header.getNumRecords();

            for (int i = 0; i < numRecords; i++) {
                //			 create one new obj for each feature
                Object obj = clazz.newInstance();
                ((GisAgent) obj).setGisAgentIndex(i);
                agentList.add(obj);

            }
            return updateAgentsFromShapefile(clazz, agentList, datasource);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dbfFileReader != null) {
                try {
                    dbfFileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

       return null;

    }

    /**
     * Update a list of GisAgents from a Shapefile
     * In case you have already created GisAgents, but want to update them from a Shapefile
     * 
     * @param clazz
     * @param agentList
     * @param datasource
     * @return
     */
    public ArrayList updateAgentsFromShapefile(Class clazz, ArrayList agentList, String datasource) {
        FeatureReader reader = null;

        try {

            String datasourceNoSHP = datasource.substring(0, datasource
                    .length() - 3);
            //String dbfString = datasourceNoSHP + "dbf";
            String shxString = datasourceNoSHP + ("shx");
            //URL shpURL = new File(datasource).toURL();
            //URL dbfURL = new File(dbfString).toURL();
            //URL shxURL = new File(shxString).toURL();

            FileInputStream shxIn = new FileInputStream(shxString);
            FileInputStream shpIn = new FileInputStream(datasource);

            ShxInputStream shxInStream = new ShxInputStream(shxIn);
            int[][] shxData = shxInStream.getIndex();

            ShpInputStream shpInStream = new ShpInputStream(shpIn);
            //int type;
            EsriGraphicList esriGraphicList = null;
            try {
                esriGraphicList = shpInStream.getGeometry(shxData);
                //  cast to correct type
                //type = esriGraphicList.getType();
            } catch (NullPointerException npe) {
                System.out.println("Error: OpenMap was not able to correctly read the shapefile\n  " +
                        datasource + "\n  Check the Projection, make sure that it \n" + 
                        "is an unprojected (lat/long) format. \n" +
                        "Sometimes this is also called WGS84  if that does not work, \n" +
                        "I try reading in the data with the GeoTools Data class. \n " +
                        "Data read in using the GeoToolsData class will not work with the OpenMapDisplay,  \n" + 
                        "so you will need to use the EsriDisplay class in conjunction with ArcGIS \n");
            }
          
            //Method[] methods = clazz.getMethods();

            int index = 0;
            Iterator esriGraphicIterator = esriGraphicList.iterator();
            int count = 0;
            while (esriGraphicIterator.hasNext()) {
                count++;
                Object[] params = new Object[1];
                OMGraphic eg = (OMGraphic) esriGraphicIterator.next();
               
               
                
                Method method = getSetMethodForName(clazz, "OMGraphic");
                if (method != null) {
                  
                    params[0] = eg;
                    method.invoke(agentList.get(index), params);
                }
                // this is for RepastPy
                Method methodGeom = getSetMethodForName(clazz, "Geometry");
                if (methodGeom != null) {
                    if (eg instanceof EsriPolygon) {
                        eg = (OMGraphic)eg;
                    }
                    else if (eg instanceof EsriPoint) {
                        eg = (EsriPoint)eg;
                    }
                    else if (eg instanceof EsriPolyline) {
                        eg = (OMGraphic)eg;
                    }
                    params[0] = eg;
                    methodGeom.invoke(agentList.get(index), params);
                }
                index++;
            }

            shxInStream.close();
            shxIn.close();
            shpIn.close();

            // data ...

            URL file = new File(datasource).toURL();
            ShapefileDataStore store = new ShapefileDataStore(file);
            String name = store.getTypeNames()[0]; // there is just one - one type per shapefile
                                                                     // - type of data in shapefile
            FeatureSource source = store.getFeatureSource(name);
            FeatureResults fsShape = source.getFeatures();

            //String[] types = store.getTypeNames();

            FeatureType ft = source.getSchema();
            reader = fsShape.reader();
            for (int i = 0; i < ft.getAttributeCount(); i++) {
                AttributeType at = ft.getAttributeType(i);
                if (Geometry.class.isAssignableFrom(at.getType())) {
                    continue;
                } else {
                    // for each attribute type -- see if there is a set method that
                // corresponds
                    Method method = this.getSetMethodForAttributeType(clazz, at);
                    // if there is invoke it for each object in the list
                    if (method != null) {
                        reader = fsShape.reader();
                        int j = 0;
                        while (reader.hasNext()) {
                            Feature feature = reader.next();
                            // invoke with data for each
                            Object[] params = new Object[1];
                            params[0] = feature.getAttribute(i);
                            method.invoke(agentList.get(j), params);
                            j++;
                        }

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return agentList;
    }

    
    /**
     * Looks into a Shapefile and returns an array of FieldNameAndType objects which 
     * show the names and types of fields in the Shapefile's dbf file
     * 
     * @param datasource
     * @return
     * @throws IOException
     */
    public FieldNameAndType[] interrogate(String datasource) throws IOException {
        FieldNameAndType[] fieldNamesTypes = null;
        DbaseFileReader dbfFileReader = null;
        FileChannel in = null;
        ShapeFile shapeFile = null;
        try {

            String datasourceNoSHP = datasource.substring(0, datasource
                    .length() - 3);
            shapeFile = new ShapeFile(datasource);
            String dbfString = datasourceNoSHP + "dbf";
            //String shxString = datasourceNoSHP + ("shx");
            //URL shpURL = new File(datasource).toURL();
            //URL dbfURL = new File(dbfString).toURL();
            //URL shxURL = new File(shxString).toURL();

            in = new FileInputStream(dbfString).getChannel();
            dbfFileReader = new DbaseFileReader(in);
            DbaseFileHeader header = dbfFileReader.getHeader();
            //int numRecords = header.getNumRecords();
            int numFields = dbfFileReader.getHeader().getNumFields();

            fieldNamesTypes = new FieldNameAndType[numFields + 1]; // allot one extra for geom
            int i = 0;
            for (; i < numFields; i++) {
                fieldNamesTypes[i] = new FieldNameAndType(header.getFieldName(i), header.getFieldClass(i).getName());

            }
            String geomType = "com.bbn.openmap.dataAccess.shape.";
            if (shapeFile.getShapeType() == 5) {
                geomType = "com.bbn.openmap.omGraphics.OMGraphic";

//                geomType += "EsriPolygon";
            } else if (shapeFile.getShapeType() == 1) {
                geomType += "EsriPoint";
            } else if (shapeFile.getShapeType() == 3) {
                geomType = "com.bbn.openmap.omGraphics.OMGraphic";
            } else if (shapeFile.getShapeType() == 0) {
                geomType = "null";
            } else
                geomType = "com.bbn.openmap.omGraphics.OMGraphic";

            fieldNamesTypes[i] = new FieldNameAndType("Geometry", "" + geomType);
            //System.out.println("Shape type : " + shapeFile.getShapeType());

            //   return fieldNamesTypes;
        } catch (IOException ex) {
            throw ex;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (dbfFileReader != null) {
                try {
                    dbfFileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (shapeFile != null) {
                try {
                    shapeFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return fieldNamesTypes;
    }

    /**
     * Sorts a Collection of GisAgents using the GisAgent's getGisAgentIndex function
     * 
     * @param gisAgents
     * @return
     */
    public Collection sortGisAgentsbyIndex(Collection gisAgents) {

        Collections.sort((List) gisAgents, new Comparator() {
            public int compare(Object a, Object b) {
                return (((GisAgent) a).getGisAgentIndex() < ((GisAgent) b)
                        .getGisAgentIndex() ? -1 : (((GisAgent) a)
                        .getGisAgentIndex() == ((GisAgent) b)
                        .getGisAgentIndex() ? 0 : 1));
            }
        });
        return gisAgents;
    }

    /**
     * 
     * write agents to the specified datasource
     * datasource should end with .shp.
     * Writes the .shp, .dbf and .shx files
     * 
     * Reads in the data from the shapefile, overwrites the original Shapefile data with 
     * data  from the GisAgent
     * 
     * In order to overwrite Shapefile data with GisAgent data, the GisAgent must implement a 
     * setDbfField() function where DbfField is the name of field in the .dbf file associated with the Shapefile 
     * 
     * Note: this version of the function will overwrite the original data.
     * To prevent this use the version of this function which takes two datasource parameters
     * 
     * @param agents
     * @param datasource
     */
    public void writeAgents(Collection agents, String datasource) {
        this.writeAgents(agents, datasource, datasource);
    }

    /**
     * write agents to the specified datasource
     * datasource should end with .shp.
     * Writes the .shp, .dbf and .shx files
     * 
     * Reads in the data from the shapefile, overwrites the original Shapefile data with 
     * data  from the GisAgent
     * 
     * In order to overwrite Shapefile data with GisAgent data, the GisAgent must implement a 
     * setDbfField() function where DbfField is the name of field in the .dbf file associated with the Shapefile 
     * 
     * Note: this version of the function will read data from the origDatasource, and use that data in 
     * combination with data from the GisAgent to write the newDatasource
     * 
     * 
     * 
     * @param gisAgents
     * @param origDatasource
     * @param newDatasource
     */
    public void writeAgents(Collection gisAgents, String origDatasource,
            String newDatasource) {

        writeShpUsingShp(gisAgents, origDatasource, newDatasource);
        FeatureArrayList featureList = buildFeatureArrayList(gisAgents,origDatasource);
        writeFeatureArrayListToDbf(gisAgents, featureList, origDatasource,newDatasource);

    }

    /**
     * A helper function for the writeAgents function
     * 
     * 
     * @param gisAgents
     * @param origDatasource
     * @param newDatasource
     */
    public void writeShpUsingShp(Collection gisAgents, String origDatasource,
            String newDatasource) {
        try {
            // go through each agent and set feature values + construct esri graphic list
            // find type of OMGraphic of agents - create esriGraphicList for that
            String origDatasourceNoSHP = origDatasource.substring(0,
                    origDatasource.length() - 3);
            String origDbfString = origDatasourceNoSHP + "dbf";
            String origShxString = origDatasourceNoSHP + "shx";
            //String newDatasourceNoSHP = newDatasource.substring(0,
            //       newDatasource.length() - 3);
            //String newDbfString = newDatasourceNoSHP + "dbf";
            URL shpURL = new File(origDatasource).toURL();
            URL dbfURL = new File(origDbfString).toURL();
            URL shxURL = new File(origShxString).toURL();
            EsriLayer esriLayer = new EsriLayer("createAgents", dbfURL, shpURL,shxURL, DrawingAttributes.DEFAULT);
            EsriGraphicList esriGraphicList = esriLayer.getEsriGraphicList();
            DbfTableModel dbfModel = esriLayer.getModel();

            //				 look into the orig shape file - see what shp type
            int shpType = esriLayer.getType(); //  0 (point), 3  (polyline), or 5(polygon)
            // construct list of that type
            EsriGraphicList egList = null;
            if (shpType == 0) { // point
                egList = new EsriPointList();
            } else if (shpType == 3) { // polyline
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
      //      System.out.println("wrtie agents  add om graphic   DONE");
            // write out esrigraphics
            String newDatasourceNoSHPNoDot = newDatasource.substring(0,
                    newDatasource.length() - 4);
            EsriShapeExport export = new EsriShapeExport(esriGraphicList,
                    dbfModel, newDatasourceNoSHPNoDot);
            export.setWriteDBF(false);

            export.export();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes agents created from OpenMap to a Shapefile (.shp, .dbf and .shx).
     * Must be GisAgents, and need to be of an OpenMap type that can be cast to either
     * EsriPoint, EsriPoly, or EsriPolygon (in other words GisAgents with
     * OMGraphic of type Location, OMArc, and OMCircle will not work with this function)
     * 
     * 
     * @param gisAgents
     * @param proj - use the OpenMapDisplay class to get the projection
     * @param datasource - Shapefile to write to (should end in .shp)
     */
    public void writeAgentsNoShp(Collection gisAgents, Projection proj, String datasource) {
        try {

            OMGraphicList omList = new OMGraphicList();
            Iterator omIterator = gisAgents.iterator();
            while (omIterator.hasNext()) {

                OpenMapAgent om = (OpenMapAgent) omIterator.next();
                omList.add(om.getOMGraphic());
            }
            String datasourceNoSHP = datasource.substring(0, datasource.length() - 4);
            EsriShapeExport export = new EsriShapeExport(omList, proj,datasourceNoSHP);
            export.setWriteDBF(false);
            export.export();

            writeDbfFromAgents(gisAgents, datasource);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * helper function for writeAgentsNoShp
     * writes the dbf file from a list of GisAgents
     * 
     * @param gisAgents
     * @param datasource
     * @return
     */
    public ArrayList writeDbfFromAgents(Collection gisAgents, String datasource) {
        DBFWriter writer = null;
        try {
            //ArrayList records = new ArrayList();

            Class clazz = null;
            if (gisAgents.size() > 0) {
                clazz = gisAgents.iterator().next().getClass();
            }

            // get list of methods from omPropertyLIst - every 2nd should be a
         // method
            GisAgent omd = (GisAgent) gisAgents.iterator().next();
            String[] propertyList = omd.gisPropertyList();
            String[] fieldNames = new String[propertyList.length / 2];
            String[] methodNames = new String[propertyList.length / 2];
            int fieldIndex = 0;
            // get name
            JDBField[] fields = new JDBField[propertyList.length / 2];
            for (int i = 0; i < propertyList.length; i += 2) {
                fieldNames[fieldIndex] = propertyList[i];
                methodNames[fieldIndex] = propertyList[i + 1];
                fieldIndex++;
            }

            char fieldType = 'C';
            int length = 20;
            int decimalCount = 0;
            for (int j = 0; j < fieldNames.length; j++) {
                // use reflection to get through agent class and configure fields correctly
                Method m = clazz.getMethod(methodNames[j], null);
                // use name from omPropList for name in dbf
                // check type:
                // if int - type N length 20 decimal count 0
                // if double type N length20, decimal count 11
                //if String type c length 254 decimal count 0
                // if boolean type L length 1 decimal count 0
                if (m.getReturnType().getName().equals("int")) {
                    fieldType = 'N';
                    length = 20;
                    decimalCount = 0;
                } else if (m.getReturnType().getName().equals("double")) {
                    fieldType = 'N';
                    length = 20;
                    decimalCount = 11;
                }
                if (m.getReturnType().getName().equals("boolean")) {
                    fieldType = 'L';
                    length = 1;
                    decimalCount = 0;
                }
                if (m.getReturnType().getName().equals("java.lang.String")) {
                    fieldType = 'C';
                    length = 254;
                    decimalCount = 0;
                }
                if (m.getReturnType().getName().equals("float")) {
                    fieldType = 'N';
                    length = 20;
                    decimalCount = 11;
                }
                fields[j] = new JDBField(fieldNames[j], fieldType, length,
                        decimalCount);
            }

            // now have fields go through each omd - match fields to methods - create entry
            // go thru each agent get methods create entry
            ArrayList entries = new ArrayList();

            if (gisAgents.size() > 0) {
                clazz = gisAgents.iterator().next().getClass();
            }
            Iterator iter = gisAgents.iterator();
            while (iter.hasNext()) {
                Object[] entry = new Object[fields.length];
                omd = (GisAgent) iter.next();
                for (int i = 0; i < entry.length; i++) {
                    Method method;

                    method = clazz.getMethod(methodNames[i], null);

                    entry[i] = method.invoke(omd, null);

                }
                entries.add(entry);
            }

            String datasourceNoSHP = datasource.substring(0, datasource
                    .length() - 3);
            String newDbfString = datasourceNoSHP + "dbf";
            writer = new DBFWriter(newDbfString, fields);

            int numEntries = entries.size();
            for (int i = 0; i < numEntries; i++) {

                writer.addRecord((Object[]) entries.get(i));

            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (JDBFException e) {
                    e.printStackTrace();
                }
            }
        }
        //  }

        return null;
    }

    public int getShpType(String datasource) {
        try {

            String origDatasourceNoSHP = datasource.substring(0, datasource
                    .length() - 3);
            String dbfString = origDatasourceNoSHP + "dbf";
            String shxString = origDatasourceNoSHP + "shx";
            URL shpURL = new File(datasource).toURL();
            URL dbfURL = new File(dbfString).toURL();
            URL shxURL = new File(shxString).toURL();
            EsriLayer esriLayer = new EsriLayer("createAgents", dbfURL, shpURL,
                    shxURL, DrawingAttributes.DEFAULT);
            return esriLayer.getType(); //  0 (point), 3 (polyline), or 5(polygon)
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    
    /**
     * 
     * Takes a Collection of GisAgents, gets the OMGraphic from each, and attempts to build 
     * an EsriGraphicList from them.  
     * 
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
     *  Takes a Collection of GisAgents, gets the OMGraphic from each, and attempts to build 
     * an EsriGraphicList from them.  Uses the shpType to set the type of EsriGraphic used for the list
     * 
     * Requires that the GisAgents OMGraphics can be casted to EsriGraphic
     * 
     * 
     * @param gisAgents
     * @param shpType
     * @return
     */
    public EsriGraphicList buildEsriGraphicList(Collection gisAgents,
            int shpType) {
        //int shpType = esriLayer.getType(); // 0 (point), 3 (polyline), or
      // 5(polygon)
        // construct list of that type
        EsriGraphicList egList = null;
        if (shpType == 0) { // point
            egList = new EsriPointList();
        } else if (shpType == 3) { // polyline
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
     * helper function for writeAgents
     * 
     * takes a Collection of GisAgents and a datasource and creates a FeatureArrayList to store the 
     * features
     * 
     * @param gisAgents
     * @param datasource
     * @return
     */
    public FeatureArrayList buildFeatureArrayList(Collection gisAgents,
            String datasource) {

        FeatureReader reader = null;
        try {
            Class clazz = null;
            if (gisAgents.size() > 0) {
                clazz = gisAgents.iterator().next().getClass();
            }

            // using gt to get features- add to list:

            //		    get feature info - features, types
            URL file = new File(datasource).toURL();

            ShapefileDataStore store = new ShapefileDataStore(file);
            String name = store.getTypeNames()[0]; // there is  just one - one type per  shapefile
                                                                     // - type  of data  in  shapefile
            FeatureStore featureStore = (FeatureStore) store
                    .getFeatureSource(name);
            FeatureResults fsShape = featureStore.getFeatures();

            // create a feature coolection to hold the features. then make a new
         // shapefile data store
            FeatureArrayList featureList = new FeatureArrayList();

            //					 get feature type to create new shapefile
            FeatureType ft = featureStore.getSchema();
            int numAttributes = ft.getAttributeCount();

            reader = fsShape.reader();
            for (Iterator iter = gisAgents.iterator(); iter.hasNext();) {
                Feature feature = reader.next();

                Object agent = iter.next();
                for (int i = 0; i < numAttributes; i++) {
                    AttributeType at = ft.getAttributeType(i);
                    if (Geometry.class.isAssignableFrom(at.getType())) {
                        continue;
                    }

                    else {
                        // for each attribute type -- see if there is a get method that corresponds
                        Method method = this.getGetMethodForAttributeType(
                                clazz, at);
                        // if there is invoke it for each object in the list
                        if (method != null) {
                            // invoke with data for each
                            feature.setAttribute(i, method.invoke(agent, null));
                        }
                    }
                }
                featureList.add(feature);
            }

            return featureList;
            // end gt for featureList
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * helper function for writeAgents
     * 
     * takes the FeatueArrayList and writes it to a dbf file
     * 
     * 
     * @param gisAgents
     * @param featureList
     * @param origDatasource
     * @param newDatasource
     */
    public void writeFeatureArrayListToDbf(Collection gisAgents, FeatureArrayList featureList, String origDatasource,
            String newDatasource) {
        DBFWriter writer = null;
        DBFReader dbfReader = null;
        try {

            Class clazz = null;
            if (gisAgents.size() > 0) {
                clazz = gisAgents.iterator().next().getClass();
            }

            ArrayList entryList = new ArrayList();

            int agentNum = -1;
            int numAttributes = ((Feature) featureList.get(0))
                    .getNumberOfAttributes();
            FeatureType featureType = ((Feature) featureList.get(0))
                    .getFeatureType();
            AttributeType[] attributeTypes = featureType.getAttributeTypes();
            
            for (Iterator iter = gisAgents.iterator(); iter.hasNext();) {
                Object agent = iter.next();
                agentNum++;
                // go through each column in dbf

                Object[] entry = new Object[numAttributes];
                Object[] featureEntry = featureList.getFeature(agentNum)
                        .getAttributes(entry);//dbfFileReader.readEntry();
                for (int i = 0; i < numAttributes; i++) {

                    String fieldName = attributeTypes[i].getName();

                    // for each attribute type -- see if there is a get method that
                // corresponds
                    Method method = this.getGetMethodForName(clazz, fieldName);
                    // if there is invoke it for each object in the list
                    if (method != null) {
                        // invoke with data for each

                        entry[i] = method.invoke(agent, null);
                    } else { // get entry value from feature list
                        entry[i] = featureEntry[i];
                    }

                }
                entryList.add(entry);
            }

            String origDatasourceNoSHP = origDatasource.substring(0,
                    origDatasource.length() - 3);
            String origDbfString = origDatasourceNoSHP + "dbf";
            dbfReader = new DBFReader(origDbfString);
            int numFields = dbfReader.getFieldCount();
            JDBField[] fields = new JDBField[numFields];
            for (int fieldIndex = 0; fieldIndex < numFields; fieldIndex++) {
                fields[fieldIndex] = dbfReader.getField(fieldIndex);
            }

            String newDatasourceNoSHP = newDatasource.substring(0,
                    newDatasource.length() - 3);
            String newDbfString = newDatasourceNoSHP + "dbf";
            writer = new DBFWriter(newDbfString, fields);

            int numEntries = entryList.size();

            for (int i = 0; i < numEntries; i++) {
                Object[] fEntry = (Object[]) entryList.get(i);
                Object[] entry = new Object[fEntry.length - 1];
                int entryIndex = 0;
                for (int index = 0; index < fEntry.length; index++) {

                    if (fEntry[index] instanceof com.vividsolutions.jts.geom.Geometry) {
                        continue;
                    } else {
                        entry[entryIndex] = fEntry[index];
                        entryIndex++;
                    }

                }

                writer.addRecord(entry);
            }
            //   writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (JDBFException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * for a given attribute in a datasource (.shp file) gets a set of all the attribute's values
     * 
     * 
     * @param attributeName
     * @param dataSource
     * @return
     */
    public HashSet getAttributeValues(String attributeName, String dataSource) {
        HashSet values = new HashSet();
        try {
            URL file = new File(dataSource).toURL();
            ShapefileDataStore store = new ShapefileDataStore(file);
            String name = store.getTypeNames()[0]; // there is just one - one
            														//  type per shapefile - type of data in shapefile
            FeatureSource source = store.getFeatureSource(name);
            FeatureResults fsShape = source.getFeatures();

            FeatureReader reader = fsShape.reader();
            while (reader.hasNext()) {
                Feature feature = reader.next();
                values.add(feature.getAttribute(attributeName));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAttributeException e) {
            e.printStackTrace();
        }
        return values;
    }

    
    /**
     * given a shapefile, returns an OpenMap EsriLayer
     * 
     * @param datasource
     * @return
     */
    public EsriLayer getEsriLayer(String datasource) {
        try {
            String datasourceNoSHP = datasource.substring(0, datasource
                    .length() - 3);
            String dbfString = datasourceNoSHP + "dbf";
            String shxString = datasourceNoSHP + ("shx");
            File shpFile = new File(datasource);
            String layerName = shpFile.getName().substring(0,
                    shpFile.getName().length() - 4);
            URL shpURL = shpFile.toURL();
            URL dbfURL = new File(dbfString).toURL();
            URL shxURL = new File(shxString).toURL();

            EsriLayer esriLayer = new EsriLayer(layerName, dbfURL, shpURL,
                    shxURL);

            return esriLayer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 
     * gets the extents for a shapefile
     * 
     * The lat/lon extent of the EsriGraphicList contents, returned as  miny, minx, maxy maxx in order of the array.
     * 
     * @param datasource
     * @return
     */
    public float[] getExtents(String datasource) {
        EsriLayer eLayer = getEsriLayer(datasource);
        return eLayer.getEsriGraphicList().getExtents();
    }

    /**
     * gets the extents for an EsriGraphicList
     * 
     * The lat/lon extent of the EsriGraphicList contents, returned as  miny, minx, maxy maxx in order of the array.
     * 
     * 
     * @param egList
     * @return
     */
    public float[] getExtents(EsriGraphicList egList) {
        return egList.getExtents();
    }

    
    /**
     * 
     * gets the extents for an OMGraphic
     * 
     * The lat/lon extent of the EsriGraphic contents, returned as  miny, minx, maxy maxx in order of the array.
     * 
     * 
     * @param omg
     * @return
     */
    public float[] getExtents(OMGraphic omg) {
        if (omg instanceof EsriGraphic) {
            return ((EsriGraphic) omg).getExtents();
        } else if (omg instanceof OMPoint) {
            float points[] = new float[2];
            points[0] = ((OMPoint) omg).getLat();
            points[1] = ((OMPoint) omg).getLon();
            return points;
        } else if (omg instanceof OMPoly) {
            EsriPolygon ep = EsriPolygon.convert((OMPoly) omg);
            return ep.getExtents();
        }
        return null;
    }

    /**
     * gets the center of the extents of the EsriGraphic
     * 
     * @param omd
     * @return
     */
    public LatLonPoint getCenter(EsriGraphic eg) {
        return getCenter(eg.getExtents());
    }

    /**
     * gets the center of the extents of the EsriGraphicList
     * 
     * @param omd
     * @return
     */
    public LatLonPoint getCenter(EsriGraphicList egList) {
        return getCenter(egList.getExtents());
    }

    /**
     * gets the center of the extents of the OMGraphic
     * 
     * Requires that the OMGraphic is an instance of EsriGraphic
     * 
     * @param omd
     * @return
     */
    public LatLonPoint getCenter(OpenMapAgent omd) {
        OMGraphic omg = omd.getOMGraphic();
        if (omg instanceof EsriGraphic) {
            return getCenter(((EsriGraphic) omg).getExtents());
        }
        return null;

    }

    /**
     * gets the center of the extents 
     * 
     * @param omd
     * @return
     */
    public LatLonPoint getCenter(float[] extents) {
        //extents: the lat/lon extent of the EsriGraphicList contents,
        //returned as miny, minx, maxy maxx in order of the array.
        LatLonPoint center = new LatLonPoint();

        center.setLatitude((extents[0] + extents[2]) / 2);
        center.setLongitude((extents[1] + extents[3]) / 2);
        return center;
    }

    /**
     * gets the center of the extents of the datasource
     * 
     * Requires that the datasource is a Shapefile
     * 
     * @param omd
     * @return
     */
    public LatLonPoint getCenter(String datasource) {
        float[] extents = this.getExtents(datasource);
        if (extents == null)
            return null;
        return getCenter(extents);
    }

    
    /**
     * 
     * Gets the area for an OMGraphic using the com.bbn.openmap.geo.Geo class
     * 
     * @param omGraphic
     * @return
     */
    public double getArea(OMGraphic omGraphic) {
        if (omGraphic instanceof OMPoly) {
            Vector geos = new Vector();
            float[] latlons = ((OMPoly) omGraphic).getLatLonArray();

            for (int i = 0; i < latlons.length; i += 2) {
                Geo g = new Geo((double)latlons[i], (double)latlons[i+1]);
                geos.add(g);
            }
            Enumeration e = geos.elements();
            return Geo.area(e);
        }
        return 0.0;
    }

    /**
     * 
     * Gets the area for an EsriGraphic using the com.bbn.openmap.geo.Geo class
     * 
     * @param omGraphic
     * @return
     */
    public double getArea(EsriGraphic esriGraphic) {
        if (esriGraphic instanceof OMPoly) {
            Vector geos = new Vector();
            float[] latlons = ((OMPoly) esriGraphic).getLatLonArray();

            for (int i = 0; i < latlons.length; i += 2) {
                Geo g = new Geo((double)latlons[i], (double)latlons[i+1]);
                geos.add(g);
            }
            Enumeration e = geos.elements();
            return Geo.area(e);
            //return 0.0;
        }
        return 0.0;
    }

    /**
     * 
     * helper function
     * 
     * given an Attribute type (generally from a Shapefile's dbf file) get the related set method
     * 
     * @param clazz
     * @param at
     * @return
     */
    public Method getSetMethodForAttributeType(Class clazz, AttributeType at) {
        if (at.getName().equalsIgnoreCase("the_geom")) return null; // make sure not using geotools
        Method method = null;

        Method[] methods = clazz.getMethods();

        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equalsIgnoreCase("set" + at.getName())) {
                return methods[i];
            }
        }
        return method;
    }

    /**
     * 
     * helper function
     * 
     * given an Column name (generally from a Shapefile's dbf file) get the related set method
     * 
     * @param clazz
     * @param at
     * @return
     */
    public Method getSetMethodForName(Class clazz, String columnName) {
        if (columnName.equalsIgnoreCase("the_geom")) return null; // make sure not using geotools
        Method method = null;

        Method[] methods = clazz.getMethods();

        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equalsIgnoreCase("set" + columnName)) {
                return methods[i];
            }
        }
        return method;
    }

    /**
     * 
     * helper function
     * 
     * given an Column name  (generally from a Shapefile's dbf file) get the related get method
     * 
     * @param clazz
     * @param at
     * @return
     */
    public Method getGetMethodForName(Class clazz, String name) {
        if (name.equalsIgnoreCase("the_geom")) return null; // make sure not using geotools
        Method method = null;

        try {
            method = clazz.getMethod("get" + name, null);
        } catch (NoSuchMethodException ex) {

            //		 try again but with standard java caml type name
            String capName = Character.toUpperCase(name.charAt(0))
                    + name.substring(1);
            try {
                method = clazz.getMethod("get" + capName, null);
            } catch (NoSuchMethodException e) {
                //e.printStackTrace(); //To change body of catch statement use
             // File | Settings | File Templates.
            }
        }

        return method;
    }

    /**
     * 
     * helper function
     * 
     * given an Attribute type (generally from a Shapefile's dbf file) get the related get method
     * 
     * @param clazz
     * @param at
     * @return
     */
    public Method getGetMethodForAttributeType(Class clazz, AttributeType at) {
        if (at.getName().equalsIgnoreCase("the_geom")) return null; // make sure not using geotools
        Method method = null;

        String propName = at.getName();
        try {
            method = clazz.getMethod("get" + propName, null);
        } catch (NoSuchMethodException ex) {

            //	 try again but with standard java caml type name
            String capName = Character.toUpperCase(propName.charAt(0))
                    + propName.substring(1);
            try {
                method = clazz.getMethod("get" + capName, null);
            } catch (NoSuchMethodException e) {
               // e.printStackTrace();
            }
        }

        return method;
    }

    /**
     * 
     * given the feature type and the name, get the Attribute Position
     * 
     * @param name
     * @param ft
     * @return
     */
    public int getAttributePosition(String name, FeatureType ft) {
        AttributeType[] attributeTypes = ft.getAttributeTypes();
        // go through the attributeTypes andd find which has the name that matches
        for (int i = 0; i < attributeTypes.length; i++) {
            if (attributeTypes[i].getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

}