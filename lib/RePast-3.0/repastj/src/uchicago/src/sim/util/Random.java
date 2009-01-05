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

import java.util.Date;

import uchicago.src.sim.math.Pareto;
import cern.jet.random.Beta;
import cern.jet.random.Binomial;
import cern.jet.random.BreitWigner;
import cern.jet.random.BreitWignerMeanSquare;
import cern.jet.random.ChiSquare;
import cern.jet.random.Distributions;
import cern.jet.random.Empirical;
import cern.jet.random.EmpiricalWalker;
import cern.jet.random.Exponential;
import cern.jet.random.ExponentialPower;
import cern.jet.random.Gamma;
import cern.jet.random.HyperGeometric;
import cern.jet.random.Hyperbolic;
import cern.jet.random.Logarithmic;
import cern.jet.random.NegativeBinomial;
import cern.jet.random.Normal;
import cern.jet.random.Poisson;
import cern.jet.random.PoissonSlow;
import cern.jet.random.StudentT;
import cern.jet.random.Uniform;
import cern.jet.random.VonMises;
import cern.jet.random.Zeta;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;

/**
 * Encapsulates the colt library's random number generation. The Random class
 * consolidates all the random number distributions provided by the
 * colt library into a single class. The intent here is two-fold - to
 * provide easier access
 * to these distributions and more importantly to insure that each
 * distribution uses the same random number stream, allowing for easily
 * repeatable behavoir of random number generation.<p>
 *
 * Random contains a variety of random number distributions as static
 * instance variables as well as a few next* methods for returning the
 * next psuedo-random value from a distribution. Before using any of the
 * instance variable distributions, these distributions must be created via
 * the appropriate create* method. For example,
 * <code><pre>
 * // initialize Random.uniform
 * Random.createUniform();
 *
 * // Random.uniform is now initialized and can be used.
 * int index = Random.uniform.getNextIntFromTo(0, 100);
 * </pre></code>
 *
 * Once created a distribution can be used anywhere in your code through the
 * use of Random's static instance variables. So for example, if you create
 * the Uniform distribution in your model's begin() method, you can access the
 * same Uniform distribution in your model's agent code with
 * <code>Random.uniform</code>. This instance variable should always be
 * referenced as such, that is, as Random.<distribution name>, and not as
 * the right hand side of an assignment. Assigning a variable in this way
 * can lead to unpredictable results when setting the random seed.<p>
 *
 * All the distributions in Random are from the colt library. The instance
 * variable name is the same as the corresponding colt object with the first
 * character in lower case. For example, the Zeta distribution is called zeta.
 * See the colt library documentation in <code>repast/docs/colt</code> for more
 * information about these distributions.
 *
 * The next* methods can be used as is, without creating a distribution
 * before hand. For example,
 * <code><pre>
 * double s = Random.nextCauchy();
 * </pre></code><p>
 *
 * Random also allows you to set and get the random seed as well as the
 * random number generator associated with that seed. Setting a new seed with
 * <code>setSeed</code> will create a new random number generator and
 * <b>invalidate</b> any previously created distributions. If you wish to use
 * a distribution after the seed has been set, you must create it as
 * described above. All of the distributions contained by Random will use the
 * same generator - a MersenneTwister. Consequently, once the seed is set
 * all the distributions will use the same generator, same random
 * number stream and repeatable randomness can be easily achieved.<p>
 *
 * Note that Random creates a default random number generator using the
 * current timestamp as the seed. If you do not explicitly set your own seed,
 * the distributions created via the create* calls will use this default
 * generator.<p>
 *
 *
 * @see "The RePast random how to in repast/docs/how_to/random.html"
 * @see "The colt library docs in repast/docs/colt/index.html"
 */

public class Random {

  public static final int LINEAR_INTERPOLATION = Empirical.LINEAR_INTERPOLATION;
  public static final int NO_INTERPOLATIONN = Empirical.NO_INTERPOLATION;

  private static long rngSeed;
  private static RandomEngine generator;

  public static Beta beta;
  public static Binomial binomial;
  public static BreitWigner breitWigner;
  public static BreitWignerMeanSquare breitWignerMeanSquare;
  public static ChiSquare chiSquare;
  public static Empirical empirical;
  public static EmpiricalWalker empiricalWalker;
  public static Exponential exponential;
  public static ExponentialPower exponentialPower;
  public static Gamma gamma;
  public static Hyperbolic hyperbolic;
  public static HyperGeometric hyperGeometric;
  public static Logarithmic logarithmic;
  public static NegativeBinomial negativeBinomial;
  public static Normal normal;
  public static Pareto pareto;
  public static Poisson poisson;
  public static PoissonSlow poissonSlow;
  public static StudentT studentT;
  public static Uniform uniform;
  public static VonMises vonMises;
  public static Zeta zeta;

  static {
    Date d = new Date();
    rngSeed = d.getTime();
    generator = new MersenneTwister(d);
  }

  public static double geometricPdf(int k, double p) {
    return Distributions.geometricPdf(k, p);
  }

  public static double nextBurr1(double r, int nr) {
    return Distributions.nextBurr1(r, nr, generator);
  }

  public static double nextBurr2(double r, double k, int nr) {
    return Distributions.nextBurr2(r, k, nr, generator);
  }

  public static double nextCauchy() {
    return Distributions.nextCauchy(generator);
  }

  public static double nextErlang(double variance, double mean) {
    return Distributions.nextErlang(variance, mean, generator);
  }

