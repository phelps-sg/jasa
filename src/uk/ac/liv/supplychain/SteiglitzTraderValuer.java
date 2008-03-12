/**
 * 
 */
package uk.ac.liv.supplychain;

import java.io.Serializable;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Uniform;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.agent.TradingAgent;
import uk.ac.liv.auction.agent.ValuationPolicy;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.prng.GlobalPRNG;

/**
 * @author moyaux
 * 
 */
public class SteiglitzTraderValuer implements ValuationPolicy, Serializable {

	public static final String P_b00 = "b00";

	public static final String P_b01 = "b01";

	public static final String P_b0inf = "b0inf";

	protected double minB00B01B0inf = -1;

	protected double b00;

	protected double b01;

	protected double b0inf;

	protected double gamma;

	private TradingAgent agent;

	static protected AbstractContinousDistribution distribution;

	protected double skillF = 0;

	protected double skillG = 0;

	public double getSkillF() {
		return skillF;
	}

	public double getSkillG() {
		return skillG;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.liv.auction.agent.ValuationPolicy#determineValue(uk.ac.liv.auction.core.Auction)
	 */
	public double determineValue(Auction auction) {
		Auction[] agentAuctions = ((SupplyChainAgent) agent).getAuction();
		int auctionIdx = (int) ((SupplyChainRandomRobinAuction) auction).getId();

		double f = 0; // food
		double r = 0; // size of food reserve
		if (((SupplyChainAgent) agent).sourceWhichBuysInAuction[auctionIdx] >= 0) {
			f += ((SupplyChainAgent) agent).source[((SupplyChainAgent) agent).sourceWhichBuysInAuction[auctionIdx]];
			r += ((SupplyChainAgent) agent).source_capacity[((SupplyChainAgent) agent).sourceWhichBuysInAuction[auctionIdx]];
		}
		if (((SupplyChainAgent) agent).sourceWhichSellsInAuction[auctionIdx] >= 0
		    && !(((SupplyChainAgent) agent).sourceWhichBuysInAuction[auctionIdx] >= 0)) {
			f += ((SupplyChainAgent) agent).source[((SupplyChainAgent) agent).sourceWhichSellsInAuction[auctionIdx]];
			r += ((SupplyChainAgent) agent).source_capacity[((SupplyChainAgent) agent).sourceWhichSellsInAuction[auctionIdx]];
		}
		if (((SupplyChainAgent) agent).deliverWhichBuysInAuction[auctionIdx] >= 0) {
			f += ((SupplyChainAgent) agent).deliver[((SupplyChainAgent) agent).deliverWhichBuysInAuction[auctionIdx]];
			r += ((SupplyChainAgent) agent).deliver_capacity[((SupplyChainAgent) agent).deliverWhichBuysInAuction[auctionIdx]];
		}
		if (((SupplyChainAgent) agent).deliverWhichSellsInAuction[auctionIdx] >= 0
		    && !(((SupplyChainAgent) agent).deliverWhichBuysInAuction[auctionIdx] >= 0)) {
			f += ((SupplyChainAgent) agent).deliver[((SupplyChainAgent) agent).deliverWhichSellsInAuction[auctionIdx]];
			r += ((SupplyChainAgent) agent).deliver_capacity[((SupplyChainAgent) agent).deliverWhichSellsInAuction[auctionIdx]];
		}

		// XXX System.out.println("SteiglitzTraderValuer.determineValue r="+r+"
		// for Agent "+((SupplyChainAgent) agent).getId() + " and
		// source_capa="+((SupplyChainAgent) agent).source_capacity[0]);

		double fbar = f / r;
		double g = ((SupplyChainAgent) agent).getFunds(); // gold
		double previousPrice = ((SupplyChainRandomRobinAuction) auction)
		    .getTransactionPrice(auction.getAge());
		double gbar = g / (previousPrice * r);
		// System.out.println(auction.getAge()+"
		// SteiglitzTraderValuer.determineValue for Agent "+
		// ((AbstractTradingAgent) agent).getId()+": B="+b(fbar, gbar) + ",
		// price="+previousPrice);

		/*
		 * if ( g < 0) System.out.println(auction.getAge()+"
		 * SteiglitzTraderValuer.determineValue: Agent "+((SupplyChainAgent)agent
		 * ).getId()+" has gold="+g+"<0: "+((SupplyChainAgent)agent ).toString() + ",
		 * previousPrice="+previousPrice + " r="+STEIGLITZreserve);
		 * 
		 * if ( fbar>1 && b(fbar, gbar)>1 ) { System.out.println(auction.getAge()+"
		 * SteiglitzTraderValuer.determineValue: Agent "+((SupplyChainAgent)agent
		 * ).getId()+" has fbar="+fbar+">1 and b="+b(fbar, gbar)+">1:
		 * "+((SupplyChainAgent)agent ).toString() + ",
		 * previousPrice="+previousPrice + " r="+STEIGLITZreserve); } if ( fbar<1 &&
		 * b(fbar, gbar)<1 ) System.out.println(auction.getAge()+"
		 * SteiglitzTraderValuer.determineValue: Agent "+((SupplyChainAgent)agent
		 * ).getId()+" has fbar="+fbar+"<1 and b="+b(fbar, gbar)+"<1:
		 * "+((SupplyChainAgent)agent ).toString() + ",
		 * previousPrice="+previousPrice + " r="+STEIGLITZreserve);
		 */
		/*
		 * if (auction.getAge() ==3 && ((SupplyChainAgent) agent).getId() ==15) {
		 * System.out.println("\n\n\n\n\n\n\n"); System.out.println("f="+f);
		 * System.out.println("r="+r); System.out.println("=> fbar="+fbar);
		 * System.out.println("==> 1-fbar="+(1-fbar)); System.out.println("g="+g);
		 * System.out.println("r="+r); System.out.println("price="+previousPrice);
		 * System.out.println("=> gbar="+gbar); double b0gbar = b0inf - ( b0inf -
		 * b00 ) * Math.exp( - gamma * gbar ); System.out.println("==>
		 * b0gbar="+b0gbar); System.out.println("===> bfg="+(Math.pow( b0gbar ,
		 * 1-fbar ))); System.out.println("\n\n\n\n\n\n\n"); }
		 */

		return (b(fbar, gbar) * previousPrice);
		// return Math.pow( gbar , (1-fbar) );
	}// determineValue

	private double b(double fbar, double gbar) {
		double b0gbar = b0inf - (b0inf - b00) * Math.exp(-gamma * gbar);
		double bfg = Math.pow(b0gbar, 1 - fbar);
		return bfg;
	}// b(f,g)

	public void printAgentState() {
		System.out.println("SteiglitzTraderValuer.printAgentState Agent "
		    + ((SupplyChainAgent) agent).getId());
		for (int i = 0; i < ((SupplyChainAgent) agent).source.length; i++) {
			System.out.println("sour." + i + ":"
			    + ((SupplyChainAgent) agent).source[i]);
		}
		for (int i = 0; i < ((SupplyChainAgent) agent).deliver.length; i++) {
			System.out.println("del." + i + ":"
			    + ((SupplyChainAgent) agent).deliver[i]);
		}
	}// printAgentState

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.liv.auction.agent.ValuationPolicy#consumeUnit(uk.ac.liv.auction.core.Auction)
	 */
	public void consumeUnit(Auction auction) {
		// TODO Auto-generated method stub
	}// consumeUnit

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.liv.util.Resetable#reset()
	 */
	public void reset() {

	}// reset

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.liv.util.Parameterizable#setup(ec.util.ParameterDatabase,
	 *      ec.util.Parameter)
	 */
	public void setup(ParameterDatabase parameters, Parameter base) {
		// super.setup(parameters, base);
		b00 = parameters.getDoubleWithDefault(base.push(P_b00), null,
		    minB00B01B0inf);
		b01 = parameters.getDoubleWithDefault(base.push(P_b01), null,
		    minB00B01B0inf);
		b0inf = parameters.getDoubleWithDefault(base.push(P_b0inf), null,
		    minB00B01B0inf);

		gamma = Math.log((b0inf - b00) / (b0inf - b01));
		// initialise();

		distribution = new Uniform(0, 10, GlobalPRNG.getInstance());

		skillF = 3 + (distribution.nextDouble() / 10);
		skillG = 1.25 + (distribution.nextDouble() / 20);

		// System.out.println("SteiglitzTraderValuer.setup: skillF="+skillF + "
		// skillG="+skillG);
	}

	public void setAgent(TradingAgent agent) {
		this.agent = agent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.liv.auction.agent.ValuationPolicy#eventOccurred(uk.ac.liv.auction.event.AuctionEvent)
	 */
	public void eventOccurred(AuctionEvent event) {
		// super.eventOccurred(event);
		/*
		 * if ( event instanceof EndOfDayEvent) System.out.println("End of Day"); if (
		 * event instanceof RoundClosingEvent) System.out.println("end of Round");
		 * if ( event instanceof RoundClosedEvent) processEndOfRound(event); if (
		 * event instanceof AuctionOpenEvent ) auctionOpen((AuctionOpenEvent)
		 * event);
		 */
	}// eventOccurred

}
