/** 
 * similar to a RandomRobinAuction, except that initialization is different
 */

package uk.ac.liv.supplyChain;

import java.util.LinkedList;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.agent.TradingAgent;
import uk.ac.liv.auction.core.AuctionClosedException;
import uk.ac.liv.auction.core.AuctionError;
import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.event.TransactionExecutedEvent;

public class SupplyChainRandomRobinAuction extends RandomRobinAuction {
	
	protected static final String P_INITIAL_PRICE	= "initialPrice";
	protected static double initialPrice = -1;
	
	private double[] transactionPrice = { };	// transactionPrice [ round ]
	
	public double	getTransactionPrice(int round) { return transactionPrice[round]; }
	public double	getTransactionPrice() { return transactionPrice[getAge()]; }
	public void		setNewTransactionPrice(double newPrice) { this.transactionPrice[ getAge() + 1 ] = newPrice; }
	
	public void initialiseTransactionPrice(int maximumRounds) {
		transactionPrice = new double[maximumRounds];
		for ( int i=0 ; i< maximumRounds ; i++)
			transactionPrice[i] = initialPrice;
	}
	
	public void setup( ParameterDatabase parameters, Parameter base ) {
		super.setup(parameters, base);
		initialPrice	= parameters.getDouble(base.push(P_INITIAL_PRICE), null, 1);
		System.out.println("Initial price in market "+getId()+ " ("+getName()+") is "+initialPrice);
	}
	
	public void endRound() {
		/*
		 transactionPrice[ getAge()+1 ] = ((uk.ac.liv.supplyChain.ClearingHouseAuctioneer)	auctioneer).getPrice();
		System.out.println("new price="+transactionPrice[ getAge()+1 ]);
		*/
		super.endRound();
	}

	public LinkedList getRegisteredTraders() { return registeredTraders;}
	
	public LinkedList getActiveTraders() { return activeTraders; }
	
	public void run() {
		
		if ( auctioneer == null ) {
			throw new AuctionError("No auctioneer has been assigned for auction " + name);
		}
		//begin();
		try {
			//while ( !closed ) {
				step();
			//}
		} catch ( AuctionClosedException e ) {
			throw new AuctionError(e);
		}
		//end();
	}//run
	
	
	
	
	public void clear( Shout ask, Shout bid, double transactionPrice ) {
	    assert ask.getQuantity() == bid.getQuantity();
	    assert transactionPrice >= ask.getPrice();
	    assert transactionPrice <= bid.getPrice();
	    
		//System.out.println(getAge()+"SupplyChainRandomRobin.clear for Auction"+getId()  + ": price="+" transactionPrice="+transactionPrice+"\n\n\n");
		/*
		for ( int i=getAge()+1 ; i<this.transactionPrice.length ; i++)
			this.transactionPrice[ i ] = transactionPrice;
			*/
		//this.transactionPrice[ getAge()+1 ] = transactionPrice;

	    clear(ask, bid, transactionPrice, transactionPrice, ask.getQuantity());
	  }

	  public void clear( Shout ask, Shout bid, double buyerCharge, double sellerPayment, int quantity ) {

	    SupplyChainAgent buyer = (SupplyChainAgent) bid.getAgent();
	    SupplyChainAgent seller = (SupplyChainAgent) ask.getAgent();

	    assert buyer.isBuyer(this);
	    assert seller.isSeller(this);
	   
	    TransactionExecutedEvent transactionEvent = new TransactionExecutedEvent(
	        this, round, ask, bid, buyerCharge, ask.getQuantity());
	    fireEvent(transactionEvent);
	    

	    //auctioneer.getAccount().doubleEntry(buyer.getAccount(), buyerCharge, 
	    //                                    seller.getAccount(), sellerPayment);
	    buyer.getCommodityHolding().transfer(seller.getCommodityHolding(), quantity);
	    
	    

	    int deliverIdx = seller.deliverWhichSellsInAuction[ (int) getId() ];
	    if ( deliverIdx >= 0 ) {
	    	int availableQuantity = (int)seller.deliver[ deliverIdx ];
	    	
	    	if( availableQuantity >= quantity ) {
	    		buyer.shoutAccepted(this, bid, buyerCharge, quantity);
	    		seller.shoutAccepted(this, ask, sellerPayment, quantity);
	    	}
	    	else {
	    		buyer.shoutAccepted(this, bid, buyerCharge, availableQuantity);
	    		seller.shoutAccepted(this, ask, sellerPayment, availableQuantity);
	    	}
	    }
	    
	  }//clear


	public void generateReport() {
		System.out.print("\n\n************************");
		for ( int i=0 ; i<getName().length() ; i++)
			System.out.print("*");
	    System.out.print("\n* REPORTS FOR AUCTION " + getName() + " *");
	    System.out.print("\n************************");
		for ( int i=0 ; i<getName().length() ; i++)
			System.out.print("*");
		System.out.print("\n");
		
	    super.generateReport();
	  }//generateReport
}//class