/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

import java.io.Serializable;

import uk.ac.liv.auction.core.RoundRobinAuction;

import uk.ac.liv.util.Parameterizable;

import org.apache.log4j.Logger;


/**
 * <p>
 * An abstract implementation of MarketDataLogger that provides
 * functionality common to all loggers.
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class AbstractMarketDataLogger
     implements MarketDataLogger, Parameterizable {

  static Logger logger = Logger.getLogger(AbstractMarketDataLogger.class);

  /**
   * The auction we are keeping statistics on.
   */
  protected RoundRobinAuction auction;


  public void setAuction( RoundRobinAuction auction ) {
    this.auction = auction;
    logger.debug("Set auction to " + auction);
  }

}
