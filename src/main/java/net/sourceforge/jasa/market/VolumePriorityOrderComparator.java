package net.sourceforge.jasa.market;

public class VolumePriorityOrderComparator extends OrderComparator {

	public VolumePriorityOrderComparator(int priceDirection) {
		super(priceDirection);
	}

	@Override
	public int compare(Order shout1, Order shout2) {
			int quantityComparision = compareQuantities(shout1, shout2);
			if (quantityComparision != 0) {
				return quantityComparision;
			} else {
				int priceComparision = comparePrices(shout1, shout2);
				if (priceComparision != 0) {
					return priceComparision;
				} else {
					return compareTimeStamps(shout1, shout2);
			}
		}
	}
	
	

}
