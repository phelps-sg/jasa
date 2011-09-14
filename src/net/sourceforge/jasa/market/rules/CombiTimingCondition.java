/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2009 Steve Phelps
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

package net.sourceforge.jasa.market.rules;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.jabm.util.Parameterizable;
import net.sourceforge.jasa.market.MarketFacade;

/**
 * The class for expressing the combination of timing conditions.
 * 
 * <p>
 * <b>Parameters</b><br>
 * </p>
 * <table>
 * <tr>
 * <td valign=top><i>base</i><tt>.n</tt><br>
 * <font size=-1>int &gt;= 1</font></td>
 * <td valign=top>(the number of different conditions to configure)</td>
 * <tr>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 * 
 */

public class CombiTimingCondition extends TimingCondition implements
    Parameterizable, AuctionClosingCondition, DayEndingCondition {

	protected List<TimingCondition> conditions = null;

	public static final int OR = 0;

	public static final int AND = 1;

	protected int relation;

	// private static Logger report =
	// Logger.getLogger(CombiTimingCondition.class);

	public CombiTimingCondition() {
		this.conditions = new LinkedList<TimingCondition>();
	}

	/*
	 * @see net.sourceforge.jabm.util.Parameterizable#setup(ec.util.ParameterDatabase,
	 *      ec.util.Parameter)
	 */
//	public void setup(ParameterDatabase parameters, Parameter base) {
//		int numConditions = parameters.getInt(base.push(P_NUM), null, 0);
//
//		String s = parameters.getStringWithDefault(base.push(P_RELATION), null,
//		    "OR");
//		if (s == null || s.length() == 0 || s.equalsIgnoreCase("OR"))
//			relation = OR;
//		else
//			relation = AND;
//
//		for (int i = 0; i < numConditions; i++) {
//			TimingCondition condition = (TimingCondition) parameters
//			    .getInstanceForParameter(base.push(i + ""), null,
//			        TimingCondition.class);
//			condition.setAuction(getAuction());
//			if (condition instanceof Parameterizable) {
//				((Parameterizable) condition).setup(parameters, base.push(i + ""));
//			}
//			addCondition(condition);
//		}
//	}

	public void addCondition(TimingCondition condition) {
		conditions.add(condition);
	}

	public Iterator<TimingCondition> conditionIterator() {
		return conditions.iterator();
	}

	public void setAuction(MarketFacade auction) {
		super.setAuction(auction);
		Iterator<TimingCondition> i = conditionIterator();
		while (i.hasNext()) {
			TimingCondition condition = (TimingCondition) i.next();
			condition.setAuction(auction);
		}
	}

	public boolean eval() {

		boolean isTrue = false;
		Iterator<TimingCondition> i = conditionIterator();
		while (i.hasNext()) {
			TimingCondition condition = (TimingCondition) i.next();

			if (relation == AND)
				isTrue = isTrue && condition.eval();
			else
				// if relation == OR
				isTrue = condition.eval();

			if (isTrue)
				break;
		}

		return isTrue;
	}
}