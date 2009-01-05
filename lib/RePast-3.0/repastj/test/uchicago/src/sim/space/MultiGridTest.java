package uchicago.src.sim.space;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.space.Cell;
import uchicago.src.sim.space.Multi2DGrid;
import uchicago.src.sim.space.Multi2DHexagonalGrid;
import uchicago.src.sim.space.Multi2DHexagonalTorus;
import uchicago.src.sim.space.Multi2DTorus;
import uchicago.src.sim.space.ObjectLocation;
import uchicago.src.sim.space.OrderedCell;
import uchicago.src.sim.space.OrderedMulti2DGrid;
import uchicago.src.sim.space.OrderedMulti2DHexagonalGrid;
import uchicago.src.sim.space.OrderedMulti2DHexagonalTorus;
import uchicago.src.sim.space.OrderedMulti2DTorus;

public class MultiGridTest extends TestCase {

  Multi2DGrid grid;
  Multi2DTorus torus;
  OrderedMulti2DGrid ogrid;
  OrderedMulti2DTorus otorus;

  Multi2DHexagonalGrid hgrid;
  Multi2DHexagonalTorus htorus;
  OrderedMulti2DHexagonalGrid ohgrid;
  OrderedMulti2DHexagonalTorus ohtorus;
  

  public MultiGridTest(String name) {
    super(name);
  }

  public void setUp() {
    grid = new Multi2DGrid(20, 30, true);
    grid.putObjectAt(10, 10, new Integer(3));
    grid.putObjectAt(10, 10, new Integer(4));

    grid.putObjectAt(17, 13, new Integer(12));
    grid.putObjectAt(13, 22, new Integer(-1));

    /*
     | 1    | 5, 6  | 8
      ----------------
    3| null | null  | 9
      -----------------
     | 3, 4 |  7    | 10, 11, 12
    */

    grid.putObjectAt(16, 1, new Integer(3));
    grid.putObjectAt(17, 0, new Integer(1));
    grid.putObjectAt(17, 2, new Integer(3));
    grid.putObjectAt(17, 2, new Integer(4));
    grid.putObjectAt(18, 0, new Integer(5));
    grid.putObjectAt(18, 0, new Integer(6));
    grid.putObjectAt(18, 2, new Integer(7));
    grid.putObjectAt(19, 0, new Integer(8));
    grid.putObjectAt(19, 1, new Integer(9));
    grid.putObjectAt(19, 2, new Integer(10));
    grid.putObjectAt(19, 2, new Integer(11));
    grid.putObjectAt(19, 2, new Integer(12));
    
    torus = new Multi2DTorus(20, 30, true);
    torus.putObjectAt(10, 10, new Integer(3));
    torus.putObjectAt(10, 40, new Integer(4));
    torus.putObjectAt(30, 10, new Integer(50));
    torus.putObjectAt(30, 40, new Integer(60));

    /*
      | 1    | 5, 6, -1 | 8         -->  
      ----------------
    3| null | null      | 9             14
      -----------------
     | 3, 4 |  7        | 10, 11, 12     13
    */
    
    torus.putObjectAt(16, 1, new Integer(3));
    torus.putObjectAt(17, 0, new Integer(1));
    torus.putObjectAt(17, 2, new Integer(3));
    torus.putObjectAt(17, 2, new Integer(4));
    torus.putObjectAt(18, 30, new Integer(-1));
    torus.putObjectAt(18, 0, new Integer(5));
    torus.putObjectAt(18, 0, new Integer(6));
    torus.putObjectAt(18, 2, new Integer(7));
    torus.putObjectAt(19, 0, new Integer(8));
    torus.putObjectAt(19, 1, new Integer(9));
    torus.putObjectAt(19, 2, new Integer(10));
    torus.putObjectAt(19, 2, new Integer(11));
    torus.putObjectAt(19, 2, new Integer(12));
    torus.putObjectAt(20, 2, new Integer(13));  // 0, 2
    torus.putObjectAt(20, 31, new Integer(14)); // 0, 1
    
    

    torus.putObjectAt(1, 5, new Integer(12));
    torus.putObjectAt(13, 22, new Integer(-1));
    torus.putObjectAt(21, -55, new Integer(15));


    ogrid = new OrderedMulti2DGrid(20, 30, false);
    ogrid.putObjectAt(10, 10, new Integer(3));
    ogrid.putObjectAt(10, 10, new Integer(4));
    ogrid.putObjectAt(10, 10, new Integer(50));

    ogrid.putObjectAt(17, 13, new Integer(12));
    ogrid.putObjectAt(13, 22, new Integer(-1));

    otorus = new OrderedMulti2DTorus(20, 30, true);
    otorus.putObjectAt(1, 5, new Integer(3));
    otorus.putObjectAt(21, -55, new Integer(4));
    otorus.putObjectAt(-39, 35, new Integer(50));

    otorus.putObjectAt(17, 13, new Integer(12));
    otorus.putObjectAt(13, 22, new Integer(-1));

    hGridSetup();
    
  }

