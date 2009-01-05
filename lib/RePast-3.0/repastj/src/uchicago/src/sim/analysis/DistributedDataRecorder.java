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

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import uchicago.src.sim.engine.IController;
import uchicago.src.sim.engine.RemoteBatchController;
import uchicago.src.sim.engine.SimEvent;
import uchicago.src.sim.engine.SimModel;

/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Jan 2, 2003
 * Time: 1:07:53 PM
 * To change this template use Options | File Templates.
 */
public class DistributedDataRecorder extends AbstractDataSourceRecorder{
  protected DataWriter writer;
  protected String name;
  protected String fileName;
  protected IController control;
  protected Hashtable dynParams;
  protected boolean writeHeader = true;
  protected String writeKey;
  public static final String WRITE_END_KEY = "_WRITE_END";


  public DistributedDataRecorder(String fileName, DataWriter writer, SimModel model, String headerComment){
    this.writer = writer;
    initData(model, headerComment);
    this.fileName = fileName;
    //use the controller to get/ set the writeHeader persistent objects
    control = model.getController();
    control.addSimEventListener(this);
    dynParams = dfHeader.getDynParamMethod();
    //this had been in SimData, it is related to the header

    writeKey = this.fileName + DataFileHeader.WRITE_HEADER;
    //this is a horrible hack, must be fixed
    //TODO: get rid of this cast someday
    writeHeader = ((RemoteBatchController)control).getPutPersistentObject(writeKey, Boolean.FALSE);
    //end of code from SimData
    Enumeration e = dynParams.keys();
    HashSet numbers = new HashSet();
    numbers.add(int.class);
    numbers.add(float.class);
    numbers.add(double.class);
    numbers.add(byte.class);
    numbers.add(long.class);
    numbers.add(char.class);
    data.addToHeader("run");
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      //System.out.println(key);
      Method m = (Method) dynParams.get(key);
      if (Object.class.isAssignableFrom(m.getReturnType())) {
        createObjectDataSource(key, this.model, m.getName());
      } else if (numbers.contains(m.getReturnType())) {
        createNumericDataSource(key, this.model, m.getName(), -1, -1);
      } else {
        DataSource s = new ObjectDataSource(key, model,
                (Method) dynParams.get(key));
        sources.add(s);
        data.addToHeader(key);
      }
    }
  }

  public DistributedDataRecorder(String fileName, DataWriter writer, SimModel model){
    this(fileName, writer,model,"");
  }

  public void record() {
    Vector v = new Vector();
    if(model == null){
      System.out.println("no model - record");
    }
    Double tick = new Double(model.getTickCount());
    v.add(tick);
    for (int i = 0; i < sources.size(); i++) {
      DataSource s = (DataSource)sources.elementAt(i);
      if (s instanceof ListDataSource) {
        v.addAll((List)s.execute());
      } else {
        Object d = s.execute();
        v.add(d);
      }
    }
    v.add(0, new Long(control.getRunCount()));
    data.addData(v);
  }

  public void writeToFile() {
    writer.write(data, writeHeader);
  }

  public void write() {
    writeToFile();
  }

  public void simEventPerformed(SimEvent event) {
  }

  public void writeEnd() {
      writer.writeEnd();

  }
}
