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

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.util.Parameterizable;
import net.sourceforge.jasa.view.UserFrame;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartUtilities;

/**
 * A historicalDataReport that logs data to JFreeChart graphs.
 * 
 * <p>
 * <b>Parameters</b><br>
 * </p>
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.name</tt><br>
 * <font size=-1>string</font></td>
 * <td valign=top></td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.graph.n</tt><br>
 * <font size=-1>int</font></td>
 * <td valign=top>(the number of JFreeChart graphs to generate)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.savetofile</tt><br>
 * <font size=-1>boolean</font></td>
 * <td valign=top>(whether to save graphs into files as jpg pictures)</td>
 * <tr>
 * 
 * </table>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

@Deprecated
public class FreeChartReport extends AbstractAuctionReport implements
    Parameterizable, Serializable, Cloneable, WindowListener {

	static Logger logger = Logger.getLogger(FreeChartReport.class);

	private boolean saveToFile = false;

//	private boolean exitOnClose = false;

	private UserFrame frame;

	private FreeChartGraph graphs[];

	public FreeChartReport() {
	}

//	public void setup(ParameterDatabase parameters, Parameter base) {
//
//		Parameter defBase = new Parameter(P_DEF_BASE);
//
//		frame = new UserFrame();
//		frame.setup(parameters, base);
//
//		graphs = new FreeChartGraph[parameters.getInt(base.push(P_GRAPH)
//		    .push(P_NUM), null)];
//
//		for (int i = 0; i < graphs.length; i++) {
//			graphs[i] = (FreeChartGraph) parameters.getInstanceForParameterEq(base
//			    .push(P_GRAPH).push(String.valueOf(i)), defBase.push(P_GRAPH).push(
//			    String.valueOf(i)), FreeChartGraph.class);
//			graphs[i].setReport(this);
//			graphs[i].setup(parameters, base.push(P_GRAPH).push(String.valueOf(i)));
//		}
//
//		exitOnClose = parameters.getBoolean(base.push(P_EXIT),
//		    defBase.push(P_EXIT), exitOnClose);
//		if (exitOnClose) {
//			frame.addWindowListener(this);
//		}
//
//		saveToFile = parameters.getBoolean(base.push(P_SAVETOFILE), defBase
//		    .push(P_SAVETOFILE), saveToFile);
//
//		JPanel canvas = new JPanel();
//
//		if (graphs.length > 2)
//			canvas.setLayout(new GridLayout(0, 1, 5, 5));
//		else
//			canvas.setLayout(new GridLayout(0, 1, 5, 5));
//
//		JPanel p;
//		for (int i = 0; i < graphs.length; i++) {
//			p = new JPanel();
//			p.setLayout(new BorderLayout());
//			p.add(BorderLayout.CENTER, graphs[i]);
//			canvas.add(p);
//		}
//
//		JScrollPane scrollP = new JScrollPane(canvas);
//		frame.setContentPane(scrollP);
//		// frame.pack();
//		RefineryUtilities.centerFrameOnScreen(frame);
//		frame.setVisible(true);
//	}

	public void eventOccurred(SimEvent event) {
		for (int i = 0; i < graphs.length; i++) {
			graphs[i].eventOccurred(event);
		}
	}

	public void produceUserOutput() {
		logger.info("");
		logger.info("Auction statistics");
		logger.info("------------------");
		if (saveToFile) {
			File file = null;
			String name = null;
			for (int i = 0; i < graphs.length; i++) {
				name = graphs[i].getChart().getTitle().getText().replace(
				    File.separatorChar, '_')
				    + ".jpg";
				file = new File(name);
				try {
					ChartUtilities.saveChartAsJPEG(file, graphs[i].getChart(), graphs[i]
					    .getWidth(), graphs[i].getHeight());
				} catch (IOException e) {
					logger.info(e);
				}
			}
		} else {
			logger.info("Output of " + getClass() + " is empty.");
		}
	}

	@SuppressWarnings("rawtypes")
	public Map getVariables() {
		HashMap vars = new HashMap();
		return vars;
	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
	}
}