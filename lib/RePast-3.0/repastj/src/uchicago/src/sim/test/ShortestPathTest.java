/*$$
 * Copyright (c) 2004, Repast Organization for Architecture and Design (ROAD)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with 
 * or without modification, are permitted provided that the following 
 * conditions are met:
 *
 *	 Redistributions of source code must retain the above copyright notice,
 *	 this list of conditions and the following disclaimer.
 *
 *	 Redistributions in binary form must reproduce the above copyright notice,
 *	 this list of conditions and the following disclaimer in the documentation
 *	 and/or other materials provided with the distribution.
 *
 * Neither the name of the ROAD nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE TRUSTEES OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *$$*/
/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Dec 2, 2002
 * Time: 2:34:32 PM
 * To change this template use Options | File Templates.
 */
package uchicago.src.sim.test;

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
        List nodeList = NetworkFactory.getNetwork("uchicago/src/sim/test/pathmatrix.dl",
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
        return new TestSuite(uchicago.src.sim.test.ShortestPathTest.class);
    }

    public static void main(String[] args) {

        junit.textui.TestRunner.run(suite());
    }
}
