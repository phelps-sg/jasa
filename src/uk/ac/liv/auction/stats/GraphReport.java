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

package uk.ac.liv.auction.stats;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;

import javax.swing.event.EventListenerList;

import java.util.Iterator;

import org.apache.log4j.Logger;

import uchicago.src.sim.analysis.Sequence;

/**
 * <p>
 * A report that logs data to a RePast graph sequence.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class GraphReport extends MeanValueDataWriterReport implements
    Parameterizable, Resetable {

	/**
	 * @uml.property name="currentSeries"
	 */
	protected int currentSeries;

	/**
	 * @uml.property name="allSeries"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	protected RepastGraphSequence[] allSeries;

	protected static GraphReport singletonInstance;

	/**
	 * @uml.property name="askQuoteSeries"
	 * @uml.associationEnd readOnly="true"
	 */
	protected RepastGraphSequence askQuoteSeries;

	/**
	 * @uml.property name="bidQuoteSeries"
	 * @uml.associationEnd readOnly="true"
	 */
	protected RepastGraphSequence bidQuoteSeries;

	/**
	 * @uml.property name="transPriceSeries"
	 * @uml.associationEnd readOnly="true"
	 */
	protected RepastGraphSequence transPriceSeries;

	/**
	 * @uml.property name="listenerList"
	 * @uml.associationEnd
	 */
	protected EventListenerList listenerList = new EventListenerList();

	// protected GraphDataEvent event = new GraphDataEvent(this);

	static Logger logger = Logger.getLogger(GraphReport.class);

	public GraphReport() {
		super();
		askQuoteLog = new RepastGraphSequence("mean ask quote per round");
		bidQuoteLog = new RepastGraphSequence("mean bid quote per round");
		transPriceLog = new RepastGraphSequence("mean transaction price per round");
		askLog = new RepastGraphSequence("ask");
		bidLog = new RepastGraphSequence("bid");
		allSeries = new RepastGraphSequence[] { (RepastGraphSequence) askQuoteLog,
		    (RepastGraphSequence) bidQuoteLog, (RepastGraphSequence) transPriceLog };
	}

	public void setup(ParameterDatabase parameters, Parameter base) {
		singletonInstance = this;
	}

	public static GraphReport getSingletonInstance() {
		return singletonInstance;
	}

	public void reset() {
		// TODO
		// fireGraphChanged(new GraphDataEvent(this));
	}

	public Iterator getSequenceIterator() {
		return new Iterator() {

			int currentSequence = 0;

			public boolean hasNext() {
				return currentSequence < allSeries.length;
			}

			public Object next() {
				return (Sequence) allSeries[currentSequence++];
			}

			public void remove() {
			}

		};
	}

}
