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
package uchicago.src.sim.parameter;

import java.util.Vector;

import uchicago.src.sim.engine.ConstIncrementer;
import uchicago.src.sim.engine.Incrementer;
import uchicago.src.sim.engine.ListIncrementer;

/**
 * Abstract base class for batch run parameters.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public abstract class Parameter {
    public final static int INCREMENT = 0;
    public final static int CONSTANT = 1;
    public final static int LIST = 2;

    // can be INCREMENT,CONSTANT,LIST
    private int parameterType;
    
  protected String name;
  protected Parameter parent;
  protected Vector list;
  protected int listIndex = 0;

  protected Vector subParams = new Vector();

  // set one ahead as the first call to parameter just gets the starting
  // values without incrementing the number of runs
  protected long numRunsIndex = 2;

  // set by the reader so parent can set the numRuns of children
  protected long subRuns = -1;

  // number of runs should run with one set of parameters before incrementing
  protected long numRuns = -1;

  protected Incrementer incrementer;

  // indicates whether this parameter is input or output
  private boolean isInput;
  /**
   * Sets the parent parameter of this parameter
   */
  public void setParent(Parameter p) {
    parent = p;
  }

  /**
   * Gets the parent parameter of this parameter
   */
  public Parameter getParent() {
    return parent;
  }


  public void setSubRuns(long runs) {
    subRuns = runs;
  }

  public long getSubRuns() {
    return subRuns;
  }

  /**
   * Sets the name of this to the specified name.
   *
   * @param name the name of the parameter
   */

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the name of this parameter.
   */
  public String getName() {
    return name;
  }

  /**
   * Does this parameter have all its necessary values assigned.
   *
   * @return true if this Parameter is complete, otherwise false.
   */
  public abstract boolean isComplete();

  /**
   * Adds a Parameter to this parameter as a child.
   *
   * @param subParam the parameter to add
   */
  public void addChild(Parameter subParam) {
    subParams.add(subParam);
  }

  /**
   * Gets the children of this parameter.
   */
  public Vector getChildren() {
    return subParams;
  }

  /**
   * Does this Parameter have any children.
   *
   * @return true if this Parameter has children, otherwise false.
   */
  public boolean hasChildren() {
    return subParams.size() > 0;
  }

  /**
   * Removes the specified Parameter from this Parameter's list of children.
   */
  public void removeChild(Parameter p) {
    subParams.remove(p);
  }

  /**
   * Sets the number of runs associated with this parameter.
   */
  public void setNumRuns(long runs) {
    numRuns = runs;
  }

  /**
   * Gets the number of runs associated with this Parameter.
   */
  public long getNumRuns() {
    return numRuns;
  }

  /*
  public boolean equals(Object o) {
    if (!(o instanceof Parameter))
      return false;

    Parameter p = (Parameter)o;
    return p.getName().equals(this.name);
  }
  */

  public void setList(Vector v) {
      list = v;
      incrementer = new ListIncrementer(v);
      setParameterType(Parameter.LIST);
  }
  
  public Vector getList() {
      return list;
    }

  public void setConstVal(Object o) {
    incrementer = new ConstIncrementer(o);
    setParameterType(Parameter.CONSTANT);
  }

  public String getValAsParameterSet() {
    return incrementer.getValAsParameterSet();
  }

  /**
   * Increments the parameter.
   *
   * @return true if the parameter can be incremented (hasn't reached its limit
   * condition and false if cannot.
   */
  public abstract boolean increment();

  /**
   * Gets the current value of this parameter as a string. Used to write
   * a run's parameters to a file, and set the model to the current parameter.
   *
   * @return the current value of this parameter
   */
  public abstract String getStringValue();

  /**
   * Gets the current value of this parameter as an Object. Used to write
   * a run's parameters to a file, and set the model to the current parameter.
   *
   * @return the current value of this parameter
   */
  public abstract Object getValue();

  
  public boolean isConstant() {
      if(parameterType == Parameter.CONSTANT) {
          return true;
      }
      
      return false;
  }

  public boolean isList() {
      if(parameterType == Parameter.LIST) {
          return true;
      }
      
      return false;
  }

  public boolean isIncrement() {
      if(parameterType == Parameter.INCREMENT) {
          return true;
      }
      
      return false;
  }

  public abstract void setStart(Object o);

  public abstract void setEnd(Object o);

  public abstract void setIncr(Object o);

  public abstract Object getStart();

  public abstract Object getEnd();

  public abstract Object getIncr();

  //public abstract String getStartAsString();

  //public abstract String getIncrAsString();

  //public abstract String getEndAsString();
/**
 * If true it is input, output otherwise
 * @return Returns the isInput.
 */
public boolean isInput() {
	return isInput;
}
/**
 * @param isInput The isInput to set.
 */
public void setInput(boolean isInput) {
	this.isInput = isInput;
}
    /**
     * @return Returns the parameterType.
     */
    public int getParameterType() {
        return parameterType;
    }
    /**
     * @param parameterType The parameterType to set.
     */
    public void setParameterType(int parameterType) {
        this.parameterType = parameterType;
    }
}
