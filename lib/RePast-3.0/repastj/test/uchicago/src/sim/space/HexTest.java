package uchicago.src.sim.space;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Title:        Repast Tests
 * Description:  These are the unit tests, mostly for spaces
 * Copyright:    Copyright (c) 2002
 * Company:      SRC
 * @author Tom Howe
 * @version 1.0
 */

public class HexTest extends TestCase {
	private Object2DHexagonalTorus torus = new Object2DHexagonalTorus(7, 7);

  private Object2DHexagonalGrid grid = new Object2DHexagonalGrid(7, 7);
  
	public HexTest(String name){
		super(name);
	}

  public void setUp(){
    // make a hexagonal grid where each cell is some number
    // from 0 - 48. The numbers increase by column so that
    // 0,0 = 0, 1,0 = 1 and so on.
    int k = 0;
    for (int y = 0; y < 7; y++) {
      for (int x = 0; x < 7; x++) {
        grid.putObjectAt(x, y, new Integer(k++));
      }
    }
		k = 0;
		for (int y = 0; y < 7; y++){
			for(int x = 0; x < 7 ; x++){
				torus.putObjectAt(x,y, new Integer(k++));
			}
		}
				
	}

  public void testgetOddNeigh() {
    Vector v = grid.getNeighbors(3, 3, 1, true);
    assertEquals(6, v.size());
    assertEquals(new Integer(17), v.get(0));
    assertEquals(new Integer(18), v.get(1));
    assertEquals(new Integer(25), v.get(2));
    assertEquals(new Integer(31), v.get(3));
    assertEquals(new Integer(23), v.get(4));
    assertEquals(new Integer(16), v.get(5));
  }

  public void testNMultiNeigh() {
    Vector v = grid.getNeighbors(3, 3, 2, true);
    assertEquals(18, v.size());

    assertEquals(new Integer(10), v.get(0));
    assertEquals(new Integer(11), v.get(1));
    assertEquals(new Integer(19), v.get(2));
    assertEquals(new Integer(26), v.get(3));
    assertEquals(new Integer(33), v.get(4));
    assertEquals(new Integer(32), v.get(5));

    assertEquals(new Integer(38), v.get(6));
    assertEquals(new Integer(30), v.get(7));
    assertEquals(new Integer(29), v.get(8));
    assertEquals(new Integer(22), v.get(9));
    assertEquals(new Integer(15), v.get(10));
    assertEquals(new Integer(9), v.get(11));
    
    assertEquals(new Integer(17), v.get(12));
    assertEquals(new Integer(18), v.get(13));
    assertEquals(new Integer(25), v.get(14));
    assertEquals(new Integer(31), v.get(15));
    assertEquals(new Integer(23), v.get(16));
    assertEquals(new Integer(16), v.get(17));
  }
  
  public void testGetObjectAt(){
    Integer objExpected = new Integer(39);
    Integer recieved = (Integer) grid.getObjectAt(4,5);
    assertEquals(objExpected,recieved);
  }

	public void testGetTwoPlusNeigh(){
		//Vector expected = new Vector();
		Vector received = grid.getNeighbors(3, 3, 3, false);
		assertEquals(36, received.size());
		assertEquals(new Integer(3), received.get(0));
		assertEquals(new Integer(4), received.get(1));
		assertEquals(new Integer(12), received.get(2));
		assertEquals(new Integer(13), received.get(3));
		assertEquals(new Integer(20), received.get(4));
		assertEquals(new Integer(27), received.get(5));
		assertEquals(new Integer(34), received.get(6));
		assertEquals(new Integer(40), received.get(7));
		assertEquals(new Integer(39), received.get(8));
		assertEquals(new Integer(45), received.get(9));
		assertEquals(new Integer(37), received.get(10));
		assertEquals(new Integer(36), received.get(11));
		assertEquals(new Integer(28), received.get(12));
		assertEquals(new Integer(21), received.get(13));
		assertEquals(new Integer(14), received.get(14));
		assertEquals(new Integer(7), received.get(15));
		assertEquals(new Integer(8), received.get(16));
		assertEquals(new Integer(2), received.get(17));
				
		
    assertEquals(new Integer(10), received.get(18));
    assertEquals(new Integer(11), received.get(19));
    assertEquals(new Integer(19), received.get(20));
    assertEquals(new Integer(26), received.get(21));
    assertEquals(new Integer(33), received.get(22));
    assertEquals(new Integer(32), received.get(23));

    assertEquals(new Integer(38), received.get(24));
    assertEquals(new Integer(30), received.get(25));
    assertEquals(new Integer(29), received.get(26));
    assertEquals(new Integer(22), received.get(27));
    assertEquals(new Integer(15), received.get(28));
    assertEquals(new Integer(9), received.get(29));
    
    assertEquals(new Integer(17), received.get(30));
    assertEquals(new Integer(18), received.get(31));
    assertEquals(new Integer(25), received.get(32));
    assertEquals(new Integer(31), received.get(33));
    assertEquals(new Integer(23), received.get(34));
    assertEquals(new Integer(16), received.get(35));
	}

	public void testFindMax(){
		Vector objExpected = new Vector();
		objExpected.add(new Integer(47));
		Vector recieved = grid.findMaximum(5, 5, 1, true);
		assertEquals(objExpected, recieved);
	}

	public void testFindMinOdd(){
		Vector objExpected = new Vector();
		objExpected.add(new Integer(32));
		Vector received = grid.findMinimum(5, 5, 1, true);
		assertEquals(objExpected, received);
	}

	public void testGetEvenNeighbors(){
		Vector expected = new Vector();
		expected.add(new Integer(25));
		expected.add(new Integer(33));
		expected.add(new Integer(40));
		expected.add(new Integer(39));
		expected.add(new Integer(38));
		expected.add(new Integer(31));
		Vector received = grid.getNeighbors(4, 4, 1, true);
		assertEquals(expected, received);
	}
		
	

	public void testFindMinEven(){
		Vector objExpected = new Vector();
		objExpected.add(new Integer(25));
		Vector received = grid.findMinimum(4, 4, 1, true);
		assertEquals(objExpected, received);
	}

	
	public void testTorusWrap(){
		Vector expected = new Vector();
		expected.add(new Integer(41));
		expected.add(new Integer(42));
		expected.add(new Integer(0));
		expected.add(new Integer(6));
		expected.add(new Integer(5));
		expected.add(new Integer(47));
		Vector received = torus.getNeighbors(6,6,true);
		assertEquals(expected, received);
	}
	
		
	public void testTryEdge(){
		Vector expected = new Vector();
		expected.add(new Integer(41));
		expected.add(new Integer(47));
		Vector received = grid.getNeighbors(6,6,false);
		assertEquals(expected, received);
	}

  public void testNorthWrap(){
		Vector expected = new Vector();
		expected.add(new Integer(48));
		expected.add(new Integer(0));
		expected.add(new Integer(7));
		expected.add(new Integer(13));
		expected.add(new Integer(12));
		expected.add(new Integer(5));
		Vector received = torus.getNeighbors(6,0,false);
		assertEquals(expected, received);
	}

	
	public static Test suite(){
		return new TestSuite(uchicago.src.sim.space.HexTest.class);
	}

	public static void main(String[] args){
		junit.textui.TestRunner.run(suite());
	}

}
