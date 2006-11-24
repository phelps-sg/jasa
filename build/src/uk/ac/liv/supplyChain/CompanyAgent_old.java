/**
 * 
 */
package uk.ac.liv.supplyChain;

import uk.ac.liv.auction.agent.AbstractTradingAgent;
import uk.ac.liv.auction.core.Auction;

import java.io.Serializable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author moyaux
 *
 */
public class CompanyAgent_old extends AbstractTradingAgent implements Serializable {

	/**
	 * 
	 */
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
	
	/**
	 * Parameter names used when initialising from parameter db
	 */
	public static final String P_INI_WIP_INVENTORY = "initialwipinventory";
	public static final String P_WIP_INVENTOR_CAPA = "wipinventorycapacity";
	
	protected boolean isActive = true;
	  
	public CompanyAgent_old() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void setup( ParameterDatabase parameters, Parameter base ) {
		
		wipInventory = parameters.getInt(base.push(P_INI_WIP_INVENTORY));
		wipInventoryCapacity = parameters.getInt(base.push(P_WIP_INVENTOR_CAPA));
	    
	    super.setup(parameters, base);
	  }

	public double equilibriumProfits(Auction arg0, double arg1, int arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean active() {
		return isActive;
	}

}
