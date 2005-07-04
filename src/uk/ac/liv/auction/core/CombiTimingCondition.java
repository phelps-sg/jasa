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

package uk.ac.liv.auction.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ec.util.Parameter;
import ec.util.ParameterDatabase;
import uk.ac.liv.util.Parameterizable;

/**
 * The class for expressing the combination of closing conditions.
 *
 * <p><b>Parameters</b><br></p>
 * <table>
 * <tr><td valign=top><i>base</i><tt>.n</tt><br>
 * <font size=-1>int &gt;= 1</font></td>
 * <td valign=top>(the number of different conditions to configure)</td><tr>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 *
 */

public class CombiTimingCondition extends TimingCondition implements Parameterizable {
  
  private static final String P_NUM = "n";
  private static final String P_RELATION = "relation";
  
  protected List conditions = null;
  
  public static final int OR = 0;
  public static final int AND = 1;
  
  protected int relation;


  public CombiTimingCondition() {
    this.conditions = new LinkedList();
  }


  /* 
   * @see uk.ac.liv.util.Parameterizable#setup(ec.util.ParameterDatabase, ec.util.Parameter)
   */
  public void setup(ParameterDatabase parameters, Parameter base) {
    int numConditions = parameters.getInt(base.push(P_NUM), null, 0);
    
    String s = parameters.getStringWithDefault(base.push(P_RELATION), null, "AND");
    if (s == null || s.length() == 0 || s.equalsIgnoreCase("AND"))
      relation = AND;
    else
      relation = OR;

    for( int i=0; i<numConditions; i++ ) {
      TimingCondition condition = (TimingCondition)
        parameters.getInstanceForParameter(base.push(i+""), null,
            TimingCondition.class);
      condition.setAuction(getAuction());
      if ( condition instanceof Parameterizable ) {
        ((Parameterizable) condition).setup(parameters, base.push(i+""));
      }
      addCondition(condition);
    }
  }
  
  public void addCondition(TimingCondition condition) {
    conditions.add(condition);
  }
  
  public Iterator conditionIterator() {
    return conditions.iterator();
  }

  public void setAuction( RoundRobinAuction auction ) {
    super.setAuction(auction);
    Iterator i = conditionIterator();
    while ( i.hasNext() ) {
      TimingCondition condition = (TimingCondition) i.next();
      condition.setAuction(auction);
    }
  }


  public boolean eval() {

    boolean isClosing = false;
    Iterator i = conditionIterator();
    while ( i.hasNext() ) {
      TimingCondition condition = (TimingCondition) i.next();
      if (relation == AND)
        isClosing = isClosing && condition.eval();
      else // if relation == OR
        isClosing = condition.eval();
      
      if (isClosing) break;
    }
    
    return isClosing;
  }
}