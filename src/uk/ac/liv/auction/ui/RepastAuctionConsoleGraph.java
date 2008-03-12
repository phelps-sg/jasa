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

package uk.ac.liv.auction.ui;

import java.util.Iterator;

import uk.ac.liv.auction.stats.GraphReport;
import uk.ac.liv.auction.stats.RepastGraphSequence;

import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.analysis.OpenSequenceGraph;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class RepastAuctionConsoleGraph extends OpenSequenceGraph {

	public RepastAuctionConsoleGraph(String title, SimModel repastModel,
	    GraphReport graphModel) {
		super(title, repastModel);
		Iterator i = graphModel.getSequenceIterator();
		while (i.hasNext()) {
			RepastGraphSequence sequence = (RepastGraphSequence) i.next();
			addSequence(sequence.getName(), sequence);
		}

	}
}
