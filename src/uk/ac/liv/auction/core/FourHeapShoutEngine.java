package uk.ac.liv.auction.core;

import uk.ac.liv.auction.agent.TraderAgent;

import uk.ac.liv.util.BinaryHeap;
import uk.ac.liv.util.FastBinaryHeap;
import uk.ac.liv.util.QueueDisassembler;
import uk.ac.liv.util.PriorityQueue;
import uk.ac.liv.util.Debug;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Comparator;

import java.io.PrintStream;
import java.io.Serializable;

/**
 * <p>
 * This class provides auction shout management services using the 4-Heap algorithm
 * presented in the paper:
 * </p>
 *
 * <p>
 * "Flexible Double Auctions for Electronic Commerce: Theory and Implementation"
 * by Wurman, Walsh amnd Wellman 1998.
 * </p>
 *
 * <p>
 * All state is maintained in memory resident data structures and no crash recovery
 * is provided.
 * </p>
 */

public class FourHeapShoutEngine implements ShoutEngine, Serializable {

  /**
   * Matched bids in ascending order
   */
  protected BinaryHeap bIn;

  /**
   * Unmatched bids in descending order
   */
  protected BinaryHeap bOut;

  /**
   * Matched asks in descending order
   */
  protected BinaryHeap sIn;

  /**
   * Unmatched asks in ascending order
   */
  protected BinaryHeap sOut;

  protected static AscendingShoutComparator greaterThan =
    new AscendingShoutComparator();

  protected static DescendingShoutComparator lessThan =
    new DescendingShoutComparator();


  public FourHeapShoutEngine() {
    initialise();
  }

  public synchronized void removeShout( Shout shout ) {
    checkBalanced();
    if ( shout.isAsk() ) {
      removeAsk(shout);
    } else {
      removeBid(shout);
    }
    checkBalanced();
  }

  protected void removeAsk( Shout shout ) {
    if ( sIn.remove(shout) ) {
      reinsert(bIn, shout.getQuantity());
    } else {
      sOut.remove(shout);
    }
  }

  protected void removeBid( Shout shout ) {
    if ( bIn.remove(shout) ) {
      reinsert(sIn, shout.getQuantity());
    } else {
      bOut.remove(shout);
    }
  }

  public String toString() {
    return "sIn = " + sIn + "\nbIn = " + bIn + "\nsOut = " + sOut + "\nbOut = " + bOut;
  }

  /**
   * Log the current state of the auction.
   */
  public void printState() {
    //checkBalanced();
    System.out.println(this.toString());
  }


  /**
   * Insert a shout into a binary heap.
   *
   * @param heap  The heap to insert into
   * @param shout The shout to insert
   *
   */
  private static void insertShout( BinaryHeap heap, Shout shout ) throws DuplicateShoutException {
    try {
      heap.insert(shout);
    } catch ( IllegalArgumentException e ) {
      throw new DuplicateShoutException("Duplicate shout: " + shout.toString());
    }
  }


  /**
   * Insert a matched ask into the appropriate heap
   */
  private void insertMatchedAsk( Shout ask ) throws DuplicateShoutException {
    insertShout(sIn, ask);
  }

  /**
   * Insert a matched bid into the appropriate heap.
   */
  private void insertMatchedBid( Shout bid ) throws DuplicateShoutException {
    insertShout(bIn, bid);
  }

  /**
   * Insert an unmatched ask into the approriate heap.
   */
  public void insertUnmatchedAsk( Shout ask ) throws DuplicateShoutException {
    insertShout(sOut, ask);
  }

  /**
   * Insert an unmatched bid into the approriate heap.
   */
  public void insertUnmatchedBid( Shout bid ) throws DuplicateShoutException {
    insertShout(bOut, bid);
  }

  /**
   * Get the highest unmatched bid.
   */
  public Shout getHighestUnmatchedBid() {
    return (Shout) bOut.getFirst();
  }


  /**
   * Get the lowest matched bid
   */
  public Shout getLowestMatchedBid() {
    return (Shout) bIn.getFirst();
  }

  /**
   * Get the lowest unmatched ask.
   */
  public Shout getLowestUnmatchedAsk() {
    return (Shout) sOut.getFirst();
  }

  /**
   * Get the highest matched ask.
   */
  public Shout getHighestMatchedAsk() {
    return (Shout) sIn.getFirst();
  }

  /**
   * Unify the shout at the top of the heap with the supplied shout,
   * so that quantity(shout) = quantity(top(heap)).  This is achieved
   * by splitting the supplied shout or the shout at the top of the heap.
   *
   * @param shout The shout.
   * @param heap  The heap.
   *
   * @return A reference to the, possibly modified, shout.
   *
   */
  protected static Shout unifyShout( Shout shout, BinaryHeap heap ) {

    Shout top = (Shout) heap.getFirst();

    if ( shout.getQuantity() > top.getQuantity() ) {
      shout = shout.splat( shout.getQuantity() - top.getQuantity() );
    } else {
      if ( top.getQuantity() > shout.getQuantity() ) {
        Shout remainder = top.split( top.getQuantity() - shout.getQuantity() );
        heap.insert(remainder);
      }
    }

    return shout;
  }

  protected int displaceShout( Shout shout, BinaryHeap from, BinaryHeap to ) throws DuplicateShoutException {
    shout = unifyShout(shout, from);
    from.transfer(to);
    from.insert(shout);
    return shout.getQuantity();
  }

