package uk.ac.liv.auction.core;

import java.util.Comparator;

import java.io.Serializable;

/**
 * A comparator that can be used for arranging shouts in descending
 * order, i.e. highest price first.
 *
 * @author Steve Phelps
 */

public class DescendingShoutComparator implements Comparator, Serializable {

  public DescendingShoutComparator() {
  }

  public int compare(Object parm1, Object parm2) {
    Shout shout1 = (Shout) parm1;
    Shout shout2 = (Shout) parm2;
    return shout2.compareTo(shout1);
  }
}