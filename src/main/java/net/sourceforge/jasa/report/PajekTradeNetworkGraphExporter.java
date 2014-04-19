package net.sourceforge.jasa.report;

import java.io.IOException;
import java.io.PrintWriter;

import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.report.WeightedEdge;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.io.PajekNetWriter;

public class PajekTradeNetworkGraphExporter extends
		AbstractTradeNetworkGraphExporter {

	public PajekTradeNetworkGraphExporter() {
		super();
	}
	
	public PajekTradeNetworkGraphExporter(TradeNetworkReport relationshipTracker,
			Object fileNamePrefix, Object fileNameSuffix) {
		super();
		this.tradeNetworkReport = relationshipTracker;
		this.fileNamePrefix = fileNamePrefix;
		this.fileNameSuffix = fileNameSuffix;
		initialise();
	}

	public void exportGraph() {
		
		PajekNetWriter<Agent, WeightedEdge> graphWriter = 
			new PajekNetWriter<Agent, WeightedEdge>();
//		DotWriter<Agent, WeightedEdge> graphWriter =
//			new DotWriter<Agent, WeightedEdge>();
		
		try {
			
			String fileName = baseFileName + "-" + this.graphNumber + ".net";
			PrintWriter out = new PrintWriter(fileName);
			
			Transformer<WeightedEdge, Number> nev =
				new Transformer<WeightedEdge, Number>() {

					public Number transform(WeightedEdge str) {
						return new Double(str.getValue());
					}
				
			};
			
			Transformer<Agent, String> vs =
				new Transformer<Agent, String>() {

					public String transform(Agent agent) {
//						AbstractTradingAgent tradingAgent = (AbstractTradingAgent) agent;
//						double score = tradingAgent.getFunds();
//						return "" + imageScoreAgent.hashCode() + ":" + score;
//						return new DecimalFormat("#.00").format(score);
						return agent.getClass().toString();
					}
				
			};
			
			graphWriter.save(tradeNetworkReport.getGraph(), out, vs, nev);
			
			this.graphNumber++;
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
