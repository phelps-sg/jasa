/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 */

package test.uk.ac.liv.util;

import junit.framework.*;

import uk.ac.liv.util.*;

import java.util.Random;
import java.util.Iterator;
import java.util.LinkedList;

public class BinaryHeapTest extends TestCase {

  BinaryHeap h1;

  public BinaryHeapTest( String name ) {
    super(name);
  }

  public void setUp() {

    h1 = new BinaryHeap();

    h1.insert(new Integer(1));
    h1.insert(new Integer(3));
    h1.insert(new Integer(9));
    h1.insert(new Integer(3));
    h1.insert(new Integer(5));
    h1.insert(new Integer(7));
  }

  public void test() {
    System.out.println("h1 = " + h1);
    assertTrue( h1.contains(new Integer(3)) );
    assertTrue( h1.contains(new Integer(9)) );
    assertTrue( h1.contains(new Integer(1)) );
    assertTrue( h1.contains(new Integer(5)) );
    assertTrue( !h1.contains(new Integer(10)) );
    assertTrue( !h1.contains(new Integer(-1)) );
    Object x = h1.removeFirst();
    System.out.println("h1 after removing first = " + h1);
    checkOrder(h1);
    assertTrue( ((Integer) x).equals(new Integer(1)));
    assertTrue( !h1.contains(new Integer(1)) );
    assertTrue( h1.contains(new Integer(3)) );
    assertTrue( h1.contains(new Integer(9)) );
    assertTrue( h1.contains(new Integer(5)) );
    h1.remove(new Integer(9));
    System.out.println("h1 after removing 9 = " + h1);
    assertTrue( h1.contains(new Integer(3)) );
    assertTrue( !h1.contains(new Integer(9)) );
    assertTrue( h1.remove( new Integer(3) ) );
    System.out.println("h1 after removing 3 = " + h1);
    // assertTrue( ! h1.contains(new Integer(3)) );
    x = h1.removeFirst();
    System.out.println("h1 after removing first = " + h1);
    h1.removeFirst();
    System.out.println("h1 after removing first = " + h1);
    assertTrue( h1.remove( new Integer(7) ) );
    System.out.println("h1 after removing 7 = " + h1);
    assertTrue( h1.isEmpty() );
    assertTrue( ! h1.remove( new Integer(7) ) );
    h1.add( new Integer(666) );
    h1.add( new Integer(667) );
    assertTrue( h1.remove(new Integer(667)) );
    assertTrue( h1.size() == 1 );
    assertTrue( ! h1.contains(new Integer(667)) );
    assertTrue( h1.remove(new Integer(666)) );

  }


  public void checkOrder( BinaryHeap h ) {
    LinkedList l = new LinkedList();
    Iterator it = new QueueDisassembler(h);
    Integer lastNum = null;
    while ( it.hasNext() ) {
      Integer num = (Integer) it.next();
      assertTrue( lastNum == null || num.intValue() >= lastNum.intValue() );
      lastNum = num;
      l.add(num);
    }
    it = l.iterator();
    while ( it.hasNext() ) {
      h.add( it.next() );
    }
  }

  public void testRandom() {
    Random randGenerator = new Random();
    for( int i=0; i<1000; i++ ) {
      BinaryHeap h = new BinaryHeap();
      for( int r=0; r<100; r++ ) {
        h.add( new Integer( randGenerator.nextInt(100)) );
      }
      for( int r=0; r<20; r++ ) {
        h.remove( new Integer(r) );
        //h.removeFirst();
        h.add( new Integer( randGenerator.nextInt(100) ));
      }
      checkOrder(h);
    }
  }


  public static void main( String[] args ) {
    junit.textui.TestRunner.run (suite());
  }

  public static Test suite() {
    return new TestSuite(BinaryHeapTest.class);
  }

}