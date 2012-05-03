package net.sourceforge.jasa.report;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Map;

import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationEvent;
import net.sourceforge.jabm.report.ReportVariables;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.report.TradeNetworkReport.TransactionList;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.io.PajekNetWriter;

public class TradeNetworkGraphExporter implements ReportVariables {

	protected TradeNetworkReport tradeNetworkReport;
	
	protected String baseFileName;
	
	protected int graphNumber = 0;
	
	public TradeNetworkGraphExporter(TradeNetworkReport relationshipTracker,
			Object fileNamePrefix, Object fileNameSuffix) {
		super();
		this.tradeNetworkReport = relationshipTracker;
		this.baseFileName = "" + fileNamePrefix + fileNameSuffix;
	}

	public Map<Object, Number> getVariableBindings() {
		// TODO Auto-generated method stub
		return null;
	}

	public void exportGraph() {
		
		PajekNetWriter<Agent, TransactionList> graphWriter = 
			new PajekNetWriter<Agent, TransactionList>();
//		DotWriter<Agent, TransactionList> graphWriter =
//			new DotWriter<Agent, RelationshipStrength>();
		
		try {
			
			String fileName = baseFileName + "-" + this.graphNumber + ".net";
			PrintWriter out = new PrintWriter(fileName);
			
			Transformer<TransactionList, Number> nev =
				new Transformer<TransactionList, Number>() {

					public Number transform(TransactionList str) {
						return new Double(str.getValue());
					}
				
			};
			
			Transformer<Agent, String> vs =
				new Transformer<Agent, String>() {

					public String transform(Agent agent) {
						AbstractTradingAgent tradingAgent = (AbstractTradingAgent) agent;
						double score = tradingAgent.getFunds();
//						return "" + imageScoreAgent.hashCode() + ":" + score;
						return new DecimalFormat("#.00").format(score);
					}
				
			};
			
			graphWriter.save(tradeNetworkReport.getGraph(), out, vs, nev);
			
			this.graphNumber++;
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public TradeNetworkReport getTradeNetworkReport() {
		return tradeNetworkReport;
	}

	public void setTradeNetworkReport(TradeNetworkReport relationshipTracker) {
		this.tradeNetworkReport = relationshipTracker;
	}

	public String getFileName() {
		return baseFileName;
	}

	public void setFileName(String fileName) {
		this.baseFileName = fileName;
	}

	public void compute(SimEvent event) {
		exportGraph();
	}

	public void reset(SimulationEvent event) {
	}

	public void eventOccurred(SimEvent event) {
	}

	public void dispose(SimEvent event) {
	}
	
	public void initialise(SimEvent event) {
	}

	public String getName() {
		return getClass().getName();
	}
	
}
