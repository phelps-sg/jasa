/**
 * adapted from uk.ac.liv.auction.zi.ZITraderAgent
 */

package uk.ac.liv.supplyChain.bin;

import java.util.Random;
import java.util.logging.Logger;

import ec.util.Parameter;
import ec.util.ParameterDatabase;
import sun.security.krb5.internal.ccache.au;
import sun.security.krb5.internal.crypto.e;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;
import uk.ac.liv.supplychain.SupplyChainRandomRobinAuction;

public class _CompanyAgent extends uk.ac.liv.auction.zi.ZITraderAgent {
	
	private static final String P_SOURCE			= "source";
	private static final String P_SOURCE_TYPES		= "n";
	private static final String P_SOURCE_CAPACITY	= "capacity";
	private static final String P_SOURCE_INITAL		= "initial";
	private static final String P_SOURCE_BUYS		= "buysInAuction";
	private static final String P_SOURCE_SELLS		= "sellsInAuction";
	
	private static final String P_MAKE				= "make";
	private static final String P_MAKE_COST			= "cost";
	private static final String P_MAKE_CAPACITY		= "capacity";
	private static final String P_MAKE_SPEED		= "speed";
	
	private static final String P_DELIVER			= "deliver";
	private static final String P_DELIVER_TYPES		= "n";
	private static final String P_DELIVER_CAPACITY	= "capacity";
	private static final String P_DELIVER_INITIAL	= "initial";
	private static final String P_DELIVER_BUYS		= "buysInAuction";
	private static final String P_DELIVER_SELLS		= "sellsInAuction";
	
	//private static final String P_AGENT_BUYS		= "buysInAuction";
	//private static final String P_AGENT_SELLS		= "sellsInAuction";
	
	protected int[] source = {};	//content of the source inventories
	protected int[] source_capacity = {};	//capacity of every source inventory
	//auctionForWhichThisSourceBuys[SOURCE] points to an AUCTION, ie AUCTION[ auctionForWhichThisSourceBuys[SOURCE] ] is used by SOURCE 
	protected int[] source_buysInAuction = {};	//in which AUCTION to buy
	protected int[] source_sellsInAuction = {};
	//sourceWhichBuysInAuction[AUCTION] corresponds to the SOURCE used to buy in AUCTION, ie SOURCE[ sourceWhichBuysInAuction[AUCTION] ] is used for AUCTION 
	protected int[] auction_source4buy = {};	//which SOURCE buys in an AUCTION = mapping opposite to auctionForWhichThisSourceBuys
	protected int[] auction_source4sell = {};
	
	protected int   make;
	protected int   make_cost;
	protected int   make_capacity;
	protected int   make_speed;
	
	protected int[] deliver = {};
	protected int[] deliver_capacity = {};
	protected int[] deliver_buysInAuction = {};
	protected int[] deliver_sellsInAuction = {};
	protected int[] auction_deliver4buy = {};
	protected int[] auction_deliver4sell = {};
	
	protected double[] previousTransactionPrice = {};	//previousTransactionPrice[auctionIdx]
	
	private final int DEFAULT_INT = -1;
	private int lastProductionStartDate = 0;
	
	protected SupplyChainRandomRobinAuction[] auction;
	
	public SupplyChainRandomRobinAuction[] getAuction() { return auction;}
	public int[] getDeliver() { return deliver;}
	public int[] getSource() { return source;}
	
	public double getValuation( Auction auction ) {
		return super.getValuation(auction);
	}
	
	public void requestShout( Auction auction ) {
		super.requestShout(auction);
		//printSetup();
	}
	
