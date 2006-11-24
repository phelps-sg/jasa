/**
 * adapted from uk.ac.liv.auction.zi.ZITraderAgent
 */

package uk.ac.liv.supplyChain;

import java.util.Random;
import java.util.logging.Logger;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Uniform;

import ec.util.Parameter;
import ec.util.ParameterDatabase;
import sun.security.krb5.internal.ccache.au;
import sun.security.krb5.internal.crypto.e;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;
import uk.ac.liv.prng.GlobalPRNG;

public abstract class SupplyChainAgent extends uk.ac.liv.auction.zi.ZITraderAgent {
	
	boolean display = true;
	
	protected static final String P_SOURCE			= "source";
	protected static final String P_SOURCE_TYPES	= "n";
	protected static final String P_SOURCE_CAPACITY	= "capacity";
	protected static final String P_SOURCE_INITIAL	= "initial";
	protected static final String P_SOURCE_BUYS		= "buysInAuction";
	protected static final String P_SOURCE_SELLS	= "sellsInAuction";
	
	protected static final String P_MAKE			= "make";
	protected static final String P_MAKE_COST		= "cost";
	protected static final String P_MAKE_CAPACITY	= "capacity";
	protected static final String P_MAKE_SPEED		= "speed";
	
	protected static final String P_DELIVER			= "deliver";
	protected static final String P_DELIVER_TYPES	= "n";
	protected static final String P_DELIVER_CAPACITY= "capacity";
	protected static final String P_DELIVER_INITIAL	= "initial";
	protected static final String P_DELIVER_BUYS	= "buysInAuction";
	protected static final String P_DELIVER_SELLS	= "sellsInAuction";
	
	//private static final String P_AGENT_BUYS		= "buysInAuction";
	//private static final String P_AGENT_SELLS		= "sellsInAuction";
	
	protected double[] source = {};	//content of the source inventories
	protected double[] source_capacity = {};	//capacity of every source inventory
	//auctionForWhichThisSourceBuys[SOURCE idx] points to an AUCTION idx, ie AUCTION[ auctionForWhichThisSourceBuys[SOURCE idx] ] is used by SOURCE 
	protected int[] auctionForWhichThisSourceBuys = {};	//in which AUCTION to buy
	protected int[] auctionForWhichThisSourceSells = {};
	//sourceWhichBuysInAuction[AUCTION idx] corresponds to the SOURCE used to buy in AUCTION, ie SOURCE[ sourceWhichBuysInAuction[AUCTION] ] is used for AUCTION 
	protected int[] sourceWhichBuysInAuction = {};	//which SOURCE buys in this AUCTION = mapping opposite to auctionForWhichThisSourceBuys
	protected int[] sourceWhichSellsInAuction = {};
	
	protected double   make;
	protected double   make_cost;
	protected double   make_capacity;
	protected double   make_speed;
	
	protected double[] deliver = {};
	protected double[] deliver_capacity = {};
	protected int[] auctionForWhichThisDeliverBuys = {};
	protected int[] auctionForWhichThisDeliverSells = {};
	protected int[] deliverWhichBuysInAuction = {};
	protected int[] deliverWhichSellsInAuction = {};
	
	private final int DEFAULT_INT = -1;
	protected int lastProductionStartDate = 0;
	
	protected SupplyChainRandomRobinAuction[] auction;
	
	protected AbstractContinousDistribution distribution;
	
	public SupplyChainRandomRobinAuction[] getAuction() { return auction;}
	public double[] getDeliver() { return deliver;}
	public double[] getSource() { return source;}
	
	public double getValuation( Auction auction ) {
		return super.getValuation(auction);
	}
	
	public void requestShout( Auction auction ) {
		produce(auction);
		//System.out.println(auction.getAge()+" ag"+getId()+ " SupplyChainAgent.requestShout: "+toString());
		super.requestShout(auction);
	}//requestShout
	
