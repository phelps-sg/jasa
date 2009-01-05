This is an implementation of the second model (model II) described in
Jin, Girvan, and Newman, "The Structure of Growing Social Networks."
Santa Fe Institute 2001 working paper. This paper can be found on-line
at http://www.santafe.edu/sfi/publications/Abstracts/01-06-032abs.html

Their abstract follows:

"We propose some simple models of the growth of social networks, based
on three general principles: (1) meetings take place between pairs of
individuals at a rate which is high if a pair has one or more mutual
friends and low otherwise; (2) acquaintances between pairs of
individuals who rarely meet decay over time; (3) there is an upper
limit on the number of friendships an individual can maintain. using
computer simulations, we find that models that incorporate all of
these features reproduce many of the features of real social networks,
including high levels of clustering or network transitivity and strong
community structure in which individuals have more links to others
within their community than to individuals from other communities."

In this implementation, links formed at random are colored green, and
those formed on the basis of shared "neighbors" are colored red.

Parameter notes:

Many of the parameters are described in the paper. Those that are not
are described below.

degree hist: if checked then show a histogram of node degree.

layout type: there are three choices here. Fruch (Fruchmann Reingold
graph layout), KK (Kamada Kawai graph layout) and circular. Fruch and
KK are fairly slow. Fruch gives much better layouts once the model has
been run for a while. Both will be interrupted if you click pause,
stop or step.

plot: plots the cluster coefficient, density, and number of components
vs. tick count.

updateEveryN: both the Fruch and the KK layouts involve many
iterations through the list of nodes, altering their x and y
coordinates each time.  The display will update to show these new x
and y coordinates every n iterations where n is the value of
updateEveryN. This helps to visualize the structure of the network.

Zooming -- you can zoom in on parts of the network while the
simulation is paused. To do this, drag the mouse while holding down
the ctrl key to draw a box around the part of the network you wish to
zoom in on. Press the z key to zoom in and r to zoom out.

Speed -- the simulation can be fairly slow when run with the Fruch or
KK layouts. You can speed it up by setting updateEveryN to 0, or
better, minimizing the display window and maximizing it when you want
to see the current structure.


These source for this simulation can be found at 
repast/src/uchicago/src/sim/jinGirNew


