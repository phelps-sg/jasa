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

import org.apache.log4j.Logger;

import uk.ac.liv.util.Parameterizable;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * Defines the strategy used by the specified agent group.
 * 
 * <p>
 * <b>Parameters </b> <br>
 * 
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.isseller</tt><br>
 * <font size=-1> boolean </font></td>
 * <td valign=top>(specifies which type of agents to be affected)</td>
 * </tr>
 * 
 * </table>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class Strategy implements ParameterBasedCase, Parameterizable {

	static Logger logger = Logger.getLogger(Strategy.class);

	/**
	 * @uml.property name="c"
	 * @uml.associationEnd
	 */
	private Case c;

	/**
	 * @uml.property name="isSeller"
	 */
	private boolean isSeller;

	private static final String P_ISSELLER = "isseller";

	public Strategy() {
	}

	/*
	 * @see uk.ac.liv.util.Parameterizable#setup(ec.util.ParameterDatabase,
	 *      ec.util.Parameter)
	 */
	public void setup(ParameterDatabase parameters, Parameter base) {
		isSeller = parameters.getBoolean(base.push(P_ISSELLER), null, true);
	}

	public void setValue(String value) {
		try {
			c = (Case) Class.forName(value).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			logger.error(e.toString());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			logger.error(e.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
	}

	public String toString() {
		return c.toString();
	}

	public void apply(ParameterDatabase pdb, Parameter base) {
		int index = isSeller ? 0 : 1;
		c.apply(pdb, base.push("agenttype." + index + ".strategy"));
	}
}