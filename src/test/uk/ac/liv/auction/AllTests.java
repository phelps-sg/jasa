package test.uk.ac.liv.auction;

import junit.framework.*;
import junit.swingui.TestRunner;


public class AllTests {

  public static void main( String[] args ) {
    try {
      TestRunner.run(Class.forName("test.uk.ac.liv.auction.AllTests"));
    } catch ( ClassNotFoundException e ) {
      e.printStackTrace();
    }
  }

  public static Test suite() {
    TestSuite suite = new TestSuite("auction test suite");
    suite.addTest(test.uk.ac.liv.util.BinaryHeapTest.suite());
    suite.addTest(test.uk.ac.liv.auction.agent.RoundRobinTraderTest.suite());
    suite.addTest(test.uk.ac.liv.auction.agent.ZISTraderTest.suite());
    suite.addTest(test.uk.ac.liv.auction.core.AuctioneerTest.suite());
    suite.addTest(test.uk.ac.liv.auction.core.AuctionTest.suite());
    suite.addTest(test.uk.ac.liv.auction.core.FourHeapTest.suite());
    suite.addTest(test.uk.ac.liv.ai.learning.RothErevLearnerTest.suite());
    return suite;
  }


}

