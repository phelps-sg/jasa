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

package uk.ac.liv.auction.agent.jade;

import jade.content.Predicate;

/**
 * A predicate representing information a new agent registration in an
 * auction.
 */

public class RegisterAction implements Predicate {

  String agent;

  public RegisterAction() {
  }

  public void setAgent( String agent ) {
    this.agent = agent;
  }

  public String getAgent() {
    return agent;
  }

}