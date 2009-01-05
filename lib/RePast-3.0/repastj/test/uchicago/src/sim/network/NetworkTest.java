package uchicago.src.sim.network;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.network.AdjacencyMatrix;
import uchicago.src.sim.network.AdjacencyMatrixFactory;
import uchicago.src.sim.network.DefaultEdge;
import uchicago.src.sim.network.DefaultNode;
import uchicago.src.sim.network.NetUtilities;
import uchicago.src.sim.network.NetworkConstants;
import uchicago.src.sim.network.NetworkConvertor;
import uchicago.src.sim.network.NetworkFactory;
import uchicago.src.sim.network.NetworkRecorder;


public class NetworkTest extends TestCase {

  boolean windows = false;

  String outDir = ".";

  double[][] binMatrix = {{0, 0, 1, 1, 0, 0, 0, 0, 1},
                          {1, 0, 1, 1, 0, 0, 1, 0, 1},
                          {0, 0, 0, 0, 0, 0, 0, 1, 0},
                          {0, 1, 0, 0, 0, 0, 0, 0, 0},
                          {0, 0, 0, 0, 0, 0, 1, 0, 0},
                          {0, 0, 0, 0, 1, 0, 1, 1, 0},
                          {0, 1, 0, 0, 0, 0, 0, 0, 0},
                          {0, 0, 0, 0, 1, 0, 0, 0, 1},
                          {0, 0, 0, 1, 0, 0, 1, 0, 0}
  };

  double[][] byteMatrix = {{0, 0, 120, 1, 0, 0, 0, 0, 1},
                           {1, 0, 1, 1, 0, 0, 1, 0, 1},
                           {0, 0, 0, 0, 0, 0, 0, 1, 0},
                           {0, 1, 0, 0, 0, 0, 0, 0, 0},
                           {0, 0, 0, 0, 0, 0, 1, 0, 0},
                           {0, 0, 0, 0, 1, 0, 1, 1, 0},
                           {0, -30, 0, 0, 0, 0, 0, 0, 0},
                           {0, 0, 0, 0, 1, 0, 0, 0, 1},
                           {0, 0, 0, 1, 0, 0, 12, 0, 0}
  };

  double[][] doubleMatrix = {{0, 0, 120000.23, 1, 0, 0, 0, 0, 1},
                             {1, 0, 1, 1, 0, 0, 1, 0, 1},
                             {0, 0, 0, 0, 0, 0, 0, 1, 0},
                             {0, 1, 0, 0, 0, 0, 0, 0, 0},
                             {0, 0, 0, 0, 0, 0, 1, 0, 0},
                             {0, 0, 0, 0, 1, 0, 1, 1, 0},
                             {0, -31123123.112, 0, 0, 0, 0, 0, 0, 0},
                             {0, 0, 0, 0, 1, 0, 0, 0, 1},
                             {0, 0, 0, 1, 0, 0, 32343, 0, 0}
  };

  public NetworkTest(String name) {
    super(name);
  }

  public void setUp() {
    String os = System.getProperty("os.name");

    if (os.startsWith("Windows")) {
      windows = true;
    }
  }

  private boolean matrixComp(double[][] expected, AdjacencyMatrix actual) {
    for (int i = 0; i < expected.length; i++) {
      double[] row = expected[i];

      for (int j = 0; j < row.length; j++) {
        if (row[j] != actual.get(i, j)) {
          System.out.println("expected: " + row[j] + ", got: " +
                             actual.get(i, j));
          return false;
        }
      }
    }

    return true;
  }

  private String getFileAsString(String fileName) {
    String lines = "";

    try {
      BufferedReader in = new BufferedReader(new FileReader(fileName));
      String line = "";

      while ((line = in.readLine()) != null) {
        lines += line + "\n";
      }
      in.close();
    } catch (IOException ex) {
      throw new RuntimeException(ex.getMessage());
    }

    return lines.trim();
  }

  private String getComment(String netout) {
    int start = netout.indexOf("#");
    int end = netout.indexOf("\n", start);

    return netout.substring(start, end);
  }