	public void setup( ParameterDatabase parameters, Parameter base, SupplyChainRandomRobinAuction[] auction ) {
	    super.setup(parameters, base);
	    
	    distribution = new Uniform(0, 10, GlobalPRNG.getInstance());
	    
	    this.auction					= auction;
	    
		deliverWhichBuysInAuction		= new int[auction.length];
		deliverWhichSellsInAuction		= new int[auction.length];
		sourceWhichBuysInAuction		= new int[auction.length];
		sourceWhichSellsInAuction		= new int[auction.length];
		for  ( int i=0 ; i < auction.length ; i++)
			deliverWhichBuysInAuction[i] = deliverWhichSellsInAuction[i] = sourceWhichBuysInAuction[i] = sourceWhichSellsInAuction[i] = DEFAULT_INT;
		
	    Parameter sourceParamT = base.push(P_SOURCE); 
	    int source_types				= parameters.getInt(sourceParamT.push(P_SOURCE_TYPES));
	    source							= new double[source_types];
	    source_capacity					= new double[source_types];
	    auctionForWhichThisSourceBuys			= new int[source_types];
	    auctionForWhichThisSourceSells			= new int[source_types];
	    
	    for (int i=0 ; i < source_types ; i++) {
	        Parameter defTypeParamT		= sourceParamT.push("" + i);
	        source[i]					= parameters.getInt(defTypeParamT.push(P_SOURCE_INITIAL), null, DEFAULT_INT);
	        source_capacity[i]			= parameters.getInt(defTypeParamT.push(P_SOURCE_CAPACITY), null, DEFAULT_INT);
	        
	        //source[i]					= source[i] + (int) ( distribution.nextDouble() / 2 ) ;
	        
	        auctionForWhichThisSourceBuys[i]		= parameters.getInt(defTypeParamT.push(P_SOURCE_BUYS),  null, DEFAULT_INT);
	        if ( auctionForWhichThisSourceBuys[i] >= 0) {
	        	try {
	        		sourceWhichBuysInAuction[auctionForWhichThisSourceBuys[i]]	= i;
	        	}
	        	catch ( java.lang.ArrayIndexOutOfBoundsException e ) {
	        		System.out.println("Error: the source "+i+" of agent "+getId()+" buys in the nonexistent Auction "+auctionForWhichThisSourceBuys[i]+" "+toString());
	        		e.printStackTrace();
	        	}
	        }
	        
	        auctionForWhichThisSourceSells[i]	= parameters.getInt(defTypeParamT.push(P_SOURCE_SELLS), null, DEFAULT_INT);
	        if ( auctionForWhichThisSourceSells[i] >= 0) {
	        	try {
	        		sourceWhichSellsInAuction[auctionForWhichThisSourceSells[i]]	= i;
	        	}
	        	catch ( java.lang.ArrayIndexOutOfBoundsException e ) {
	        		System.out.println("Error: the source "+i+" of agent "+getId()+" buys in the nonexistent Auction "+auctionForWhichThisSourceSells[i]+" "+toString());
	        		e.printStackTrace();
	        	}
	        }
	    }//for
	    
	    Parameter makeParamT = base.push(P_MAKE);
	    make_cost = parameters.getInt(makeParamT.push(P_MAKE_COST), null, DEFAULT_INT);
	    make_capacity = parameters.getInt(makeParamT.push(P_MAKE_CAPACITY), null, DEFAULT_INT);
	    make_speed = parameters.getInt(makeParamT.push(P_MAKE_SPEED), null, DEFAULT_INT);
	    
	    Parameter deliverParamT = base.push(P_DELIVER);
	    int deliver_types		= parameters.getInt(deliverParamT.push(P_DELIVER_TYPES));
	    deliver					= new double[deliver_types];
	    deliver_capacity		= new double[deliver_types];
	    auctionForWhichThisDeliverBuys	= new int[deliver_types];
	    auctionForWhichThisDeliverSells	= new int[deliver_types];
	    
	    for (int i=0 ; i < deliver_types ; i++) {
	        Parameter defTypeParamT		= deliverParamT.push("" + i);
	        deliver[i]					= parameters.getInt(defTypeParamT.push(P_DELIVER_INITIAL), null, DEFAULT_INT);
	        deliver_capacity[i]			= parameters.getInt(defTypeParamT.push(P_DELIVER_CAPACITY), null, DEFAULT_INT);
	        
	        auctionForWhichThisDeliverBuys[i]	= parameters.getInt(defTypeParamT.push(P_DELIVER_BUYS),  null, DEFAULT_INT);
	        if ( auctionForWhichThisDeliverBuys[i] >= 0 ) {
	        	try {
	        		deliverWhichBuysInAuction[auctionForWhichThisDeliverBuys[i]]	= i;
	        	}
	        	catch ( java.lang.ArrayIndexOutOfBoundsException e ) {
	        		System.out.println("Error: the deliver "+i+" of agent "+getId()+" buys in the nonexistent Auction "+auctionForWhichThisDeliverBuys[i]+" "+toString());
	        		throw(e);
	        	}
	        }
	        
	        auctionForWhichThisDeliverSells[i]	= parameters.getInt(defTypeParamT.push(P_DELIVER_SELLS), null, DEFAULT_INT);
	        if ( auctionForWhichThisDeliverSells[i] >= 0 ) {
	        	try {
	        		deliverWhichSellsInAuction[auctionForWhichThisDeliverSells[i]]	= i;
	        	}
	        	catch ( java.lang.ArrayIndexOutOfBoundsException e ) {
	        		System.out.println("Error: the deliver "+i+" of agent "+getId()+" sells in the nonexistent Auction "+auctionForWhichThisDeliverSells[i]+" "+toString());
	        		throw(e);
	        	}
	        }
	    }//for
	}//setup
	
