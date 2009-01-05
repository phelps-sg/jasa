/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Dec 2, 2002
 * Time: 2:34:32 PM
 * To change this template use Options | File Templates.
 */
package uchicago.src.sim.network;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.network.DefaultEdge;
import uchicago.src.sim.network.DefaultNode;
import uchicago.src.sim.network.NetworkConstants;
import uchicago.src.sim.network.NetworkFactory;
import uchicago.src.sim.network.NetworkPath;
import uchicago.src.sim.network.ShortestNetworkPath;

public class ShortestPathTest extends TestCase{
    double[][] doubleMatrix = {
        {0, 0, 3},
        {0, 0, 4},
        {1, 1, 0}
    };


    public ShortestPathTest(String name){
        super(name);
    }

    public void testPath(){
        List nodeList = NetworkFactory.getNetwork("test/uchicago/src/sim/network/pathmatrix.dl",
                NetworkFactory.DL,
                DefaultNode.class,
                DefaultEdge.class,
                NetworkConstants.LARGE);
        for(int i = 0 ; i < nodeList.size() ; i++){
            ((DefaultNode) nodeList.get(i)).setNodeLabel("node" + i);
        }
        DefaultNode i = (DefaultNode) nodeList.get(0);
        DefaultNode j = (DefaultNode) nodeList.get(1);
        ShortestNetworkPath path = new ShortestNetworkPath(nodeList, i);
        NetworkPath p = path.getPath(j);
        assertTrue(p.getDistance() == 4);
        NetworkPath p1 = new NetworkPath(4);
        p1.push(j);
        p1.push(nodeList.get(2));
        p1.push(i);
        assertTrue(compPaths(p, p1));

    }

    private boolean compPaths(NetworkPath p1, NetworkPath p2){
        boolean same = true;
        if(p1.size() != p2.size()){
            return false;
        }
        for(int i = 0 ; i < p1.size() ; i++){
            if(p1.get(i) != p2.get(i)){
                return false;
            }
        }
        return same;
    }

        public static Test suite() {
        return new TestSuite(uchicago.src.sim.network.ShortestPathTest.class);
    }

    public static void main(String[] args) {

        junit.textui.TestRunner.run(suite());
    }
}
