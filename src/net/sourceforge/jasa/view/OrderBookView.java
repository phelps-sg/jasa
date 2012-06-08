/*
 * JABM - Java Agent-Based Modeling Toolkit
 * Copyright (C) 2011 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package net.sourceforge.jasa.view;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.springframework.beans.factory.InitializingBean;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.report.Report;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.auctioneer.Auctioneer;

public class OrderBookView extends JTable implements Report, TableModel,
		InitializingBean {

	protected LinkedList<TableModelListener> listeners = new LinkedList<TableModelListener>();

	protected Auctioneer auctioneer;

	Map<Object, Number> variableBindings;

	protected int currentDepth;

	protected List<Order> bids = new ArrayList<Order>(0);

	protected List<Order> asks = new ArrayList<Order>(0);

	protected int maxDepth;

	DecimalFormat priceFormat = new DecimalFormat("#00000.00");

	DecimalFormat qtyFormat = new DecimalFormat("#00000");

	public static final int NUM_COLUMNS = 4;

	public OrderBookView() {
		super();
	}

	public DecimalFormat getPriceFormat() {
		return priceFormat;
	}

	public void setPriceFormat(DecimalFormat format) {
		this.priceFormat = format;
	}

	public DecimalFormat getQtyFormat() {
		return qtyFormat;
	}

	public void setQtyFormat(DecimalFormat qtyFormat) {
		this.qtyFormat = qtyFormat;
	}

	public void notifyTableChanged() {
		for (TableModelListener l : listeners) {
			l.tableChanged(new TableModelEvent(this));
		}
	}

	public void update() {
		this.bids = auctioneer.getUnmatchedBids();
		this.asks = auctioneer.getUnmatchedAsks();
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	@Override
	public int getRowCount() {
		this.currentDepth = Math.max(asks.size(), bids.size());
		return Math.max(maxDepth, currentDepth);
	}

	@Override
	public int getColumnCount() {
		return NUM_COLUMNS;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "Bid Price";
		case 1:
			return "Bid Qty";
		case 2:
			return "Ask Price";
		case 3:
			return "Ask Qty";
		}
		return "NA";
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return rowIndex < bids.size() ? priceFormat.format(bids.get(
					rowIndex).getPrice()) : "";
		case 1:
			return rowIndex < bids.size() ? qtyFormat.format(bids.get(rowIndex)
					.getQuantity()) : "";
		case 2:
			return rowIndex < asks.size() ? priceFormat.format(asks.get(
					rowIndex).getPrice()) : "";
		case 3:
			return rowIndex < asks.size() ? qtyFormat.format(asks.get(rowIndex)
					.getQuantity()) : "";
		}
		return "";
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	@Override
	public void eventOccurred(SimEvent event) {
		if (event instanceof MarketEvent) {
			onMarketEvent(event);
		}
	}

	public void onMarketEvent(SimEvent event) {
		this.auctioneer = ((MarketEvent) event).getAuction()
				.getAuctioneer();
		if (this.isShowing()) {
			update();
			notifyTableChanged();
		}
	}

	@Override
	public Map<Object, Number> getVariableBindings() {
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.setModel(this);
		this.setPreferredSize(new Dimension(400, 200));
	}

	@Override
	public String getName() {
		return "JASA: Order Book";
	}
}
