package uk.ac.liv.auction.core;

/**
 * @author Steve Phelps
 */

public class DuplicateShoutException extends IllegalShoutException {

  public DuplicateShoutException( String message ) {
    super(message);
  }

}