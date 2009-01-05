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
package uchicago.src.sim.games;

import java.util.List;

import uchicago.src.sim.util.Random;

/**
 * Utility methods for payoff matrix games.
 */

public class GameUtilities {

  /**
   * Returns the best (highest payoff) strategy for the last round. This
   * calls getStrategy and getPayoff on each GameAgent in the list and
   * returns which strategy had the highest total payoff. Strategies are
   * defined as int constants in GameAgent (GameAgent.LEFT and
   * GameAgent.RIGHT). If the totalPayoffs are equal then one will be
   * picked at random. Note that this does plain old floating point addition
   * and so the result is not exactly accurate. 
   *
   * @param gameAgents a list of GameAgents
   */
  public static int getBestStrategy(List gameAgents) {
    double leftTotal = 0;
    double rightTotal = 0;

    int size = gameAgents.size();
    for (int i = 0; i < size; i++) {
      GameAgent agent = (GameAgent)gameAgents.get(i);
      if (agent.getStrategy() == GameAgent.LEFT)
	leftTotal += agent.getPayoff();
      else
	rightTotal += agent.getPayoff();
    }

    if (leftTotal == rightTotal) {
      int num = Random.uniform.nextIntFromTo(0, 1);
      return num == 1 ? GameAgent.LEFT : GameAgent.RIGHT;
    } else if (leftTotal > rightTotal) return GameAgent.LEFT;
    else return GameAgent.RIGHT;
  }
}
