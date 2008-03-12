package uk.ac.liv.supplychain;

import ec.util.Parameter;
import ec.util.ParameterDatabase;
import uk.ac.liv.auction.stats.GraphReport;
import uk.ac.liv.auction.stats.RepastGraphSequence;
import uk.ac.liv.util.io.DataWriter;

public class MyGraphReport extends uk.ac.liv.auction.stats.GraphReport {

	protected static GraphReport[] mySingletonInstance;

	protected RepastGraphSequence[][] myAllSeries;

	/**
	 * output for the ask component of market quotes as time series.
	 * 
	 * @uml.property name="askQuoteLog"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected DataWriter[] myAskQuoteLog = null;

	/**
	 * output for the bid component of market quotes as time series.
	 * 
	 * @uml.property name="bidQuoteLog"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected DataWriter[] myBidQuoteLog = null;

	/**
	 * @uml.property name="transPriceLog"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected DataWriter[] myTransPriceLog = null;

	public MyGraphReport() {
		super();
		if (myAllSeries == null) {
			myAllSeries = new RepastGraphSequence[1][1];
			myAllSeries[0] = allSeries;
			myAskQuoteLog = new DataWriter[1];
			myAskQuoteLog[0] = askLog;
			myBidQuoteLog = new DataWriter[1];
			myBidQuoteLog[0] = bidLog;
		} else {
			int auctionIdx = mySingletonInstance.length;
			GraphReport[] temp = new GraphReport[auctionIdx + 1];
			for (int i = 0; i < auctionIdx; i++)
				temp[i] = mySingletonInstance[i];
			temp[auctionIdx] = this;
			mySingletonInstance = temp;
		}
	}

	/*
	 * public void updateTransPriceLog( TransactionExecutedEvent event ) {
	 * super.updateTransPriceLog(event);
	 * 
	 * Auction auction = event.getAuction(); System.out.println("MYGr
	 * age="+auction.getAge() + " auction=" +
	 * ((SupplyChainRandomRobinAuction)auction).getId() + " $=" + event.getPrice() ); }
	 */

	public static GraphReport getSingletonInstance(int auctionIdx) {
		return mySingletonInstance[auctionIdx];
	}// getSingletonInstance

	public void setup(ParameterDatabase parameters, Parameter base) {
		// if it's the first added GraphReport
		if (mySingletonInstance == null) {
			mySingletonInstance = new GraphReport[1];
			mySingletonInstance[0] = this;
		} else {
			int auctionIdx = mySingletonInstance.length;
			GraphReport[] temp = new GraphReport[auctionIdx + 1];
			for (int i = 0; i < auctionIdx; i++)
				temp[i] = mySingletonInstance[i];
			temp[auctionIdx] = this;
			mySingletonInstance = temp;
		}
	}// setup
}// class