  private void hGridSetup() {
    hgrid = new Multi2DHexagonalGrid(7, 7, false);
    htorus = new Multi2DHexagonalTorus(7, 7, false);
    ohtorus = new OrderedMulti2DHexagonalTorus(7, 7, false);
    ohgrid = new OrderedMulti2DHexagonalGrid(7, 7, false);
    
    int k = 0;
    
    for (int i = 0; i < 7; i++) {
      for (int j = 0; j < 7; j++) {
        hgrid.putObjectAt(j, i, new Integer(k));
        htorus.putObjectAt(j, i, new Integer(k));
        ohgrid.putObjectAt(j, i, new Integer(k));
        ohtorus.putObjectAt(j, i, new Integer(k));
        k++; 
      }
    }

    hgrid.clear(5, 4);
    htorus.clear(5, 4);
    ohtorus.clear(5, 4);
    ohgrid.clear(5, 4);
    
    hgrid.putObjectAt(4, 2, new Integer(50));
    hgrid.putObjectAt(4, 2, new Integer(51));
    hgrid.putObjectAt(4, 2, new Integer(52));

    htorus.putObjectAt(4, 2, new Integer(50));
    htorus.putObjectAt(4, 2, new Integer(51));
    htorus.putObjectAt(4, 2, new Integer(52));

    ohgrid.putObjectAt(4, 2, new Integer(50));
    ohgrid.putObjectAt(4, 2, new Integer(51));
    ohgrid.putObjectAt(4, 2, new Integer(52));

    ohtorus.putObjectAt(4, 2, new Integer(50));
    ohtorus.putObjectAt(4, 2, new Integer(51));
    ohtorus.putObjectAt(4, 2, new Integer(52));
    
    // so now 4, 2 contains 18, 50, 51, and 52.
  }

  public void testHMGGet() {
    HashSet cellSet = new HashSet();
    cellSet.add(new Integer(50));
    cellSet.add(new Integer(51));
    cellSet.add(new Integer(52));
    cellSet.add(new Integer(18));
    
    List l = hgrid.getObjectsAt(4, 2);
    assertEquals(4, l.size());

    Object val = l.get(0);
    assertTrue(cellSet.contains(val));
    cellSet.remove(val);

    val = l.get(1);
    assertTrue(cellSet.contains(val));
    cellSet.remove(val);

    val = l.get(2);
    assertTrue(cellSet.contains(val));
    cellSet.remove(val);

    val = l.get(3);
    assertTrue(cellSet.contains(val));
    cellSet.remove(val); 
  }

  public void testHMOGGet() {
    List l = ohgrid.getObjectsAt(4, 2);
    assertEquals(4, l.size());
    assertEquals(new Integer(18), l.get(0));
    assertEquals(new Integer(50), l.get(1));
    assertEquals(new Integer(51), l.get(2));
    assertEquals(new Integer(52), l.get(3));
  }

  public void testHMGOTGet() {
    List l = ohtorus.getObjectsAt(-3, 9);
    assertEquals(4, l.size());
    assertEquals(new Integer(18), l.get(0));
    assertEquals(new Integer(50), l.get(1));
    assertEquals(new Integer(51), l.get(2));
    assertEquals(new Integer(52), l.get(3));
  }

  public void testHMTGet() {
    HashSet cellSet = new HashSet();
    cellSet.add(new Integer(50));
    cellSet.add(new Integer(51));
    cellSet.add(new Integer(52));
    cellSet.add(new Integer(18));
    
    List l = htorus.getObjectsAt(4, 2);
    assertEquals(4, l.size());

    Object val = l.get(0);
    assertTrue(cellSet.contains(val));
    cellSet.remove(val);

    val = l.get(1);
    assertTrue(cellSet.contains(val));
    cellSet.remove(val);

    val = l.get(2);
    assertTrue(cellSet.contains(val));
    cellSet.remove(val);

    val = l.get(3);
    assertTrue(cellSet.contains(val));
    cellSet.remove(val); 
  }

  public void testHMTGetWrap() {
    HashSet cellSet = new HashSet();
    cellSet.add(new Integer(50));
    cellSet.add(new Integer(51));
    cellSet.add(new Integer(52));
    cellSet.add(new Integer(18));
    
    List l = htorus.getObjectsAt(-3, 9);
    assertEquals(4, l.size());

    Object val = l.get(0);
    assertTrue(cellSet.contains(val));
    cellSet.remove(val);

    val = l.get(1);
    assertTrue(cellSet.contains(val));
    cellSet.remove(val);

    val = l.get(2);
    assertTrue(cellSet.contains(val));
    cellSet.remove(val);

    val = l.get(3);
    assertTrue(cellSet.contains(val));
    cellSet.remove(val); 
  }

  public void testHMGGetNeighbors() {
    ArrayList list = hgrid.getNeighbors(3, 3, 2, true);
    HashSet uoSet = new HashSet();
    uoSet.add(new Integer(18));
    uoSet.add(new Integer(50));
    uoSet.add(new Integer(51));
    uoSet.add(new Integer(52));
    
    // 18 individuals plus 3 extra at 4, 2
    assertEquals(21, list.size());
    assertEquals(new Integer(10), list.get(0));
    assertEquals(new Integer(11), list.get(1));
    assertEquals(new Integer(19), list.get(2));
    assertEquals(new Integer(26), list.get(3));
    assertTrue(list.get(4) == null);
    assertEquals(new Integer(32), list.get(5));

    assertEquals(new Integer(38), list.get(6));
    assertEquals(new Integer(30), list.get(7));
    assertEquals(new Integer(29), list.get(8));
    assertEquals(new Integer(22), list.get(9));
    assertEquals(new Integer(15), list.get(10));
    assertEquals(new Integer(9), list.get(11));
    assertEquals(new Integer(17), list.get(12));

    HashSet set = new HashSet(list.subList(13, 17));
    assertEquals(uoSet, set);
    assertEquals(new Integer(25), list.get(17));
    assertEquals(new Integer(31), list.get(18));
    assertEquals(new Integer(23), list.get(19));
    assertEquals(new Integer(16), list.get(20));
  }

