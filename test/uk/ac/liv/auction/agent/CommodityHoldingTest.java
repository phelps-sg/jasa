package uk.ac.liv.auction.agent;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CommodityHoldingTest extends TestCase {

  protected CommodityHolding holding;
  
  public static final int INITIAL = 10;
  
  public CommodityHoldingTest(String name) {
    super(name);
  }
  
  public void setUp() {
    holding = new CommodityHolding(INITIAL);
  }
  
  public void testRemove() {
    int removeQty = 20;
    holding.remove(removeQty);
    assertTrue(holding.getQuantity() == (INITIAL - removeQty));
  }
  
  public void testAdd() {
    holding.add(20);
    assertTrue(holding.getQuantity() == INITIAL+20);
    holding.add(-10);
    assertTrue(holding.getQuantity() == INITIAL+20-10);
  }
  
  public void testSetOwner() {
    MockTrader owner = new MockTrader(this, 0, 0);
    holding.setOwner(owner);
    assertTrue(holding.getOwner().equals(owner));
  }
  
  public void testTransfer() {
    int transferQty = 6;
    CommodityHolding other = new CommodityHolding(0);
    holding.transfer(other, transferQty);
    assertTrue(holding.getQuantity()== (INITIAL - transferQty));
    assertTrue(other.getQuantity() == (0 + transferQty));
  }
  

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(CommodityHoldingTest.class);
  }

  
  
}
