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

import java.util.Iterator;

import uchicago.src.sim.engine.AutoStepable;
import uchicago.src.sim.network.DefaultDrawableNode;
import uchicago.src.sim.util.Random;

/**
 * This is the agent that adjusts the location of {@link Competitor}s based on 
 * the correctness of their solution. 
 * <br/>
 * As competitors get closer to the solution they are moved towards the center
 * of the display.
 * 
 * @author Jerry Vos
 * @version $Revision$ $Date$
 */
public class Judge extends DefaultDrawableNode implements AutoStepable {
	
	private CompetitionSpace space;
	
	private double desiredAmount	= 0.0;
	
	public Judge(CompetitionSpace space, double x, double y) {
		this.space = space;
		this.setX(x);
		this.setY(y);
	}

	public void preStep() {
	}

	public void step() {
	}

	/*
	 * The following functions are used to compute the coordinates of the agent.
	 * They calculate the angle to move the agent on, and the normalized distance
	 * the agent is from the center (normalized to [0, 1]). 
	 */
	
	public double[] screenCoordsToNormalizedCartesianCoords(double x, double y) {
		// divide width/height by two
		double width	= space.getWidth() / 2.0;
		double height	= space.getHeight() / 2.0;
		
		double cartesianX = x - width;
		double cartesianY = (y > height ? height - y : -(y - height));
//		System.out.println("cx: " + cartesianX + ", cy:" + cartesianY);
		double normalizedCX = cartesianX / width;
		double normalizedCY = cartesianY / height;
		
		return new double[] { normalizedCX, normalizedCY };
	}
	
	public double[] normalizedCartesianCoordsToScreenCoords(double x, double y) {
		double width	= space.getWidth() / 2.0;
		double height	= space.getHeight() / 2.0;
		
		double cartesianX = x * width;
		double cartesianY = y * height;

		double screenX = cartesianX + width;
		double screenY = (cartesianY > height ? -height - cartesianY : (-cartesianY + height));
				
		return new double[] { screenX, screenY };
	}
	
	
	public int calcDistFromOptimal(int targetAmount, CoinStruct currentAmount) {
        int totalCoins = currentAmount.quarters + currentAmount.dimes
			+ currentAmount.nickels + currentAmount.pennies;
		
		int coinCountDist = 
			Math.abs(totalCoins - computeOptimalCoinCount(targetAmount));
		
		
		int changeAmount = currentAmount.quarters * 25 + currentAmount.dimes * 10 +
			currentAmount.nickels * 5 + currentAmount.pennies;
			
		int coinValDist = Math.abs(targetAmount - changeAmount);
		
		return coinCountDist + coinValDist;
	}
		
	private double getTheta(Competitor comp) {
		double coords[] = 
			this.screenCoordsToNormalizedCartesianCoords(comp.getX(), comp.getY());
//		System.out.println("x: " + coords[0] + ", y:" + coords[1]);
		// rise over run
		if (coords[0] == 0)
			coords[0] = .00001;
		
		return Math.atan(coords[1] / coords[0]);
	}
	
	private double getAngle(boolean inLeftQuads, double angle) {
		return (inLeftQuads ? Math.PI + angle : angle);
	}
	
	public int computeOptimalCoinCount(int amountInCents) {
		int numCoins = amountInCents / 25;
		amountInCents %= 25;
		
		numCoins += amountInCents / 10;
		amountInCents %= 10;
		
		numCoins += amountInCents / 5;
		amountInCents %= 5;
		
		numCoins += amountInCents;
		
		return numCoins;
	}
	
	public void postStep() {
		for (Iterator iter = space.getCompetitors().iterator(); iter.hasNext();) {
			Competitor comp = (Competitor) iter.next();

			CoinStruct coins = comp.getCoinCount();
			// distance from optimal (0 tasks is optimal)
			int dist = calcDistFromOptimal((int) (this.desiredAmount * 100), coins);
			
			// the normalized distance
			double hypotenuse = dist / 200.0;
			
			// the angle the agent is from the center of the office
			double theta = getTheta(comp);
			
//			System.out.println("theta: " + theta);
			
			// the agent is moved along a line with angle theta (with the 
			// +x axis) to a distance of frac.  This adjusts the angle based
			// on what quadrant the agent is in
			theta = getAngle(comp.getX() < space.getWidth() / 2, theta);

			// add some random deviation
			theta += Random.uniform.nextDoubleFromTo(-Math.PI / 8, Math.PI / 8);
			
			// the new agent location
			double newX = hypotenuse * Math.cos(theta);
			double newY = hypotenuse * Math.sin(theta);
			
			double[] screenCoords = normalizedCartesianCoordsToScreenCoords(newX, newY);
//			System.out.println(screenCoords[0]);
//			System.out.println(screenCoords[1]);
			comp.setX(screenCoords[0]);
			comp.setY(screenCoords[1]);
		}
	}
	
	/**
	 * @return Returns the desiredAmount.
	 */
	public double getDesiredAmount() {
		return desiredAmount;
	}
	
	/**
	 * @param desiredAmount The desiredAmount to set.
	 */
	public void setDesiredAmount(double desiredAmount) {
		this.desiredAmount = desiredAmount;
	}

	
	/**
	 * used for testing the coordinate functions
	 */
	public static void main(String[] args) {
		CompetitionSpace space = new CompetitionSpace(40, 80);
		Judge judge = new Judge(space, 0, 0);
		
		double[] cs = judge.screenCoordsToNormalizedCartesianCoords(10, 20);
		System.out.println("(" + cs[0] + ", " + cs[1] + ")");
		cs = judge.normalizedCartesianCoordsToScreenCoords(cs[0], cs[1]);
		System.out.println("(" + cs[0] + ", " + cs[1] + ")");
		
		cs = judge.screenCoordsToNormalizedCartesianCoords(30, 10);
		System.out.println("(" + cs[0] + ", " + cs[1] + ")");
		cs = judge.normalizedCartesianCoordsToScreenCoords(cs[0], cs[1]);
		System.out.println("(" + cs[0] + ", " + cs[1] + ")");
		
		cs = judge.screenCoordsToNormalizedCartesianCoords(15, 50);
		System.out.println("(" + cs[0] + ", " + cs[1] + ")");
		cs = judge.normalizedCartesianCoordsToScreenCoords(cs[0], cs[1]);
		System.out.println("(" + cs[0] + ", " + cs[1] + ")");
		
		cs = judge.screenCoordsToNormalizedCartesianCoords(35, 70);
		System.out.println("(" + cs[0] + ", " + cs[1] + ")");
		cs = judge.normalizedCartesianCoordsToScreenCoords(cs[0], cs[1]);
		System.out.println("(" + cs[0] + ", " + cs[1] + ")");
		
		System.exit(0);
	}
}