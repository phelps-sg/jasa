package uk.ac.liv.util;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class AbstractSeeder extends AbstractSeedable
    implements  Parameterizable, Seeder {

  protected long prngSeed;

  public static final String P_PRNG = "prng";
  public static final String P_SEED = "seed";

  public void setup( ParameterDatabase parameters, Parameter base ) {

    uk.ac.liv.prng.PRNGFactory.setup(parameters, base.push(P_PRNG));

    prngSeed =
        parameters.getLongWithDefault(base.push(P_SEED), null,
                                      System.currentTimeMillis());
    setSeed(prngSeed);

  }

  public long nextSeed() {
    return prng.choose(0, Integer.MAX_VALUE);
  }


}