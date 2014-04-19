package net.sourceforge.jasa.report;

import java.util.Map;

import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationEvent;
import net.sourceforge.jabm.report.ReportVariables;

import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractTradeNetworkGraphExporter implements
		InitializingBean, ReportVariables {

	protected TradeNetworkReport tradeNetworkReport;
	
	protected String baseFileName;
	
	protected int graphNumber = 0;
	
	protected Object fileNamePrefix;
	
	protected Object fileNameSuffix;

	public AbstractTradeNetworkGraphExporter() {
		super();
	}

	public Map<Object, Number> getVariableBindings() {
		// TODO Auto-generated method stub
		return null;
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
		this.graphNumber++;
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
	
	public Object getFileNamePrefix() {
		return fileNamePrefix;
	}

	public void setFileNamePrefix(Object fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}

	public Object getFileNameSuffix() {
		return fileNameSuffix;
	}

	public void setFileNameSuffix(Object fileNameSuffix) {
		this.fileNameSuffix = fileNameSuffix;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initialise();
	}
	
	public void initialise() {
		this.baseFileName = "" + fileNamePrefix + fileNameSuffix;
	}
	
	public abstract void exportGraph();

	
	
}