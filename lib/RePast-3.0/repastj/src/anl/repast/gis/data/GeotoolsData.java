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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureResults;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.filter.Filter;
import org.geotools.geometry.Geometry;

import anl.repast.gis.GisAgent;

import com.vividsolutions.jts.geom.Envelope;


/**
 * @author Robert Najlis
 */
public class GeotoolsData {

	private static GeotoolsData instance = new GeotoolsData();
	  public GeotoolsData() {}
	  
	  public static GeotoolsData getInstance() {
	    return instance;
	  }

	  public void readNeighborhoodInfo(String neighborhoodFile, ArrayList gisAgents) {
	  	try {
			 Pattern p = Pattern.compile(" "); 
			 String[] data;
			 //int lineCount =0;
			 String s = new String();
			 BufferedReader in = new BufferedReader(new FileReader(neighborhoodFile));
			 int agentNum = 0;
			 in.readLine(); // deal with the header line -- just read it and do nothing
	         while ((s = in.readLine()) != null) {
	             data = p.split(s);
	            agentNum = ( Integer.parseInt(data[0]) - 1) ;
	            if (Integer.parseInt(data[1]) > 0) {
	            	// there are neighbors
	            	// reaad the next line, parse it, and that to agent neighbor list
	            	s = in.readLine();
	            	data = p.split(s);
	            	// neighbors = new ArrayList();
	            	int [] neighbors = new int[data.length];
	            	for (int i=0; i<data.length; i++) {
	            		neighbors[i] = ( Integer.parseInt(data[i]) - 1); // have to subtract 1 to acccount for id #
	            	}
	            	((GisAgent)gisAgents.get(agentNum)).setNeighbors(neighbors);
	            }
	            else {
	            	s = in.readLine(); // read line but do nothing
	            	int [] noNeighbors = new int[0];
	            	((GisAgent)gisAgents.get(agentNum)).setNeighbors(noNeighbors);
	            }
	         }
			   	
			 
			 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	  	
	  public ArrayList createAgents(Class clazz, String datasource) {
	    ArrayList agentList = new ArrayList();
	    try {
	      // get feature info - features, types 
	      URL file = new File(datasource).toURL();
	      ShapefileDataStore store = new ShapefileDataStore(file);
	      String name = store.getTypeNames()[0];  // there is just one - one type per shapefile - type of data in shapefile
	      FeatureSource source = store.getFeatureSource(name);
	      FeatureResults fsShape = source.getFeatures();

	      //String[] types = store.getTypeNames();

	      //Method[] methods = clazz.getMethods();
				 
				
	      FeatureType ft = source.getSchema();
	      FeatureReader reader = fsShape.reader();
	      while (reader.hasNext()) {
	        reader.next();   
	        //			 create one new obj for each feature
	        Object obj = clazz.newInstance();
	        agentList.add(obj);
	      }
	      reader.close();

	      for (int i = 0; i < ft.getAttributeCount(); i++) {
	        AttributeType at = ft.getAttributeType(i);
	        if (Geometry.class.isAssignableFrom(at.getType())) {
	          continue;
	        } else {
	          // for each attribute type -- see if there is a set method that corresponds
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
	            reader.close();
	          }
	        }
	      }
	      
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return agentList;
	  }

	  public ArrayList updateAgentsFromShapefile(Class clazz, ArrayList agentList, String datasource) {
		    //ArrayList agentList = new ArrayList();
		    try {
		      // get feature info - features, types 
		      URL file = new File(datasource).toURL();
		      ShapefileDataStore store = new ShapefileDataStore(file);
		      String name = store.getTypeNames()[0];  // there is just one - one type per shapefile - type of data in shapefile
		      FeatureSource source = store.getFeatureSource(name);
		      FeatureResults fsShape = source.getFeatures();

		      //String[] types = store.getTypeNames();

		      //Method[] methods = clazz.getMethods();
					 
		      FeatureType ft = source.getSchema();
		      FeatureReader reader = fsShape.reader();
	
		      for (int i = 0; i < ft.getAttributeCount(); i++) {
		        AttributeType at = ft.getAttributeType(i);
		        if (Geometry.class.isAssignableFrom(at.getType())) {
		          continue;
		        } else {
		          // for each attribute type -- see if there is a set method that corresponds
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
		            reader.close();
		          }
		        }
		      }
		      
		      
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		    return agentList;
		  }
	  
	  public FieldNameAndType[] interrogate(String fileName) throws IOException {
	    FieldNameAndType[] fieldNamesTypes = null;
	    //String fileURL = "file://" + fileName;
	    try {
	      URL file = new File(fileName).toURL();
	      ShapefileDataStore shpDS = new ShapefileDataStore(file);
				
	      String[] types = shpDS.getTypeNames();
	      for (int i = 0; i < types.length; i++) {
	      }

	      FeatureType fType = shpDS.getSchema(types[0]);

	      AttributeType[] attributeTypes = fType.getAttributeTypes();
	      fieldNamesTypes = new FieldNameAndType[attributeTypes.length];

	      for (int i = 0; i < attributeTypes.length; i++) {
	        fieldNamesTypes[i] = new FieldNameAndType(attributeTypes[i].getName(), attributeTypes[i].getType().getName());
	      }
				
	      // go throught the list of attributeTypes and and the name and type of each to the list
	      return fieldNamesTypes;
	    } catch (IOException ex) {
	      throw ex;
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return fieldNamesTypes;
	  }

	  public void writeAgents(Collection agents, String datasource) {
	      try {
	      Class clazz = null;
	      if (agents.size() > 0) {
	        clazz = agents.iterator().next().getClass();
	      }
				
	      // get feature info - features, types 
	      URL file = new File(datasource).toURL();

	      ShapefileDataStore store = new ShapefileDataStore(file);
	      String name = store.getTypeNames()[0];  // there is just one - one type per shapefile - type of data in shapefile
	      FeatureStore featureStore = (FeatureStore) store.getFeatureSource(name);
	      FeatureResults fsShape = featureStore.getFeatures();
				
	      // create a feature coolection to hold the features. then make a new shapefile data store
	      FeatureArrayList featureSet = new FeatureArrayList();

         //	get feature type to create new shapefile
	      FeatureType ft = featureStore.getSchema();


	      FeatureReader reader = fsShape.reader();
	      for (Iterator iter = agents.iterator(); iter.hasNext();) {
	        Feature feature = reader.next();
	        featureSet.add(feature);
	        Object agent = iter.next();
	        for (int i = 0; i < ft.getAttributeCount(); i++) {
	          AttributeType at = ft.getAttributeType(i);
	          if (Geometry.class.isAssignableFrom(at.getType())) {
	            continue;
	          } else {
	            // for each attribute type -- see if there is a get method that corresponds
	            Method method = this.getGetMethodForAttributeType(clazz, at);
	            // if there is invoke it for each object in the list
	            if (method != null) {
	              // invoke with data for each 
	              feature.setAttribute(i, method.invoke(agent, null));
	            }
	          }
	        }
	      }
	      
	      reader.close();
	      
	      Transaction transaction = featureStore.getTransaction();//newFeatureStore.getTransaction();
	      FeatureWriter featureWriter = store.getFeatureWriter(name, Filter.NONE, transaction);
	      if (featureWriter == null) System.out.println("Cannot write shapefile, FeatureWriter is null");
	      for (Iterator iter = featureSet.iterator(); iter.hasNext();) {
	        Feature feature = featureWriter.next();
	        Feature storedFeature = (Feature) iter.next();
	        Object[] attributes = new Object[storedFeature.getNumberOfAttributes()];
	        storedFeature.getAttributes(attributes);
	        feature.setAttributes(attributes);
	        featureWriter.write();
	      }

	      featureSet.clear();
	      transaction.commit();
	      transaction.close();
	      featureWriter.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }

	 
	  

	    public void writeAgents(Collection agents, String origDatasource, String newDatasource) {
		      try {
		      Class clazz = null;
		      if (agents.size() > 0) {
		        clazz = agents.iterator().next().getClass();
		      }
					
		      // copy the original file to the new location for use in writing the new Shapefile data
		      String origDatasourceNoSHP = origDatasource.substring(0, origDatasource.length() - 3);
	            String origDbfString = origDatasourceNoSHP + "dbf";
	            String origShxString = origDatasourceNoSHP + "shx";
	            String newDatasourceNoSHP = newDatasource.substring(0, newDatasource.length() - 3);
	            String newDbfString = newDatasourceNoSHP + "dbf";
	            String newShxString = newDatasourceNoSHP + "shx";
			   
	            // Create channel on the source
	            FileChannel srcChannelShp = new FileInputStream(origDatasource).getChannel();
	            FileChannel srcChannelDbf = new FileInputStream(origDbfString).getChannel();
	            FileChannel srcChannelShx = new FileInputStream(origShxString).getChannel();
	            // Create channel on the destination
	            FileChannel dstChannelShp = new FileOutputStream(newDatasource).getChannel();
	            FileChannel dstChannelDbf = new FileOutputStream(newDbfString).getChannel();
	            FileChannel dstChannelShx = new FileOutputStream(newShxString).getChannel();
	            // Copy file contents from source to destination
	            dstChannelShp.transferFrom(srcChannelShp, 0, srcChannelShp.size());
	            dstChannelDbf.transferFrom(srcChannelDbf, 0, srcChannelDbf.size());
	            dstChannelShx.transferFrom(srcChannelShx, 0, srcChannelShx.size());
			        
	            // Close the channels
	            srcChannelShp.close();
	            dstChannelShp.close();
	            
	            srcChannelDbf.close();
	            dstChannelDbf.close();
	            
	            srcChannelShx.close();
	            dstChannelShx.close();
			        
			  // get feature info - features, types 
		      URL file = new File(newDatasource).toURL();

		      ShapefileDataStore store = new ShapefileDataStore(file);
		      String name = store.getTypeNames()[0];  // there is just one - one type per shapefile - type of data in shapefile
		      FeatureStore featureStore = (FeatureStore) store.getFeatureSource(name);
		      FeatureResults fsShape = featureStore.getFeatures();
					
		      // create a feature coolection to hold the features. then make a new shapefile data store
		      FeatureArrayList featureSet = new FeatureArrayList();

	         //	get feature type to create new shapefile
		      FeatureType ft = featureStore.getSchema();


		      FeatureReader reader = fsShape.reader();
		      for (Iterator iter = agents.iterator(); iter.hasNext();) {
		        Feature feature = reader.next();
		        featureSet.add(feature);
		        Object agent = iter.next();
		        for (int i = 0; i < ft.getAttributeCount(); i++) {
		          AttributeType at = ft.getAttributeType(i);
		          if (Geometry.class.isAssignableFrom(at.getType())) {
		            continue;
		          } else {
		            // for each attribute type -- see if there is a get method that corresponds
		            Method method = this.getGetMethodForAttributeType(clazz, at);
		            // if there is invoke it for each object in the list
		            if (method != null) {
		              // invoke with data for each 
		              feature.setAttribute(i, method.invoke(agent, null));
		            }
		          }
		        }
		      }
		      
		      reader.close();
		      
		      Transaction transaction = featureStore.getTransaction();//newFeatureStore.getTransaction();
		      FeatureWriter featureWriter = store.getFeatureWriter(name, Filter.NONE, transaction);
		      if (featureWriter == null) System.out.println("Cannot write shapefile, FeatureWriter is null");
		      for (Iterator iter = featureSet.iterator(); iter.hasNext();) {
		        Feature feature = featureWriter.next();
		        Feature storedFeature = (Feature) iter.next();
		        Object[] attributes = new Object[storedFeature.getNumberOfAttributes()];
		        storedFeature.getAttributes(attributes);
		        feature.setAttributes(attributes);
		        featureWriter.write();
		      }

		      featureSet.clear();
		      transaction.commit();
		      transaction.close();
		      featureWriter.close();
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		  }

	    
	    /**
	     * Sorts a Collection of GisAgents using the GisAgent's getGisAgentIndex function
	     * 
	     * @param gisAgents
	     * @return
	     */
	    public Collection sortGisAgentsByIndex(Collection gisAgents) {

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
	    
	  public Envelope getEnvelope(String datasource) {
	    try {
	      URL file = new File(datasource).toURL();
	      ShapefileDataStore store = new ShapefileDataStore(file);
	      String name = store.getTypeNames()[0];  // there is just one - one type per shapefile - type of data in shapefile
	      FeatureSource source = store.getFeatureSource(name);
	      Envelope envelope = source.getBounds();
	      return envelope;
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return null;
	  }

	  public double[] getCenter(Envelope envelope) {
	    double[] centerXY = new double[2];
	    centerXY[0] = ((envelope.getMinX() + envelope.getMaxX())) / 2;
	    centerXY[1] = ((envelope.getMinY() + envelope.getMaxY())) / 2;
	    return centerXY;
	  }

	  public Method getSetMethodForAttributeType(Class clazz, AttributeType at) {
	    Method method = null;

	    Method[] methods = clazz.getMethods();

	    for (int i = 0; i < methods.length; i++) {
	      if (methods[i].getName().equalsIgnoreCase("set" + at.getName())) {
	        return methods[i];
	      }
	    }
	    return method;
	  }

	  public Method getGetMethodForAttributeType(Class clazz, AttributeType at) {
	    Method method = null;
			
	    String propName = at.getName();
	    try {
	      method = clazz.getMethod("get" + propName, null);
	    } catch (NoSuchMethodException ex) {

         //	  try again but with standard java caml type name
	      String capName = Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
	      try {
	        method = clazz.getMethod("get" + capName, null);
	      } catch (NoSuchMethodException e) {
	      }
	    }

	    return method;
	  }


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
	
	