  public void testHMTGetNeighbors() {
    ArrayList list = htorus.getNeighbors(3, 3, 2, true);
    HashSet uoSet = new HashSet();
    uoSet.add(new Integer(18));
    uoSet.add(new Integer(50));
    uoSet.add(new Integer(51));
    uoSet.add(new Integer(52));
    
    // 18 individuals plus 3 extra at 4, 2
    assertEquals(21, list.size());
    assertEquals(new Integer(10), list.get(0));
    assertEquals(new Integer(11), list.get(1));
    assertEquals(new Integer(19), list.get(2));
    assertEquals(new Integer(26), list.get(3));
    assertTrue(list.get(4) == null);
    assertEquals(new Integer(32), list.get(5));

    assertEquals(new Integer(38), list.get(6));
    assertEquals(new Integer(30), list.get(7));
    assertEquals(new Integer(29), list.get(8));
    assertEquals(new Integer(22), list.get(9));
    assertEquals(new Integer(15), list.get(10));
    assertEquals(new Integer(9), list.get(11));
    assertEquals(new Integer(17), list.get(12));

    HashSet set = new HashSet(list.subList(13, 17));
    assertEquals(uoSet, set);
    assertEquals(new Integer(25), list.get(17));
    assertEquals(new Integer(31), list.get(18));
    assertEquals(new Integer(23), list.get(19));
    assertEquals(new Integer(16), list.get(20));
  }

  public void testHMGGetNeighborsLoc() {
    ArrayList list = hgrid.getNeighborsLoc(3, 3, 2, true);
    HashSet uoSet = new HashSet();
    uoSet.add(new ObjectLocation(new Integer(18), 4, 2));
    uoSet.add(new ObjectLocation(new Integer(50), 4, 2));
    uoSet.add(new ObjectLocation(new Integer(51), 4, 2));
    uoSet.add(new ObjectLocation(new Integer(52), 4, 2));
    
    // 18 individuals plus 3 extra at 4, 2
    assertEquals(21, list.size());
    assertEquals(new ObjectLocation(new Integer(10), 3, 1), list.get(0));
    assertEquals(new ObjectLocation(new Integer(11), 4, 1), list.get(1));
    assertEquals(new ObjectLocation(new Integer(19), 5, 2), list.get(2));
    assertEquals(new ObjectLocation(new Integer(26), 5, 3), list.get(3));
    assertEquals(new ObjectLocation(null, 5, 4), list.get(4));
    assertEquals(new ObjectLocation(new Integer(32), 4, 4), list.get(5));
    assertEquals(new ObjectLocation(new Integer(38), 3, 5), list.get(6));
    assertEquals(new ObjectLocation(new Integer(30), 2, 4), list.get(7));
    assertEquals(new ObjectLocation(new Integer(29), 1, 4), list.get(8));
    assertEquals(new ObjectLocation(new Integer(22), 1, 3), list.get(9));
    assertEquals(new ObjectLocation(new Integer(15), 1, 2), list.get(10));
    assertEquals(new ObjectLocation(new Integer(9), 2, 1), list.get(11));
    assertEquals(new ObjectLocation(new Integer(17), 3, 2), list.get(12));
    HashSet set = new HashSet(list.subList(13, 17));
    assertEquals(uoSet, set);
    
    assertEquals(new ObjectLocation(new Integer(25), 4, 3), list.get(17));
    assertEquals(new ObjectLocation(new Integer(31), 3, 4), list.get(18));
    assertEquals(new ObjectLocation(new Integer(23), 2, 3), list.get(19));
    assertEquals(new ObjectLocation(new Integer(16), 2, 2), list.get(20));
  }

