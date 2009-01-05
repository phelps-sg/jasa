package uchicago.src.sim.space;

import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.space.Discrete2DSpace;
import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.space.Object2DTorus;


public class RectTest extends TestCase{
	private Object2DGrid grid = new Object2DGrid(10, 10);
	private Object2DTorus torus = new Object2DTorus(10, 10);

	public RectTest(String name){
		super(name);
	}

  /*
0	   1	 2	 3	 4	 5	 6	 7	 8	 9	
10	11	12	13	14	15	16	17	18	19	
20	21	22	23	24	25	26	27	28	29	
30	31	32	33	34	35	36	37	38	39	
40	41	42	43	44	45	46	47	48	49	
50	51	52	53	54	55	56	57	58	59	
60	61	62	63	64	65	66	67	68	69	
70	71	72	73	74	75	76	77	78	79	
80	81	82	83	84	85	86	87	88	89	
90	91	92	93	94	95	96	97	98	99
  */


	public void setUp(){
    int k = 0;
		for(int y = 0 ; y < 10 ; y++){
			for (int x = 0 ; x < 10 ; x++){
				grid.putObjectAt(x, y, new Integer(k));
				torus.putObjectAt(x, y, new Integer(k));
        k++;
			}
		}
	}

	public void testFindMooreMax(){
		Integer expected = new Integer(66);
		Integer received = (Integer) (grid.findMaximum(5, 5, 1, true, Discrete2DSpace.MOORE)).get(0);
		assertEquals(expected, received);
	}

	public void testNormX(){
		Integer expected = new Integer(51);
		Integer received = (Integer) torus.getObjectAt(11,5);
		assertEquals(expected, received);
	}
		
	public void testFindVNMax(){
		Vector expected = new Vector();
		//expected.add(new Integer(5*6));
		expected.add(new Integer(65));
		Vector received = grid.findMaximum(5, 5, 1, true, Discrete2DSpace.VON_NEUMANN);
		assertEquals(expected, received);
	}

	public void testFindExtendedMooreMax(){
		Integer expected = new Integer(77);
		Integer received = (Integer) (grid.findMaximum(5, 5, 2, true, Discrete2DSpace.MOORE)).get(0);
		assertEquals(expected, received);
	}

	public void testFindExtendedVNMax(){
		Vector expected = new Vector();
		expected.add(new Integer(75));
		Vector received = grid.findMaximum(5,5,2, true, Discrete2DSpace.VON_NEUMANN);
		assertEquals(expected, received);
	}


	 public void testFindExtendedVNTorusMin(){
		Vector expected = new Vector();
		expected.add(new Integer(9));
		Vector received = torus.findMinimum(9,9,2,true, Discrete2DSpace.VON_NEUMANN);
		assertEquals(expected, received);
	}


	public void testTorusExtendedVN(){
		Vector expected = new Vector();
		expected.add(new Integer(57));
		expected.add(new Integer(58));
		expected.add(new Integer(51));
		expected.add(new Integer(50));
		expected.add(new Integer(39));
		expected.add(new Integer(49));
		expected.add(new Integer(79));
		expected.add(new Integer(69));
		Vector received = torus.getVonNeumannNeighbors(9,5,2,2,true);
		assertEquals(expected, received);
	}

	public void testSEMooreEdge(){
		Vector expected = new Vector();
		expected.add(new Integer(88));
		expected.add(new Integer(89));
		expected.add(new Integer(80));
		expected.add(new Integer(98));
		expected.add(new Integer(90));
		expected.add(new Integer(8));
		expected.add(new Integer(9));
		expected.add(new Integer(0));
		Vector received = torus.getMooreNeighbors(9,9,false);
		assertEquals(expected, received);
	}

	public void testSWMooreEdge(){
		Vector expected = new Vector();
		expected.add(new Integer(89));
		expected.add(new Integer(80));
		expected.add(new Integer(81));
		expected.add(new Integer(99));
		expected.add(new Integer(91));
		expected.add(new Integer(9));
		expected.add(new Integer(0));
		expected.add(new Integer(1));
		Vector received = torus.getMooreNeighbors(0,9,false);
		assertEquals(expected, received);
	}
		
	public void testNWMooreEdge(){
		Vector expected = new Vector();
		expected.add(new Integer(99));
		expected.add(new Integer(90));
		expected.add(new Integer(91));
		expected.add(new Integer(9));
		expected.add(new Integer(1));
		expected.add(new Integer(19));
		expected.add(new Integer(10));
		expected.add(new Integer(11));
		Vector received = torus.getMooreNeighbors(0,0,false);
		assertEquals(expected, received);
	}
	
	public void testNEMooreEdge(){
		Vector expected = new Vector();
		expected.add(new Integer(98));
		expected.add(new Integer(99));
		expected.add(new Integer(90));
		expected.add(new Integer(8));
		expected.add(new Integer(0));
		expected.add(new Integer(18));
		expected.add(new Integer(19));
		expected.add(new Integer(10));
		Vector received = torus.getMooreNeighbors(9,0,false);
		assertEquals(expected, received);
	}

	public void testSEVNEdge(){
		Vector expected = new Vector();
		expected.add(new Integer(98));
		expected.add(new Integer(90));
		expected.add(new Integer(89));
		expected.add(new Integer(9));
		Vector received = torus.getVonNeumannNeighbors(9,9,false);
		assertEquals(expected, received);
	}
	
	public void testSWVNEdge(){
		Vector expected = new Vector();
		expected.add(new Integer(99));
		expected.add(new Integer(91));
		expected.add(new Integer(80));
		expected.add(new Integer(0));
		Vector received = torus.getVonNeumannNeighbors(0,9,false);
		assertEquals(expected, received);
	}
	
	public void testNWVNEdge(){
		Vector expected = new Vector();
		expected.add(new Integer(9));
		expected.add(new Integer(1));
		expected.add(new Integer(90));
		expected.add(new Integer(10));
		Vector received = torus.getVonNeumannNeighbors(0,0,false);
		assertEquals(expected, received);
	}
	
	public void testNEVNEdge(){
		Vector expected = new Vector();
		expected.add(new Integer(8));
		expected.add(new Integer(0));
		expected.add(new Integer(99));
		expected.add(new Integer(19));
		Vector received = torus.getVonNeumannNeighbors(9,0,false);
		assertEquals(expected, received);
	}

	public void testMooreMin(){
		Vector expected = new Vector();
		expected.add(new Integer(44));
		Vector received = grid.findMinimum(5,5,1, true, Discrete2DSpace.MOORE);
		assertEquals(expected,received);
	}

  public void testGridVNEdge() {
    Vector expected = new Vector();
    expected.add(new Integer(18));
    expected.add(new Integer(9));
    expected.add(new Integer(29));

    Vector received = grid.getVonNeumannNeighbors(9, 1, 1, 1, true);
    assertEquals(expected, received);
  }

  public void testGridMooreEdge() {
    Vector expected = new Vector();
    expected.add(new Integer(80));
    expected.add(new Integer(81));
    expected.add(new Integer(91));

    Vector received = grid.getMooreNeighbors(0, 9, 1, 1, true);
    assertEquals(expected, received);
  }


    

	public static Test suite(){
		return new TestSuite(uchicago.src.sim.space.RectTest.class);
	}

	public static void main(String[] args){
		junit.textui.TestRunner.run(suite());
	}
}
		
