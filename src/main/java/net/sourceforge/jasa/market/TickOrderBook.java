/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2015 Steve Phelps
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
package net.sourceforge.jasa.market;

/**
 * An order-book with a finite tick size. 
 * 
 * All orders submitted to the book will have their prices
 * rounded to the specified number of decimal places.
 * 
 * @author sphelps
 *
 */
public class TickOrderBook extends FourHeapOrderBook {

	public static final int DEFAULT_TICK_SIZE = 4;
	
	protected int decimalPlaces;
	
	protected double multiplier = Math.pow(10, decimalPlaces);
	
	public TickOrderBook(int tickSize) {
		super();
		this.decimalPlaces = tickSize;
	}
	
	public TickOrderBook() {
		this(DEFAULT_TICK_SIZE);
	}

	@Override
	public void add(Order shout) throws DuplicateShoutException {
		roundPrice(shout);
		super.add(shout);
	}
	
	protected void roundPrice(Order order) {
		order.setPrice(Math.round(order.getPrice() * multiplier) / multiplier);
	}

	public int getDecimalPlaces() {
		return decimalPlaces;
	}

	public void setDecimalPlaces(int tickSize) {
		this.decimalPlaces = tickSize;
	}
	
	public double getTickSize() {
		return 1 / multiplier;
	}
	
	
}
