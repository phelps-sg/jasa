package test.uk.ac.liv.auction.agent;

import junit.framework.*;

import uk.ac.liv.auction.agent.RoundRobinTrader;
import uk.ac.liv.auction.core.*;


public class RoundRobinTraderTest extends TestCase {

  TestTrader trader1, trader2;

  public RoundRobinTraderTest( String name ) {
    super(name);
  }

  public void setUp() {
    trader1 = new TestTrader(this, 50, 10000);
    trader2 = new TestTrader(this, 20, 20000);
  }

  public void test() {
    trader1.purchaseFrom(trader2, 5, 1000);
    assertTrue( trader1.getStock() == 55 );
    assertTrue( trader2.getStock() == 15 );
    assertTrue( trader1.getFunds() == 5000 );
    assertTrue( trader2.getFunds() == 25000 );
    trader1.reset();
    assertTrue( trader1.getFunds() == 10000 );
    assertTrue( trader1.getStock() == 50 );
    trader2.reset();
    assertTrue( trader2.getFunds() == 20000 );
    assertTrue( trader2.getStock() == 20 );
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run (suite());
  }

  public static Test suite() {
    return new TestSuite(RoundRobinTraderTest.class);
  }
}