  public void testHMTGetNeighborsLoc() {
    ArrayList list = htorus.getNeighborsLoc(3, 3, 2, true);
    HashSet uoSet = new HashSet();
    uoSet.add(new ObjectLocation(new Integer(18), 4, 2));
    uoSet.add(new ObjectLocation(new Integer(50), 4, 2));
    uoSet.add(new ObjectLocation(new Integer(51), 4, 2));
    uoSet.add(new ObjectLocation(new Integer(52), 4, 2));
    
    // 18 individuals plus 3 extra at 4, 2
    assertEquals(21, list.size());
    assertEquals(new ObjectLocation(new Integer(10), 3, 1), list.get(0));
    assertEquals(new ObjectLocation(new Integer(11), 4, 1), list.get(1));
    assertEquals(new ObjectLocation(new Integer(19), 5, 2), list.get(2));
    assertEquals(new ObjectLocation(new Integer(26), 5, 3), list.get(3));
    assertEquals(new ObjectLocation(null, 5, 4), list.get(4));
    assertEquals(new ObjectLocation(new Integer(32), 4, 4), list.get(5));
    assertEquals(new ObjectLocation(new Integer(38), 3, 5), list.get(6));
    assertEquals(new ObjectLocation(new Integer(30), 2, 4), list.get(7));
    assertEquals(new ObjectLocation(new Integer(29), 1, 4), list.get(8));
    assertEquals(new ObjectLocation(new Integer(22), 1, 3), list.get(9));
    assertEquals(new ObjectLocation(new Integer(15), 1, 2), list.get(10));
    assertEquals(new ObjectLocation(new Integer(9), 2, 1), list.get(11));
    assertEquals(new ObjectLocation(new Integer(17), 3, 2), list.get(12));
    HashSet set = new HashSet(list.subList(13, 17));
    assertEquals(uoSet, set);
    
    assertEquals(new ObjectLocation(new Integer(25), 4, 3), list.get(17));
    assertEquals(new ObjectLocation(new Integer(31), 3, 4), list.get(18));
    assertEquals(new ObjectLocation(new Integer(23), 2, 3), list.get(19));
    assertEquals(new ObjectLocation(new Integer(16), 2, 2), list.get(20));
  }

  public void testHMGGetNeighborsLocNoNull() {
    ArrayList list = hgrid.getNeighborsLoc(3, 3, 2, false);
    HashSet uoSet = new HashSet();
    uoSet.add(new ObjectLocation(new Integer(18), 4, 2));
    uoSet.add(new ObjectLocation(new Integer(50), 4, 2));
    uoSet.add(new ObjectLocation(new Integer(51), 4, 2));
    uoSet.add(new ObjectLocation(new Integer(52), 4, 2));
    
    // 18 individuals plus 3 extra at 4, 2
    assertEquals(20, list.size());
    assertEquals(new ObjectLocation(new Integer(10), 3, 1), list.get(0));
    assertEquals(new ObjectLocation(new Integer(11), 4, 1), list.get(1));
    assertEquals(new ObjectLocation(new Integer(19), 5, 2), list.get(2));
    assertEquals(new ObjectLocation(new Integer(26), 5, 3), list.get(3));
    
    assertEquals(new ObjectLocation(new Integer(32), 4, 4), list.get(4));
    assertEquals(new ObjectLocation(new Integer(38), 3, 5), list.get(5));
    assertEquals(new ObjectLocation(new Integer(30), 2, 4), list.get(6));
    assertEquals(new ObjectLocation(new Integer(29), 1, 4), list.get(7));
    assertEquals(new ObjectLocation(new Integer(22), 1, 3), list.get(8));
    assertEquals(new ObjectLocation(new Integer(15), 1, 2), list.get(9));
    assertEquals(new ObjectLocation(new Integer(9), 2, 1), list.get(10));
    assertEquals(new ObjectLocation(new Integer(17), 3, 2), list.get(11));
    HashSet set = new HashSet(list.subList(12, 16));
    assertEquals(uoSet, set);
    
    assertEquals(new ObjectLocation(new Integer(25), 4, 3), list.get(16));
    assertEquals(new ObjectLocation(new Integer(31), 3, 4), list.get(17));
    assertEquals(new ObjectLocation(new Integer(23), 2, 3), list.get(18));
    assertEquals(new ObjectLocation(new Integer(16), 2, 2), list.get(19));
  }

  public void testHMTGetNeighborsLocNoNull() {
    ArrayList list = htorus.getNeighborsLoc(3, 3, 2, false);
    HashSet uoSet = new HashSet();
    uoSet.add(new ObjectLocation(new Integer(18), 4, 2));
    uoSet.add(new ObjectLocation(new Integer(50), 4, 2));
    uoSet.add(new ObjectLocation(new Integer(51), 4, 2));
    uoSet.add(new ObjectLocation(new Integer(52), 4, 2));
    
    // 18 individuals plus 3 extra at 4, 2
    assertEquals(20, list.size());
    assertEquals(new ObjectLocation(new Integer(10), 3, 1), list.get(0));
    assertEquals(new ObjectLocation(new Integer(11), 4, 1), list.get(1));
    assertEquals(new ObjectLocation(new Integer(19), 5, 2), list.get(2));
    assertEquals(new ObjectLocation(new Integer(26), 5, 3), list.get(3));
    
    assertEquals(new ObjectLocation(new Integer(32), 4, 4), list.get(4));
    assertEquals(new ObjectLocation(new Integer(38), 3, 5), list.get(5));
    assertEquals(new ObjectLocation(new Integer(30), 2, 4), list.get(6));
    assertEquals(new ObjectLocation(new Integer(29), 1, 4), list.get(7));
    assertEquals(new ObjectLocation(new Integer(22), 1, 3), list.get(8));
    assertEquals(new ObjectLocation(new Integer(15), 1, 2), list.get(9));
    assertEquals(new ObjectLocation(new Integer(9), 2, 1), list.get(10));
    assertEquals(new ObjectLocation(new Integer(17), 3, 2), list.get(11));
    HashSet set = new HashSet(list.subList(12, 16));
    assertEquals(uoSet, set);
    
    assertEquals(new ObjectLocation(new Integer(25), 4, 3), list.get(16));
    assertEquals(new ObjectLocation(new Integer(31), 3, 4), list.get(17));
    assertEquals(new ObjectLocation(new Integer(23), 2, 3), list.get(18));
    assertEquals(new ObjectLocation(new Integer(16), 2, 2), list.get(19));
  }

