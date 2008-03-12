/**
 * 
 */
package uk.ac.liv.auction.speculation;

import java.io.Serializable;

import uchicago.src.sim.util.Random;
import uk.ac.liv.auction.agent.AbstractTradingAgent;
import uk.ac.liv.auction.agent.TradingAgent;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.stats.EquilibriumReport;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author moyaux
 * 
 */
public class SimpleTradingAgent extends AbstractTradingAgent implements
    Serializable {

	public double dailyConsumption = 1;

	public double previousEqPrice = 0;

	public double b00 = 0;

	public double b01 = 0;

	public double b0inf = 0;

	public double skillF = 0;

	public double skillG = 0;

	public double reserve = 0; // r[i]

	// public double food = 0; //f[i]
	// public double gold = 0; //g[i]

	public static final String P_B00 = "b00";

	public static final String P_B01 = "b01";

	public static final String P_B0INF = "b0inf";

	public static final String P_SKILL_FOOD_MIN = "skillfoodmin";

	public static final String P_SKILL_FOOD_MAX = "skillfoodmax";

	public static final String P_SKILL_GOLD_MIN = "skillgoldmin";

	public static final String P_SKILL_GOLD_MAX = "skillgoldmax";

	public static final String P_RESERVE_MIN = "reserveMin";

	public static final String P_RESERVE_MAX = "reserveMax";

	public static final String P_INITIAL_FOOD = "initialfood";

	public static final String P_INITIAL_GOLD = "initialgold";

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.liv.auction.agent.AbstractTradingAgent#equilibriumProfits(uk.ac.liv.auction.core.Auction,
	 *      double, int)
	 */
	public double equilibriumProfits(Auction auction, double equilibriumPrice,
	    int quantity) {

		double surplus = 0;
		if (isSeller) {
			surplus = equilibriumPrice - getValuation(auction);
		} else {
			surplus = getValuation(auction) - equilibriumPrice;
		}
		// TODO
		if (surplus < 0) {
			surplus = 0;
		}
		return auction.getRound() * quantity * surplus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.liv.auction.agent.AbstractTradingAgent#active()
	 */
	public boolean active() {
		return true;
	}

	public void requestShout(Auction auction) {
		// if (getId()==0) System.out.println("DAY in
		// requestShout="+auction.getDay());

		produce();

		// agents consume 1 unit of food a day (cf. bottom p.3 of MBC book)
		stock.remove((int) dailyConsumption);

		/*
		 * MBC book p5: "when the food inventory is below reserve, bid (calculated
		 * by _PreviousPriceValuer) yields an offer price above previous price ==>
		 * agent is here a seller
		 */
		if (getStock() > reserve) {
			isSeller = false;
			// if(getId() == 2) System.out.println("Agent "+getId()+" tries to
			// buy");
		}// if

		if (getStock() < reserve) {
			isSeller = true;
			// if(getId() == 2) System.out.println("Agent "+getId()+" tries to
			// sell");
		}// if

		super.requestShout(auction);
	}// requestShout

	public void setup(ParameterDatabase parameters, Parameter base) {

		super.setup(parameters, base);

		// initialize Random.uniform
		Random.createUniform();

		b00 = parameters.getInt(base.push(P_B00), null);
		b01 = parameters.getInt(base.push(P_B01), null);
		b0inf = parameters.getInt(base.push(P_B0INF), null);

		int min = parameters.getInt(base.push(P_SKILL_FOOD_MIN), null);
		int max = parameters.getInt(base.push(P_SKILL_FOOD_MAX), null);
		skillF = Random.uniform.nextIntFromTo(min, max);

		min = parameters.getInt(base.push(P_SKILL_GOLD_MIN), null);
		max = parameters.getInt(base.push(P_SKILL_GOLD_MAX), null);
		skillG = Random.uniform.nextIntFromTo(min, max);

		min = parameters.getInt(base.push(P_RESERVE_MIN), null);
		max = parameters.getInt(base.push(P_RESERVE_MAX), null);
		reserve = Random.uniform.nextIntFromTo(min, max);

		// transfert b00, b01, ... skillF/G... reserve to the valuer
		valuer.setAgent(this);
		valuer.reset();

	}// setup

	public double updateEquilibriumPrice(Auction auction) {
		EquilibriumReport eqReport = new EquilibriumReport(
		    (RandomRobinAuction) auction);
		eqReport.calculate();
		previousEqPrice = eqReport.calculateMidEquilibriumPrice();
		// System.out.println("simpleTradingAgent.updateEquilibriumPrice: new
		// price for agent "+getId()+"="+previousEqPrice);
		// System.out.println(toString(auction));
		return previousEqPrice;
	}// updateEquilirbiumPrice

	public void informOfSeller(Auction auction, Shout winningShout,
	    TradingAgent seller, double price, int quantity) {
		// TODO
		// super.informOfSeller(auction, winningShout, seller, price, quantity);

		if (((SimpleTradingAgent) seller).acceptDeal(auction, price, quantity)) {
			// TODO
			// purchaseFrom(auction, (SimpleTradingAgent) seller, quantity,
			// price);
			// if(getId() == 2) System.out.println("Agent "+getId()+" buys
			// "+quantity+" items from agent
			// "+((SimpleTradingAgent)seller).getId()+" for $"+price+"/item.");
		} else { // this else shouldn't occur
			assert ((SimpleTradingAgent) seller).acceptDeal(auction, price, quantity);
			// if(getId() == 2) System.out.println("Agent "+getId()+" REJECTS to
			// buy "+quantity+" items from agent
			// "+((SimpleTradingAgent)seller).getId()+" for $"+price+"/item.");
		}
	}

	public boolean acceptDeal(Auction auction, double price, int quantity) {
		assert isSeller;
		// if (getId()==2) System.out.println("acceptDeal"+ isSeller +
		// "\tprice="+price+"bid="+valuer.determineValue(auction));
		return price >= valuer.determineValue(auction);
	}

	public String toString() {
		return new String("Ag" + getId() + ": [FOOD=" + getStock() + " skF="
		    + skillF + " res=" + reserve + "]  [ GOLD=" + getFunds() + " skG="
		    + skillG + "] eqPrice=" + previousEqPrice);
	}

	public String toString(Auction auction) {
		return "Day" + Integer.toString(auction.getDay()) + "R"
		    + Integer.toString(auction.getRound()) + " " + toString() + "\tbid="
		    + valuer.determineValue(auction);
	}

	public void endOfDay(AuctionEvent event) {

		// updateEquilibriumPrice() is called at the end of the day, so that
		// _PreviousPriceValuer can use the previous price.
		Auction auction = (Auction) event.getAuction();
		updateEquilibriumPrice(auction);

		// if (getId()==0) System.out.println("DAY in
		// endOfDay="+auction.getDay());

		// if(getId() ==2) System.out.println(toString(auction)+"\n");

		super.endOfDay(event);
	}

	public void produce() {
		/**
		 * MBC book, p.4: agent produces gold if and only if skillG >
		 * previousEqPrice.skillF (equation1) else, food is produced
		 */
		// NOTE: in comparison with MBC book, agents don't starve to death
		// TODO
		// if (skillG > previousEqPrice * skillF && stock > dailyConsumption) {
		if (true) {
			// TODO
			// funds += skillG;
			// if(getId() == 2) System.out.println("Agent "+getId()+" produces
			// "+skillG+ " units of gold
			// (food="+stock+"/gold="+funds+"/price="+previousEqPrice+")");
		} else {
			stock.add((int) skillF);
			// if(getId() == 2) System.out.println("Agent "+getId()+" produces
			// "+skillF+ " units of food
			// (food="+stock+"/gold="+funds+"/price="+previousEqPrice+")");
		}

		if (getStock() < 0) {
			// System.out.println("Agent "+getId()+" has a negative inventory of
			// " + getStock());
		}
		if (getFunds() < 0) {
			// System.out.println("Agent "+getId()+" has a negative funds of " +
			// getFunds());
		}
	}

}
