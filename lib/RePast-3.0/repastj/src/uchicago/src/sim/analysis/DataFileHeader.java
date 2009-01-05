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

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import uchicago.src.reflector.MethodFactory;
import uchicago.src.sim.engine.IController;
import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.parameter.ParameterUtility;
import uchicago.src.sim.util.SimUtilities;


/**
 * Given a model and this will create the appropriate header for any data file.
 * For batch runs, constant parameters will be in one header (the true header),
 * and dynamic parameters will be in a block header and reflect the
 * parameter value at that time.
 * 
 * Also, the user has an option to provide their own custom header.
 *
 * <b>Note</b>: the actual parameter values in the file header are
 * created via the call to getFileHeader(). For DataRecorder's this
 * call is made in the DataRecorder's constructor. Consequently, any
 * changes to model parameter's made after this call are not reflected in
 * the file header.
 */

public class DataFileHeader {

  public static final String WRITE_HEADER = "_WRITE_HEADER";

  private String modelHeader = "";
  private boolean isBatch = false;
  private SimModel model;
  private String lineSep;
  private String headerComment = "";
  private Hashtable propertyMethod;


  /**
   * This should only be used for testing various Recorders that
   * use DataFileHeader but don't need to create a model.
   */
  public DataFileHeader() {
    lineSep = System.getProperty("line.separator");
  }

  /**
   * Constructor which allows users to specify their own custom header.
   * 
   * @param modelHeader - user provided header.
   */
  public DataFileHeader( String modelHeader) {
  	this.headerComment = modelHeader;
    lineSep = System.getProperty("line.separator");
  }

  public DataFileHeader(SimModel model) {
    this.model = model;
    lineSep = System.getProperty("line.separator");
    isBatch = model.getController().isBatch();
  }

  private void createHeader() {
    Hashtable propsVals = null;
    if (isBatch) {
      propsVals = createBatchHeader();
      createBlockHeader();
    } else {
      if (model == null) {
        propsVals = new Hashtable();
//        propsVals.put("Dummy_Parameter", "Dummy_Value");
      } else {
        try {
          propsVals = ParameterUtility.getInstance().getModelProperties(model);
        } catch (IntrospectionException ex) {
          SimUtilities.showError("Error getting model parameters", ex);
          System.exit(0);

        } catch (IllegalAccessException ex) {
          SimUtilities.showError("Error getting model parameters", ex);
          System.exit(0);
        } catch (InvocationTargetException ex) {
          SimUtilities.showError("Error getting model parameters", ex);
          System.exit(0);
        }
      }
    }

    StringBuffer b = new StringBuffer();
    if (headerComment.length() != 0) {
      b.append(headerComment);
      b.append(lineSep);
    }

    b.append("Timestamp: ");
    b.append(DateFormat.getDateTimeInstance().format(new Date()));
    b.append(lineSep);

    ArrayList list = new ArrayList(propsVals.keySet());
    Collections.sort(list);

    for (int i = 0; i < list.size(); i++) {
      Object key = list.get(i);
      b.append(key);
      b.append(": ");
      b.append(propsVals.get(key));
      b.append(lineSep);
    }

    modelHeader = b.toString();

  }

  private Hashtable createBatchHeader() {
    // gets a list of all the parameters to be set from a parameter
    // file. If a parameter from the model is not in this list, it
    // goes into the model header. If in list, but constant it goes
    // in the model header, else the parameter is assumed to be
    // dynamic and goes in the block header.
    Hashtable propsVals = null;
    ParameterUtility pu = ParameterUtility.getInstance();
    try {
      propsVals = pu.getDefaultParameters(model);
    } catch (IntrospectionException e) {
      SimUtilities.showError("Error getting default parameters", e);
    } catch (InvocationTargetException e) {
      SimUtilities.showError("Error getting default parameters", e);
    } catch (IllegalAccessException e) {
      SimUtilities.showError("Error getting default parameters", e);
    }
    Enumeration e = propsVals.keys();
    while (e.hasMoreElements()) {
      String prop = (String) e.nextElement();
      /*
      if (control.isParameter(prop)) {
        if (control.isParameterConstant(prop)) {
          // ??????!!!
          control.removeParameter(prop);
        } else {
          // isDynamic so not in file header
          propsVals.remove(prop);
        }
      }
      */
      if (pu.isDefaultParam(prop) && (!pu.isConstantDefaultParam(prop))) {
        propsVals.remove(prop);
      }
    }

    return propsVals;
  }