  public void testHMGNeighSideEdgeLoc() {
    ArrayList list = hgrid.getNeighborsLoc(6, 2, true);
    assertEquals(4, list.size());
    assertEquals(new ObjectLocation(new Integer(13), 6, 1), list.get(0));
    assertEquals(new ObjectLocation(new Integer(27), 6, 3), list.get(1));
    assertEquals(new ObjectLocation(new Integer(26), 5, 3), list.get(2));
    assertEquals(new ObjectLocation(new Integer(19), 5, 2), list.get(3));
  }

  public void testHMTNeighSideEdgeLoc() {
    ArrayList list = htorus.getNeighborsLoc(6, 2, true);
    assertEquals(6, list.size());
    assertEquals(new ObjectLocation(new Integer(13), 6, 1), list.get(0));
    assertEquals(new ObjectLocation(new Integer(14), 0, 2), list.get(1));
    assertEquals(new ObjectLocation(new Integer(21), 0, 3), list.get(2));
      
    assertEquals(new ObjectLocation(new Integer(27), 6, 3), list.get(3));
    assertEquals(new ObjectLocation(new Integer(26), 5, 3), list.get(4));
    assertEquals(new ObjectLocation(new Integer(19), 5, 2), list.get(5));
  }
  
  public void testHMGGetNeighborsNoNull() {
    ArrayList list = hgrid.getNeighbors(3, 3, 2, false);
    HashSet uoSet = new HashSet();
    uoSet.add(new Integer(18));
    uoSet.add(new Integer(50));
    uoSet.add(new Integer(51));
    uoSet.add(new Integer(52));
    
    // 18 individuals plus 3 extra at 4, 2
    assertEquals(20, list.size());
    assertEquals(new Integer(10), list.get(0));
    assertEquals(new Integer(11), list.get(1));
    assertEquals(new Integer(19), list.get(2));
    assertEquals(new Integer(26), list.get(3));
    assertEquals(new Integer(32), list.get(4));

    assertEquals(new Integer(38), list.get(5));
    assertEquals(new Integer(30), list.get(6));
    assertEquals(new Integer(29), list.get(7));
    assertEquals(new Integer(22), list.get(8));
    assertEquals(new Integer(15), list.get(9));
    assertEquals(new Integer(9), list.get(10));
    assertEquals(new Integer(17), list.get(11));

    HashSet set = new HashSet(list.subList(12, 16));
    assertEquals(uoSet, set);
    assertEquals(new Integer(25), list.get(16));
    assertEquals(new Integer(31), list.get(17));
    assertEquals(new Integer(23), list.get(18));
    assertEquals(new Integer(16), list.get(19));
  }


  public void testHMTGetNeighborsNoNull() {
    ArrayList list = htorus.getNeighbors(3, 3, 2, false);
    HashSet uoSet = new HashSet();
    uoSet.add(new Integer(18));
    uoSet.add(new Integer(50));
    uoSet.add(new Integer(51));
    uoSet.add(new Integer(52));
    
    // 18 individuals plus 3 extra at 4, 2
    assertEquals(20, list.size());
    assertEquals(new Integer(10), list.get(0));
    assertEquals(new Integer(11), list.get(1));
    assertEquals(new Integer(19), list.get(2));
    assertEquals(new Integer(26), list.get(3));
    assertEquals(new Integer(32), list.get(4));

    assertEquals(new Integer(38), list.get(5));
    assertEquals(new Integer(30), list.get(6));
    assertEquals(new Integer(29), list.get(7));
    assertEquals(new Integer(22), list.get(8));
    assertEquals(new Integer(15), list.get(9));
    assertEquals(new Integer(9), list.get(10));
    assertEquals(new Integer(17), list.get(11));

    HashSet set = new HashSet(list.subList(12, 16));
    assertEquals(uoSet, set);
    assertEquals(new Integer(25), list.get(16));
    assertEquals(new Integer(31), list.get(17));
    assertEquals(new Integer(23), list.get(18));
    assertEquals(new Integer(16), list.get(19));
  }

  public void testHMGNeighSideEdge() {
    ArrayList list = hgrid.getNeighbors(6, 2, true);
    assertEquals(4, list.size());
    assertEquals(new Integer(13), list.get(0));
    assertEquals(new Integer(27), list.get(1));
    assertEquals(new Integer(26), list.get(2));
    assertEquals(new Integer(19), list.get(3));
  }

  public void testHMTNeighSideEdge() {
    ArrayList list = htorus.getNeighbors(6, 2, true);
    assertEquals(6, list.size());
    assertEquals(new Integer(13), list.get(0));
    assertEquals(new Integer(14), list.get(1));
    assertEquals(new Integer(21), list.get(2));
    assertEquals(new Integer(27), list.get(3));
    assertEquals(new Integer(26), list.get(4));
    assertEquals(new Integer(19), list.get(5));
  }

  public void testHMGNeighTopEdge() {
    ArrayList list = htorus.getNeighbors(5, 0, true);
    assertEquals(6, list.size());
    assertEquals(new Integer(47), list.get(0));
    assertEquals(new Integer(48), list.get(1));
    assertEquals(new Integer(6), list.get(2));
    assertEquals(new Integer(12), list.get(3));
    assertEquals(new Integer(4), list.get(4));
    assertEquals(new Integer(46), list.get(5));
  }

