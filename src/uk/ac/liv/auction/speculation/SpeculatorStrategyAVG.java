/**
 * adapted from uk.ac.liv.auction.agent.TruthTellingStrategy;
 */
package uk.ac.liv.auction.speculation;

import java.io.Serializable;

import uk.ac.liv.auction.agent.AbstractTradingAgent;
import uk.ac.liv.auction.agent.FixedQuantityStrategyImpl;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.stats.EquilibriumReport;

/**
 * @author moyaux
 * 
 */
public class SpeculatorStrategyAVG extends FixedQuantityStrategyImpl implements
    Serializable {

	private int margin = 1;

	private double previousPrice = 0;

	private double forecastedPrice = 0;

	private float forecastedPriceCoefficient = 0; // in [0 ; 1]

	public SpeculatorStrategyAVG(AbstractTradingAgent agent) {
		super(agent);
	}

	public SpeculatorStrategyAVG() {
		super();
	}

	public boolean modifyShout(Shout.MutableShout shout) {

		// retrieve current equilibrium price (taken from
		// uk.ac.liv.auction.agent.EquilibriumPriceStrategy)
		EquilibriumReport eqReport = new EquilibriumReport(
		    (RoundRobinAuction) auction);
		eqReport.calculate();
		double currentPrice = eqReport.calculateMidEquilibriumPrice();

		// update forecasted price
		forecastedPrice = forecastedPriceCoefficient * forecastedPrice
		    + (1 - forecastedPriceCoefficient) * currentPrice;

		// if the speculator decides to buy, it buys as much as it can
		if (previousPrice < forecastedPrice * (1 - margin)) {
			if (agent.getFunds() > 0) {
				agent.setIsSeller(false);
				shout.setPrice(previousPrice * (1 + margin));
				shout.setQuantity((int) (currentPrice / agent.getFunds()));
			} else {
				shout.setQuantity(0);
			}
		}

		// if the speculator decides to sell, it sells everything
		if (previousPrice < forecastedPrice * (1 + margin)) {
			if (agent.getStock() > 0) {
				agent.setIsSeller(true);
				shout.setPrice(previousPrice * (1 - margin));
				shout.setQuantity(agent.getStock());
			} else {
				shout.setQuantity(0);
			}
		}

		// System.out.println(agent.getFunds() + " / " + agent.getStock() );

		return super.modifyShout(shout);
	}

	public void endOfRound(Auction auction) {
		// update forecasted equilibrium price (taken from
		// uk.ac.liv.auction.agent.EquilibriumPriceStrategy)
		EquilibriumReport eqReport = new EquilibriumReport(
		    (RoundRobinAuction) auction);
		eqReport.calculate();
		previousPrice = eqReport.calculateMidEquilibriumPrice();
	}
}