	public void printSetup(){
		System.out.println("\n A G E N T  " + getId());
	    for (int i=0 ; i<auction.length ; i++) {
	    	System.out.println("Agent " + getId() + " buys  with deliver " + deliverWhichBuysInAuction[i] + " in Auction "+i);
    		System.out.println("Agent " + getId() + " sells with deliver " + deliverWhichSellsInAuction[i] + " in Auction "+i);
    		System.out.println("Agent " + getId() + " buys  with source  " + sourceWhichBuysInAuction[i] + " in Auction "+i);
    		System.out.println("Agent " + getId() + " sells with source  " + sourceWhichSellsInAuction[i] + " in Auction "+i);
	    }
	    for ( int i=0 ; i<source.length ; i++){
	    	System.out.println("Agent " + getId() + "'s source  " + i + " buys  in auction " + auctionForWhichThisSourceBuys[i]  + " and contains " + source[i]);
    		System.out.println("Agent " + getId() + "'s source  " + i + " sells in auction " + auctionForWhichThisSourceSells[i] + " and contains " + source[i]);
	    }
	    for ( int i=0 ; i<deliver.length ; i++){
	    	System.out.println("Agent " + getId() + "'s deliver " + i + " buys  in auction:"   + auctionForWhichThisDeliverBuys[i] + " and contains " + deliver[i]);
    		System.out.println("Agent " + getId() + "'s deliver " + i + " sells in auction:" + auctionForWhichThisDeliverBuys[i] + " and contains " + deliver[i]);
	    }
	  }//printSetup
	
	public String toString() {
		String toReturn = super.toString();
		
		toReturn += " [Funds=" + String.valueOf(getFunds())+"]";
		
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
	}//toString
	
	public boolean isSeller( Auction auction ) {
		//seems to be never used
		boolean toReturn = false;
		for ( int i=0 ; i < auctionForWhichThisSourceSells.length ; i++ )
			if ( auctionForWhichThisSourceSells[i] >= 0 )
				try {
					if ( (this.auction) [ auctionForWhichThisSourceSells[i] ] == auction)
						toReturn = true;
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					System.out.println("source."+i+".sellsInAuction not defined for auction " +auction.toString() );
					e.printStackTrace();
					throw(e);
				}
				
		for ( int i=0 ; i < auctionForWhichThisDeliverSells.length ; i++)
			if ( auctionForWhichThisDeliverSells[i] >= 0 )
				try {
					if ( (this.auction) [ auctionForWhichThisDeliverSells[i] ] == auction)
						toReturn = true;
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					System.out.println("deliver."+i+".sellsInAuction not defined for auction " +auction.toString() );
					e.printStackTrace();
					throw(e);
				}
		return toReturn;
	}//isSeller

	public boolean isBuyer( Auction auction ) {
		boolean toReturn = false;
		
		for ( int i=0 ; i < auctionForWhichThisSourceBuys.length ; i++)
			if ( auctionForWhichThisSourceBuys[i] >= 0 )
				try{
					if ( (this.auction) [ auctionForWhichThisSourceBuys[i] ] == auction)
						toReturn = true;
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					System.out.println("source."+i+".buysInAuction not defined for auction " +auction.toString() );
					e.printStackTrace();
					throw(e);
				}
				
		//the next for loop shouldn't be necessary, because "deliver" shouldn't buy but only sell
		for ( int i=0 ; i < auctionForWhichThisDeliverBuys.length ; i++)
			if ( auctionForWhichThisDeliverBuys[i] >= 0 )
				try{
					if ( (this.auction) [ auctionForWhichThisDeliverBuys[i] ] == auction)
						toReturn = true;
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					System.out.println("deliver."+i+".buysInAuction not defined for auction " +auction.toString() );
					e.printStackTrace();
					throw(e);
				}
		return toReturn;
	}//isBuyer
	
