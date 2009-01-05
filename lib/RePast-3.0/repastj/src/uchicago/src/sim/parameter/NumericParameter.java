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

import uchicago.src.sim.engine.AddIncrementer;
import uchicago.src.sim.engine.ListIncrementer;

/**
 * A numeric batch run parameter. <code>NumericParameter</code>s are created
 * by a {@link uchicago.src.sim.parameter.ParameterReader ParameterReader} for
 * use by a <code> {@link uchicago.src.sim.engine.BatchController
 * BatchController}. Consequently, a user should never need to create one.<p>
 *
 * A numeric parameter like contains a starting value, an ending value, and an
 * increment value. At the start of a batch run, the current value of a
 * NumericParameter is the starting value. At the beginning of the next run,
 * this starting value is incremented by the increment amount until the
 * current amount is greater than the ending amount. The parameter space
 * defined by a NumericParameter is thus inclusive of both the starting and
 * ending values.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see uchicago.src.sim.engine.BatchController
 * @see uchicago.src.sim.engine.SimInit
 */


public class NumericParameter extends Parameter {
  /*
  double start;
  double end;
  double incr;
  double cur;
  */

  private InitVals initVals = new InitVals();

  class InitVals {
    private double start;
    private double end;
    private double incr;

    boolean startSet = false;
    boolean endSet = false;
    boolean incrSet = false;

    public void setStart(double s) {
      start = s;
      startSet = true;
    }

    public void setEnd(double e) {
      end = e;
      endSet = true;
    }

    public void setIncr(double i) {
      incr = i;
      incrSet = true;
    }

    public AddIncrementer getIncrementer() {
      if (startSet && endSet && incrSet) {
        return new AddIncrementer(start, end, incr);
      } else {
        throw new IllegalArgumentException("Not all values expliciltly set");
      }
    }
	/**
	 * @return Returns the end.
	 */
	public double getEnd() {
		return end;
	}
	/**
	 * @return Returns the incr.
	 */
	public double getIncr() {
		return incr;
	}
	/**
	 * @return Returns the start.
	 */
	public double getStart() {
		return start;
	}
  };

  //private static DecimalFormat format = new DecimalFormat("#.#");


  /**
   * Sets the start value of this parameter.
   * @param start the starting value
   */
  public void setStart(double start) {
    //this.start = start;
    initVals.setStart(start);
  }


  /**
   * Sets the ending value of this parameter.
   * @param end the ending value
   */
  public void setEnd(double end) {
    initVals.setEnd(end);
  }

  /**
   * Sets the increment value of this parameter.
   * @param incr the increment value
   */
  public void setIncr(double incr) {
    initVals.setIncr(incr);

    // the incr should be set last so the setup is complete
    incrementer = initVals.getIncrementer();
    setParameterType(Parameter.INCREMENT);
  }

  /**
   * Sets the starting value to the specified value. <b>Note: the specified
   * object must be a Double.
   *
   * @param o the starting Double value
   */
  public void setStart(Object o) {
    Double d = (Double)o;
    setStart(d.doubleValue());
  }

  /**
   * Sets the ending value to the specified value. <b>Note: the specified
   * object must be a Double.
   *
   * @param o the ending Double value
   */
  public void setEnd(Object o) {
    Double d = (Double)o;
    setEnd(d.doubleValue());
  }

  /**
   * Sets the increment value to the specified value. <b>Note: the specified
   * object must be a Double.
   *
   * @param o the increment Double value
   */
  public void setIncr(Object o) {
    Double d = (Double)o;
    setIncr(d.doubleValue());
  }

  public void setList(Vector v) {
    super.setList(v);
    incrementer = new ListIncrementer(v);
  }


  /**
   * Is this parameter complete? Does it have start, end, and increment set.
   * @return true if complete, otherwise false
   */
  public boolean isComplete() {
    return incrementer != null;
  }

  /**
   * Increments this parameter according to the increment value
   * @return true if value after incrementing is <= end value, otherwise
   * false
   */
  public boolean increment() {
    boolean retVal = false;
    if (this.hasChildren()) {
      for (int i = 0; i < subParams.size(); i++) {
        NumericParameter p = (NumericParameter)subParams.elementAt(i);
        if (p.increment()) {
          retVal = true;
        } else {
          retVal = incrementMe();
          break;
        }
      }
    } else {
      retVal = incrementMe();
    }

    //if (!retVal) {
    //  System.out.println(this.name + " cannot be incremented");
    //}
    return retVal;
  }

  // returns false if nothing more to increment
  private boolean incrementMe() {
    boolean retVal = false;
    if (numRunsIndex > numRuns) {
      numRunsIndex = 1;
      // constant values are entirely controlled by the
      // runs value.
      if (isConstant()) retVal = false;
      else retVal = incrementer.increment();
    } else {
      retVal = true;
    }
    numRunsIndex++;
    return retVal;
  }

  /**
   * Returns the current value
   */
  public String getStringValue() {
    return incrementer.getStringValue();//String.valueOf(cur);
  }


/**
 * @return
 * @see uchicago.src.sim.parameter.Parameter#getValue()
 */
public Object getValue() {
    return incrementer.getValue();
}


/* (non-Javadoc)
 * @see uchicago.src.sim.parameter.Parameter#getStart()
 */
public Object getStart() {
	return new Double(initVals.getStart());
}


/* (non-Javadoc)
 * @see uchicago.src.sim.parameter.Parameter#getEnd()
 */
public Object getEnd() {
	return new Double(initVals.getEnd());
}


/* (non-Javadoc)
 * @see uchicago.src.sim.parameter.Parameter#getIncr()
 */
public Object getIncr() {
	return new Double(initVals.getIncr());
}

//  /**
//   * Returns true if this is constant (if the incrementing value == 0).
//   */
//  public boolean isConstant() {
//    return incrementer.isConstant();
//  }

  /*
  public void printToScreen() {
    System.out.println(name);
    System.out.println("start: " + start);
    System.out.println("end: " + end);
    System.out.println("incr: " + incr);

    for (int i = 0; i < subParams.size(); i++) {
      print((NumericParameter)subParams.elementAt(i), "\t");
    }
  }
  */

  /**
   * Gets the starting value as a formatted string
   */

  //public String getStartAsString() {
 //   return format.format(start);
  //}

  /**
   * Gets the increment value as a formatted string
   */
  //public String getIncrAsString() {
  //  return format.format(incr);
  //}

  /**
   * Gets the ending value as a formatted string
   */
  //public String getEndAsString() {
  //  return format.format(end);
  //}

  /*
  private void print(NumericParameter p, String tabs) {
    System.out.println(tabs + p.getName());
    System.out.println(tabs + "runs: " + p.getNumRuns());
    System.out.println(tabs + "start: " + p.start);
    System.out.println(tabs + "end: " + p.end);
    System.out.println(tabs + "incr: " + p.incr);

    for (int i = 0; i < p.getChildren().size(); i++) {
      tabs += "\t";
      print((NumericParameter)p.getChildren().elementAt(i), tabs);
    }
  }
  */
}
