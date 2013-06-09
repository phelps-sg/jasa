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
package net.sourceforge.jasa.market;

import java.io.Serializable;

public class Account implements Serializable {

	protected double funds;

	protected Object owner;

	public Account(Object owner, double funds) {
		this.funds = funds;
		this.owner = owner;
	}

	public Account() {
		this.funds = 0;
		owner = null;
	}

	public void credit(double additionalFunds) {
		funds += additionalFunds;
	}

	public void transfer(Account other, double payment) {
		other.credit(payment);
		this.debit(payment);
	}

	protected void debit(double payment) {
		funds -= payment;
	}

	public void doubleEntry(Account payer, double charge, Account payee,
	    double payment) {
		payer.transfer(this, charge);
		transfer(payee, payment);
	}

	public double getFunds() {
		return funds;
	}

	public void setFunds(double funds) {
		this.funds = funds;
	}

	public Object getOwner() {
		return owner;
	}

	protected void setOwner(Object owner) {
		this.owner = owner;
	}

	public String toString() {
		return "(" + getClass() + " funds:" + funds + ")";
	}

}
