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


/**
 * Abstracts a prisoner's dilemma cooperation type game. Stores the
 * payoffs for each player depending on what the other player played.
 *
 * @author Nick Collier
 * @version 1.0 8/25/99
 */

public abstract class Game {

  /*
   * payoff matrices:
   *
   *                  player 2
   *
   *               left        right
   *
   *          left   val        val
   * player 1
   *          right  val        val
   *
   *
   * One for each player.
   */

  /*
  private float[][] p1Payoff = new float[2][2];
  private float[][] p2Payoff = new float[2][2];
  */

  private float[] p1Payoff = new float[4];
  private float[] p2Payoff = new float[4];

  public void setPayoffs(PayoffPair leftLeft, PayoffPair leftRight,
                        PayoffPair rightLeft, PayoffPair rightRight)
  {
    /*
    p1Payoff[GameAgent.LEFT][GameAgent.LEFT] = leftLeft.playerOnePayoff;
    p2Payoff[GameAgent.LEFT][GameAgent.LEFT] = leftLeft.playerTwoPayoff;

    p1Payoff[GameAgent.LEFT][GameAgent.RIGHT] = leftRight.playerOnePayoff;
    p2Payoff[GameAgent.LEFT][GameAgent.RIGHT] = leftRight.playerTwoPayoff;

    p1Payoff[GameAgent.RIGHT][GameAgent.LEFT] = rightLeft.playerOnePayoff;
    p2Payoff[GameAgent.RIGHT][GameAgent.LEFT] = rightLeft.playerTwoPayoff;

    p1Payoff[GameAgent.RIGHT][GameAgent.RIGHT] = rightRight.playerOnePayoff;
    p2Payoff[GameAgent.RIGHT][GameAgent.RIGHT] = rightRight.playerTwoPayoff;
    */

    p1Put(GameAgent.LEFT, GameAgent.LEFT, leftLeft.playerOnePayoff);
    p2Put(GameAgent.LEFT, GameAgent.LEFT, leftLeft.playerTwoPayoff);

    p1Put(GameAgent.LEFT, GameAgent.RIGHT, leftRight.playerOnePayoff);
    p2Put(GameAgent.LEFT, GameAgent.RIGHT, leftRight.playerTwoPayoff);

    p1Put(GameAgent.RIGHT, GameAgent.LEFT, rightLeft.playerOnePayoff);
    p2Put(GameAgent.RIGHT, GameAgent.LEFT, rightLeft.playerTwoPayoff);

    p1Put(GameAgent.RIGHT, GameAgent.RIGHT, rightRight.playerOnePayoff);
    p2Put(GameAgent.RIGHT, GameAgent.RIGHT, rightRight.playerTwoPayoff);
  }

  private void p1Put(int row, int col, float val) {
    p1Payoff[row * 2 + col] = val;
  }

  private void p2Put(int row, int col, float val) {
    p2Payoff[row * 2 + col] = val;
  }

  public float getP1Payoff(int onePlay, int twoPlay) {
    return p1Payoff[onePlay * 2 + twoPlay];
  }

  public float getP2Payoff(int onePlay, int twoPlay) {
    return p2Payoff[onePlay * 2 + twoPlay];
  }
}