	public void setup( ParameterDatabase parameters, Parameter base, SupplyChainRandomRobinAuction[] auction ) {
	    super.setup(parameters, base);
	    this.auction					= auction;
	    previousTransactionPrice		= new double[auction.length];
	    	    
		auction_deliver4buy				= new int[auction.length];
		auction_deliver4sell			= new int[auction.length];
		auction_source4buy				= new int[auction.length];
		auction_source4sell				= new int[auction.length];
		for  ( int i=0 ; i < auction.length ; i++)
			auction_deliver4buy[i] = auction_deliver4sell[i] = auction_source4buy[i] = auction_source4sell[i] = DEFAULT_INT;
		
	    Parameter sourceParamT = base.push(P_SOURCE); 
	    int source_types				= parameters.getInt(sourceParamT.push(P_SOURCE_TYPES));
	    source							= new int[source_types];
	    source_capacity					= new int[source_types];
	    source_buysInAuction			= new int[source_types];
	    source_sellsInAuction			= new int[source_types];
	    
	    for (int i=0 ; i < source_types ; i++) {
	        Parameter defTypeParamT		= sourceParamT.push("" + i);
	        source[i]					= parameters.getInt(defTypeParamT.push(P_SOURCE_INITAL), null, DEFAULT_INT);
	        source_capacity[i]			= parameters.getInt(defTypeParamT.push(P_SOURCE_CAPACITY), null, DEFAULT_INT);
	        
	        source_buysInAuction[i]		= parameters.getInt(defTypeParamT.push(P_SOURCE_BUYS),  null, DEFAULT_INT);
	        if ( source_buysInAuction[i] >= 0)
	        	auction_source4buy[source_buysInAuction[i]]	= i;
	        
	        source_sellsInAuction[i]	= parameters.getInt(defTypeParamT.push(P_SOURCE_SELLS), null, DEFAULT_INT);
	        if ( source_sellsInAuction[i] >= 0)
	        	auction_source4sell[source_sellsInAuction[i]]	= i;
	    }//for
	    
	    
	    Parameter makeParamT = base.push(P_MAKE);
	    make_cost = parameters.getInt(makeParamT.push(P_MAKE_COST), null, DEFAULT_INT);
	    make_capacity = parameters.getInt(makeParamT.push(P_MAKE_CAPACITY), null, DEFAULT_INT);
	    make_speed = parameters.getInt(makeParamT.push(P_MAKE_SPEED), null, DEFAULT_INT);
	    
	    Parameter deliverParamT = base.push(P_DELIVER);
	    int deliver_types		= parameters.getInt(deliverParamT.push(P_DELIVER_TYPES));
	    deliver					= new int[deliver_types];
	    deliver_capacity		= new int[deliver_types];
	    deliver_buysInAuction	= new int[deliver_types];
	    deliver_sellsInAuction	= new int[deliver_types];
	    
	    for (int i=0 ; i < deliver_types ; i++) {
	        Parameter defTypeParamT		= deliverParamT.push("" + i);
	        deliver[i]					= parameters.getInt(defTypeParamT.push(P_DELIVER_INITIAL), null, DEFAULT_INT);
	        deliver_capacity[i]			= parameters.getInt(defTypeParamT.push(P_DELIVER_CAPACITY), null, DEFAULT_INT);
	        
	        deliver_buysInAuction[i]	= parameters.getInt(defTypeParamT.push(P_DELIVER_BUYS),  null, DEFAULT_INT);
	        if ( deliver_buysInAuction[i] >= 0 )
	        	auction_deliver4buy[deliver_buysInAuction[i]]	= i;
	        
	        deliver_sellsInAuction[i]	= parameters.getInt(defTypeParamT.push(P_DELIVER_SELLS), null, DEFAULT_INT);
	        if ( deliver_sellsInAuction[i] >= 0 )
	        	auction_deliver4sell[deliver_sellsInAuction[i]]	= i;
	    }//for
	}
	
