/*$$
 * Copyright (c) 1999, Trustees of the University of Chicago
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
 * Neither the name of the University of Chicago nor the names of its
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
package uchicago.src.sim.network;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections.BinaryHeap;

//import uchicago.src.sim.network.*;

/**
 * 
 * 
 * @author Tom Howe
 * @version $Revision$ $Date$
 */
public class ShortestNetworkPath {
    private BinaryHeap V = new BinaryHeap(true, new Comparator(){
            public int compare(Object o1, Object o2){
                DefaultNode i = (DefaultNode) o1;
                DefaultNode j = (DefaultNode) o2;
                int result = 0;
                if(((Double)d.get(i)).doubleValue() < ((Double)d.get(j)).doubleValue()){
                    return -1;
                }else if(((Double)d.get(i)).doubleValue() > ((Double)d.get(j)).doubleValue()){
                    return 1;
                }
                return result;
            }
        });
    HashMap d;
    HashMap pi;
    DefaultNode i;
    List nodes;


    public HashMap getPi(){
        return pi;
    }

    public ShortestNetworkPath(List nodeList, DefaultNode i){
        nodes = nodeList;
        this.i = i;
        d = new HashMap(nodeList.size());
        pi = new HashMap(nodeList.size());
        ListIterator iter = nodeList.listIterator();
        while(iter.hasNext()){
            Node v = (Node) iter.next();
            d.put(v, new Double(Double.MAX_VALUE));
            pi.put(v, null);
        }
        d.put(i, new Double(0));
        dijkstra();
    }

    public void relax(DefaultNode u, DefaultNode v){
        uchicago.src.sim.network.Edge w =
                (uchicago.src.sim.network.Edge) u.getEdgesTo(v).iterator().next();
        double vD = ((Double) d.get(v)).doubleValue();
        double uD = ((Double) d.get(u)).doubleValue();
        if(vD > uD + w.getStrength()){
            //System.out.println("v.getNodeLabel() = " + v.getNodeLabel());
            //System.out.println("uD = " + uD + w.getStrength());
            d.put(v, new Double(uD + w.getStrength()));
            pi.put(v, u);
        }
    }

    public void dijkstra(){
        V.addAll(nodes);
        while(!V.isEmpty()){
            DefaultNode u = (DefaultNode) V.pop();
            ArrayList links = u.getOutEdges();
            Iterator iter = links.iterator();
            while(iter.hasNext()){
                DefaultNode v = (DefaultNode) ((DefaultEdge) iter.next()).getTo();
                relax(u,v);
            }
        }
    }

    public NetworkPath getPath(DefaultNode node){
        NetworkPath p = new NetworkPath(((Double)d.get(node)).doubleValue());
        while(node != i){
            if(pi.get(node) == null){
                return null;
            }
            p.push(node);
            node = (DefaultNode) pi.get(node);
        }
        p.push(i);
        //p.finalize();
        return p;
    }

    public static void main(String[] args){
        DefaultNode node1 = new DefaultNode("node1");
        DefaultNode node2 = new DefaultNode("node2");
        DefaultNode node3 = new DefaultNode("node3");
        DefaultNode node4 = new DefaultNode("node4");
        DefaultNode node5 = new DefaultNode("node5");
        Edge e1 = new DefaultEdge(node1, node2);
        e1.setStrength(25.4);
        node1.addOutEdge(e1);
        node2.addInEdge(e1);
        Edge e2 = new DefaultEdge(node2, node3);
        e2.setStrength(423.5);
        node2.addOutEdge(e2);
        node3.addInEdge(e2);
        Edge e3 = new DefaultEdge(node1, node4);
        e3.setStrength(429.5);
        node1.addOutEdge(e3);
        node4.addInEdge(e3);
        ArrayList foo = new ArrayList();
        foo.add(node1);
        foo.add(node2);
        foo.add(node3);
        foo.add(node4);
        foo.add(node5);
        ShortestNetworkPath shortest = new ShortestNetworkPath(foo, node1);
        shortest.dijkstra();
        NetworkPath path = shortest.getPath(node3);
        if(path != null){
            for(int i = 0 ; i < path.size() ; i++){
                DefaultNode node = (DefaultNode) path.get(i);
                System.out.println("node.getNodeLabel() = " + node.getNodeLabel());
            }
            System.out.println("path.getDistance() = " + path.getDistance());
            System.out.println("path.getSubpathDistance(node2, node3) = " + path.getSubpathDistance(node2, node3));
        }else{
            System.out.println("no path");
        }

    }
}
