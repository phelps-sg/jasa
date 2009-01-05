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
package uchicago.src.repastdemos.genetic;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

import uchicago.src.sim.engine.AutoStepable;
import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.network.DefaultDrawableNode;
import uchicago.src.sim.util.RepastException;

/**
 * This is an agent in the simulation.  The brains of the agent are represented
 * by a genetic algorithm.
 * 
 * @author Jerry Vos
 * @version $Revision$ $Date$
 */
public class Competitor extends DefaultDrawableNode implements AutoStepable {
	private static int baseIdNumber;

	private static Image competitorPicture;
	
	private CoinStruct coinCount	= null;

	private Judge judge;
	
	private double oldDesiredAmount;
	
	private double error			= 0.0;

	
	private Genotype population;
	
	private Configuration gaConfiguration;
	
	
	public Competitor(double x, double y, Judge judge) throws RepastException {
		super(new OvalNetworkItem(x, y));

		this.judge = judge;
		
		loadCompetitorPicture();
		
		buildGA();
		
		this.setHeight(5);
		this.setWidth(2);
		this.setNodeLabel("Competitor " + ++baseIdNumber);
	}


	
	private static void loadCompetitorPicture() {
		if (competitorPicture == null) {
			java.net.URL employeePicURL = Competitor.class.getResource("person.gif");
			competitorPicture = new ImageIcon(employeePicURL).getImage(); 
		}
	}
	
	public static void resetIndices() {
		baseIdNumber = 0;
	}
	
	private void buildGA() throws RepastException {
		try {
	        // Start with a DefaultConfiguration, which comes setup with the
	        // most common settings.
	        gaConfiguration = new DefaultConfiguration();
	
	        // Load the fitness function into the configuration.  This converts
	        // the desired amount of money into an integral cent value.
	        loadFitnessFunction(gaConfiguration,
	        					(int) (this.judge.getDesiredAmount() * 100));
		        
	        // Now we need to tell the Configuration object how we want our
	        // Chromosomes to be setup. We do that by actually creating a
	        // sample Chromosome and then setting it on the Configuration
	        // object. As mentioned earlier, we want our Chromosomes to each
	        // have four genes, one for each of the coin types. We want the
	        // values (alleles) of those genes to be integers, which represent
	        // how many coins of that type we have. We therefore use the
	        // IntegerGene class to represent each of the genes. That class
	        // also lets us specify a lower and upper bound, which we set
	        // to sensible values for each coin type.
	        Gene[] sampleGenes = new Gene[4];
	
	        sampleGenes[0] = 
	        	new IntegerGene(0, 3);  // Quarters
	        sampleGenes[1] = 
	        	new IntegerGene(0, 2);  // Dimes
	        sampleGenes[2] = 
	        	new IntegerGene(0, 1);  // Nickels
	        sampleGenes[3] = 
	        	new IntegerGene(0, 4);  // Pennies
	
	        Chromosome sampleChromosome = new Chromosome( sampleGenes );
	
	        gaConfiguration.setSampleChromosome( sampleChromosome );
	
	        // Finally, we need to tell the Configuration object how many
	        // Chromosomes we want in our population. The more Chromosomes,
	        // the larger number of potential solutions (which is good for
	        // finding the answer), but the longer it will take to evolve
	        // the population (which could be seen as bad). 
	        // We'll just set the population size to 3 here because the
	        // problem this GA is solving is so simple that if we don't it
	        // will always solve it in the first step.
	        gaConfiguration.setPopulationSize( 3 );
	
	        // Create random initial population of Chromosomes.
	        population = Genotype.randomInitialGenotype( gaConfiguration );
		} catch (Exception ex) {
			throw new RepastException("Error building genetic algorithm", ex);
		}
	}

	private void loadFitnessFunction(Configuration conf, int amountInCents) throws InvalidConfigurationException {
		// if the conditions we are looking for have changed, we have to load in
		// a new fitness function
		double desiredAmount = judge.getDesiredAmount();
		
		if (desiredAmount == oldDesiredAmount)
			return;
		
		// Set the fitness function we want to use, which is our
        // MinimizingMakeChangeFitnessFunction. We construct it with
        // the target amount of change passed in to this method.
        FitnessFunction myFunc =
            new MinimizingMakeChangeFitnessFunction(amountInCents);
        
        conf.setFitnessFunction(myFunc);
        
        oldDesiredAmount = desiredAmount;
	}
	
	/**
	 * This trains the agent by evolving the population once more
	 */
	private synchronized void train() {
//		System.out.println("training");
		population.evolve();
	}
	
	/**
	 * @return the value the GA returns
	 */
	private synchronized CoinStruct retrieve() {
		return MinimizingMakeChangeFitnessFunction
				.getCoinsAtGene(population.getFittestChromosome());
	}
	
	public void preStep() throws RepastException {
		try {
			// if the conditions we are looking for have changed, we have to load in
			// a new fitness function
			double desiredAmount = judge.getDesiredAmount();
			
			loadFitnessFunction(this.gaConfiguration, (int) (desiredAmount * 100));
			
			train();
		} catch (Exception ex) {
			throw new RepastException("Error prestepping " + this.getNodeLabel(), ex);
		}
	}

	public void step() throws RepastException {
		// query the GA for the current best solution so we know how many coins
		// to present to the judge
		coinCount = retrieve();
	}
	
	public void postStep() {
		
	}

	public void draw(SimGraphics g) {
//		super.draw(g);
		// draw the employee's picture
		g.drawImage(competitorPicture);
		
		// grab the width of the picture
		int width = competitorPicture.getWidth(null);
		
		g.setFont(super.getFont());
		
		// get the size of the node's text
		Rectangle2D bounds = g.getStringBounds(this.getNodeLabel());
		
		// set the graphics to draw the text above the label
		// the x coordinate is relative to the upper left corner of the image
		// so the coordinates are shifted to account for that
		g.setDrawingCoordinates((float) (this.getX() + width / 2.0 - bounds.getWidth() / 2.0),
								(float) (this.getY() - bounds.getHeight() - 2),
								0f);
		
		// draw the label
		g.drawString(getNodeLabel(), Color.BLACK);
	}
	
	public CoinStruct getCoinCount() {
		return coinCount;
	}

	/**
	 * @return returns the neural network's error
	 */
	public double getError() {
		return error;
	}
}