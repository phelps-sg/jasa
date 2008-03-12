package uk.ac.liv.supplychain;

import ec.util.Parameter;
import ec.util.ParameterDatabase;
import uk.ac.liv.auction.core.Auction;

public class RawMaterialSupplierAgent extends SupplyChainAgent {

	public void produce(Auction auction) {
		// TODO: take `deliver' capacities into account
		if (super.auction[0].getAge() >= super.lastProductionStartDate + make_speed) {
			for (int i = 0; i < deliver.length; i++)
				deliver[i] += make_capacity;
			// make = 0;
			// for( int i=0 ; i < source.length ; i++ )
			// source[i] -= make_capacity;
			super.lastProductionStartDate = super.auction[0].getAge();
			// XXX System.out.println("JJ
			// RawMaterialSupplierAgent.simulateProduction: Ag"+getId()+":
			// make");
		}
	}// simulateProduction

	public void setup(ParameterDatabase parameters, Parameter base,
	    SupplyChainRandomRobinAuction[] auction) {
		super.setup(parameters, base, auction);

		if (source.length != 0)
			System.out
			    .println("Warning: Agent "
			        + getId()
			        + " of type uk.ac.liv.supplychain.RawMaterialSupplierAgent should not source in any market ("
			        + source.length + " are configured).");
		if (deliver.length == 0)
			System.out
			    .println("Warning: Agent "
			        + getId()
			        + " of type uk.ac.liv.supplychain.RawMaterialSupplierAgent should deliver in exactly one market (none is configured).");
		if (deliver.length > 1)
			System.out
			    .println("Warning: Agent "
			        + getId()
			        + " of type uk.ac.liv.supplychain.RawMaterialSupplierAgent should not deliver in more than one market.");
	}// setup
}// class
