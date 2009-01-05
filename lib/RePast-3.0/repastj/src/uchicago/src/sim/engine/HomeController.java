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
package uchicago.src.sim.engine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import org.objectweb.proactive.ProActive;

import uchicago.src.reflector.Introspector;
import uchicago.src.sim.analysis.DataWriter;
import uchicago.src.sim.parameter.Parameter;
import uchicago.src.sim.parameter.ParameterReader;
import uchicago.src.sim.util.SimUtilities;

/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Feb 13, 2003
 * Time: 7:49:51 PM
 * To change this template use Options | File Templates.
 */
public class HomeController{
  protected ArrayList dynNameList;
  protected Hashtable nameParam = new Hashtable(5);
  protected Vector params;
  protected boolean finished = false;
  protected ArrayList nameList = new ArrayList();
  private long batchCount = 0;
  private Hashtable persistent;
  private RemoteBatchController controllers;
  protected String modelClass;

  /**
   * No-arg constructor for Active Object
   */
  public HomeController(){
    persistent = new Hashtable();
    params = new Vector();
    modelClass = "";
  }

  /**
   * create the controller using the parameter file name
   * @param fileName
   */
  public HomeController(String fileName, String modelClass){
    persistent = new Hashtable();
    ParameterReader reader = null;
    //read the parameter file
    try{
      reader = new ParameterReader(fileName);
    }catch(IOException ie){
      ie.printStackTrace();
    }
    //get the parameter vector
    params = reader.getParameters();
    getParameterNames();
    //ParameterUtility.createInstance(params);
    this.modelClass = modelClass;
  }

  public void setControllers(RemoteBatchController c){
    controllers = c;
  }

  /**
   * Gets the current Tick count
   * @return
   */
  public long getRunCount(){
    return batchCount;
  }

  private void incrementBatchCount(){
    batchCount++;
  }

  /**
   * Set the parameters in the remote model.  This should only be called from the
   * synchronized nextModel method below
   * @param i The model introspector used to set the parameters in the model.
   */
  protected void setModelParameters(Introspector i){
    for (int j = 0; j < params.size(); j++) {
      Parameter p = (Parameter) params.elementAt(j);
      setParameters(i, p);
    }
  }

  private void setParameters(Introspector i, Parameter p) {
    if (p.hasChildren()) {
      Vector children = p.getChildren();
      for (int j = 0; j < children.size(); j++) {
        //assign values to particular parameters
        setParameters(i, (Parameter) children.elementAt(j));
      }
    }
    try {
      //set the parameters in the model
      i.invokeSetMethod(p.getName(), p.getStringValue());
    } catch (InvocationTargetException ex) {
      SimUtilities.showError("Unable to set model parameter "
              + p.getName(), ex);
      ex.printStackTrace();
      System.exit(0);
    } catch (IllegalAccessException ex) {
      SimUtilities.showError("Unable to set model parameter "
              + p.getName(), ex);
      ex.printStackTrace();
      System.exit(0);
    } catch (NullPointerException ex) {
      SimUtilities.showError("Unable to set model parameter "
              + p.getName(), ex);
      ex.printStackTrace();
      System.exit(0);
    }
  }


  // increment the parameters appropriately
  protected void incrementParameters(Introspector is) {
    for (int i = 0; i < params.size(); i++) {
      Parameter p = (Parameter) params.elementAt(i);
      if (!p.increment()) {
        if (p.isConstant()) {
          if (getDynamicParameterNames().size() == 0) {
            finished = true;
            return;
          }
        } else {
          finished = true;
          return;
        }
      }
    }
    //If we have arrived here, call the setModelParams using the included introspector
    setModelParameters(is);
  }

  /**
   * Gets a list of the parameter names.
   */
  public ArrayList getParameterNames() {
    nameList = new ArrayList(11);
    getParamNames(params);
    return nameList;
  }

  /**
   * Gets a list of the Dynamic (non-constant) Parameter names.
   */
  public ArrayList getDynamicParameterNames() {
    dynNameList = new ArrayList(11);
    getDynParamNames(params);
    return dynNameList;
  }

  private void getParamNames(Vector list) {
    for (int i = 0; i < list.size(); i++) {
      Parameter p = (Parameter) list.get(i);
      StringBuffer b = new StringBuffer(p.getName());
      char c = Character.toUpperCase(b.charAt(0));
      b.setCharAt(0, c);
      nameParam.put(b.toString(), p);
      String name = p.getName().toLowerCase();

      nameList.add(name);
      if (p.hasChildren()) {
        getParamNames(p.getChildren());
      }
    }
  }

  private void getDynParamNames(Vector list) {
    for (int i = 0; i < list.size(); i++) {
      Parameter p = (Parameter) list.get(i);
      if (!p.isConstant()) {
        String name = p.getName().toLowerCase();
        dynNameList.add(name);
      }
      if (p.hasChildren()) {
        getDynParamNames(p.getChildren());
      }
    }
  }

  /**
   * This is the synchronized method that does all of the work for the remote controllers.
   * It increments the count and the parameters and uses the Introspector to set the
   * next set of parameters
   *
   * @param i The remote (Active) introspector used to set the remote model's parameters
   * @return The current tick count
   */
  public synchronized long nextModel(Introspector i){
    incrementBatchCount();
    if(batchCount == 1){
      setModelParameters(i);
    }else{
      incrementParameters(i);
    }
    if(finished == true){
      return -1;

    }else{
      System.out.println("Run: " + getRunCount());
      return getRunCount();
    }
  }

  Object persKey = new Object();

  public boolean getPutPersistenceObject(Object key, Object value){
    synchronized(persKey){
      if (persistent.get(key) == null) {
        persistent.put(key,value);
        return true;
      } else {
        return false;
      }
    }
  }

  /**
   * Put an object that needs to be stored between runs
   * @param key
   * @param value
   */
  public void putPersistentObj(Object key, Object value){
    synchronized(persKey){
      persistent.put(key, value);
    }
  }

  /**
   * Get an object that has been stored between runs
   * @param key
   * @return
   */
  public Object getPersistentObj(Object key){
    synchronized(persKey){
      return persistent.get(key);
    }
  }

  public void begin(){

    DataWriter writer = new DataWriter();
    try{
      //add the persistent writer
      putPersistentObj("WRITER", ProActive.turnActive(writer));
      //set the remote model
      controllers.setModelClass(modelClass);
      //begin the run and wait for it to finish
      System.out.println("about to start controllers");
      ProActive.waitFor(controllers.beginRemoteRun());
      //tell all of the remote controllers to stop
      controllers.quit();
      writer.writeEnd();
      System.out.println("Batch Done");
    }catch(Exception e){
      e.printStackTrace();
      System.exit(0);
    }
  }
}
