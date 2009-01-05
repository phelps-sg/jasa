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
package uchicago.src.sim.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import cern.jet.random.AbstractDistribution;
//import cern.jet.random.engine.DRand;

/**
 * This is a helper class designed to apply random numbers generated using a 
 * statistical distribution to a particular variable in your Agent Set.  
 * The random numbers are generated from classes contained in the 
 * cern.jet.random package.  Examples include chi-squared, normal, and
 * pareto.
 * This class can be used in conjunction with the uchicago.src.sim.util.DistributionFactory to quickly apply a distribution to agents.
 * @see cern.jet.random
 * @see uchicago.src.sim.util.DistributionFactory
 * @author $Author$
 * @version $Revision$ $Date$
 **/

public class AgentDistribution{
	private AbstractDistribution dist;
	private ArrayList agents;
	private Method m;
	private String var;

	/**
	 * Constructs an AgentDistribution usin the distribution, the agent list and 
	 * variable name.
	 * @param d The distribution to use when generating random numbers.
	 * @param agents The agents
	 * @param var The name of the variable you want to set.
	 **/
	public AgentDistribution(AbstractDistribution d, ArrayList agents, String var){
		dist = d;
		this.agents = agents;
		this.var = var;
	}

	public void setDistribution(AbstractDistribution d){
		dist = d;
	}

	public AbstractDistribution getDistribution(){
		return dist;
	}

	public void setAgents(ArrayList agents){
		this.agents = agents;
	}

	public ArrayList getAgents(){
		return agents;
	}

	public void setVar(String var){
		this.var = var;
	}

	public String getVar(){
		return var;
	}

	/**
	 * Sets the chosen variable in the agents to a random number generated using
	 * the distribution.
	 **/

	public ArrayList distributeAgents() throws InvocationTargetException, IllegalAccessException{
		Class agent = agents.get(0).getClass();
		System.out.println(agent.getName());
		Method[] methods = agent.getMethods();
		for(int i = 0 ; i < methods.length ; i++){
			if(methods[i].getName().equalsIgnoreCase("set" + var))
					m = methods[i];
		}
		Class[] paramTypes = m.getParameterTypes();
		Class paramType = paramTypes[0];
		Object[] params = new Object[1];
		for(int i = 0 ; i < agents.size(); i++){
			if(paramType.getName().equals("int"))
				params[0] = new Integer(dist.nextInt());
			else if(paramType.getName().equals("double"))
				params[0] = new Double(dist.nextDouble());
			else
				throw new IllegalArgumentException("The distribution function can only return integers and doubles");
			m.invoke(agents.get(i), params);
		}
		return agents;
	}
}
