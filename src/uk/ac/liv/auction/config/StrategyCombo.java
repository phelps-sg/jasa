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
 * A shortcut to define strategies each for one agent group.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class StrategyCombo implements ParameterBasedCase, Parameterizable {

	static Logger logger = Logger.getLogger(StrategyCombo.class);

	private Case cases[];

	public StrategyCombo() {
	}

	/*
	 * @see uk.ac.liv.util.Parameterizable#setup(ec.util.ParameterDatabase,
	 *      ec.util.Parameter)
	 */
	public void setup(ParameterDatabase parameters, Parameter base) {
	}

	public void setValue(String value) {
		String classes[] = value.split("\\s");
		cases = new Case[classes.length];
		try {
			for (int i = 0; i < cases.length; i++) {
				cases[i] = (Case) Class.forName(classes[i]).newInstance();
			}
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
		String s = "";
		for (int i = 0; i < cases.length; i++) {
			if (i == 0) {
				s += cases[i].toString();
			} else {
				s += "|" + cases[i].toString();
			}
		}
		return s;
	}

	public void apply(ParameterDatabase pdb, Parameter base) {
		for (int i = 0; i < cases.length; i++) {
			cases[i].apply(pdb, base.push("agenttype." + i + ".strategy"));
		}
	}
}