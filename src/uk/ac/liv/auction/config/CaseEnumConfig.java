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

package uk.ac.liv.auction.config;

import java.util.Observable;

import org.apache.log4j.Logger;

import uk.ac.liv.util.Parameterizable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

/**
 * The class initializing a set of different auction settings - a combination of
 * properties each taking one or more values.
 * 
 * <p>
 * <b>Parameters</b><br>
 * 
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.n</tt><br>
 * <font size=-1> int </font></td>
 * <td valign=top>(the number of CaseEnums to generate a set of different
 * auction )</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.i</tt><br>
 * <font size=-1> classname inherits uk.ac.liv.auction.config.CaseEnum </font></td>
 * <td valign=top>(the enumeration of different values of a property to
 * generate a set of different auctions)</td>
 * </tr>
 * 
 * </table>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class CaseEnumConfig extends Observable implements Parameterizable {

  private static CaseEnumConfig instance = null;

  static Logger logger = Logger.getLogger(CaseEnumConfig.class);

  public static final String P_N = "n";

  /**
   * All the <code>CaseEnum</code> to produce combinations.
   */
  protected static CaseEnum varieties[];

  /**
   * The current combination of <code>Case</code>s.
   */
  protected static Case combo[];

  /**
   * @uml.property name="title"
   */
  protected String title;

  public CaseEnumConfig() {
    instance = this;
  }

  public static CaseEnumConfig getInstance() {
    return instance;
  }

  /*
   * @see uk.ac.liv.util.Parameterizable#setup(ec.util.ParameterDatabase,
   *      ec.util.Parameter)
   */
  public void setup( ParameterDatabase parameters, Parameter base ) {

    int numOfEnums = parameters.getIntWithDefault(base.push(P_N), null, 0);

    varieties = new CaseEnum[numOfEnums];
    combo = new Case[numOfEnums];

    for ( int i = 0; i < numOfEnums; i++ ) {
      if ( parameters.exists(base.push(String.valueOf(i))) ) {
        varieties[i] = (CaseEnum) parameters.getInstanceForParameterEq(base
            .push(String.valueOf(i)), null, CaseEnum.class);
      } else {
        // ParameterBasedCaseEnum is default
        varieties[i] = new ParameterBasedCaseEnum();
      }

      varieties[i].setup(parameters, base.push(String.valueOf(i)));
      if ( varieties[i].moreCases() )
        combo[i] = varieties[i].nextCase();
      else
        logger.error("No case at all in the enumeration of "
            + varieties[i].getName());

    }
  }

  public boolean next() {
    int index = varieties.length - 1;

    while ( index >= 0 && !varieties[index].moreCases() ) {
      varieties[index].reset();
      combo[index] = varieties[index].nextCase();
      index--;
    }

    if ( index < 0 )
      return false;
    else {
      combo[index] = varieties[index].nextCase();
    }

    return true;
  }

  public void apply( ParameterDatabase parameters, Parameter base ) {
    for ( int i = 0; i < combo.length; i++ ) {
      combo[i].apply(parameters, base);
    }
  }

  public int getCaseEnumNum() {
    return varieties.length;
  }

  public CaseEnum getCaseEnumAt( int i ) {
    return varieties[i];
  }

  public Case getCaseAt( int i ) {
    return combo[i];
  }

  public String getCurrentDesc() {
    String title = "";
    for ( int i = 0; i < combo.length; i++ ) {
      if ( i == 0 )
        title += combo[i];
      else
        title += " & " + combo[i];
    }

    return title;
  }

}