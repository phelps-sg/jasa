package uk.ac.liv.supplychain;

import org.apache.log4j.Logger;

import ec.util.Parameter;
import ec.util.ParameterDatabase;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.event.TransactionExecutedEvent;
import uk.ac.liv.auction.stats.PriceStatisticsReport;

public class PriceStatisticsInPeriodReport extends PriceStatisticsReport {
	protected static final String P_RECORD_FROM = "recordfrom";

	protected static final String P_RECORD_TO = "recordto";

	private int recordfrom = -1;

	private int recordto = -1;

	static Logger logger = Logger.getLogger(PriceStatisticsReport.class);

	int[] lastDisplayedTransactionPrice = { -1, -1, -1 };

	public void updateTransPriceLog(TransactionExecutedEvent event) {
		Auction auction = event.getAuction();
		if ((auction.getAge() >= recordfrom) && (auction.getAge() <= recordto))
			stats[TRANS_PRICE].newData(event.getPrice());

		/*
		 * if( lastDisplayedTransactionPrice[ (int)
		 * ((SupplyChainRandomRobinAuction)auction).getId() ] < auction.getAge() ) {
		 * System.out.print("["+((SupplyChainRandomRobinAuction)auction).getId() + "
		 * "+event.getPrice()+"]"); lastDisplayedTransactionPrice[ (int)
		 * ((SupplyChainRandomRobinAuction)auction).getId() ] = auction.getAge(); if (
		 * ((SupplyChainRandomRobinAuction)auction).getId() == 0 )
		 * System.out.println("new week"); }
		 */
	}// updateTransPriceLog

	public void setup(ParameterDatabase parameters, Parameter base) {
		super.setup(parameters, base);

		recordfrom = parameters.getInt(base.push(P_RECORD_FROM), null);
		recordto = parameters.getInt(base.push(P_RECORD_TO), null);
		if (recordfrom > recordto)
			System.out.println("WARNING: " + base.push(P_RECORD_FROM) + " > "
			    + base.push(P_RECORD_TO));
	}// setup

	public void produceUserOutput() {
		reportHeader();
		logger.info("WARNING: Transaction Price is given between Weeks "
		    + recordfrom + " and " + recordto);
		for (int i = 0; i < stats.length; i++) {
			printStats(stats[i]);
		}
	}// produceUserOutput
}
