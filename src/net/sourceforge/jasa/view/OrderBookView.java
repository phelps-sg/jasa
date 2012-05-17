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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.report.Report;
import net.sourceforge.jasa.event.OrderPlacedEvent;
import net.sourceforge.jasa.event.TransactionExecutedEvent;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.auctioneer.Auctioneer;

/**
 * JFrameReportVariables automatically record other ReportVariables to a 
 * swing JTable on a window whenever they are computed.
 * 
 * @author Steve Phelps
 *
 */
public class OrderBookView implements Report, TableModel, InitializingBean {

	protected JFrame frame;
	
	protected JTable table;
		protected LinkedList<TableModelListener> listeners 
		= new LinkedList<TableModelListener>();
	
	protected Auctioneer auctioneer;
	
	Map<Object, Number> variableBindings;
	
	protected int maxDepth;
	
	DecimalFormat priceFormat
		= new DecimalFormat("#00000.00");
	

	DecimalFormat qtyFormat
		= new DecimalFormat("#00000");
	
	
	public OrderBookView() {
	}

	public DecimalFormat getFormat() {
		return priceFormat;
	}

	public void setFormat(DecimalFormat format) {
		this.priceFormat = format;
	}

	public void notifyTableChanged() {
		for(TableModelListener l : listeners) {
			l.tableChanged(new TableModelEvent(this));
		}
		
	}
	
	public Auctioneer getAuctioneer() {
		return auctioneer;
	}

	@Required
	public void setAuctioneer(Auctioneer auctioneer) {
		this.auctioneer = auctioneer;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	@Override
	public int getRowCount() {
		int currentDepth = Math.max(auctioneer.getUnmatchedAsks().size(),
					auctioneer.getUnmatchedBids().size());
		return Math.max(maxDepth, currentDepth);
	}

	@Override
	public int getColumnCount() {
		return 4;
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
		List<Order> bids = auctioneer.getUnmatchedBids();
		List<Order> asks = auctioneer.getUnmatchedAsks();
		switch (columnIndex) {
		case 0:
			return rowIndex < bids.size() 
						? priceFormat.format(bids.get(rowIndex).getPrice()) 
						: "";
		case 1:
			return rowIndex < bids.size() 
						? qtyFormat.format(bids.get(rowIndex).getQuantity()) 
						: "";
		case 2:
			return rowIndex < asks.size() 
						? priceFormat.format(asks.get(rowIndex).getPrice()) 
						: "";
		case 3:
			return rowIndex < asks.size() 
					? qtyFormat.format(asks.get(rowIndex).getQuantity()) 
					: "";
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
		if (event instanceof OrderPlacedEvent || event instanceof TransactionExecutedEvent) {
			notifyTableChanged();
		}
	}

	@Override
	public Map<Object, Number> getVariableBindings() {
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		frame = new JFrame();
		frame.setTitle("JASA: Order Book");
		table = new JTable(this);
		table.setPreferredSize(new Dimension(400,200));
		frame.add(table);
		frame.pack();
		frame.setVisible(true);
	}
}
