package uk.ac.liv.supplyChain;

import ec.util.Parameter;
import ec.util.ParameterDatabase;
import uk.ac.liv.auction.core.Auction;

public class EndCustomerAgent extends SupplyChainAgent {
	
	protected static final String P_CONSUMPTION		= "consumption";
	protected static final String P_PRODUCE_MONEY	= "produceMoney";
	
	protected double consumption = -1;
	protected double produceMoney = -1;

	public void setup( ParameterDatabase parameters, Parameter base, SupplyChainRandomRobinAuction[] auction ) {
		super.setup(parameters, base, auction);
	
	    consumption		= parameters.getDouble(base.push(P_CONSUMPTION), base.push(P_CONSUMPTION), -1);
	    
	    produceMoney	= parameters.getDouble(base.push(P_PRODUCE_MONEY), base.push(P_PRODUCE_MONEY), -1);
	    
	    if ( deliver.length != 0 )
	    	System.out.println("Warning: Agent " + getId() + " of type uk.ac.liv.supplyChain.RawMaterialSupplierAgent should not deliver in any market ("+source.length+" are configured).");
	    if ( source.length == 0 )
	    	System.out.println("Warning: Agent " + getId() + " of type uk.ac.liv.supplyChain.RawMaterialSupplierAgent should source in exactly one market (none is configured).");
	    if ( source.length > 1 )
	    	System.out.println("Warning: Agent " + getId() + " of type uk.ac.liv.supplyChain.RawMaterialSupplierAgent should not source in more than one market.");
	}//setup
	
	public void produce(Auction auction) {
		//XXX System.out.println("AV EndCustomerAgent.simulateProduction Ag="+getId()+" "+toString());
		//SupplyChainRandomRobinAuction auction = (SupplyChainRandomRobinAuction) event.getAuction();
		int auctionIdx = (int) ((SupplyChainRandomRobinAuction) auction).getId();
		
		if ( sourceWhichBuysInAuction[auctionIdx] >= 0) {
			source[ sourceWhichBuysInAuction[auctionIdx] ]	-= consumption ;
			pay( produceMoney );
		}
		else if ( sourceWhichSellsInAuction[auctionIdx] >= 0) {
			source[ sourceWhichSellsInAuction[auctionIdx] ]	-= consumption ;
			pay( produceMoney );
		}
		//XXX System.out.println("AP EndCustomerAgent.simulateProduction Ag="+getId()+" "+toString());
		
	}//simulateProduction
}//class
