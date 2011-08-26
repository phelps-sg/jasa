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

package net.sourceforge.jasa.view;

import java.util.Map;

import net.sourceforge.jasa.market.MarketFacade;
import net.sourceforge.jasa.report.SupplyAndDemandStats;
import net.sourceforge.jasa.report.TrueSupplyAndDemandStats;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class TrueSupplyAndDemandFrame extends SupplyAndDemandFrame {

	public static final String TITLE = "Supply and Demand Graph";

	public TrueSupplyAndDemandFrame(MarketFacade auction) {
		super(auction);
	}

	public String getGraphName() {
		return TITLE;
	}

	public SupplyAndDemandStats getSupplyAndDemandStats() {
		return new TrueSupplyAndDemandStats(auction, supplyCurve, demandCurve);
	}


}
