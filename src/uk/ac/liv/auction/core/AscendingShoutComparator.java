package uk.ac.liv.auction.core;

import java.util.Comparator;

import java.io.Serializable;


/**
 * This class can be used to as a Comparator to rank shouts
 * in ascending order.
 *
 * @author Steve Phelps
 *
 */

public class AscendingShoutComparator implements Comparator, Serializable {

  public AscendingShoutComparator() {
  }

  public int compare(Object parm1, Object parm2) {
    Shout shout1 = (Shout) parm1;
    Shout shout2 = (Shout) parm2;
    return shout1.compareTo(shout2);
  }
}