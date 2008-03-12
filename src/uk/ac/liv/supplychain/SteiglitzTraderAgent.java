package uk.ac.liv.supplychain;

import uk.ac.liv.auction.core.Auction;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class SteiglitzTraderAgent extends SupplyChainAgent {
	boolean display = false;

	protected static final String P_CONSUMPTION = "consumption";

	protected double consumption = -1;

	public void setup(ParameterDatabase parameters, Parameter base,
	    SupplyChainRandomRobinAuction[] auction) {
		super.setup(parameters, base, auction);

		Parameter paramT = base.push(P_CONSUMPTION);
		consumption = parameters.getDouble(paramT, paramT, -1);
	}// setup

	public void produce(Auction auction) {
		// SupplyChainRandomRobinAuction auction =
		// (SupplyChainRandomRobinAuction) event.getAuction();
		int auctionIdx = (int) ((SupplyChainRandomRobinAuction) auction).getId();
		double price = ((SupplyChainRandomRobinAuction) auction)
		    .getTransactionPrice(auction.getAge());

		source[auctionIdx] -= consumption; // consume some units of food every
		// day

		if (((SteiglitzTraderValuer) valuer).skillG >= price
		    * ((SteiglitzTraderValuer) valuer).skillF) {
			// produce gold
			pay(((SteiglitzTraderValuer) valuer).skillG);

			if (display)
				System.out.println(auction.getAge() + " Ag " + getId() + " produces "
				    + ((SteiglitzTraderValuer) valuer).skillG + " GOLD because skillG="
				    + ((SteiglitzTraderValuer) valuer).skillG + "> price=" + price
				    + " * skillF=" + ((SteiglitzTraderValuer) valuer).skillF + " = "
				    + (price * ((SteiglitzTraderValuer) valuer).skillF));
		} else {
			// produce food
			source[0] += ((SteiglitzTraderValuer) valuer).skillF;

			if (display)
				System.out.println(auction.getAge() + " Ag " + getId() + " produces "
				    + ((SteiglitzTraderValuer) valuer).skillF + " FOOD because skillG="
				    + ((SteiglitzTraderValuer) valuer).skillG + "<= price=" + price
				    + " * skillF=" + ((SteiglitzTraderValuer) valuer).skillF + " = "
				    + (price * ((SteiglitzTraderValuer) valuer).skillF));
		}
		// XXX System.out.println(auction.getAge()+"
		// EndCustomerAgent.simulateProduction Ag "+getId()+ ": "+toString());
	}// simulateProduction

}
