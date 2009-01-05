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
package uchicago.src.repastdemos.enn;

import uchicago.src.sim.games.Game;
import uchicago.src.sim.games.GameAgent;

import java.util.List;


/**
 * A partial implementation of Randal Picker's Endogenous
 * Neighborhood * game as described in his as yet unpublished
 * "Endogenous Neighborhoods and Norms." This simulation extends prior
 * work by Randal Picker. See "Simple Games in a Complex World: A
 * Generative Approach to the Adoption of Norms", 64 University of
 * Chicago Law Review 1225 (1997). Thanks to Randal Picker * for
 * allowing us to include this with Repast. * * Randall Picker's game
 * from Endogneous Neighborhoods and Norms. Here * an agent plays the
 * each of its neighbors in a 5x5 grid centered on the * agent. * *
 * @author Nick Collier and Randal Picker
 * @version $Revision$ $Date$
 */
public class EnnGame extends Game {
	/**
	 * Plays the game. The specified agent plays each of its neighbors
	 * (the * members of the specified List) and its payoff is set
	 * accordingly.
	 *
	 * @param player the agent who plays its
	 * neighboring agents and whose payoff is recalculated.
	 * @param neighbors the neighbors of the playing agent  */
	public void play(GameAgent player, List neighbors) {
		float payoff = 0;
		int playerStrategy = player.getStrategy();

		for (int i = 0; i < neighbors.size(); i++) {
			int nbrStrategy = ((GameAgent) neighbors.get(i)).getStrategy();
			payoff += this.getP1Payoff(playerStrategy, nbrStrategy);
		}

		player.setPayoff(payoff);
	}
}
