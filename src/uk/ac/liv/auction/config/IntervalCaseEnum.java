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

package uk.ac.liv.auction.config;

import org.apache.log4j.Logger;

import uk.ac.liv.util.Parameterizable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * A type of CaseEnum that can automatically generate a list of value cases of
 * an auction property based on the specified value range.
 * 
 * 
 * <p>
 * <b>Parameters </b> <br>
 * 
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.name</tt><br>
 * <font size=-1> string </font></td>
 * <td valign=top>(the name of this property)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.case</tt><br>
 * <font size=-1> classname inherits uk.ac.liv.auction.config.ParameterBasedCase
 * </font></td>
 * <td valign=top>(the class whose instance represents a value of the property)
 * </td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.min</tt><br>
 * <font size=-1>double</font></td>
 * <td valign=top>(the lower bound of the range) </td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.max</tt><br>
 * <font size=-1>double</font></td>
 * <td valign=top>(the upper bound of the range) </td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.step</tt><br>
 * <font size=-1>double</font></td>
 * <td valign=top>(the interval length to sample within the range) </td>
 * </tr>
 * 
 * </table>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class IntervalCaseEnum extends CaseEnum {

	static Logger logger = Logger.getLogger(IntervalCaseEnum.class);

	private ParameterBasedCase cases[];

	private int i;

	private static final String P_NAME = "name";

	private static final String P_CASE = "case";

	private static final String P_MIN = "min";

	private static final String P_MAX = "max";

	private static final String P_STEP = "step";

	public void setup(ParameterDatabase pdb, Parameter base) {

		setName(pdb.getString(base.push(P_NAME), null));

		Class c = pdb.getInstanceForParameterEq(base.push(P_CASE), null,
		    ParameterBasedCase.class).getClass();

		int count;
		Number min, max, step;
		try {
			min = new Integer(pdb.getInt(base.push(P_MIN), null));
			max = new Integer(pdb.getInt(base.push(P_MAX), null));
			step = new Integer(pdb.getInt(base.push(P_STEP), null));
			count = (max.intValue() - min.intValue()) / step.intValue() + 1;
		} catch (NumberFormatException e) {
			min = new Double(pdb.getDoubleWithDefault(base.push(P_MIN), null, 0));
			max = new Double(pdb.getDoubleWithDefault(base.push(P_MAX), null, 1));
			step = new Double(pdb.getDoubleWithDefault(base.push(P_STEP), null, 0.1));
			count = (int) ((max.doubleValue() - min.doubleValue())
			    / step.doubleValue() + 1);
		}

		assert max.doubleValue() > min.doubleValue();

		cases = new ParameterBasedCase[count];
		for (int i = 0; i < cases.length; i++) {
			try {
				cases[i] = (ParameterBasedCase) c.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				logger.error(e.toString());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				logger.error(e.toString());
			}

			if (cases[i] instanceof Parameterizable) {
				((Parameterizable) cases[i]).setup(pdb, base.push(P_CASE));
			}
			if (min instanceof Integer) {
				cases[i].setValue(String.valueOf(min.intValue() + i * step.intValue()));
			} else {
				if (min.doubleValue() + i * step.doubleValue() > max.doubleValue()) {
					cases[i].setValue(max.toString());
				} else {
					cases[i].setValue(String.valueOf(min.doubleValue() + i
					    * step.doubleValue()));
				}
			}
		}

		reset();
	}

	public void reset() {
		i = 0;
	}

	/*
	 * @see uk.ac.liv.auction.config.CaseEnum#moreCases()
	 */
	public boolean moreCases() {
		return i < cases.length;
	}

	/*
	 * @see uk.ac.liv.auction.config.CaseEnum#nextCase()
	 */
	public Case nextCase() {
		assert moreCases();

		return cases[i++];
	}
}