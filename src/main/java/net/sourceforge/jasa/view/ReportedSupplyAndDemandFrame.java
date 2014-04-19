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

package net.sourceforge.jasa.view;

import net.sourceforge.jasa.report.ReportedSupplyAndDemandStats;
import net.sourceforge.jasa.report.SupplyAndDemandStats;

/**
 * @author Steve Phelps
 * @version $Revision$
 */
public class ReportedSupplyAndDemandFrame extends SupplyAndDemandFrame {

	public static final String TITLE = "Reported Supply and Demand";

	public ReportedSupplyAndDemandFrame() {
		super();
	}

	public String getGraphName() {
		return TITLE;
	}

	public SupplyAndDemandStats getSupplyAndDemandStats() {
		return new ReportedSupplyAndDemandStats(auction, supplyCurve,
				demandCurve);
	}

}
