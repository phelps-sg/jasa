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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sourceforge.jabm.event.InteractionsFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationFinishedEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;
import net.sourceforge.jabm.report.DataSeriesWriter;
import net.sourceforge.jabm.report.Report;
import net.sourceforge.jabm.report.ReportWithGUI;
import net.sourceforge.jabm.view.XYDatasetAdaptor;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.report.SupplyAndDemandStats;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

//TODO refactor this to use JFreeChart library

public abstract class SupplyAndDemandFrame
		implements ReportWithGUI {
	
	protected Market auction;

	protected JFreeChart graph;

	protected DataSeriesWriter supplyCurve = new DataSeriesWriter();

	protected DataSeriesWriter demandCurve = new DataSeriesWriter();

	protected JButton updateButton;

	protected JCheckBox autoUpdate;
	
	protected XYDatasetAdaptor dataset;

	protected float maxX;

	private JPanel panel;

	public static final int SERIES_SUPPLY = 0;

	public static final int SERIES_DEMAND = 1;

	static Logger logger = Logger.getLogger(SupplyAndDemandFrame.class);

	public SupplyAndDemandFrame() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initialiseGUI();
			}
		});
	}
	
	public void initialiseGUI() {
		panel = new JPanel();
		BorderLayout layout = new BorderLayout();
		panel.setLayout(layout);

		ArrayList<DataSeriesWriter> dataSeries = new ArrayList<DataSeriesWriter>(2);
		dataSeries.add(0, supplyCurve);
		dataSeries.add(1, demandCurve);
		
		ArrayList<String> seriesNames = new ArrayList<String>(2);
		seriesNames.add(0, "Supply");
		seriesNames.add(1, "Demand");
		
		dataset = new XYDatasetAdaptor(dataSeries, seriesNames); 
		graph = ChartFactory
				.createXYLineChart("Supply and demand", "Price", "Quantity",
						dataset, PlotOrientation.HORIZONTAL, 
						true, true, false);
		
		ChartPanel chartPanel = new ChartPanel(graph, false);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        panel.add(chartPanel);
	}

	public void open() {
//		pack();
		panel.setVisible(true);
	}

	public void close() {
		panel.setVisible(false);
	}

	public void updateData() {
		supplyCurve.clear();
		demandCurve.clear();
		SupplyAndDemandStats stats = getSupplyAndDemandStats();
		stats.calculate();
		stats.produceUserOutput();
	}

	@Override
	public void eventOccurred(final SimEvent event) {
		if (event instanceof SimulationStartingEvent) {
			onSimulationStarting(event);
		} else if (event instanceof SimulationFinishedEvent) {
			onSimulationFinished();
		} else if (event instanceof InteractionsFinishedEvent) {
			onInteractionsFinished((InteractionsFinishedEvent) event);
		}
	}

	public void onInteractionsFinished(final InteractionsFinishedEvent event) {
		if (panel.isShowing()) {
			try {
				updateData();
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						dataset.datasetChanged(event);
					}
				});
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (InvocationTargetException e) {
				logger.error(e);
				throw new RuntimeException(e);
			}
		}
	}

	public void onSimulationFinished() {
		close();
	}

	public void onSimulationStarting(final SimEvent event) {
		this.auction = (Market) 
				((SimulationStartingEvent) event).getSimulation();
		open();
	}

//	public void onOrderPlaced() {
//		updateData();
//	}

	@Override
	public Map<Object, Number> getVariableBindings() {
		return new HashMap<Object, Number>();
	}
	
	@Override
	public String getName() {
		return getGraphName();
	}
	
	@Override
	public JComponent getComponent() {
		return panel;
	}

	public abstract String getGraphName();

	public abstract SupplyAndDemandStats getSupplyAndDemandStats();

}
