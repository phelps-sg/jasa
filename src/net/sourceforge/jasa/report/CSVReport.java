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

import java.io.OutputStream;

import net.sourceforge.jabm.report.CSVWriter;
import net.sourceforge.jabm.util.Parameterizable;

/**
 * A historicalDataReport that records data in CSV (comma-separated values) files.
 * 
 * <p>
 * <b>Parameters</b><br>
 * </p>
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.quotelogfile</tt><br>
 * <font size=-1>string</font></td>
 * <td valign=top>(the filename to store the quote data)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.shoutlogfile</tt><br>
 * <font size=-1>string</font></td>
 * <td valign=top>(the filename to store the shout data)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.translogfile</tt><br>
 * <font size=-1>string</font></td>
 * <td valign=top>(the filename to store the transaction price data)</td>
 * <tr>
 * 
 * </table>
 * 
 * @author Steve Phelps
 * @version $Revision$
 * 
 * @deprecated
 */

public class CSVReport extends DataWriterReport implements Parameterizable {

	public static final String P_DEF_BASE = "csvreport";

	static final String P_ASK_QUOTE_LOG_FILE = "askquotelogfile";

	static final String P_BID_QUOTE_LOG_FILE = "bidquotelogfile";

	static final String P_ASK_LOG_FILE = "asklogfile";

	static final String P_BID_LOG_FILE = "bidlogfile";

	static final String P_TRANS_LOG_FILE = "translogfile";

	static final int CSV_COLS = 2;

	/**
	 * Assign an output stream for logging market quote data in comma-separated
	 * variable (CSV) format.
	 */
	public void setCSVAskQuoteLog(OutputStream stream) {
		askQuoteLog = new CSVWriter(stream, CSV_COLS);
	}

	/**
	 * Assign an output stream for logging market quote data in comma-separated
	 * variable (CSV) format.
	 */
	public void setCSVBidQuoteLog(OutputStream stream) {
		bidQuoteLog = new CSVWriter(stream, CSV_COLS);
	}

	/**
	 * Assign an output stream for logging shouts in comma-separated variable
	 * (CSV) format. This can significantly impact the performance of an market.
	 */
	public void setCSVAskLog(OutputStream stream) {
		askLog = new CSVWriter(stream, CSV_COLS);
	}

	public void setCSVBidLog(OutputStream stream) {
		bidLog = new CSVWriter(stream, CSV_COLS);
	}

	/**
	 * Assign an output stream for logging transaction price data in CSV format.
	 */
	public void setCSVTransPriceLog(OutputStream stream) {
		transPriceLog = new CSVWriter(stream, CSV_COLS);
	}

}
