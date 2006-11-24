package uk.ac.liv.supplyChain;

import java.io.Serializable;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Uniform;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.agent.FixedQuantityStrategyImpl;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.prng.GlobalPRNG;

public class SpeculatorStrategy extends FixedQuantityStrategyImpl implements
		Serializable {
	
	protected AbstractContinousDistribution distribution;
	
	private double margin = 0;

	public void endOfRound(Auction auction) {
		// TODO Auto-generated method stub
	}//endOfRound
	
	public void initialise() {
	    super.initialise();
	    distribution = new Uniform(0, 0.5, GlobalPRNG.getInstance());
	    margin = distribution.nextDouble();
	  }//initialise
	
	public void setup( ParameterDatabase parameters, Parameter base ) {
	    super.setup(parameters, base);
	    initialise();
	  }//setup
	
	public boolean modifyShout( Shout.MutableShout shout ) {
		boolean toReturn		= super.modifyShout(shout);
	    double previousPrice	= ((SupplyChainRandomRobinAuction) auction).getTransactionPrice( auction.getAge() );
	    int auctionIdx			= (int) ((SupplyChainRandomRobinAuction) auction).getId();
		double valuation		= agent.getValuation( auction );
		
		System.out.println("SpeculatorStrategy: agent "+agent.getId()+" margin="+margin+" valuation="+valuation);

		//equation 7 page 10 of Chap 1 in book MBC
		if ( previousPrice < valuation * ( 1 - margin)) {
			System.out.println("SpeculatorStrategy: BUY SpeculatorStrategy previousPrice="+previousPrice+ " < valuation="+valuation+" * ( 1 - margin="+margin+")="+(valuation * ( 1 - margin)));
			shout.setIsBid(true);		//buy
	    	shout.setPrice(valuation * ( 1 + margin));
	    	shout.setQuantity( (int) ( agent.getFunds() / ( valuation * ( 1 - margin) ) ) );
	    	toReturn = true;
		}//equation 8 page 10 of Chapt 1 of book MBC
		else if ( previousPrice > valuation * ( 1 + margin)) {
			System.out.println("SpeculatorStrategy: SELL SpeculatorStrategy previousPrice="+previousPrice+ " < valuation="+valuation+" * ( 1 + margin="+margin+")="+(valuation * ( 1 + margin)));
			shout.setIsBid(false);		//sell
			shout.setPrice(valuation * ( 1 + margin));
			shout.setQuantity( (int) ((SupplyChainAgent) agent).source[0]) ;
			toReturn = true;
		}
		else {
			toReturn = false;
		}
		
		if ( (shout.getQuantity() == 0) || (java.lang.Double.isNaN(shout.getPrice())) || (java.lang.Double.isInfinite(shout.getPrice())) )
	    	toReturn = false;
				
		
		System.out.println("INV="+((SupplyChainAgent) agent).source[0]);
		
		if (toReturn==true) {
			if (shout.isAsk())
				System.out.print("Speculator Agent "+agent.getId() +" sells "	);
			if (shout.isBid())
				System.out.print("Speculator Agent "+agent.getId() +" buys ");
			System.out.println(shout.getQuantity()+" items at $"+shout.getPrice());
		}

		return toReturn;
	}//modifyShout
}