  public static int nextGeometric(double p) {
    return Distributions.nextGeometric(p, generator);
  }

  public static double nextLambda(double l3, double l4) {
    return Distributions.nextLambda(l3, l4, generator);
  }

  public static double nextLaplace() {
    return Distributions.nextLaplace(generator);
  }

  public static double nextLogistic() {
    return Distributions.nextLogistic(generator);
  }

  public static double nextPowLaw(double alpha, double cut) {
    return Distributions.nextPowLaw(alpha, cut, generator);
  }

  public static double nextTriangular() {
    return Distributions.nextTriangular(generator);
  }

  public static double nextWeibull(double alpha, double beta) {
    return Distributions.nextWeibull(alpha, beta, generator);
  }

  public static int nextZipfInt(double z) {
    return Distributions.nextZipfInt(z, generator);
  }

  public static void createZeta(double ro, double pk) {
    zeta = new Zeta(ro, pk, generator);
  }

  public static void createVonMises(double freedom) {
    vonMises = new VonMises(freedom, generator);
  }

  public static void createPareto(double loc, double shape) {
    pareto = new uchicago.src.sim.math.Pareto(loc, shape, generator);
  }

  public static void createUniform(double min, double max) {
    uniform = new Uniform(min, max, generator);
  }

  public static void createUniform() {
    uniform = new Uniform(generator);
  }

  public static void createStudentT(double freedom) {
    studentT = new StudentT(freedom, generator);
  }

  public static void createPoissonSlow(double mean) {
    poissonSlow = new PoissonSlow(mean, generator);
  }

  public static void createPoisson(double mean) {
    poisson = new Poisson(mean, generator);
  }

  public static void createNormal(double mean, double standardDeviation) {
    normal = new Normal(mean, standardDeviation, generator);
  }

  public static void createNegativeBinomial(int n, double p) {
    negativeBinomial = new NegativeBinomial(n, p, generator);
  }

  public static void createLogarithmic(double p) {
    logarithmic = new Logarithmic(p, generator);
  }

  public static void createHyperGeometric(int N, int s, int n) {
    hyperGeometric = new HyperGeometric(N, s, n, generator);
  }

  public static void createHyperbolic(double alpha, double beta) {
    hyperbolic = new Hyperbolic(alpha, beta, generator);
  }

  public static void createGamma(double alpha, double lambda) {
    gamma = new Gamma(alpha, lambda, generator);
  }

  public static void createExponentialPower(double tau) {
    exponentialPower = new ExponentialPower(tau, generator);
  }

  public static void createExponential(double lambda) {
    exponential = new Exponential(lambda, generator);
  }

  public static void createEmpiricalWalker(double[] pdf,
      int interpolationType)
  {
    empiricalWalker = new EmpiricalWalker(pdf, interpolationType, generator);
  }

  public static void createEmpirical(double[] pdf, int interpolationType) {
    empirical = new Empirical(pdf, interpolationType, generator);
  }

  public static void createChiSquare(double freedom) {
    chiSquare = new ChiSquare(freedom, generator);
  }

  public static void createBreitWignerMeanSquareState(double mean,
      double gamma, double cut)
  {
    breitWignerMeanSquare = new BreitWignerMeanSquare(mean, gamma, cut, generator);
  }

  public static void createBreitWigner(double mean, double gamma, double cut) {
    breitWigner = new BreitWigner(mean, gamma, cut, generator);
  }

  public static void createBinomial(int n, double p) {
    binomial = new Binomial(n, p, generator);
  }

  public static void createBeta(double alpha, double beta) {
    Random.beta = new Beta(alpha, beta, generator);
  }

  /**
   * Generates a new random number generator using the
   * the current timestamp as a the seed.
   * This will <b>invalidate</b> any previously created
   * distributions.
   */
  public static RandomEngine generateNewSeed() {
    Date d = new Date();
    rngSeed = d.getTime();
    generator = new MersenneTwister(d);
    invalidateDists();
    return generator;
  }

  /**
   * Gets the current random number generator.
   */
  public static RandomEngine getGenerator() {
    return generator;
  }

  /**
   * Creates a new random number generator with the specified
   * seed and returns this new generator.
   * This will <b>invalidate</b> any previously created
   * distributions.
   *
   * @param seed the new generator seed
   *
   * @return the new generator
   */
  public static RandomEngine getGenerator(long seed) {
    rngSeed = seed;
    generator = new MersenneTwister((int)seed);
    invalidateDists();
    return generator;
  }

  /**
   * Creates a new random number generator with the specified
   * seed. This will <b>invalidate</b> any previously created
   * distributions.
   *
   * @param seed the seed for the new generator
   */
  public static void setSeed(long seed) {
    getGenerator(seed);
  }

  private static void invalidateDists() {
    beta = null;
    binomial = null;
    breitWigner = null;
    breitWignerMeanSquare = null;
    chiSquare = null;
    empirical = null;
    empiricalWalker = null;
    exponential = null;
    exponentialPower = null;
    gamma = null;
    hyperbolic = null;
    hyperGeometric = null;
    logarithmic = null;
    negativeBinomial = null;
    normal = null;
    poisson = null;
    poissonSlow = null;
    studentT = null;
    uniform = null;
    vonMises = null;
    zeta = null;

  }

  /**
   * Gets the current randon number generator seed.
   */
  public static long getSeed() {
    return rngSeed;
  }
}
