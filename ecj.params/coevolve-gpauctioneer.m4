
include(`ecj.m4')
include(`utils.m4') 

define(`NUM_SELLERS', ENV_VAR(`$GP_NS', 3))
define(`NUM_BUYERS', ENV_VAR(`$GP_NB', 3))
define(`SELLER_CAPACITY', ENV_VAR(`$GP_CS', 10))
define(`BUYER_CAPACITY', ENV_VAR(`$GP_CB', 10))

define(`POPULATION_SIZE', 50)
define(`NUM_GENERATIONS', 100)

define(`RESULTS', `CONF_OUTHOME/gpcoevolve-MRE')

define(`PARAM_SUMMARY', `NUM_SELLERS-NUM_BUYERS-SELLER_CAPACITY-BUYER_CAPACITY')

parent.0 = CONF_ECJHOME/ec/simple/simple.params

eval = uk.ac.liv.ec.coevolve.CoEvolutionaryEvaluator

eval.problem = uk.ac.liv.auction.ec.gp.GPCoEvolveAuctionProblem
eval.problem.maxrounds = 1000
eval.problem.ns = NUM_SELLERS
eval.problem.nb = NUM_BUYERS
eval.problem.cs = SELLER_CaPACITY
eval.problem.cb = BUYER_CAPACITY
eval.problem.randomprivatevalues = false
eval.problem.marketstatsfile = RESULTS/marketstats-PARAM_SUMMARY.csv

stat = uk.ac.liv.auction.ec.gp.CoEvolveAuctionStatistics
stat.file = RESULTS/gpcoevolve-PARAM_SUMMARY.out
stat.gather-full = true
stat.serfilenameprefix = RESULTS/gpind-PARAM_SUMMARY.data

generations = NUM_GENERATIONS
checkpoint = false

eval.problem.data = uk.ac.liv.ec.gp.func.GPGenericData
eval.problem.stack.context.data = uk.ac.liv.ec.gp.func.GPGenericData

gp.type.a.size = 2
gp.type.a.0.name = number
gp.type.a.1.name = bool

gp.type.s.size = 0

## Two tree constraints, one for each method we are trying to evolve

gp.tc.size = 2

gp.tc.0 = ec.gp.GPTreeConstraints
gp.tc.0.name = auctioneerTreeConstraints
gp.tc.0.fset = determineClearingPrice
gp.tc.0.returns = number
gp.tc.0.init = ec.gp.koza.HalfBuilder

gp.tc.1 = ec.gp.GPTreeConstraints
gp.tc.1.name = strategyTreeConstraints
gp.tc.1.fset = tradingStrategy
gp.tc.1.returns = number
gp.tc.1.init = ec.gp.koza.HalfBuilder

gp.koza.half.min-depth = 2
gp.koza.half.max-depth = 6
gp.koza.half.growp = 0.5

gp.nc.size = 8

gp.nc.0 = ec.gp.GPNodeConstraints
gp.nc.0.name = binaryarithop
gp.nc.0.size = 2
gp.nc.0.child.0 = number
gp.nc.0.child.1 = number
gp.nc.0.returns = number

gp.nc.1 = ec.gp.GPNodeConstraints
gp.nc.1.name = binaryboolop
gp.nc.1.size = 2
gp.nc.1.child.0 = bool
gp.nc.1.child.1 = bool
gp.nc.1.returns = bool

gp.nc.2 = ec.gp.GPNodeConstraints
gp.nc.2.name = unaryboolop
gp.nc.2.size = 1
gp.nc.2.child.0 = bool
gp.nc.2.returns = bool

gp.nc.3 = ec.gp.GPNodeConstraints
gp.nc.3.name = numberterminal
gp.nc.3.size = 0
gp.nc.3.returns = number

gp.nc.4 = ec.gp.GPNodeConstraints
gp.nc.4.name = comparator
gp.nc.4.size = 2
gp.nc.4.child.0 = number
gp.nc.4.child.1 = number
gp.nc.4.returns = bool

gp.nc.5 = ec.gp.GPNodeConstraints
gp.nc.5.name = numberboolnumbernumber
gp.nc.5.size = 3
gp.nc.5.child.0 = bool
gp.nc.5.child.1 = number
gp.nc.5.child.2 = number
gp.nc.5.returns = number

gp.nc.6 = ec.gp.GPNodeConstraints
gp.nc.6.name = numberboolnumber
gp.nc.6.size = 2
gp.nc.6.child.0 = bool
gp.nc.6.child.1 = number
gp.nc.6.returns = number

gp.nc.7 = ec.gp.GPNodeConstraints
gp.nc.7.name = bool
gp.nc.7.size = 0
gp.nc.7.returns = bool

### Two function sets

gp.fs.size = 2

################### determineClearingPrice

NEW_FUNCTION_SET(determineClearingPrice, 10)

NEW_FUNCTION(uk.ac.liv.ec.gp.func.Add, binaryarithop)
NEW_FUNCTION(uk.ac.liv.ec.gp.func.Subtract, binaryarithop)
NEW_FUNCTION(uk.ac.liv.ec.gp.func.Multiply, binaryarithop)
NEW_FUNCTION(uk.ac.liv.ec.gp.func.Divide, binaryarithop)
NEW_FUNCTION(uk.ac.liv.ec.gp.func.One, numberterminal)
NEW_FUNCTION(uk.ac.liv.ec.gp.func.DoubleERC, numberterminal)
NEW_FUNCTION(uk.ac.liv.auction.ec.gp.func.AskPrice, numberterminal)
NEW_FUNCTION(uk.ac.liv.auction.ec.gp.func.BidPrice, numberterminal)
NEW_FUNCTION(uk.ac.liv.auction.ec.gp.func.QuoteAskPrice, numberterminal)
NEW_FUNCTION(uk.ac.liv.auction.ec.gp.func.QuoteBidPrice, numberterminal)

################### tradingStrategy

NEW_FUNCTION_SET(tradingStrategy, 15)

NEW_FUNCTION(uk.ac.liv.ec.gp.func.Add, binaryarithop)
NEW_FUNCTION(uk.ac.liv.ec.gp.func.Subtract, binaryarithop)
NEW_FUNCTION(uk.ac.liv.ec.gp.func.Multiply, binaryarithop)
NEW_FUNCTION(uk.ac.liv.ec.gp.func.Divide, binaryarithop)
NEW_FUNCTION(uk.ac.liv.ec.gp.func.One, numberterminal)
NEW_FUNCTION(uk.ac.liv.ec.gp.func.Equals, comparator)
NEW_FUNCTION(uk.ac.liv.ec.gp.func.GreaterThan, comparator)
NEW_FUNCTION(uk.ac.liv.ec.gp.func.LessThan, comparator)
NEW_FUNCTION(uk.ac.liv.ec.gp.func.IfElse, numberboolnumbernumber)
NEW_FUNCTION(uk.ac.liv.ec.gp.func.Nand, binaryboolop)
NEW_FUNCTION(uk.ac.liv.ec.gp.func.True, bool)
NEW_FUNCTION(uk.ac.liv.ec.gp.func.DoubleERC, numberterminal)
NEW_FUNCTION(uk.ac.liv.auction.ec.gp.func.PrivateValue, numberterminal)
NEW_FUNCTION(uk.ac.liv.auction.ec.gp.func.QuoteAskPrice, numberterminal)
NEW_FUNCTION(uk.ac.liv.auction.ec.gp.func.QuoteBidPrice, numberterminal)

###############################################

gp.koza.xover.source.0 = ec.select.BestSelection
select.best.n = 10


##




#
# We define the fitness of an individual to use the traditional
# Koza-style fitness metrics, just to make everyone happy :-)
#


init = ec.gp.GPInitializer


define(`NUM_STRATEGIES', eval(NUM_SELLERS+NUM_BUYERS))
define(`NUM_SUBPOPS', eval(NUM_STRATEGIES+1))

pop.subpops = NUM_SUBPOPS



#
# Co-evolution group sizes
#

forloop(`i', 0, NUM_SUBPOPS, `eval.groupsize.i = 1
' )

#
# The auctioneer population
#
pop.subpop.0 = ec.Subpopulation
pop.subpop.0.size = POPULATION_SIZE
pop.subpop.0.fitness = ec.gp.koza.KozaFitness
pop.subpop.0.duplicate-retries = 100
pop.subpop.0.species = ec.gp.GPSpecies
pop.subpop.0.species.ind = uk.ac.liv.auction.ec.gp.func.GPAuctioneer
pop.subpop.0.species.ind.numtrees = 1
pop.subpop.0.species.ind.tree.0 = ec.gp.GPTree
pop.subpop.0.species.ind.tree.0.tc = auctioneerTreeConstraints
pop.subpop.0.species.pipe = ec.breed.MultiBreedingPipeline
pop.subpop.0.species.pipe.generate-max = false
pop.subpop.0.species.pipe.num-sources = 2
pop.subpop.0.species.pipe.source.0 = ec.gp.koza.CrossoverPipeline
pop.subpop.0.species.pipe.source.0.prob = 0.9
pop.subpop.0.species.pipe.source.1 = ec.breed.ReproductionPipeline
pop.subpop.0.species.pipe.source.1.prob = 0.1

#
# the strategy populations
#
define( STRATEGY_SUBPOPULATION, `
pop.subpop.$1 = ec.Subpopulation
pop.subpop.$1.size = POPULATION_SIZE
pop.subpop.$1.fitness = ec.gp.koza.KozaFitness
pop.subpop.$1.duplicate-retries = 100
pop.subpop.$1.species = ec.gp.GPSpecies
pop.subpop.$1.species.ind = uk.ac.liv.auction.ec.gp.func.GPTradingStrategy
pop.subpop.$1.species.ind.numtrees = 1
pop.subpop.$1.species.ind.tree.0 = ec.gp.GPTree
pop.subpop.$1.species.ind.tree.0.tc = strategyTreeConstraints
pop.subpop.$1.species.pipe = ec.breed.MultiBreedingPipeline
pop.subpop.$1.species.pipe.generate-max = false
pop.subpop.$1.species.pipe.num-sources = 2
pop.subpop.$1.species.pipe.source.0 = ec.gp.koza.CrossoverPipeline
pop.subpop.$1.species.pipe.source.0.prob = 0.9
pop.subpop.$1.species.pipe.source.1 = ec.breed.ReproductionPipeline
pop.subpop.$1.species.pipe.source.1.prob = 0.1
' )

forloop(`i', 1, NUM_SUBPOPS, STRATEGY_SUBPOPULATION(i) )



#
# Here we define the default values for Crossover,
# Reproduction, Mutation, as well as our selection
# approaches (Koza I).  These can be overridden on a per-species
# level of course.
#

# Reproduction will use Tournament Selection 
breed.reproduce.source.0 = ec.select.TournamentSelection

# Crossover will use Tournament Selection, try only 1
# time, have a max depth of 17, and use KozaNodeSelector
gp.koza.xover.source.0 = ec.select.TournamentSelection
gp.koza.xover.source.1 = same
gp.koza.xover.ns.0 = ec.gp.koza.KozaNodeSelector
gp.koza.xover.ns.1 = same
gp.koza.xover.maxdepth = 17
# This is the default for Koza and lil-gp, though it's
# a little wimpy; on the other hand, a higher number can
# make things really slow
gp.koza.xover.tries = 1




# Point Mutation will use Tournament Selection, try only 1
# time, have a max depth of 17, and use KozaNodeSelector
# and GROW for building.  Also, Point Mutation uses a GrowBuilder
# by default, with a default of min-depth=max-depth=5
# as shown a ways below
gp.koza.mutate.source.0 = ec.select.TournamentSelection
gp.koza.mutate.ns.0 = ec.gp.koza.KozaNodeSelector
gp.koza.mutate.build.0 = ec.gp.koza.GrowBuilder
gp.koza.mutate.maxdepth = 17
# This is the default for Koza and lil-gp, though it's
# a little wimpy; on the other hand, a higher number can
# make things really slow
gp.koza.mutate.tries = 1




#
# The default tournament size for TournamentSelection is 7
#

select.tournament.size = 7




# Since GROW is only used for subtree mutation, ECJ uses
# the Koza-standard subtree mutation GROW values for the
# default for GROW as a whole.  This default is
# min-depth=max-depth=5, which I don't like very much,
# but hey, that's the standard.  
# This means that if someone decided to use GROW to generate
# new individual trees, it's also use the defaults below
# unless he overrided them locally.
gp.koza.grow.min-depth = 1
gp.koza.grow.max-depth = 3



#
# We specify a few things about ADFs  -- what kind 
# of stack they use, and what kind of context
#

eval.problem.stack = ec.gp.ADFStack
eval.problem.stack.context = ec.gp.ADFContext

# 
# Here we define the default values for KozaNodeSelection;
# as always, these can be overridden by values hanging off
# of the Crossover/Reproduction/Mutation/whatever pipelines,
# like we did for node-building, but hey, whatever. 
# The default is 10% terminals, 90% nonterminals when possible,
# 0% "always pick the root", 0% "pick any node"

gp.koza.ns.terminals = 0.1
gp.koza.ns.nonterminals = 0.9
gp.koza.ns.root = 0.0




