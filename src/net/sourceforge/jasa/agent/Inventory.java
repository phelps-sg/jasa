/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2013 Steve Phelps
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

package net.sourceforge.jasa.agent;

import java.io.Serializable;

/**
 * Class to track inventories of agents.  The inventory will typically
 * specify the number of shares currently held by the specified agent.
 * 
 * @author Steve Phelps
 */
public class Inventory implements Serializable {

	/**
	 * The number of shares held by the agent.
	 */
	protected int quantity;

	/**
	 * The agent whose inventory we are tracking.
	 */
	protected TradingAgent owner;

	
	public Inventory() {
		this(0);
	}

	public Inventory(int quantity) {
		this.quantity = quantity;
	}

	public void add(int quantity) {
		this.quantity += quantity;
	}

	public void remove(int quantity) {
		this.quantity -= quantity;
	}

	public void transfer(Inventory other, int quantity) {
		this.remove(quantity);
		other.add(quantity);
	}

	public TradingAgent getOwner() {
		return owner;
	}

	public void setOwner(TradingAgent owner) {
		this.owner = owner;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String toString() {
		return "(" + getClass() + " quantity:" + quantity + " owner:" + owner + ")";
	}

}
