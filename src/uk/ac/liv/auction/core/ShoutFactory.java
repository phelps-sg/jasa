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


package uk.ac.liv.auction.core;

import uk.ac.liv.auction.agent.TraderAgent;

/**
 * <p>
 * A factory for creating Shout objects.  This factory should be used
 * to create new Shout instances instead of the standard constructor.
 * </p>
 *
 * <p>
 * By default this factory fetches Shout objects from a pool (implemented
 * by the PooliT library).  When Shout instances are no longer required
 * they can be returned to the pool and reused the next time a new shout
 * is required.  This can significantly improve performance by reducing
 * garbage collection overhead.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <p>
 * <code>
 * Shout myNewShout =
 *   ShoutFactory.getFactory().create(agent, 1, 4.5, false);
 * </code>
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class ShoutFactory {

  protected static ShoutFactory factory = new UnpooledShoutFactory();

  /**
   * Get the concrete factory.
   */
  public static ShoutFactory getFactory() {
    return factory;
  }

  /**
   * Construct a new shout.
   */
  public abstract Shout create( TraderAgent agent, int quantity, double price,
                              boolean isBid );

}

class UnpooledShoutFactory extends ShoutFactory {
  
  public UnpooledShoutFactory() {
  }
  
  public Shout create( TraderAgent agent, int quantity, double price,
                        boolean isBid ) {
     return new Shout(agent, quantity, price, isBid);   
  }
  
}