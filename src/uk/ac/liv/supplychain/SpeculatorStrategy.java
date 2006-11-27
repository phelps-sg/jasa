package uk.ac.liv.supplychain;

import java.io.Serializable;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Uniform;
import ec.util.Parameter;
import ec.util.ParameterDatabase;
//import edu.stanford.smi.protege.model.Facet;

import uk.ac.liv.auction.agent.FixedQuantityStrategyImpl;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.prng.GlobalPRNG;

public class SpeculatorStrategy extends FixedQuantityStrategyImpl implements Serializable {
	boolean display = false;
	
	public static final String P_MARGIN		= "margin";
	public static final String P_ACTIVATION_DATE	= "activationdate";
	
	private double margin = 0;
	private double activationdate = 0;

	public void endOfRound(Auction auction) {
		// TODO Auto-generated method stub
	}//endOfRound
	
	public void initialise() {
	    super.initialise();
	  }//initialise
	
	public void setup( ParameterDatabase parameters, Parameter base ) {
	    super.setup(parameters, base);
	    
	    Parameter typeParam = base.push(P_MARGIN);
	    ParameterInitializer parameterInitializer = (ParameterInitializer)parameters.getInstanceForParameterEq(typeParam, null, ParameterInitializer.class);
	    parameterInitializer.setup(parameters, base);
	    margin = parameterInitializer.getValue();
	    
	    activationdate = parameters.getIntWithDefault(base.push(P_ACTIVATION_DATE), null, 0);
	    
	    initialise();
	  }//setup
	
	public boolean modifyShout( Shout.MutableShout shout ) {
		boolean toReturn		= super.modifyShout(shout);
	    double previousPrice	= ((SupplyChainRandomRobinAuction) auction).getTransactionPrice( auction.getAge() );
	    int auctionIdx			= (int) ((SupplyChainRandomRobinAuction) auction).getId();
		double valuation		= agent.getValuation( auction );

		/*if (display)
			System.out.println(auction.getAge()+" SpeculatorStrategy: agent "+agent.getId()+"previousPrice="+previousPrice+ " < valuation="+valuation+" * ( 1 - margin="+margin+")="+(valuation * ( 1 - margin))+"  ***  "+"previousPrice="+previousPrice+ " < valuation="+valuation+" * ( 1 + margin="+margin+")="+(valuation * ( 1 + margin)));
		*/
		//equation 7 page 10 of Chap 1 in book MBC
		if ( previousPrice < valuation * ( 1 - margin)) {
			shout.setIsBid(true);		//buy
	    	shout.setPrice(valuation * ( 1 + margin));
	    	
	    	if ( (int) ( agent.getFunds() / valuation ) > 0 )
	    		shout.setQuantity( (int) ( agent.getFunds() / valuation ) );
	    	else
	    		shout.setQuantity(0);
	    	toReturn = true;
	    	
	    	/*if (display)
				System.out.println(auction.getAge()+" SpeculatorStrategy: BUYS     ("+toReturn+") "+shout.getQuantity()+"@$"+shout.getPrice()+" SpeculatorStrategy previousPrice="+previousPrice+ " < valuation="+valuation+" * ( 1 - margin="+margin+")="+(valuation * ( 1 - margin))+agent.toString());
				*/
		}//equation 8 page 10 of Chapt 1 of book MBC
		else if ( previousPrice > valuation * ( 1 + margin)) {
			shout.setIsBid(false);		//sell
			shout.setPrice(valuation * ( 1 + margin));
			shout.setQuantity( (int) ((SupplyChainAgent) agent).source[0]) ;
			toReturn = true;
			
			/*if (display)
				System.out.println(auction.getAge()+" SpeculatorStrategy:     SELLS("+toReturn+") "+shout.getQuantity()+"@$"+shout.getPrice()+" SpeculatorStrategy previousPrice="+previousPrice+ " < valuation="+valuation+" * ( 1 + margin="+margin+")="+(valuation * ( 1 + margin))+agent.toString());
				*/
		}
		else {
			toReturn = false;
		}
		
		if ( (shout.getQuantity() == 0) || (java.lang.Double.isNaN(shout.getPrice())) || (java.lang.Double.isInfinite(shout.getPrice())) )
	    	toReturn = false;
		
		if (display)
			System.out.println(auction.getAge()+ " speculatorStrategy "+agent.getId()+" forecast="+valuation+" previousPrice="+previousPrice);	
		
		if (display)
			if (toReturn==true) {
				if (shout.isAsk())
					System.out.print(auction.getAge()+" Speculator Agent "+agent.getId() +" SELLS "	);
				if (shout.isBid())
					System.out.print(auction.getAge()+" Speculator Agent "+agent.getId() +" BUYS ");
				System.out.println(shout.getQuantity()+" items at $"+shout.getPrice()+agent.toString());
			}
		
		if (auction.getAge() < activationdate)
			toReturn=false;

		return toReturn;
	}//modifyShout
}