	public void printSetup(){
		System.out.println("\n A G E N T  " + getId());
	    for (int i=0 ; i<auction.length ; i++) {
	    	System.out.println("Auction #" + i + ":");
	    	System.out.println(" buys  for  Deliver: " + auction_deliver4buy[i]);
    		System.out.println(" sells from Deliver: " + auction_deliver4sell[i]);
    		System.out.println(" buys  for  Source : " + auction_source4buy[i]);
    		System.out.println(" sells from Source : " + auction_source4sell[i]);
	    }
	    for ( int i=0 ; i<source.length ; i++){
	    	System.out.print("SOURCE #"+i +": ");
	    	System.out.print(" Buys  in auction "  + source_buysInAuction[i] + " and contains " + source[i]);
    		System.out.println(" Sells in auction:" + source_sellsInAuction[i] + " and contains " + source[i]);
	    }
	    for ( int i=0 ; i<deliver.length ; i++){
	    	System.out.print("DELIVER #"+i +":");
	    	System.out.print(" Buys  in auction:"   + deliver_buysInAuction[i] + " and contains " + deliver[i]);
    		System.out.println(" Sells in auction:" + deliver_buysInAuction[i] + " and contains " + deliver[i]);
	    }
	  }
	
	public String toString() {
		String toReturn = super.toString();
		
		toReturn += " [Source:";
		if(source_capacity != null)
			for(int i=0 ; i < source_capacity.length ; i++)
				toReturn += String.valueOf(source[i]) + "/" + String.valueOf(source_capacity[i]) + ",";
		
		toReturn += "] [Deliver:";
		if(deliver_capacity != null)
			for(int i=0 ; i < deliver_capacity.length ; i++)
				toReturn += String.valueOf(deliver[i]) + "/" + String.valueOf(deliver_capacity[i]) + ",";
		
		toReturn += "]";
		return toReturn;
	}
	
	public boolean isSeller( Auction auction ) {
		//seems to be never used
		boolean toReturn = false;
		for ( int i=0 ; i < source_sellsInAuction.length ; i++ )
			if ( source_sellsInAuction[i] >= 0 )
				try {
					if ( (this.auction) [ source_sellsInAuction[i] ] == auction)
						toReturn = true;
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					System.out.println("source."+i+".sellsInAuction not defined for auction " +auction.toString() );
					e.printStackTrace();
					throw(e);
				}
				
		for ( int i=0 ; i < deliver_sellsInAuction.length ; i++)
			if ( deliver_sellsInAuction[i] >= 0 )
				try {
					if ( (this.auction) [ deliver_sellsInAuction[i] ] == auction)
						toReturn = true;
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					System.out.println("deliver."+i+".sellsInAuction not defined for auction " +auction.toString() );
					e.printStackTrace();
					throw(e);
				}
						
		return toReturn;
	}

	public boolean isBuyer( Auction auction ) {
		boolean toReturn = false;
		
		for ( int i=0 ; i < source_buysInAuction.length ; i++)
			if ( source_buysInAuction[i] >= 0 )
				try{
					if ( (this.auction) [ source_buysInAuction[i] ] == auction)
						toReturn = true;
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					System.out.println("source."+i+".buysInAuction not defined for auction " +auction.toString() );
					e.printStackTrace();
					throw(e);
				}
				
		//the next for loop shouldn't be necessary, because "deliver" shouldn't buy but only sell
		for ( int i=0 ; i < deliver_buysInAuction.length ; i++)
			if ( deliver_buysInAuction[i] >= 0 )
				try{
					if ( (this.auction) [ deliver_buysInAuction[i] ] == auction)
						toReturn = true;
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					System.out.println("deliver."+i+".buysInAuction not defined for auction " +auction.toString() );
					e.printStackTrace();
					throw(e);
				}
		return toReturn;
	}
	
	public void eventOccurred( AuctionEvent event ) {
		super.eventOccurred(event);
	}//eventOccured
	
	public void roundClosed( AuctionEvent event ) {
		super.roundClosed(event);
		simulateProduction(event);
		updatePreviousPrice(event);
	  }
	
