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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sourceforge.jabm.report.DataSeriesWriter;
import net.sourceforge.jasa.market.MarketFacade;
import net.sourceforge.jasa.report.SupplyAndDemandStats;

import org.apache.log4j.Logger;

import uchicago.src.sim.analysis.plot.RepastPlot;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class SupplyAndDemandFrame extends JFrame implements Observer {

	protected MarketFacade auction;

	protected RepastPlot graph;

	protected DataSeriesWriter supplyCurve;

	protected DataSeriesWriter demandCurve;

	protected JButton updateButton;

	protected JCheckBox autoUpdate;

	protected float maxX;

	public static final int SERIES_SUPPLY = 0;

	public static final int SERIES_DEMAND = 1;

	static Logger logger = Logger.getLogger(SupplyAndDemandFrame.class);

	public SupplyAndDemandFrame(MarketFacade auction) {

		this.auction = auction;
		Container contentPane = getContentPane();
		BorderLayout layout = new BorderLayout();
		contentPane.setLayout(layout);

		graph = new RepastPlot(null);
		plotSupplyAndDemand();
		graph.addLegend(SERIES_SUPPLY, "Supply", Color.BLUE);
		graph.addLegend(SERIES_DEMAND, "Demand", Color.RED);

		contentPane.add(graph, BorderLayout.CENTER);

		JPanel controlPanel = new JPanel();
		updateButton = new JButton("Update");
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				updateGraph();
			}
		});
		controlPanel.add(updateButton);

		autoUpdate = new JCheckBox("Auto Update");
		autoUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				toggleAutoUpdate();
			}
		});
		controlPanel.add(autoUpdate);

		contentPane.add(controlPanel, BorderLayout.SOUTH);

		updateTitle();

		pack();
	}

	protected void toggleAutoUpdate() {
//		if (autoUpdate.isSelected()) {
//			auction.addObserver(this);
//		} else {
//			auction.deleteObserver(this);
//		}
		//TODO
	}

	public void update(Observable auction, Object o) {
		updateGraph();
	}

	public void updateGraph() {
		graph.clear(0);
		graph.clear(1);
		plotSupplyAndDemand();
		updateTitle();
	}

	public void updateTitle() {
		setTitle(getGraphName() + " at time "
		    + auction.getRound());
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

	protected void plotSupplyAndDemand() {
		supplyCurve = new DataSeriesWriter();
		demandCurve = new DataSeriesWriter();
		SupplyAndDemandStats stats = getSupplyAndDemandStats();
		stats.calculate();
		stats.produceUserOutput();
		maxX = Float.NEGATIVE_INFINITY;
		plotCurve(SERIES_SUPPLY, supplyCurve);
		plotCurve(SERIES_DEMAND, demandCurve);
		finishCurve(SERIES_SUPPLY, supplyCurve);
		finishCurve(SERIES_DEMAND, demandCurve);
	}

	protected void plotCurve(int seriesIndex, DataSeriesWriter curve) {
		if (curve.length() > 0) {
			for (int i = 0; i < curve.length(); i++) {
				graph.addPoint(seriesIndex, curve.getXCoord(i), curve.getYCoord(i),
				    true);
			}
			float lastPointX = curve.getXCoord(curve.length() - 1);
			if (lastPointX > maxX) {
				maxX = lastPointX;
			}
		}
	}

	protected void finishCurve(int seriesIndex, DataSeriesWriter curve) {
		if (curve.length() > 0) {
			int l = curve.length() - 1;
			double lastX = curve.getXCoord(l);
			double lastY = curve.getYCoord(l);
			if (lastX < maxX) {
				graph.addPoint(seriesIndex, maxX, lastY, true);
			}
		}
	}
}