  public int promoteShout( Shout shout, BinaryHeap from, BinaryHeap to,
                            BinaryHeap matched ) throws DuplicateShoutException {

    shout = unifyShout(shout, from);
    matched.insert(shout);
    from.transfer(to);
    return shout.getQuantity();
  }

  public int displaceHighestMatchedAsk( Shout ask ) throws DuplicateShoutException {
    return displaceShout(ask, sIn, sOut);
  }

  public int displaceLowestMatchedBid( Shout bid ) throws DuplicateShoutException {
    return displaceShout(bid, bIn, bOut);
  }

  public int promoteHighestUnmatchedBid( Shout ask ) throws DuplicateShoutException {
    return promoteShout(ask, bOut, bIn, sIn);
  }

  public int promoteLowestUnmatchedAsk( Shout bid ) throws DuplicateShoutException {
    return promoteShout(bid, sOut, sIn, bIn);
  }

  public void newBid( Shout bid ) throws DuplicateShoutException {

    double bidVal = bid.getPrice();

    int uninsertedUnits = bid.getQuantity();

    while ( uninsertedUnits > 0 ) {

      Shout sOutTop = getLowestUnmatchedAsk();
      Shout bInTop = getLowestMatchedBid();

      if ( sOutTop != null
            && bidVal >= sOutTop.getPrice()
            && (bInTop == null || bInTop.getPrice() >= sOutTop.getPrice()) ) {

        // found match
        uninsertedUnits -= promoteLowestUnmatchedAsk(bid);

      } else if ( bInTop != null && bidVal > bInTop.getPrice() ) {

        uninsertedUnits -= displaceLowestMatchedBid(bid);

      } else {
        insertUnmatchedBid(bid);
        uninsertedUnits -= bid.getQuantity();
      }

    }
  }


  public void newAsk( Shout ask ) throws DuplicateShoutException {

    double askVal = ask.getPrice();

    int uninsertedUnits = ask.getQuantity();

    while ( uninsertedUnits > 0 ) {

      Shout sInTop = getHighestMatchedAsk();
      Shout bOutTop = getHighestUnmatchedBid();

      if ( bOutTop != null
          && askVal <= bOutTop.getPrice()
          && (sInTop == null || sInTop.getPrice() <= bOutTop.getPrice()) ) {

        uninsertedUnits -= promoteHighestUnmatchedBid(ask);

      } else if ( sInTop != null && askVal <= sInTop.getPrice() ) {

        uninsertedUnits -= displaceHighestMatchedAsk(ask);

      } else {

        insertUnmatchedAsk(ask);
        uninsertedUnits -= ask.getQuantity();

      }
    }
  }

  public void newShout( Shout shout ) throws DuplicateShoutException {
    if ( shout.isAsk() ) {
      newAsk(shout);
    } else {
      newBid(shout);
    }
  }


  protected Iterator matchedBidDisassembler() {
    return new QueueDisassembler(bIn);
  }

  protected Iterator matchedAskDisassembler() {
    return new QueueDisassembler(sIn);
  }


  /**
   * <p>
   * Return a list of matched bids and asks.  The list is of the form
   * </p><br>
   *
   *   ( b0, a0, b1, a1 .. bn, an )<br>
   *
   * <p>
   * where bi is the ith bid and a0 is the ith ask.  A typical auctioneer would
   * clear by matching bi with ai for all i at some price.</p>
   */
  public List getMatchedShouts() {
    LinkedList result = new LinkedList();
    while ( ! sIn.isEmpty() ) {
      Debug.assert("count(bIn) != count(sIn)", ! bIn.isEmpty());
      Shout sInTop = (Shout) sIn.removeFirst();
      Shout bInTop = (Shout) bIn.removeFirst();
      int nS = sInTop.getQuantity();
      int nB = bInTop.getQuantity();
      if ( nS < nB ) {
        // split the bid
        Shout remainder = bInTop.split(nB-nS);
        bIn.insert(remainder);
      } else if ( nB < nS ) {
        // split the ask
        Shout remainder = sInTop.split(nS-nB);
        sIn.insert(remainder);
      }
      result.add(bInTop);
      result.add(sInTop);
    }
    return result;
  }

  protected void initialise() {
    bIn   = new FastBinaryHeap(greaterThan);
    bOut  = new FastBinaryHeap(lessThan);
    sIn   = new FastBinaryHeap(lessThan);
    sOut  = new FastBinaryHeap(greaterThan);
  }

  public synchronized void reset() {
    initialise();
  }

  /**
   * Sub-classes should override this method if they wish
   * to check auction state integrity after
   * shout insertion/removal.  This is useful for testing/debugging.
   */
  protected void checkBalanced() {
    // Do nothing
  }

  /**
   * Remove, possibly several, shouts from heap such that
   * quantity(heap) is reduced by the supplied quantity
   * and reinsert the shouts using the standard insertion
   * logic.  quantity(heap) is defined as the total quantity
   * of every shout in the heap.
   *
   * @param heap      The heap to remove shouts from.
   * @param quantity  The total quantity to remove.
   */
  protected void reinsert( BinaryHeap heap, int quantity ) {

    while ( quantity > 0 ) {

      Shout top = (Shout) heap.removeFirst();

      if ( top.getQuantity() > quantity ) {
        heap.insert( top.split(top.getQuantity() - quantity) );
      }

      quantity -= top.getQuantity();

      try {
        if ( top.isBid() ) {
          newBid(top);
        } else {
          newAsk(top);
        }
      } catch ( DuplicateShoutException e ) {
        throw new AuctionError("Invalid auction state");
      }
    }

  }

}