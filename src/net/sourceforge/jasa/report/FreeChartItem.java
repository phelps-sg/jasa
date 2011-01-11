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

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.util.Parameterizable;
import net.sourceforge.jasa.event.MarketEvent;
import net.sourceforge.jasa.event.MarketEventListener;

import org.apache.log4j.Logger;


/**
 * A class defining the common properties and methods of data series and
 * markers.
 * 
 * <p>
 * <b>Parameters</b><br>
 * 
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.name</tt><br>
 * <font size=-1> string </font></td>
 * <td valign=top></td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.axis</tt><br>
 * <font size=-1> int </font></td>
 * <td valign=top>(the index of the axis this chart item is associated with)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.renderer</tt><br>
 * <font size=-1> int </font></td>
 * <td valign=top>(the index of the renderer this chart item uses)</td>
 * </tr>
 * 
 * </table>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class FreeChartItem implements MarketEventListener, Parameterizable {

	static Logger logger = Logger.getLogger(FreeChartItem.class);

	public static final String P_DEF_BASE = "freechartitem";

	public static final String P_NAME = "name";

	public static final String P_AXIS = "axis";

	public static final String P_RENDERER = "renderer";

	protected String name;

	protected FreeChartGraph graph;

	protected int axisIndex;

	protected int rendererIndex;

	public FreeChartItem() {
		name = "";
	}


	public void eventOccurred(SimEvent event) {
	}

	public int getAxisIndex() {
		return axisIndex;
	}

	public int getRendererIndex() {
		return rendererIndex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FreeChartGraph getGraph() {
		return graph;
	}

	/**
	 * @param graph
	 *          The graph to set.
	 */
	public void setGraph(FreeChartGraph graph) {
		this.graph = graph;
	}
}