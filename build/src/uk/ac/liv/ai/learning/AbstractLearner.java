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

package uk.ac.liv.ai.learning;

import ec.util.Parameter;
import ec.util.ParamClassLoadException;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.Parameterizable;

import uk.ac.liv.util.io.DataWriter;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class AbstractLearner implements Learner, Parameterizable {

  /**
   * @uml.property name="monitor"
   * @uml.associationEnd
   */
  protected LearnerMonitor monitor = null;

  public static final String P_MONITOR = "monitor";

  public AbstractLearner() {
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    try {
      Parameter monitorParameter = base.push(P_MONITOR);
      monitor = (LearnerMonitor) parameters.getInstanceForParameter(
          monitorParameter, null, LearnerMonitor.class);
      monitor.setup(parameters, monitorParameter);
    } catch ( ParamClassLoadException e ) {
      monitor = null;
    }

  }

  public void monitor() {
    if ( monitor != null ) {
      monitor.startRecording();
      dumpState(monitor);
      monitor.finishRecording();
    }
  }

  public abstract double getLearningDelta();

  public abstract void dumpState( DataWriter out );

}