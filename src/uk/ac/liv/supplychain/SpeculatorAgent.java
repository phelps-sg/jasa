package uk.ac.liv.supplychain;

import ec.util.Parameter;
import ec.util.ParameterDatabase;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;

public class SpeculatorAgent extends SupplyChainAgent {
	
	public void setup( ParameterDatabase parameters, Parameter base, SupplyChainRandomRobinAuction[] auction ) {
		super.setup(parameters, base, auction);
	
		if ( deliver.length != 0 )
	    	System.out.println("Warning: Agent " + getId() + " of type uk.ac.liv.supplychain.RawMaterialSupplierAgent should not deliver in any market ("+source.length+" are configured).");
	    if ( source.length == 0 )
	    	System.out.println("Warning: Agent " + getId() + " of type uk.ac.liv.supplychain.RawMaterialSupplierAgent should source in exactly one market (none is configured).");
	    if ( source.length > 1 )
	    	System.out.println("Warning: Agent " + getId() + " of type uk.ac.liv.supplychain.RawMaterialSupplierAgent should not source in more than one market.");
	}//setup

	public void produce(Auction auction) {
		//System.out.println("SpeculatorAgent.produce "+toString());
	}
	
/*	public void shoutAccepted( Auction auction, Shout shout, double price, int quantity ) {
		System.out.println("\n\n"+auction.getAge()+" AV SpeculatorAgent.shoutAccepted "+toString());
		super.shoutAccepted(auction, shout,price,quantity);
		System.out.println(auction.getAge()+" AP SpeculatorAgent.shoutAccepted "+toString());
	}//shoutAccepted
*/
}