  private String getDLHeader(String netout) {
    int start = netout.indexOf("dl");
    int end = netout.indexOf("data:", start);

    return netout.substring(start, end);
  }

  private AdjacencyMatrix getMatrixFromString(String netout, int type) {
    int start = netout.indexOf("data:");
    int end = netout.indexOf("\n", start);
    String data = netout.substring(end, netout.length()).trim();
    StringTokenizer tok = new StringTokenizer(data, "\n");
    int size = tok.countTokens();
    AdjacencyMatrix matrix =
            AdjacencyMatrixFactory.createAdjacencyMatrix(size, size, type);

    int i = 0;

    while (tok.hasMoreTokens()) {
      StringTokenizer spc = new StringTokenizer(tok.nextToken(), " ");
      int j = 0;

      while (spc.hasMoreTokens()) {
        matrix.set(i, j, Double.parseDouble(spc.nextToken()));
        j++;
      }
      i++;
    }

    return matrix;
  }

  /*  public void testPajekReader(){
   List nodeList = null;
   List nodeList1 = null;
   try{
   PajekNetReader reader = new PajekNetReader("./ReadTest.net");
   PajekNetReader reader1 = new PajekNetReader("./ReadTest.net");
   nodeList = reader.getDrawableNetwork(DefaultNode.class, DefaultEdge.class, 400, 500);
   nodeList1 = reader1.getNetwork(DefaultNode.class, DefaultEdge.class);
   } catch (Exception e){
   System.out.println(e);
   }
   assertEquals(4, nodeList.size());
   assertEquals(4, nodeList1.size());
   double[][] pajekMatrix = {{1,1,0,0},
   {0,0,1,1},
   {0,0,0,2},
   {1,1,0,0}};

   Vector v = NetworkConvertor.nodesToMatrices(nodeList, NetworkConstants.LARGE);
   Vector v1 = NetworkConvertor.nodesToMatrices(nodeList1, NetworkConstants.LARGE);
   AdjacencyMatrix m = (AdjacencyMatrix) v.get(0);
   AdjacencyMatrix m1 = (AdjacencyMatrix) v1.get(0);
   assertTrue(matrixComp(pajekMatrix,m));
   assertTrue(matrixComp(pajekMatrix,m1));

   }
   */
  public void testBinFactory() {
    List nodeList = NetworkFactory.getNetwork("test/uchicago/src/sim/network/bin_matrix.dl",
                                              NetworkFactory.DL,
                                              DefaultNode.class,
                                              DefaultEdge.class,
                                              NetworkConstants.BINARY);

    assertEquals(9, nodeList.size());

    Vector v = NetworkConvertor.nodesToMatrices(nodeList,
                                                NetworkConstants.BINARY);
    AdjacencyMatrix m = (AdjacencyMatrix) v.get(0);

    assertTrue(matrixComp(binMatrix, m));
  }

  public void testByteFactory() {
    List nodeList = NetworkFactory.getNetwork("test/uchicago/src/sim/network/byte_matrix.dl",
                                              NetworkFactory.DL,
                                              DefaultNode.class,
                                              DefaultEdge.class,
                                              NetworkConstants.SMALL);

    assertEquals(9, nodeList.size());

    Vector v = NetworkConvertor.nodesToMatrices(nodeList,
                                                NetworkConstants.SMALL);
    AdjacencyMatrix m = (AdjacencyMatrix) v.get(0);

    assertTrue(matrixComp(byteMatrix, m));
  }

  public void testDoubleFactory() {
    List nodeList = NetworkFactory.getNetwork("test/uchicago/src/sim/network/double_matrix.dl",
                                              NetworkFactory.DL,
                                              DefaultNode.class,
                                              DefaultEdge.class,
                                              NetworkConstants.LARGE);

    assertEquals(9, nodeList.size());

    Vector v = NetworkConvertor.nodesToMatrices(nodeList,
                                                NetworkConstants.LARGE);
    AdjacencyMatrix m = (AdjacencyMatrix) v.get(0);

    assertTrue(matrixComp(doubleMatrix, m));
  }

