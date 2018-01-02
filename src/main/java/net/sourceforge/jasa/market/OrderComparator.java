package net.sourceforge.jasa.market;

import java.util.Comparator;

public abstract class OrderComparator implements Comparator<Order> {

	protected int priceDirection;
	
	public OrderComparator(int priceDirection) {
		super();
		this.priceDirection = priceDirection;
	}

	public int comparePrices(Order shout1, Order shout2) {
		return shout1.price.compareTo(shout2.price) * priceDirection;
//		double p1 = shout1.price * priceDirection;
//		double p2 = shout2.price * priceDirection;
//		if (p1 > p2) {
//			return 1;
//		} else if (p1 < p2) {
//			return -1;
//		} else {
//			return 0;
//		}
	}
	
	public int compareQuantities(Order shout1, Order shout2) {
		if (shout1.quantity < shout2.quantity) {
			return 1;
		} else if (shout1.quantity > shout2.quantity) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public int compareTimeStamps(Order shout1, Order shout2) {
		if (shout1.timeStamp != null && shout1.timeStamp.getTicks() < shout2.timeStamp.getTicks()) {
			return 1;
		} else if (shout1.timeStamp != null && shout1.timeStamp.getTicks() > shout2.timeStamp.getTicks()) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	public int compare(Order shout1, Order shout2) {
		int priceComparision = comparePrices(shout1, shout2);
		if (priceComparision != 0) {
			return priceComparision;
		} else {
			int quantityComparision = compareQuantities(shout1, shout2);
			if (quantityComparision != 0) {
				return quantityComparision;
			} else {
				return compareTimeStamps(shout1, shout2);
			}
		}
	}


}