	public abstract void produce( Auction auction );
	
	public int getStock() {
		System.out.println("SupplyChainAgent.getStock() shouldn't be called! (see source, make and deliver instead)");
		System.exit(-1);
		return (int)source[0];
	}//getStock
	
	public void shoutAccepted( Auction auction, Shout shout, double price, int quantity ) {
		int auctionIdx			= (int) ((SupplyChainRandomRobinAuction) auction).getId();
		
		//boolean display = (getId() == 1) && (((SupplyChainRandomRobinAuction)auction).getId()==0);
		//boolean display = ( ((SupplyChainRandomRobinAuction)auction).getAge()==9 || ((SupplyChainRandomRobinAuction)auction).getAge()==10 );
		
		//System.out.println("\n"+auction.getAge()+"DEB SupplyChainAgent.shoutAccepted: agent "+getId()+ " qty="+quantity+ "  ***  AGENT="+toString());
		
		if ( display )
			System.out.print(auction.getAge()+" SupplyChainAgent.shoutAccepted: agent "+getId()+ " qty="+ quantity+" at $"+price+" in Auct "+auctionIdx);

		//if this agent is allowed to use its SOURCE to SELL in this AUCTION:
		for ( int sourIdx = 0 ; sourIdx < source.length ;  sourIdx++ ) {
			if ( ( auctionForWhichThisSourceSells[ sourIdx ] == auctionIdx )
					&& ( shout.isAsk() ) ) {
				source[ sourIdx ] -= quantity;
				//TODO (quantity-1) (instead of quantity) was necessary because RandomRobinAuction.clear() transfers all products but pays for only one unit
				account.credit( quantity * price );
				if (display)
					System.out.println(" GIVES from its SOURCE "+sourIdx+" [FINAL: funds=" + getFunds() + " ,price="+price + ", quantity="+ quantity+"] "+ toString());
			}
			else if ( ( auctionForWhichThisSourceBuys[ sourIdx ] == auctionIdx )
					&& ( shout.isBid() ) ){
				source[ sourIdx ] += quantity;
				//TODO (quantity-1) (instead of quantity) was necessary because RandomRobinAuction.clear() transfers all products but pays for only one unit
				account.credit( - quantity * price );
				if (display)
					System.out.println(" RECEIVES into its SOURCE "+sourIdx+" [FINAL: funds=" + getFunds() + " ,price="+price + ", quantity="+ quantity+"] "+ toString());
			}
		}//else
		
		for ( int delIdx = 0 ; delIdx < deliver.length ; delIdx++ ) {
			if ( ( auctionForWhichThisDeliverSells[ delIdx ] == auctionIdx )
					&& ( shout.isAsk() ) ) {
				deliver[ delIdx ] -= quantity;
				//TODO (quantity-1) (instead of quantity) was necessary because RandomRobinAuction.clear() transfers all products but pays for only one unit
				account.credit( quantity * price );
				if (display)
					System.out.println(" GIVES from its DELIVER "+delIdx+" [FINAL: funds=" + getFunds() + " ,price="+price + ", quantity="+ quantity+"] "+ toString());
			}
			else if ( ( auctionForWhichThisDeliverBuys[ delIdx ] == auctionIdx )
					&& ( shout.isBid() ) ) {
				deliver[ delIdx ] += quantity;
				//TODO (quantity-1) (instead of quantity) was necessary because RandomRobinAuction.clear() transfers all products but pays for only one unit
				account.credit( - quantity * price );
				if (display)
					System.out.println(" RECEIVES into its DELIVER "+delIdx+" [FINAL: funds=" + getFunds() + " ,price="+price + ", quantity="+ quantity+"] "+ toString());
			}
		}//else
		
		//XXX System.out.println("SupplyChainAgent.shoutAccepted2: funds=" + getFunds() + " ,price="+price + ", quantity="+ quantity+"\n\n");
	  
	    lastShoutAccepted = true;
	    quantityTraded += quantity;
	    tradeEntitlement -= quantity;
	    valuer.consumeUnit(auction);
	    
	    //XXX System.out.println(auction.getAge()+" FIN SupplyChainAgent.shoutAccepted: agent "+getId()+ " qty="+ quantity+" at $"+price+" in Auct "+auctionIdx+toString());
	    //XXX System.out.println(auction.getAge()+"FIN SupplyChainAgent.shoutAccepted: agent "+getId()+ " qty="+quantity+ "  ***  AGENT="+toString()+"\n");
	}//shoutAccepted
	
}//class