  public void testRecorder() {
    List nodeList = NetworkFactory.getNetwork("test/uchicago/src/sim/network/bin_matrix.dl",
                                              NetworkFactory.DL,
                                              DefaultNode.class,
                                              DefaultEdge.class,
                                              NetworkConstants.BINARY);
    NetworkRecorder recorder = new NetworkRecorder(NetworkRecorder.DL,
                                                   outDir + "/recTestBin.dl",
                                                   null);

    recorder.record(nodeList, "a comment", NetworkRecorder.BINARY);
    recorder.write();

    String file = getFileAsString(outDir + "/recTestBin.dl");
    String comment = getComment(file);

    assertTrue("comments not equal", comment.equals("# a comment"));
    assertTrue("dl header not equal",
               getDLHeader(file).trim().equals("dl n=9"));

    AdjacencyMatrix m = getMatrixFromString(file, NetworkConstants.BINARY);

    assertTrue(matrixComp(binMatrix, m));

  }

  /*
   public void testExcelFactory() {
   System.out.println("excel factory test");
   if (windows) {
   List nodeList = NetworkFactory.getNetwork("f:\\src\\tests\\matrix_no_label.xls",
   NetworkFactory.EXCEL, DefaultNode.class, DefaultEdge.class);

   assertEquals(5, nodeList.size());

   Vector v = NetworkConvertor.nodesToMatrices(nodeList);
   for (int i = 0; i < v.size(); i++) {
   AdjacencyMatrix m = (AdjacencyMatrix)v.get(i);
   System.out.println(m);
   }
   }
   }
   */

  /*
   public void testExcelRecorder() {
   if (windows) {
   recorder = new NetworkRecorder(NetworkRecorder.EXCEL, tmpDir + "excelMatrix.xls", false);
   recorder.record(nodeList, "A Sample Comment");

   AbstractEdge edge = new AbstractEdge(a, c);
   edge.setType("family");
   a.addOutEdge(edge);
   recorder.record(nodeList, "A second comment");

   recorder.write();
   }
   }
   */

  public void testNetStats() {

    /*
     * - UCINet stats -
     *   density: 0.2993
     *   cluster coeff: .301275979
     *   component count: 1
     *
     * Density calculated by network->network properties->density with
     * utilize diagonal values = yes.
     *
     * Got the cluster coefficent by getting density of all ego networks
     * summing all these / 100 / num nodes. network->ego networks->density
     * Then exporting to excel summing the densities and doing the divisions.
     *
     * Component count network->regions->components.
     */

    uchicago.src.sim.util.Random.createUniform();

    List nodes = NetworkFactory.getNetwork("test/uchicago/src/sim/network/stats_test_no_sym.dl",
                                           NetworkRecorder.DL,
                                           DefaultNode.class,
                                           DefaultEdge.class,
                                           NetworkRecorder.BINARY);
    ArrayList list = new ArrayList();

    list.addAll(nodes);
    double density = NetUtilities.calcDensity(list);

    assertEquals(0.2993, density, 0);
    double clustCoef = NetUtilities.calcClustCoef(list);
    BigDecimal bd = new BigDecimal(clustCoef);

    bd = bd.setScale(9, BigDecimal.ROUND_DOWN);
    assertEquals(.301275979, bd.doubleValue(), 0);
    assertEquals(1, NetUtilities.getComponents(list).size());

    /*
     List nodes = NetworkFactory.
     createRandomDensityNetwork(200, .3, false, false, DefaultNode.class,
     DefaultEdge.class);
     NetworkRecorder rec = new NetworkRecorder(NetworkRecorder.DL,
     "uchicago/src/sim/test/stats_test_no_sym.dl", null);
     rec.record(nodes);
     rec.write();
     */


  }

  public static Test suite() {
    return new TestSuite(uchicago.src.sim.network.NetworkTest.class);
  }

  public static void main(String[] args) {

    junit.textui.TestRunner.run(suite());
  }
}
