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

package net.sourceforge.jasa.report;

import java.awt.Color;
import java.util.Iterator;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.report.DataWriter;
import net.sourceforge.jasa.event.MarketOpenEvent;
import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;
import net.sourceforge.jasa.market.rules.EquilibriumBeatingAcceptingPolicy;

import org.apache.log4j.Logger;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimePeriodValue;
import org.jfree.data.xy.XYSeries;

/**
 * A <code>FreeChartGraph</code> showing supply and demand curves.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class SupplyAndDemandGraph extends FreeChartGraph {

	static Logger logger = Logger.getLogger(SupplyAndDemandGraph.class);

	DataWriterSeries trueSupply;

	DataWriterSeries trueDemand;

	DataWriterSeries reportedSupply;

	DataWriterSeries reportedDemand;

	CombinedRangeXYPlot combinedPlot;

	XYPlot truePlot;

	XYPlot reportedPlot;

	public SupplyAndDemandGraph() {
	}

	public void eventOccurred(SimEvent event) {
		if (event instanceof OrderPlacedEvent) {

			((XYSeries) reportedSupply.getSeries()).clear();
			((XYSeries) reportedDemand.getSeries()).clear();
			ReportedSupplyAndDemandStats rsdStats = new ReportedSupplyAndDemandStats(
			    getReport().getAuction(), reportedSupply, reportedDemand);
			rsdStats.calculate();
			rsdStats.produceUserOutput();

		} else if (event instanceof MarketOpenEvent) {

			double value = ((TimePeriodValue) ReportVariableBoard.getInstance()
			    .getValue(ReportVariableBoardUpdater.EQUIL_PRICE)).getValue()
			    .doubleValue();
			Marker marker = FreeChartMarker.createMarker(value, Color.black, "EE");
			logger.debug("Equilibriuim: " + value);
			truePlot.clearRangeMarkers();
			truePlot.addRangeMarker(marker);

			updateTrueDS();

		} else if (event instanceof TransactionExecutedEvent) {

			updateTrueDS();
			updateReportedDS();

		}
	}

	private void updateTrueDS() {
		((XYSeries) trueSupply.getSeries()).clear();
		((XYSeries) trueDemand.getSeries()).clear();
		TrueSupplyAndDemandStats tsdStats = new TrueSupplyAndDemandStats(
		    getReport().getAuction(), trueSupply, trueDemand);
		tsdStats.calculate();
		tsdStats.produceUserOutput();

	}

	private void updateReportedDS() {
		TimePeriodValue tpValue = (TimePeriodValue) ReportVariableBoard
		    .getInstance().getValue(
		        EquilibriumBeatingAcceptingPolicy.EST_EQUILIBRIUM_PRICE);
		if (tpValue != null) {
			double value = tpValue.getValue().doubleValue();
			Marker marker = FreeChartMarker.createMarker(value, Color.black, "EE");
			logger.debug("Estimated equilibrium price : " + value);
			reportedPlot.clearRangeMarkers();
			reportedPlot.addRangeMarker(marker);
		}
	}

	public class DataWriterSeries extends FreeChartSeries implements DataWriter {

		public DataWriterSeries(String name) {
			series = new XYSeries(name, false, true);
		}

		public void newData(int datum) {
			newData((double) datum);
		}

		public void newData(long datum) {
			newData((double) datum);
		}

		public void newData(float datum) {
			newData((double) datum);
		}

		public void newData(String datum) {

		}

		private double temp;

		private boolean dataExpected;

		public void newData(double datum) {

			if (dataExpected) {
				((XYSeries) getSeries()).add((int) temp, datum);
			} else {
				temp = datum;
			}

			dataExpected = !dataExpected;
		}

		public void flush() {
		}

		public void close() {
		}

		@SuppressWarnings("rawtypes")
		public void newData(Iterator i) {
			throw newException();
		}

		public void newData(Object[] data) {
			throw newException();
		}

		public void newData(Object data) {
			throw newException();
		}

		public void newData(boolean data) {
			throw newException();
		}

		public void newData(Double data) {
			throw newException();
		}

		public void newData(Integer data) {
			throw newException();
		}

		public void newData(Long data) {
			throw newException();
		}

		private UnsupportedOperationException newException() {
			return new java.lang.UnsupportedOperationException(
			    "Method newData() not yet implemented.");
		}
	}

}