  public void testHMTNeighTopEdge() {
    ArrayList list = hgrid.getNeighbors(5, 0, true);
    assertEquals(3, list.size());
    assertEquals(new Integer(6), list.get(0));
    assertEquals(new Integer(12), list.get(1));
    assertEquals(new Integer(4), list.get(2));
  }
    
  
  public void testOList() {
    List l = ogrid.getObjectsAt(10, 10);
    assertEquals(new Integer(3), l.get(0));
    assertEquals(new Integer(4), l.get(1));
    assertEquals(new Integer(50), l.get(2));
  }

  public void testGetAtIndex() {
    assertEquals(new Integer(4), ogrid.getObjectAt(10, 10, 1));
    assertEquals(null, ogrid.getObjectAt(10, 10, 5));
    assertEquals(null, ogrid.getObjectAt(11, 14));
  }

  public void testPutAtIndex() {
    ogrid.putObjectAt(10, 10, 1, new Integer(14));
    assertEquals(new Integer(14), ogrid.getObjectAt(10, 10, 1));
    assertEquals(new Integer(4), ogrid.getObjectAt(10, 10, 2));
  }

  public void testGetFirstLast() {
    OrderedCell c = (OrderedCell)ogrid.getObjectAt(17, 13);
    assertEquals(new Integer(12), c.getFirst());
    assertEquals(new Integer(12), c.getLast());

    c = (OrderedCell)ogrid.getObjectAt(10, 10);
    assertEquals(new Integer(3), c.getFirst());
    assertEquals(new Integer(50), c.getLast());
  }

  public void testGetIndexOf() {
    assertEquals(2, ogrid.getIndexOf(10, 10, new Integer(50)));
    assertEquals(-1, ogrid.getIndexOf(1, 3, new Integer(1)));
  }

  public void testRemoveAt() {
    Object o = ogrid.removeObjectAt(10, 10, 2);
    assertEquals(2, ogrid.getCellSizeAt(10, 10));
    assertEquals(new Integer(50), o);
  }

  public void testRemove() {
    grid.removeObjectAt(10, 10, new Integer(3));
    assertEquals(1, grid.getCellSizeAt(10, 10));
  }

  // multi torus test

  public void testOListT() {
    List l = otorus.getObjectsAt(1, 5);
    assertEquals(new Integer(3), l.get(0));
    assertEquals(new Integer(4), l.get(1));
    assertEquals(new Integer(50), l.get(2));
  }

  public void testGetAtIndexT() {
    assertEquals(new Integer(4), otorus.getObjectAt(-39, 5, 1));
    assertEquals(null, otorus.getObjectAt(1, 5, 5));
    assertEquals(null, otorus.getObjectAt(11, 14));
  }

  public void testPutAtIndexT() {
    otorus.putObjectAt(21, 35, 1, new Integer(14));
    assertEquals(new Integer(14), otorus.getObjectAt(1, 5, 1));
    assertEquals(new Integer(4), otorus.getObjectAt(21, 35, 2));
  }

  public void testGetFirstLastT() {
   
    OrderedCell c = (OrderedCell)otorus.getObjectAt(1, 5);
    assertEquals(new Integer(3), c.getFirst());
    assertEquals(new Integer(50), c.getLast());
  }

  public void testGetIndexOfT() {
    assertEquals(2, otorus.getIndexOf(41, -55, new Integer(50)));
    assertEquals(-1, otorus.getIndexOf(1, 3, new Integer(1)));
  }

  public void testRemoveAtT() {
    Object o = otorus.removeObjectAt(1, 5, 2);
    assertEquals(2, otorus.getCellSizeAt(21, 35));
    assertEquals(new Integer(50), o);
  }

  
  public void testSize() {
    assertEquals(2, grid.getCellSizeAt(10, 10));
    assertEquals(1, grid.getCellSizeAt(17, 13));
    assertEquals(1, grid.getCellSizeAt(13, 22));
  }

  public void testIterator() {
    Iterator i = grid.getIteratorAt(18, 0);
    HashSet resSet = new HashSet();
    resSet.add(new Integer(5));
    resSet.add(new Integer(6));
    while (i.hasNext()) {
      Integer k = (Integer)i.next();
      assertTrue(resSet.contains(k));
      resSet.remove(k);
    }
  }

  public void testGetObjAtT() {
    Cell c = (Cell)torus.getObjectAt(10, 10);
    assertEquals(4, c.size());
    c = (Cell)torus.getObjectAt(10, 40);
    assertEquals(4, c.size());
    c = (Cell)torus.getObjectAt(30, 10);
    assertEquals(4, c.size());
    c = (Cell)torus.getObjectAt(30, 40);
    assertEquals(4, c.size());
    assertEquals(4, torus.getCellSizeAt(10, 40));
    c = torus.getCellAt(30, 40);
    assertEquals(4, c.size());
    c = (Cell)torus.getObjectAt(1, 5);
    assertEquals(2, c.size());
  }

  public void testGetListT() {
    List l = torus.getObjectsAt(21, 5);
    assertEquals(2, l.size());
    l = torus.getObjectsAt(0, 0);
    assertEquals(0, l.size());
    
  }

