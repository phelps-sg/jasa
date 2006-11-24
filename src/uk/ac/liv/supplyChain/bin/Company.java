package uk.ac.liv.supplyChain.bin;
//based on JiggleModel.java

import java.util.ArrayList;

//import com.bbn.openmap.omGraphics.grid.GridData.Boolean;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uchicago.src.sim.network.DefaultDrawableNode;
import uchicago.src.sim.gui.NetworkDrawable;
import uchicago.src.sim.engine.SimModelImpl;

import uk.ac.liv.auction.core.Account;
import uk.ac.liv.auction.core.Auction;
//import uk.ac.liv.auction.core.AuctionEventListener;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.event.AuctionEvent;
//import uk.ac.liv.auction.event.AuctionEvent;
//import uk.ac.liv.util.Parameterizable;
//import uk.ac.liv.util.Prototypeable;
//import uk.ac.liv.util.Resetable;
import uk.ac.liv.auction.agent.CommodityHolding;
import uk.ac.liv.auction.agent.TradingAgent;

public class Company extends DefaultDrawableNode implements TradingAgent {
	
	private int xSize, ySize;
	private SimModelImpl scModel;

	private int[] incomingInventory={0};
	private int[] incomingInventoryCapacity={99999};
	private int[] ratioIncomingToWip={1}; 
	private int wipInventory=0;				//Wip = Work-in-process inventory
	private int wipInventoryCapacity=1;
	private int wipDuration=1;				//wipDuration = production duration
	private double lastProductionStart=0;
	private int[] ratioWipToOutgoing={1};
	private int[] outgoingInventory={0};
	private int[] outgoingInventoryCapacity={99999};
	
	public void init(int xSize, int ySize, NetworkDrawable drawable) {
		
		super.setDrawable(drawable);
		this.xSize = xSize;
		this.ySize = ySize;

	}//init

	public void step() {
		// STEP 1: FLOW MANAGEMENT
		// if WIP empty, try to launch a new production batch
		if (wipInventory == 0){
			launchNewProductionBatch();
		}
		// if WIP full, check whether production duration has elapsed
		else if(lastProductionStart + wipDuration >= scModel.getTickCount()) {
			wipInventory = 0;
			for (int i=0 ; i < outgoingInventory.length ; i++) {
				outgoingInventory[i] += ratioWipToOutgoing[i]; 
			}//for
			launchNewProductionBatch();
		}//if
		
		// STEP 2: DECISION MAKING
	}//step()
	
	public void launchNewProductionBatch() {
		//check whether a batch can be transformed
		boolean readyToProduce = true;
		for (int i = 0 ; i < incomingInventory.length ; i++) {
			if (incomingInventory[i] * ratioIncomingToWip[i] < wipInventoryCapacity) {
				readyToProduce = false;
			}//if
		}//for
		
		//if possible, launch a new production batch
		if (readyToProduce == true) {
			wipInventory = wipInventoryCapacity;
			for (int i = 0 ; i < incomingInventory.length ; i++) {
				incomingInventory[i] -= ratioIncomingToWip[i];  
			}//for
			lastProductionStart = scModel.getTickCount();
		}//if
	}//launchNewProductionBatch()
		
	public Company(int xSize, int ySize, NetworkDrawable drawable, SimModelImpl scModel) {
		super(drawable);
		this.xSize = xSize;
		this.ySize = ySize;
		this.scModel = scModel;
	 }//constructor(int,int,NetworkDrawable)

	public void requestShout(Auction auction) {
		/*try {
		      if (currentShout != null ) {
		        auction.removeShout(currentShout);
		      }
		      currentShout = strategy.modifyShout(currentShout, auction);
		      lastProfit = 0;
		      lastShoutAccepted = false;
		      if ( active() && currentShout != null ) {
		        auction.newShout(currentShout);
		      }
		    } catch ( AuctionClosedException e ) {
		      logger.debug("requestShout(): Received AuctionClosedException");
		      // do nothing
		    } catch ( NotAnImprovementOverQuoteException e ) {
		      logger.debug("requestShout(): Received NotAnImprovementOverQuoteException");
		      // do nothing
		    } catch ( AuctionException e ) {
		      logger.warn(e.getMessage());
		      e.printStackTrace();
		    }
		    */
		System.out.println("Company requestShout");
		// TODO Auto-generated method stub
		
	}//requestShout(Auction)

	public boolean isBuyer() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSeller() {
		// TODO Auto-generated method stub
		return false;
	}

	public void informOfSeller(Auction arg0, Shout arg1, TradingAgent arg2, double arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}

	public void informOfBuyer(Auction arg0, TradingAgent arg1, double arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public void eventOccurred(AuctionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public boolean isBuyer(Auction auction) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSeller(Auction auction) {
		// TODO Auto-generated method stub
		return false;
	}

	public Account getAccount() {
		// TODO Auto-generated method stub
		return null;
	}

	public CommodityHolding getCommodityHolding() {
		// TODO Auto-generated method stub
		return null;
	}

	public void shoutAccepted(Auction auction, Shout shout, double price, int quantity) {
		// TODO Auto-generated method stub
		
	}
	 
}
