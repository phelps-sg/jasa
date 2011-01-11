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

package net.sourceforge.jasa.agent.valuation;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.engine.RandomEngine;
import net.sourceforge.jabm.EventScheduler;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.EndOfDayEvent;

/**
 * A valuation policy in which we are allocated a new random valuation at the
 * end of each day.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class DailyRandomValuer extends RandomValuer {

	public DailyRandomValuer() {
		super();
	}

	public DailyRandomValuer(AbstractContinousDistribution distribution) {
		super(distribution);
	}
	
	public DailyRandomValuer(double minValue, double maxValue, RandomEngine prng) {
		super(minValue, maxValue, prng);
	}

	public void eventOccurred(SimEvent event) {
		if (event instanceof EndOfDayEvent) {
			drawRandomValue();
		}
	}

	@Override
	public void subscribeToEvents(EventScheduler scheduler) {
		scheduler.addListener(EndOfDayEvent.class, this);
	}

}
