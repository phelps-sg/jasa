package net.sourceforge.jasa.agent;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.jasa.market.auctioneer.ClearingHouseAuctioneer;
import net.sourceforge.jasa.market.rules.DiscriminatoryPricingPolicy;

public class GDEfficiencyCHTest extends GDEfficiencyTest {

	public GDEfficiencyCHTest(String name) {
		super(name);
	}

	protected void assignAuctioneer() {
		auctioneer = new ClearingHouseAuctioneer(auction);
		auctioneer.setPricingPolicy(new DiscriminatoryPricingPolicy(0.5));
		auction.setAuctioneer(auctioneer);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(GDEfficiencyCHTest.class);
	}

}