  public void testGetIterT() {
    Iterator i = torus.getIteratorAt(-39, -55);
    
    int c = 0;
    while (i.hasNext()) {
      i.next();
      c++;
    }
    
    assertEquals(2, c);

    i = torus.getIteratorAt(0, 0);
    c = 0;
    while (i.hasNext()) {
      i.next();
      c++;
    }

    assertEquals(0, c);
    
  }

  public void testList() {
    List l = grid.getObjectsAt(10, 10);
    assertEquals(2, l.size());
    l = grid.getObjectsAt(0, 0);
    assertEquals(0, l.size());
  }

  public void testIter() {
    Iterator i = grid.getIteratorAt(10, 10);
    int count = 0;
    while (i.hasNext()) {
      count++;
      i.next();
    }

    assertEquals(2, count);

    count = 0;
    i = grid.getIteratorAt(0, 0);
    while (i.hasNext()) {
      count++;
      i.next();
    }

    assertEquals(0, count);
  }

  public void testGetVNLoc() {
    ArrayList l = grid.getVNNeighborsLoc(18, 1, true);
    assertEquals(5, l.size());
    ObjectLocation ol = (ObjectLocation)l.get(0);
    assertEquals(null, ol.obj);
    assertEquals(17, ol.x);
    assertEquals(1, ol.y);

    ol = (ObjectLocation)l.get(1);
    assertEquals(new Integer(9), ol.obj);
    assertEquals(19, ol.x);
    assertEquals(1, ol.y);

    ol = (ObjectLocation)l.get(2);
    assertEquals(new Integer(6), ol.obj);
    assertEquals(18, ol.x);
    assertEquals(0, ol.y);

    ol = (ObjectLocation)l.get(3);
    assertEquals(new Integer(5), ol.obj);
    assertEquals(18, ol.x);
    assertEquals(0, ol.y);

    ol = (ObjectLocation)l.get(4);
    assertEquals(new Integer(7), ol.obj);
    assertEquals(18, ol.x);
    assertEquals(2, ol.y);

    l = grid.getVNNeighborsLoc(18, 1, false);
    assertEquals(4, l.size());

    l = grid.getVNNeighborsLoc(18, 1, 3, 2, false);
    assertEquals(5, l.size());

    l = grid.getVNNeighborsLoc(18, 1, 3, 2, true);
    assertEquals(8, l.size());

    //uchicago.src.debug.DebugMethods.printCollection(l);

    l = grid.getVNNeighborsLoc(19, 1, true);
    assertEquals(5, l.size());

    // uchicago.src.debug.DebugMethods.printCollection(l);
    l = grid.getVNNeighborsLoc(17, 1, false);
    // uchicago.src.debug.DebugMethods.printCollection(l);
    assertEquals(4, l.size());
    
  }

  public void testMooreLoc() {

    ArrayList l = grid.getMooreNeighborsLoc(18, 1, true);
    assertEquals(12, l.size());
    
    ObjectLocation ol = (ObjectLocation)l.get(5);
    assertEquals(new Integer(9), ol.obj);
    assertEquals(19, ol.x);
    assertEquals(1, ol.y);

    l = grid.getMooreNeighborsLoc(18, 1, 3, 2, false);
    assertEquals(12, l.size());

    ol = (ObjectLocation)l.get(4);
    assertEquals(new Integer(3), ol.obj);
    assertEquals(16, ol.x);
    assertEquals(1, ol.y);

    l = grid.getMooreNeighborsLoc(18, 1, 3, 2, true);
    //uchicago.src.debug.DebugMethods.printCollection(l);
    assertEquals(23, l.size());
    ol = (ObjectLocation)l.get(4);
    assertEquals(new Integer(5), ol.obj);
    assertEquals(18, ol.x);
    assertEquals(0, ol.y);
  }

  public void testVNNeighbors() {
    ArrayList l = grid.getVNNeighbors(18, 1, true);
    assertEquals(5, l.size());
    assertEquals(null, l.get(0));
    assertEquals(new Integer(9), l.get(1));
    assertEquals(new Integer(6), l.get(2));
    assertEquals(new Integer(5), l.get(3));
    assertEquals(new Integer(7), l.get(4));
    
    l = grid.getVNNeighbors(18, 1, false);
    assertEquals(4, l.size());

    l = grid.getVNNeighbors(18, 1, 3, 2, false);
    assertEquals(5, l.size());

    l = grid.getVNNeighbors(18, 1, 3, 2, true);
    assertEquals(8, l.size());
    
    l = grid.getVNNeighbors(19, 1, true);
    assertEquals(5, l.size());
    l = grid.getVNNeighbors(17, 1, false);
    assertEquals(4, l.size());
  }

  public void testMooreNeighbors() {
    ArrayList l = grid.getMooreNeighbors(18, 1, true);
    assertEquals(12, l.size());
    assertEquals(new Integer(9), l.get(5));
    
    l = grid.getMooreNeighbors(18, 1, 3, 2, false);
    assertEquals(12, l.size());
    assertEquals(new Integer(3), l.get(4));
    
    l = grid.getMooreNeighbors(18, 1, 3, 2, true);
    assertEquals(23, l.size());
    assertEquals(new Integer(5), l.get(4));
  }

