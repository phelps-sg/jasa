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

import java.lang.reflect.Constructor;

import cern.jet.random.AbstractDistribution;
import cern.jet.random.engine.DRand;
//import edu.cornell.lassp.houle.RngPack.RandomElement;

/**
 * A class to easily generate statistical distributions for the AgentDistribution
 * class.  These distributions are from the colt library.  All of the distributions
 * require a set of doubles. For information on 
 * these distributions and the parameter lists, see the colt documentation.<P>
 * Some examples include: <P>
 * chi-squared : double degrees of freedom
 * pareto: double scale, double shape
 * poisson: double mean
 * uniform: double min, double max
 * <P>
 * Note: The binomial distribution cannot be created from this Factory.
 * @author $Author$
 * @version $Revision$ $Date$
 **/
public class DistributionFactory{
	private AbstractDistribution dist;
	private DRand rand;
	
	public DistributionFactory(){
		rand = new DRand();
		dist = null;
	}

	/**
	 * Creates an Abstract Distribution.  Possible distributions include Beta, 
	 * BreitWigner, BreitWignerMeanSquare, ChiSquare, Exponential, ExponentialPower
	 * Gamma, Hyperbolic, Logarithmic, Normal, Pareto, ParetoII, Poisson, 
	 * PossionSlow, StudentT, Uniform, VonMises, Zeta.
	 **/
	public AbstractDistribution createDistribution(String distClassName, double[] params) throws IllegalArgumentException{
			int numParam = params.length;
			Class[] paramClasses = new Class[params.length + 1];
			Object[] newParams = new Object[params.length + 1];
			for(int i = 0 ; i < numParam ; i++){
				paramClasses[i] = Double.TYPE;
				newParams[i] = new Double(params[i]);
			}
		  try{
				paramClasses[numParam] = Class.forName("edu.cornell.lassp.houle.RngPack.RandomElement");
				newParams[numParam] = rand;
		    Class distClass = Class.forName(distClassName);
			  Constructor c = distClass.getConstructor(paramClasses);
			  dist = (AbstractDistribution) c.newInstance(newParams);
				return dist;
			}catch(Exception e){
				throw new IllegalArgumentException(e.getMessage());
			}
	   }
		
	/**
	 * Statically creates an Abstract Distribution.  Possible distributions 
	 * include Beta, BreitWigner, BreitWignerMeanSquare, ChiSquare, Exponential, 
	 * ExponentialPower, Gamma, Hyperbolic, Logarithmic, Normal, Pareto, 
	 * ParetoII, Poisson,  PossionSlow, StudentT, Uniform, VonMises, Zeta.
	 **/
	public static AbstractDistribution staticCreateDistribution(String distClass, double[] params) throws IllegalArgumentException{
		AbstractDistribution ad = null;
		DRand drand = new DRand();
			int numParam = params.length;
			Class[] paramClasses = new Class[numParam + 1];
			Object[] newParams = new Object[numParam + 1];
			for(int i = 0 ; i < numParam ; i++){
				paramClasses[i] = Double.TYPE;
				newParams[i] = new Double(params[i]);
			}
			try{
			  paramClasses[numParam] = Class.forName("edu.cornell.lassp.houle.RngPack.RandomElement");
			  newParams[numParam] = drand;
		  	Class dist = Class.forName(distClass);
			  Constructor c = dist.getConstructor(paramClasses);
			  ad =  (AbstractDistribution) c.newInstance(newParams);
			}catch(Exception e){
				throw new IllegalArgumentException(e.getMessage());
			}
		return ad;
	}
}
