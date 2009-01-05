package ec.app.tutorial4;
import ec.util.*;
import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;

public class MultiValuedRegression extends GPProblem implements SimpleProblemForm
    {
    public static final String P_DATA = "data";

    public double currentX;
    public double currentY;
    
    public DoubleData input;

    public Object protoClone() throws CloneNotSupportedException
        {
        MultiValuedRegression newobj = (MultiValuedRegression) (super.protoClone());
        newobj.input = (DoubleData)(input.protoClone());
        return newobj;
        }

    public void setup(final EvolutionState state,
                      final Parameter base)
        {
        // very important, remember this
        super.setup(state,base);

        // set up our input -- don't want to use the default base, it's unsafe here
        input = (DoubleData) state.parameters.getInstanceForParameterEq(
            base.push(P_DATA), null, DoubleData.class);
        input.setup(state,base.push(P_DATA));
        }

    public void evaluate(final EvolutionState state, 
                         final Individual ind, 
                         final int threadnum)
        {
        if (!ind.evaluated)  // don't bother reevaluating
            {
            int hits = 0;
            double sum = 0.0;
            double expectedResult;
            double result;
            for (int y=0;y<10;y++)
                {
                currentX = state.random[threadnum].nextDouble();
                currentY = state.random[threadnum].nextDouble();
                expectedResult = currentX*currentX*currentY + currentX*currentY + currentY;
                ((GPIndividual)ind).trees[0].child.eval(
                    state,threadnum,input,stack,((GPIndividual)ind),this);

                result = Math.abs(expectedResult - input.x);
                if (result <= 0.01) hits++;
                sum += result;                  
                }

            // the fitness better be KozaFitness!
            KozaFitness f = ((KozaFitness)ind.fitness);
            f.setFitness(state,(float)sum);
            f.hits = hits;
            ind.evaluated = true;
            }
        }
    }

