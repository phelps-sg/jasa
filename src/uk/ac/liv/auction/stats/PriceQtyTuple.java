package uk.ac.liv.auction.stats;

public class PriceQtyTuple {

  public double minPrice, maxPrice;
  long quantity;

  public PriceQtyTuple() {
  }

  public PriceQtyTuple( double minPrice, double maxPrice, long quantity ) {
    this.minPrice = minPrice;
    this.maxPrice = maxPrice;
    this.quantity = quantity;
  }

  public String toString() {
    return "(" + getClass() + " minPrice:" + minPrice + " maxPrice:" + maxPrice + " quantity:" + quantity + ")";
  }
}
