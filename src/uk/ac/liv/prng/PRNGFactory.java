/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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

package uk.ac.liv.prng;

// import edu.cornell.lassp.houle.RngPack.*;

import cern.jet.random.engine.RandomEngine;

import ec.util.Parameter;
import ec.util.ParameterDatabase;
import ec.util.ParamClassLoadException;

import org.apache.log4j.Logger;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class PRNGFactory {

  public static final String P_DEF_BASE = "prngfactory";
  
  protected static PRNGFactory currentFactory = new MT();

  static Logger logger = Logger.getLogger(PRNGFactory.class);

  public static void setup( ParameterDatabase parameters, Parameter base ) {
    try {
      PRNGFactory.currentFactory = (PRNGFactory) parameters
          .getInstanceForParameter(base, new Parameter(P_DEF_BASE), PRNGFactory.class);
    } catch ( ParamClassLoadException e ) {
      logger.warn(e.getMessage());
    }
  }

  /**
   * Get the concrete factory.
   */
  public static PRNGFactory getFactory() {
    return currentFactory;
  }

  public abstract RandomEngine create();

  public abstract RandomEngine create( long seed );

  /**
   * @uml.property name="description"
   */
  public abstract String getDescription();

}