  public void testGetVNLocT() {
    HashSet set = new HashSet();
    set.add(new Integer(5));
    set.add(new Integer(-1));
    set.add(new Integer(6));
     
    
    
    ArrayList l = torus.getVNNeighborsLoc(18, 1, true);
    assertEquals(6, l.size());
    ObjectLocation ol = (ObjectLocation)l.get(0);
    assertEquals(null, ol.obj);
    assertEquals(17, ol.x);
    assertEquals(1, ol.y);

    ol = (ObjectLocation)l.get(1);
    assertEquals(new Integer(9), ol.obj);
    assertEquals(19, ol.x);
    assertEquals(1, ol.y);

    ol = (ObjectLocation)l.get(2);
    Integer val = (Integer)ol.obj;
    assertTrue(set.contains(val));
    set.remove(val);
    assertEquals(18, ol.x);
    assertEquals(0, ol.y);

    ol = (ObjectLocation)l.get(3);
    val = (Integer)ol.obj;
    assertTrue(set.contains(val));
    set.remove(val);
    assertEquals(18, ol.x);
    assertEquals(0, ol.y);

    ol = (ObjectLocation)l.get(4);
    val = (Integer)ol.obj;
    assertTrue(set.contains(val));
    set.remove(val);
    assertEquals(18, ol.x);
    assertEquals(0, ol.y);

    l = torus.getVNNeighborsLoc(18, 1, false);
    assertEquals(5, l.size());

    l = torus.getVNNeighborsLoc(18, 1, 3, 2, false);
    assertEquals(7, l.size());

    l = torus.getVNNeighborsLoc(18, 1, 3, 2, true);
    assertEquals(12, l.size());
    l = torus.getVNNeighborsLoc(19, 1, true);
    assertEquals(6, l.size());

    // uchicago.src.debug.DebugMethods.printCollection(l);
    l = torus.getVNNeighborsLoc(17, 1, false);
    // uchicago.src.debug.DebugMethods.printCollection(l);
    assertEquals(4, l.size());
    
  }

  public void testMooreLocT() {

    HashSet set = new HashSet();
    set.add(new Integer(5));
    set.add(new Integer(-1));
    set.add(new Integer(6));
    
    ArrayList l = torus.getMooreNeighborsLoc(18, 1, true);
    assertEquals(13, l.size());
    
    ObjectLocation ol = (ObjectLocation)l.get(3);
    Integer val = (Integer)ol.obj;
    assertTrue(set.contains(val));
    set.remove(val);
    assertEquals(18, ol.x);
    assertEquals(0, ol.y);

    l = torus.getMooreNeighborsLoc(18, 1, 3, 2, false);
    assertEquals(15, l.size());

    
    ol = (ObjectLocation)l.get(7);
    assertEquals(new Integer(14), ol.obj);
    assertEquals(0, ol.x);
    assertEquals(1, ol.y);
    
   
    l = torus.getMooreNeighborsLoc(18, 1, 3, 2, true);
    //uchicago.src.debug.DebugMethods.printCollection(l);
    assertEquals(39, l.size());

    ol = (ObjectLocation)l.get(7);
    assertEquals(null, ol.obj);
    assertEquals(15, ol.x);
    assertEquals(0, ol.y);
    
  }

  public void testVNNeighborsT() {
    ArrayList l = torus.getVNNeighbors(18, 1, true);
    assertEquals(6, l.size());
    assertEquals(null, l.get(0));
    assertEquals(new Integer(9), l.get(1));

    // we need to do contains rather than iterate the list here
    // as we working with a bag cell.
    assertTrue(l.contains(new Integer(6)));
    assertTrue(l.contains(new Integer(5)));
    assertTrue(l.contains(new Integer(-1)));
    
    assertEquals(new Integer(7), l.get(5));
    
    
    l = torus.getVNNeighbors(18, 1, false);
    assertEquals(5, l.size());

    l = torus.getVNNeighbors(18, 1, 3, 2, false);
    assertEquals(7, l.size());

    l = torus.getVNNeighbors(18, 1, 3, 2, true);
    assertEquals(12, l.size());
    
    l = torus.getVNNeighbors(19, 1, true);
    assertEquals(6, l.size());
    l = torus.getVNNeighbors(17, 1, false);
    assertEquals(4, l.size());
  }

  public void testMooreNeighborsT() {
    ArrayList l = torus.getMooreNeighbors(18, 1, true);
   
    assertEquals(13, l.size());
    Object val = l.get(3);

    // these are not returned in order from a Bag cell so we don't know
    // which will be returned.
    assertTrue(val.equals(new Integer(5)) || val.equals(new Integer(-1)) ||
	       val.equals(new Integer(6)));
    
    l = torus.getMooreNeighbors(18, 1, 3, 2, false);
    assertEquals(15, l.size());
    assertEquals(new Integer(14), l.get(7));
    
    l = torus.getMooreNeighbors(18, 1, 3, 2, true);
    assertEquals(39, l.size());
    assertEquals(null, l.get(7));
  }

  
  public void testGetCellAtT() {
    Cell c = torus.getCellAt(41, 5);
    assertEquals(2, c.size());
  }
  
  public static Test suite() {
    return new TestSuite(uchicago.src.sim.space.MultiGridTest.class);
  }
}
