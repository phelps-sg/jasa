package uk.ac.liv.supplyChain;
import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DDisplay;
import uchicago.src.sim.gui.RectNetworkItem;
import uchicago.src.sim.util.Random;
import uk.ac.liv.auction.RepastMarketSimulation;
import uk.ac.liv.auction.core.AuctionClosedException;
import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.core.Auctioneer;
import uk.ac.liv.supplyChain.bin.Company;

/**
 * @author moyaux
 *
 */
public class SingleMarket extends RepastMarketSimulation {

	private int numAgent = 20;
	public void setNumAgent(int numAgent) {this.numAgent = numAgent;}
	public int getNumAgent() {return numAgent;}
	private int worldXSize = (int) (100 * Math.ceil(Math.sqrt(numAgent)));
	private int worldYSize = (int) (100 * Math.ceil(Math.sqrt(numAgent)));
	
	private Schedule schedule = new Schedule(1);
	private DisplaySurface surface ;
	
	private ArrayList agentList = new ArrayList();
	
	protected RandomRobinAuction auction = new RandomRobinAuction("single market");
	protected Auctioneer auctioneer;
	
	/* (non-Javadoc)
	 * @see uchicago.src.sim.engine.SimModel#getInitParam()
	 */
	public String[] getInitParam() {
		String[] params = {"numAgent"};
		return params;
	}

	/* (non-Javadoc)
	 * @see uchicago.src.sim.engine.SimModel#begin()
	 */
	public void begin() {
		//System.out.println("begin");
		buildModel();
		buildDisplay();
		//buildSchedule();
		surface.display();
	}
	
	/**
	 * 
	 */
	public void buildModel() {
		agentList.clear();
		//create agents
		for (int i = 0 ; i < numAgent ; i++) {
			int x = Random.uniform.nextIntFromTo (0, worldXSize - 1);
			int y = Random.uniform.nextIntFromTo (0, worldYSize - 1);
			RectNetworkItem drawable = new RectNetworkItem (x, y);
			Company company = new Company(worldXSize, worldYSize, drawable, this);
			company.setBorderColor(Color.red);
			company.setBorderWidth(1);
			agentList.add(company);
		}//for
/*		
		//link every market to every retailer
		for (int retailerIndex = 1 ; retailerIndex < numRetailers+1 ; retailerIndex++) {
			Company retailer = (Company)agentList.get(retailerIndex);
			CompanyLink link = new CompanyLink(market, retailer);
			market.addOutEdge(link);
		}//for
*/		
		//	A U C T I O N
		
		//((uk.ac.liv.util.Resetable)auctioneer).reset();
		//auction.setAuctioneer(auctioneer);
		auction.activateGUIConsole();
		for (int i=0 ; i < agentList.size() ; i++) {
			Company company = (Company)agentList.get(i);
			auction.register(company);
		}//for
		auction.requestShouts();
		//auction.runSingleRound();
	}//buildModel()
	
	public void buildDisplay() {
		Network2DDisplay display = new Network2DDisplay (agentList, worldXSize, worldYSize);
		surface.addDisplayableProbeable(display, "Supply Chain View");
	    surface.addZoomable(display);
	    surface.setBackground(java.awt.Color.black);
	    addSimEventListener(surface);
	}//buildDisplay()

	/* (non-Javadoc)
	 * @see uchicago.src.sim.engine.SimModel#setup()
	 */
	public void setup() {
		//System.out.println("setup");
		Random.createUniform();
		if (surface != null) surface.dispose();
		surface = new DisplaySurface(this, "Supply chain Display");
		super.registerDisplaySurface("Buttons Display",surface);
	}//setup()

	/* (non-Javadoc)
	 * @see uchicago.src.sim.engine.SimModel#getSchedule()
	 */
	public Schedule getSchedule() {
		return schedule;
	}//getSchedule()

	/* (non-Javadoc)
	 * @see uchicago.src.sim.engine.SimModel#getName()
	 */
	public String getName() {
		return "SingleMarket";
	}//getName()

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimInit init = new SimInit();
		init.loadModel(new SingleMarket(), null, false);

	}//main()

}
