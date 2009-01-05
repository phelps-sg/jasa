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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import uchicago.src.sim.engine.SimModel;

/**
 * Classes implementing this interface should also have a no-argument
 * constructor.
 *
 * @version $Revision$ $Date$
 */
public interface ParameterSetter {

  /**
   * Initialize the setter using the specified fileName. ParameterSetters
   * will most often be created using reflection via a no-arg constructor.
   * This init method functions like a proper constructor.
   *
   * @param fileName the name of the parameter file
   * @throws IOException
   */
  public void init(String fileName) throws IOException;

  /**
   * Sets the parameters for the specified model. Sets the model's
   * parameters to the current values of the parameters contained by this
   * ParameterSetter.
   *
   * @param model the model whose parameters are set
   */
  public void setModelParameters(SimModel model);

  /**
   * Increments the parameters in this ParameterSetter and then sets
   * the model's parameters.
   *
   * @param model the model whose parameters are set
   */
  public void setNextModelParameters(SimModel model);

  /**
   * @return true if the parameter space described by this ParameterSetter
   * has more parameter combinations to be explored; false if not.
   */
  public boolean hasNext();

  /**
   * @return a list of the names dynamic (non-constant) parameters defined in
   * this ParameterSetter.
   */
  public ArrayList getDynamicParameterNames();

  /**
   * @param name the name of the parameter to test
   * @return true if the specified name is the name of a parameter in this
   * ParameterSetter; otherwise false.
   */
  public boolean isParameter(String name);

  /**
   * @param name the name of the parameter to test
   * @return true if the specified name is the name of a constant parameter in this
   * ParameterSetter. false if the named parameter is not constant <b>or</b>
   * if the named parameter is not found.
   */
  public boolean isConstant(String name);

  /**
   * Gets the current value of a named parameter. A reference to the model
   * may be necessary for those ParameterSetters whose formats allow for
   * some interaction with the model.
   *
   * @param name the name of the parameter
   * @param model a reference to the SimModel associated with these
   * parameters
   * @return the current value of the named parameter. Returns null if the
   * parameter is not found.
   */
  public Object getParameterValue(String name, SimModel model);

  /**
   * @return An iterator returning the names of the parameters contained in
   * this ParameterSetter.
   */
  public Iterator parameterNames();

  /**
   * Returns a Hashtable whose keys are parameter names and values are parameter
   * values. The parameters themselves are those specified by the model in
   * <code>getInitParam</code>  with the addition of the random seed
   * and the value is the current parameter file
   * value if the parameter exists in the parameter file. If not, then the
   * value is that of the model.
   *
   * @param model the model whose parameters we want to get
   * @return a Hashtable whose keys are parameter names and values are parameter
   * values.
   */
  public Hashtable getDefaultModelParameters(SimModel model);
}
