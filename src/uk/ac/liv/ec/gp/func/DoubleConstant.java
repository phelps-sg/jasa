/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package uk.ac.liv.ec.gp.func;

import scheme.kernel.ScmObject;
import scheme.kernel.ScmReal;

import uk.ac.liv.ec.gp.*;

import uk.ac.liv.util.UntypedDouble;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class DoubleConstant extends GPSchemeNode {

	protected UntypedDouble javaValue;
	
	protected ScmReal schemeValue;
	
	public static final String NAME = "DoubleConstant";
	
  public void eval( GPGenericData input ) {
    input.data = javaValue;
  }
  
  public void setValue( double primitiveValue ) {
  	javaValue = new UntypedDouble(primitiveValue);
  	schemeValue = new ScmReal(primitiveValue);
  }
  
  public double getValue() {
  	return javaValue.doubleValue();
  }  

  public String toString() {
    return NAME;
  }
  
  public ScmObject toScheme() {
  	return schemeValue;
  }
	
}
