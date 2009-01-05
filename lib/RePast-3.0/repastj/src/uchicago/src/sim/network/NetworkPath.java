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

import java.util.Stack;

/**
 * 
 * @author Tom Howe
 * @version $Revision$ $Date$
 */
public class NetworkPath extends Stack{
    double distance;

    public NetworkPath(double distance){
        this.distance = distance;
    }

    public void addNode(Node n){
        push(n);
    }

    public double getDistance(){
        return distance;
    }

    public boolean nodeInPath(Node n){
        boolean inPath = false;
        if(contains(n)){
            inPath = true;
        }
        return inPath;
    }

    public int size(){
        return super.size();
    }

    public Object get(int i){
        return super.get(size() - 1 - i);
    }

    public int indexOf(Object o){
        return size()  - super.indexOf(o);
    }

    public double getSubpathDistance(DefaultNode n1, DefaultNode n2) throws IllegalArgumentException{
        if(!nodeInPath(n1) || !nodeInPath(n2)){
            throw new IllegalArgumentException("Node not in path");
        }
        double dist = 0;
        int i = this.indexOf(n1);
        System.out.println("i = " + i);
        DefaultNode temp = (DefaultNode) get(i);
        while(temp != n2){
            i++;
            Edge e = (Edge) n1.getEdgesTo(temp).iterator().next();

            dist += e.getStrength();
            n1 = temp;
            temp = (DefaultNode) get(i);
        }
        Edge e = (Edge) n1.getEdgesTo(temp).iterator().next();
        dist += e.getStrength();
        return dist;
    }
}