	private double updatePreviousPrice(AuctionEvent event) {
		Auction auction = event.getAuction();
		int auctionIdx	= (int) ((SupplyChainRandomRobinAuction) auction).getId();
		int age			= ((SupplyChainRandomRobinAuction) auction).getAge() - 1;
		previousTransactionPrice[ auctionIdx ] = ((SupplyChainRandomRobinAuction) auction).getTransactionPrice( auctionIdx );
		System.out.println("SupplyChainAgent.updatePreviousPrice: Age=" + age + ", Price seen by Agent " + getId() + " in Auction " + auctionIdx + " is " + previousTransactionPrice[auctionIdx]);

		return previousTransactionPrice[ auctionIdx ];
	}//updatePreviousPrice
	
	public void simulateProduction( AuctionEvent event ) {
		//TODO: take `deliver' capacities into account
		if (auction[0].getAge() >= lastProductionStartDate + make_speed) {
			for( int i=0 ; i < deliver.length ; i++ )
				deliver[i] += make;
			make = 0;
			for( int i=0 ; i < source.length ; i++ )
				source[i] -= make_capacity;
			lastProductionStartDate = auction[0].getAge();
		}
	}//simulateProduction
	
	public int getStock() {
		System.out.println("SupplyChainAgent.getStock() shouldn't be called! (see source, make and deliver instead)");
		System.exit(-1);
		return 0;
	}//getStock
	
	public void shoutAccepted( Auction auction, Shout shout, double price, int quantity ) {
	    super.shoutAccepted( auction, shout, price, quantity );
	}//shoutAccepted
	
	
	
	
	
	/*
	 //	TODO: 2 delete
	public int _doesSourceSell( Auction auction ) {
		int toReturn = -1;
		for ( int i=0 ; i < auctionForWhichThisSourceSells.length ; i++ )
			if ( auctionForWhichThisSourceSells[i] >= 0 )
				try {
					if ( (this.auction) [ auctionForWhichThisSourceSells[i] ] == auction)
						toReturn = i;
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					System.out.println("source."+i+".sellsInAuction not defined for auction " +auction.toString() );
					e.printStackTrace();
					throw(e);
				}
		return toReturn;
	}
	
	/**
	 * Seek which of the deliver parts of the company sells in this auction.
	 * Returns -1 when no deliver part sells in this auction.
	 * 
	 * @param auction
	 *          The quantity of stock for this trader.
	 * 
	 *
	//TODO: 2 delete
	private int _doesDeliverSell( Auction auction ) {
		int toReturn = -1;				
		for ( int i=0 ; i < auctionForWhichThisDeliverSells.length ; i++)
			if ( auctionForWhichThisDeliverSells[i] >= 0 )
				try {
					if ( (this.auction) [ auctionForWhichThisDeliverSells[i] ] == auction)
						toReturn = i;
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					System.out.println("deliver."+i+".sellsInAuction not defined for auction " +auction.toString() );
					e.printStackTrace();
					throw(e);
				}
		return toReturn;
	}
	
	//TODO: 2 delete
	private int _doesSourceBuy( Auction auction ) {
		int toReturn = -1;		
		for ( int i=0 ; i < auctionForWhichThisSourceBuys.length ; i++)
			if ( auctionForWhichThisSourceBuys[i] >= 0 )
				try{
					if ( (this.auction) [ auctionForWhichThisSourceBuys[i] ] == auction)
						toReturn = i;
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					System.out.println("source."+i+".buysInAuction not defined for auction " +auction.toString() );
					e.printStackTrace();
					throw(e);
				}
		return toReturn;
	}
	
	//TODO: 2 delete
	public int _doesDeliverBuy(Auction auction) {
		int toReturn = -1;
		for ( int i=0 ; i < auctionForWhichThisDeliverBuys.length ; i++)
			if ( auctionForWhichThisDeliverBuys[i] >= 0 )
				try{
					if ( (this.auction) [ auctionForWhichThisDeliverBuys[i] ] == auction)
						toReturn = i;
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					System.out.println("deliver."+i+".buysInAuction not defined for auction " +auction.toString() );
					e.printStackTrace();
					throw(e);
				}
		return toReturn;
	}
	*/

}//class
