package uk.ac.liv.auction.agent;

import junit.framework.Test;
import junit.framework.TestSuite;
import uk.ac.liv.auction.core.ClearingHouseAuctioneer;
import uk.ac.liv.auction.core.DiscriminatoryPricingPolicy;

public class GDEfficiencyCHTest extends GDEfficiencyTest {

	public GDEfficiencyCHTest(String name) {
		super(name);
	}

	protected void assignAuctioneer() {
		auctioneer = new ClearingHouseAuctioneer();
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