  private void createBlockHeader() {
    ArrayList dynPropNames = ParameterUtility.getInstance().getDynamicParameterNames();
    Class clz = model.getClass();

    try {
      propertyMethod = MethodFactory.getInstance().findGetMethods(clz, dynPropNames);
    } catch (Exception ex) {
      String msg = "Fatal Error finding accessor methods. See repast.log";
      SimUtilities.showError(msg, ex);
      System.exit(0);
    }
  }

  /**
   * Prepends a comment to the usual file header.
   *
   * @param comment the comment to prepend
   */
  public void addHeaderComment(String comment) {
    if (comment != null) headerComment = comment;
  }

  /**
   * Gets the true file header. This includes all the constant model parameters.
   * For a non-batch run, this is just all the model parameters at the time
   * the file is first written.
   */
  public String getFileHeader() {
    if (modelHeader.length() == 0) {
      createHeader();
    }
    return modelHeader;
  }

  private String getBHeader(boolean asComments) {
    if (!isBatch) {
      return "";
    }
    StringBuffer b = new StringBuffer("# run: ");
    b.append(String.valueOf(model.getController().getRunCount()));
    b.append(lineSep);

    if (propertyMethod == null) createBlockHeader();

    Enumeration e = propertyMethod.keys();
    while (e.hasMoreElements()) {
      String prop = (String) e.nextElement();
      if (asComments) {
        b.append("# ");
      }
      b.append(prop);
      b.append(": ");
      Method m = (Method) propertyMethod.get(prop);
      String val = null;
      try {
        val = String.valueOf(m.invoke(model, new Object[]{}));
      } catch (Exception ex) {
        String msg = "Fatal Error invoking accessor method. See repast.log";
        SimUtilities.showError(msg, ex);
        System.exit(0);
      }
      b.append(val);
      b.append(lineSep);
    }

    return b.toString();
  }

  /**
   * Gets the block header. This is all the dynamic parameters and their
   * values. For a non batch run, this is an empty string. For a batch run,
   * this is the current run number, followed by the dynamic parameters and
   * their values. One parameter per line.
   */
  public String getBlockHeader() {
    return getBHeader(false);
  }

  /**
   * Gets the block header. This is all the dynamic parameters and their
   * values. For a non batch run, this is an empty string. For a batch run,
   * this is the current run number, followed by the dynamic parameters and
   * their values. One parameter per line, prepended by a "#"
   */
  public String getBlockHeaderAsComments() {
    return getBHeader(true);
  }

  /**
   * Is this DataFileHeader part of a batch run.
   */

  public boolean isBatch() {
    return isBatch;
  }

  /**
   * Should the header be written to the specified file during this run?
   * Recorder objects can
   * query their DataFileHeaders to see if the header should be written. The
   * header will always be written during a non-batch run where one run
   * is recorded per file. For a batch run where all the runs are written
   * to a single file the header is only written once.
   *
   * @param fileName the fileName for this header.
   */
  public boolean doWriteHeader(String fileName) {
    if (isBatch) {
      fileName = fileName + WRITE_HEADER;
      IController control = model.getController();
      Boolean val = (Boolean)control.getPersistentObj(fileName);
      if (val == null) {
        val = Boolean.TRUE;
        control.putPersistentObj(fileName, val);
      }

      return val.booleanValue();
    }

    return true;
  }

  /**
   * Sets whether the header should be written to the file.
   *
   * @param fileName the fileName associated with this header
   * @param val whether the header should be written to the specified file.
   * @see #doWriteHeader(String)
   */
  public void setWriteHeader(String fileName, boolean val) {
    if (isBatch) {
      fileName = fileName + WRITE_HEADER;
      IController control = model.getController();
      control.putPersistentObj(fileName, val ? Boolean.TRUE : Boolean.FALSE);
    }
  }

  /**
   * Puts an object in the persistent store. The object will then
   * persist over the course of several runs (i.e. beyond the life of this
   * DataFileHeader).
   *
   * @param key the key for the persistent object
   * @param val the object to persist.
   */
  public void putPersistentObj(Object key, Object val) {
    model.getController().putPersistentObj(key, val);
  }

  /**
   * Gets a persistent object.
   *
   * @param key the key for this object to get
   */
  public Object getPersistentObj(Object key) {
    return model.getController().getPersistentObj(key);
  }

  public Hashtable getDynParamMethod() {
    return propertyMethod;
  }
}
