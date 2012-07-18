package net.sourceforge.jasa.agent;

import net.sourceforge.jabm.SpringSimulationController;
import net.sourceforge.jasa.market.MarketSimulation;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class InventoryTest extends TestCase {

	protected Inventory holding;

	public static final int INITIAL = 10;

	public InventoryTest(String name) {
		super(name);
	}

	public void setUp() {
		holding = new Inventory(INITIAL);
	}

	public void testRemove() {
		int removeQty = 20;
		holding.remove(removeQty);
		assertTrue(holding.getQuantity() == (INITIAL - removeQty));
	}

	public void testAdd() {
		holding.add(20);
		assertTrue(holding.getQuantity() == INITIAL + 20);
		holding.add(-10);
		assertTrue(holding.getQuantity() == INITIAL + 20 - 10);
	}

	public void testSetOwner() {
		MarketSimulation simulation = new MarketSimulation();
		simulation.setSimulationController(new SpringSimulationController());
		MockTrader owner = new MockTrader(this, 0, 0, simulation);
		holding.setOwner(owner);
		assertTrue(holding.getOwner().equals(owner));
	}

	public void testTransfer() {
		int transferQty = 6;
		Inventory other = new Inventory(0);
		holding.transfer(other, transferQty);
		assertTrue(holding.getQuantity() == (INITIAL - transferQty));
		assertTrue(other.getQuantity() == (0 + transferQty));
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		return new TestSuite(InventoryTest.class);
	}

}
