package uk.ac.liv.supplyChain;

import ec.util.Parameter;
import ec.util.ParameterDatabase;
import uk.ac.liv.auction.core.Auction;

public class SpeculatorAgent extends SupplyChainAgent {
	
	public void setup( ParameterDatabase parameters, Parameter base, SupplyChainRandomRobinAuction[] auction ) {
		super.setup(parameters, base, auction);
	
		if ( deliver.length != 0 )
	    	System.out.println("Warning: Agent " + getId() + " of type uk.ac.liv.supplyChain.RawMaterialSupplierAgent should not deliver in any market ("+source.length+" are configured).");
	    if ( source.length == 0 )
	    	System.out.println("Warning: Agent " + getId() + " of type uk.ac.liv.supplyChain.RawMaterialSupplierAgent should source in exactly one market (none is configured).");
	    if ( source.length > 1 )
	    	System.out.println("Warning: Agent " + getId() + " of type uk.ac.liv.supplyChain.RawMaterialSupplierAgent should not source in more than one market.");
	}//setup

	public void produce(Auction auction) {
		//System.out.println("SpeculatorAgent.produce "+toString());
	}

}
