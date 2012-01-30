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
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sourceforge.jabm.event.InteractionsFinishedEvent;
import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationFinishedEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;
import net.sourceforge.jabm.report.DataSeriesWriter;
import net.sourceforge.jabm.report.Report;
import net.sourceforge.jabm.view.XYDatasetAdaptor;
import net.sourceforge.jasa.market.MarketFacade;
import net.sourceforge.jasa.report.SupplyAndDemandStats;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DomainOrder;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYDataset;

import uchicago.src.sim.analysis.plot.RepastPlot;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

//TODO refactor this to use JFreeChart library

public abstract class SupplyAndDemandFrame extends JFrame 
		implements Report {

	protected MarketFacade auction;

	protected JFreeChart graph;

	protected DataSeriesWriter supplyCurve = new DataSeriesWriter();

	protected DataSeriesWriter demandCurve = new DataSeriesWriter();

	protected JButton updateButton;

	protected JCheckBox autoUpdate;
	
	protected XYDatasetAdaptor dataset;

	protected float maxX;

	public static final int SERIES_SUPPLY = 0;

	public static final int SERIES_DEMAND = 1;

	static Logger logger = Logger.getLogger(SupplyAndDemandFrame.class);

	public SupplyAndDemandFrame(MarketFacade auction) {

		this.auction = auction;
		Container contentPane = getContentPane();
		BorderLayout layout = new BorderLayout();
		contentPane.setLayout(layout);

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
        getContentPane().add(chartPanel);
        
//		JPanel controlPanel = new JPanel();
//		updateButton = new JButton("Update");
//		updateButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent event) {
//				updateGraph();
//			}
//		});
//		controlPanel.add(updateButton);

		// autoUpdate = new JCheckBox("Auto Update");
		// autoUpdate.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent event) {
		// toggleAutoUpdate();
		// }
		// });
		// controlPanel.add(autoUpdate);

//		contentPane.add(controlPanel, BorderLayout.SOUTH);

		updateTitle();

		pack();
	}

	protected void toggleAutoUpdate() {
		// if (autoUpdate.isSelected()) {
		// auction.addObserver(this);
		// } else {
		// auction.deleteObserver(this);
		// }
		// TODO
	}
//
//	public void update(Observable auction, Object o) {
//	}
//
//	public void updateGraph() {
//		// graph.clear(0);
//		// graph.clear(1);
//		graph.clearPoints();
//		plotSupplyAndDemand();
//		updateTitle();
//	}

	public void updateTitle() {
		// setTitle(getGraphName() + " at time "
		// + auction.getRound());
	}

	public void open() {
		pack();
		setVisible(true);
	}

	public void close() {
		setVisible(false);
	}

	public abstract String getGraphName();

	public abstract SupplyAndDemandStats getSupplyAndDemandStats();

	public void updateData() {
		supplyCurve.clear();
		demandCurve.clear();
		SupplyAndDemandStats stats = getSupplyAndDemandStats();
		stats.calculate();
		stats.produceUserOutput();
	}
//
//	protected void plotSupplyAndDemand() {
//		maxX = Float.NEGATIVE_INFINITY;
//		plotCurve(SERIES_SUPPLY, supplyCurve);
//		plotCurve(SERIES_DEMAND, demandCurve);
//		finishCurve(SERIES_SUPPLY, supplyCurve);
//		finishCurve(SERIES_DEMAND, demandCurve);
//	}
//
//	protected void plotCurve(int seriesIndex, DataSeriesWriter curve) {
//		if (curve.length() > 0) {
//			for (int i = 0; i < curve.length(); i++) {
//				graph.addPoint(seriesIndex, curve.getXCoord(i),
//						curve.getYCoord(i), true);
//			}
//			float lastPointX = curve.getXCoord(curve.length() - 1);
//			if (lastPointX > maxX) {
//				maxX = lastPointX;
//			}
//		}
//	}
//
//	protected void finishCurve(int seriesIndex, DataSeriesWriter curve) {
//		if (curve.length() > 0) {
//			int l = curve.length() - 1;
//			double lastX = curve.getXCoord(l);
//			double lastY = curve.getYCoord(l);
//			if (lastX < maxX) {
//				graph.addPoint(seriesIndex, maxX, lastY, true);
//			}
//		}
//	}

	@Override
	public void eventOccurred(final SimEvent event) {
		if (event instanceof InteractionsFinishedEvent) {
			updateData();
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						dataset.datasetChanged(event);
					}
				});
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (event instanceof SimulationStartingEvent) {
			open();
		} else if (event instanceof SimulationFinishedEvent) {
			close();
		}
	}

	@Override
	public Map<Object, Number> getVariableBindings() {
		return new HashMap<Object, Number>();
	}
	
	
	class SupplyAndDemandDataset implements XYDataset {

		protected LinkedList<DatasetChangeListener> listeners 
			= new LinkedList<DatasetChangeListener>();
		
		public DataSeriesWriter getDataSeries(int series) {
			if (series == 0) {
				return supplyCurve;
			} else {
				return demandCurve;
			}
		}
		
		@Override
		public int getSeriesCount() {
			return 2;
		}

		@Override
		public Comparable getSeriesKey(int series) {
			if (series==0) {
				return "Supply";
			} else {
				return "Demand";
			}
		}

		@Override
		public int indexOf(Comparable seriesKey) {
			if (seriesKey.equals("Supply")) {
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public void addChangeListener(DatasetChangeListener listener) {
			listeners.add(listener);
		}

		@Override
		public void removeChangeListener(DatasetChangeListener listener) {
			listeners.remove(listener);
		}

		@Override
		public DatasetGroup getGroup() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setGroup(DatasetGroup group) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public DomainOrder getDomainOrder() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getItemCount(int series) {
			return getDataSeries(series).length();
		}

		@Override
		public Number getX(int series, int item) {
			return getDataSeries(series).getXCoord(item);
		}

		@Override
		public double getXValue(int series, int item) {
			return getDataSeries(series).getYCoord(item);
		}

		@Override
		public Number getY(int series, int item) {
			return getDataSeries(series).getYCoord(item);
		}

		@Override
		public double getYValue(int series, int item) {
			return getDataSeries(series).getXCoord(item);
		}
		
	}

}
