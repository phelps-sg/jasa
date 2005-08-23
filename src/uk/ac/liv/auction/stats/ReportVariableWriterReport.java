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

import java.text.DecimalFormat;

import java.util.HashMap;
import java.util.Map;

import org.jfree.data.time.TimePeriodValue;

import uk.ac.liv.auction.config.CaseEnumConfig;

import uk.ac.liv.auction.core.*;

import uk.ac.liv.auction.event.AuctionClosedEvent;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionOpenEvent;
import uk.ac.liv.auction.event.EndOfDayEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.io.*;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * This class writes auction data to the specified DataWriter objects, and thus
 * can be used to log data to eg, CSV files, a database backend, etc.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class ReportVariableWriterReport implements AuctionReport,
    Parameterizable {

  private static String P_AUCTION_LOG = "auctionlog";

  private static String P_DAY_LOG = "daylog";

  private static String P_ROUND_LOG = "roundlog";

  protected static boolean initialized = false;

  protected static InternalRVWriterReport auctionLog = null;

  protected static InternalRVWriterReport dayLog = null;

  protected static InternalRVWriterReport roundLog = null;

  /**
   * The auction we are keeping statistics on.
   * 
   * @uml.property name="auction"
   * @uml.associationEnd
   */
  protected RoundRobinAuction auction;

  static DecimalFormat formatter = new DecimalFormat(
      "+#########0.000;-#########.000");

  public ReportVariableWriterReport() {
  }

  public ReportVariableWriterReport( InternalRVWriterReport auctionLog,
      InternalRVWriterReport dayLog, InternalRVWriterReport roundLog ) {
    ReportVariableWriterReport.auctionLog = auctionLog;
    ReportVariableWriterReport.dayLog = dayLog;
    ReportVariableWriterReport.roundLog = roundLog;
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

    if ( !initialized ) {

      if ( parameters.getBoolean(base.push(P_AUCTION_LOG), null, true) ) {
        auctionLog = new InternalRVWriterReport();
        auctionLog.setup(parameters, base.push(P_AUCTION_LOG));
      } else {
        auctionLog = null;
      }

      if ( parameters.getBoolean(base.push(P_DAY_LOG), null, true) ) {
        dayLog = new InternalRVWriterReport();
        dayLog.setup(parameters, base.push(P_DAY_LOG));
      } else {
        dayLog = null;
      }

      if ( parameters.getBoolean(base.push(P_ROUND_LOG), null, true) ) {
        roundLog = new InternalRVWriterReport();
        roundLog.setup(parameters, base.push(P_ROUND_LOG));
      } else {
        roundLog = null;
      }

    }

  }

  public void eventOccurred( AuctionEvent event ) {
    if ( event instanceof AuctionOpenEvent ) {
      generateHeader();
    } else if ( event instanceof RoundClosedEvent ) {
      updateRoundLog((RoundClosedEvent) event);
    } else if ( event instanceof EndOfDayEvent ) {
      updateDayLog((EndOfDayEvent) event);
    } else if ( event instanceof AuctionClosedEvent ) {
      updateAuctionLog((AuctionClosedEvent) event);
    }
  }

  /**
   * Generats the CSV file header, i.e. field names in the first lines.
   * 
   */
  public void generateHeader() {

    if ( !initialized ) {
      String headers[] = { "auction", "day", "round" };

      if ( auctionLog != null ) {
        generateCaseEnumHeader(auctionLog);
        for ( int i = 0; i < 1; i++ ) {
          auctionLog.newData(headers[i]);
        }
        auctionLog.generateHeader();
        auctionLog.endRecord();
        auctionLog.flush();
      }

      if ( dayLog != null ) {
        generateCaseEnumHeader(dayLog);
        for ( int i = 0; i < 2; i++ ) {
          dayLog.newData(headers[i]);
        }
        dayLog.generateHeader();
        dayLog.endRecord();
        dayLog.flush();
      }

      if ( roundLog != null ) {
        generateCaseEnumHeader(roundLog);
        for ( int i = 0; i < 3; i++ ) {
          roundLog.newData(headers[i]);
        }
        roundLog.generateHeader();
        roundLog.endRecord();
        roundLog.flush();
      }

      initialized = true;
    }
  }

  /**
   * Generates the names of fields in the CSV file header for auction properties
   * configured by CaseEnum to define different auctions.
   * 
   * @param writer
   *          the CSV file to which data will be output
   */
  private static void generateCaseEnumHeader( CSVWriter writer ) {
    if ( CaseEnumConfig.getInstance() != null ) {
      CaseEnumConfig ceConfig = CaseEnumConfig.getInstance();
      for ( int i = 0; i < ceConfig.getCaseEnumNum(); i++ ) {
        writer.newData(ceConfig.getCaseEnumAt(i).getName());
      }
    }
  }

  /**
   * Outputs the values of fields to the starting columns in a line for auction
   * properties configured by CaseEnum to define different auctions.
   * 
   * @param writer
   *          the CSV file to which data will be output
   */
  private static void generateCaseCombination( CSVWriter writer ) {
    if ( CaseEnumConfig.getInstance() != null ) {
      CaseEnumConfig ceConfig = CaseEnumConfig.getInstance();
      for ( int i = 0; i < ceConfig.getCaseEnumNum(); i++ ) {
        writer.newData(ceConfig.getCaseAt(i).toString());
      }
    }
  }

  public void updateRoundLog( RoundClosedEvent event ) {
    if ( roundLog != null ) {
      generateCaseCombination(roundLog);
      roundLog.newData(auction.getId());
      roundLog.newData(auction.getDay());
      roundLog.newData(auction.getRound());
      roundLog.update();
      roundLog.endRecord();
      roundLog.flush();
    }
  }

  public void updateDayLog( EndOfDayEvent event ) {
    if ( dayLog != null ) {
      generateCaseCombination(dayLog);
      dayLog.newData(auction.getId());
      dayLog.newData(auction.getDay());
      dayLog.update();
      dayLog.endRecord();
      dayLog.flush();
    }
  }

  public void updateAuctionLog( AuctionClosedEvent event ) {
    if ( auctionLog != null ) {
      generateCaseCombination(auctionLog);
      auctionLog.newData(auction.getId());
      auctionLog.update();
      auctionLog.endRecord();
      auctionLog.flush();
    }
  }

  public void produceUserOutput() {
  }

  public Map getVariables() {
    return new HashMap();
  }

  /**
   * @uml.property name="auction"
   */
  public void setAuction( RoundRobinAuction auction ) {
    this.auction = auction;
  }

  static class InternalRVWriterReport extends CSVWriter {

    private static String P_VAR = "var";

    private static String P_NUM = "n";

    private String varNames[];

    public InternalRVWriterReport() {
      setAutowrap(false);
      setAppend(false);
    }

    public void setup( ParameterDatabase parameters, Parameter base ) {
      super.setup(parameters, base);

      int n = parameters.getIntWithDefault(base.push(P_VAR).push(P_NUM), null,
          0);
      varNames = new String[n];
      for ( int i = 0; i < n; i++ ) {
        varNames[i] = parameters.getString(base.push(P_VAR).push(
            String.valueOf(i)));
      }
    }

    public void generateHeader() {
      for ( int i = 0; i < varNames.length; i++ ) {
        newData(varNames[i]);
      }
    }

    public void update() {
      TimePeriodValue tpValue;
      for ( int i = 0; i < varNames.length; i++ ) {
        tpValue = ReportVariableBoard.getInstance().getValue(varNames[i]);
        if ( tpValue != null ) {
          if ( tpValue.getValue() instanceof Double ) {
            newData(formatter.format(((Double) tpValue.getValue())
                .doubleValue()));
          } else {
            newData(tpValue.getValue());
          }
        } else {
          newData(-1);
        }
      }
    }

  }

}