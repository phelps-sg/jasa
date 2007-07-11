/**
 * 
 */
package uk.ac.liv.supplychain;

import java.io.Serializable;

import javax.xml.transform.Source;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Uniform;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.agent.FixedQuantityStrategyImpl;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.AuctionRuntimeException;
import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionOpenEvent;
import uk.ac.liv.auction.event.EndOfDayEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;
import uk.ac.liv.auction.event.RoundClosingEvent;
import uk.ac.liv.auction.stats.DailyStatsReport;
import uk.ac.liv.auction.stats.EquilibriumReport;
import uk.ac.liv.prng.GlobalPRNG;
import uk.ac.liv.util.CummulativeDistribution;
import uk.ac.liv.util.Distribution;

/**
 * @author moyaux
 *
 */
public class SteiglitzStrategy extends FixedQuantityStrategyImpl implements
		Serializable {
	
	private boolean display = false;
	
	protected static final String P_MARGIN	= "margin";
	
	protected double margin = -1;
	
	public void setMargin (double margin) { this.margin = margin ; }
	
	
	protected AbstractContinousDistribution distribution;

	/* (non-Javadoc)
	 * @see uk.ac.liv.auction.agent.AbstractStrategy#endOfRound(uk.ac.liv.auction.core.Auction)
	 */
	public void endOfRound(Auction auction) {
		// TODO Auto-generated method stub
	}
	
	public void initialise() {
	    super.initialise();
	    distribution = new Uniform(0, 10, GlobalPRNG.getInstance());
	  }
	
	public void setup( ParameterDatabase parameters, Parameter base ) {
	    super.setup(parameters, base);
	    initialise();
	    Parameter paramT = base.push(P_MARGIN); 
	    margin 	= parameters.getDoubleWithDefault(paramT, paramT, -1);
	  }
	
	public boolean modifyShout( Shout.MutableShout shout ) {
		boolean toReturn		= false;
	    SupplyChainAgent agent	= (uk.ac.liv.supplychain.SupplyChainAgent)this.getAgent();
	    boolean isBuyer			= agent.isBuyer(auction);
	    boolean isSeller		= agent.isSeller(auction);
	    double previousPrice	= ((SupplyChainRandomRobinAuction) auction).getTransactionPrice( auction.getAge() );
	    int auctionIdx			= (int) ((SupplyChainRandomRobinAuction) auction).getId();
		double valuation		= agent.getValuation( auction );
	    SteiglitzTraderValuer valuer = (SteiglitzTraderValuer) agent.getValuationPolicy();
	    
	    shout.setAgent(agent);
	    
	    //display = (auctionIdx == 0);
	    //display = ( (agent.getId() > 25) && (agent.getId() < 31) );
	    //display = (agent.getId() == 20);

	    if ( display )
	    	System.out.println(auction.getAge()+" BEGINning of SteiglitzStrategy.modifyShout auction="+auctionIdx+" agent="+agent.getId()+"    ***  AGENT = "+agent.toString());

	    for ( int sourIdx = 0 ; sourIdx < agent.source.length ;  sourIdx++ ) {
	    	if ( agent.auctionForWhichThisSourceSells[ sourIdx ] == auctionIdx )
	    		if ( agent.source[ sourIdx ] > agent.source_capacity[ sourIdx ]) {
	    			shout.setIsBid(false);		//sell
	    			shout.setPrice(valuation);
	    			shout.setQuantity( (int) ( - agent.source_capacity[ sourIdx ] + agent.source[ sourIdx ]) );
	    			toReturn = true;
	    			if (display)
	    				System.out.println("case 1 source"+sourIdx + " auction="+agent.auctionForWhichThisSourceSells[ sourIdx ]+" agent="+agent.getId()+" ");
	    		}
	    	
	    	if ( agent.auctionForWhichThisSourceBuys[ sourIdx ] == auctionIdx )
	    		if ( agent.source[ sourIdx ] <= agent.source_capacity[ sourIdx ]) {
	    			shout.setIsBid(true);	//buys
	    			shout.setPrice(valuation);
	    			
	    			int qty2buy = (int) (   agent.source_capacity[ sourIdx ] - agent.source[ sourIdx ]);
	    			//if the agent can afford to buy all what is needed
	    			if ( agent.getFunds() > qty2buy * valuation ) {
	    				shout.setQuantity( qty2buy );
	    				if (display)
	    					System.out.println("case 21 agent.getFunds()="+agent.getFunds()+" > "+qty2buy+" * valuation="+valuation+" = "+(qty2buy * valuation)+" => shout.getQuantity()="+shout.getQuantity());
	    			}
	    			else {
	    				shout.setQuantity( (int) ( agent.getFunds() / valuation ) );
	    				if (display)
	    					System.out.println("case 22\n\nAgent "+agent.getId()+" is too poor and only buys "+shout.getQuantity()+"!");
	    			}
	    			
	    			// if I can find good parameters, I can replace the previous big "else" by this line:
	    			//shout.setQuantity( qty2buy );
	    			
	    			//if ( agent.getFunds() > 0 )
	    				toReturn = true;
	    			
	    			if (display)
	    				System.out.println("case 2 source"+sourIdx + " auction="+agent.auctionForWhichThisSourceBuys[ sourIdx ]+" agent="+agent.getId()+" toReturn="+toReturn+"TOTO");
	    		}
	    }//for
	    if (display)
	    	System.out.println(" toReturn1  ="+toReturn);
	    
	    for ( int delIdx = 0 ; delIdx < agent.deliver.length ; delIdx++ ) {
	    	if ( agent.auctionForWhichThisDeliverSells[ delIdx ] == auctionIdx )
	    		if ( agent.deliver[ delIdx ] > agent.deliver_capacity[ delIdx ]) {
	    			shout.setIsBid(false);		//sell
	    			shout.setPrice(valuation);
	    			shout.setQuantity( (int) ( - agent.deliver_capacity[ delIdx ] + agent.deliver[ delIdx ]) );
	    			toReturn = true;
	    			
	    			if (display)
	    				System.out.println("case 3 deliver"+delIdx + " auction="+agent.auctionForWhichThisDeliverSells[ delIdx ]+" agent="+agent.getId()+" ");
	    		}
	    	
	    	if ( agent.auctionForWhichThisDeliverBuys[ delIdx ] == auctionIdx )
	    		if ( agent.deliver[ delIdx ] <= agent.deliver_capacity[ delIdx ]) {
	    			shout.setIsBid(true);	//buys
	    			shout.setPrice(valuation);
	    			
	    			int qty2buy = (int) ( agent.deliver_capacity[ delIdx ] - agent.deliver[ delIdx ]);
	    			//if the agent can afford to buy all what is needed
	    			if ( agent.getFunds() > qty2buy * valuation ) {
	    				shout.setQuantity( qty2buy );
	    				if (display)
	    					System.out.println("case 41 agent.getFunds()="+agent.getFunds()+" > "+qty2buy+" * valuation="+valuation+" = "+(qty2buy * valuation)+" => shout.getQuantity()="+shout.getQuantity());
	    			}
	    			else {
	    				shout.setQuantity( (int) ( agent.getFunds() / valuation ) );
	    				if (display)
	    					System.out.println("case 42\n\nAgent "+agent.getId()+" is too poor and only buys "+shout.getQuantity()+"!");
	    			}
	    			//shout.setQuantity( qty2buy );
	    			
	    			if ( agent.getFunds() > 0 )
	    				toReturn = true;
	    			
	    			if (display)
	    				System.out.println("case 4 deliver"+delIdx + " auction="+agent.auctionForWhichThisDeliverBuys[ delIdx ]+" agent="+agent.getId()+" ");
	    		}
	    }//for
	    
	    if (display)
	    	System.out.println(" toReturn2  ="+toReturn);
	    
	    //System.out.println("shout.getPrice()="+shout.getPrice()+" shout.getQuantity()="+shout.getQuantity());
	    
	    if ( (shout.getPrice() < 0) || (shout.getQuantity() <= 0) || (java.lang.Double.isNaN(shout.getPrice())) || (java.lang.Double.isInfinite(shout.getPrice())) )
	    	toReturn = false;
	    
	    if (display)
	    	System.out.println(" toReturn3  ="+toReturn);
	    
	    //always sell higher than buy
	    if ( shout.isAsk() )
	    	shout.setPrice( shout.getPrice() + margin );
	    
	    //TODO: make the sell price depend on the buy price
/*	    if ( ( shout.isAsk() ) && ( agent instanceof ManufacturerAgent) ) {
	    	//shout.setPrice( shout.getPrice() + ((ManufacturerAgent) agent).getPreviousPrice( auctionIdx + 1 ) );
	    	System.out.println("SteiglitzStrategy.modifyShout: PRICE INCREASED BY "+((ManufacturerAgent) agent).getPreviousPrice( auctionIdx + 1 )+" in auction " +auctionIdx+" by "+agent.toString()+" \n");
	    }
  */  
	    if ( display ) {
	    	System.out.print(auction.getAge() + " END of SteiglitzStrategy.modifyShout: "+ agent.getClass().toString() + " "+ agent.getId()+" ("+toReturn+") ");
	    	if (shout.isAsk())
	    		System.out.print(" SELLS ");
	    	if (shout.isBid())
	    		System.out.print(" BUYS ");
	    	System.out.println(shout.getQuantity() + " items in Auc"+auctionIdx+" at $" + shout.getPrice() + ", previousPrice=" +previousPrice + ", valuation="+valuation+ "  ***  AGENT="+agent.toString());
	    }
	    
	    	    
    	return toReturn;
	  }//modifyShout
	
	public void eventOccurred( AuctionEvent event ) {
		super.eventOccurred(event);
	}//eventOccurred
}//class