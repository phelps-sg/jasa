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
package uk.ac.liv.auction.stats;

import java.util.Map;

import uk.ac.liv.auction.core.RoundRobinAuction;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public interface AuctionReport {
  
  /**
   *  Produce the final report.  Implementors can do whatever they see
   *  fit, including doing nothing.
   */
  public void generateReport();
  
  /**
   * Returns a Map of all of the variables, such as market efficiency, that are
   * produced in the report. The Map maps variables, represented by objects of
   * type ReportVariable, onto values, which may be of any class.  
   * 
   * @seealso AuctionVariable
   */
  public Map getVariables();
  
  /**
   *  Specify the auction to be used when generating the report.
   */
  public void setAuction( RoundRobinAuction auction );

}
