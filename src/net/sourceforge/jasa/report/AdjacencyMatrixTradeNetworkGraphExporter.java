package net.sourceforge.jasa.report;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.report.CSVWriter;
import net.sourceforge.jabm.report.WeightedEdge;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import edu.uci.ics.jung.algorithms.matrix.GraphMatrixOperations;
import edu.uci.ics.jung.graph.Graph;

/**
 * Export the trade network as an adjacency matrix.
 *  
 * @author Steve Phelps
 */
public class AdjacencyMatrixTradeNetworkGraphExporter extends
		AbstractTradeNetworkGraphExporter {

	protected CSVWriter out;
	
	@Override
	public void exportGraph() {
		try {
			initialiseOut();
			Map<WeightedEdge, Number> map = new HashMap<WeightedEdge, Number>();
			Graph<Agent, WeightedEdge> graph = tradeNetworkReport.getGraph();
			for(WeightedEdge edge : graph.getEdges()) {
				map.put(edge, edge.getValue());
			}
			SparseDoubleMatrix2D matrix = GraphMatrixOperations
					.graphToSparseMatrix(this.tradeNetworkReport.getGraph(),
							map);
			for (int i = 0; i < matrix.rows(); i++) {
				for (int j = 0; j < matrix.columns(); j++) {
					out.newData(matrix.get(i, j));
				}
				out.endRecord();
			}
			out.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void initialiseOut() throws FileNotFoundException {
		String fileName = baseFileName + "-" + this.graphNumber + ".csv";
		out = new CSVWriter(new FileOutputStream(fileName));
	}
}
