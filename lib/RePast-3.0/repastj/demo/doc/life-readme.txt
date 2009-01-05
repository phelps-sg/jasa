
This is an implementation of John Conway's game of life. Life is
typically played on an "infinite" square grid. Each cell can be either
live or dead. A cell comes to life if it has three neighboring live
cells, and will die of "loneliness" if it has less than two neighbors,
and will die of overcrowding if it has more than three neighbors.

Particular starting patterns of live cells can yield some very
interesting results. This implementation provides a rather bland
random initialization as well as the R-Pentimino. You can create your
own starting pattern by choosing Empty as the initial pattern and
stepping one tick into the simulation to show the display. You can
then click on the display to add live cells wherever you want. Percent
full is used to specify the percent full for the random initial
pattern.  This implementation provides an "infinite" (a really large)
grid as well as a torus of user-defined size. When using the the
infinite space you can scroll the display to see what's happening
off-screen. Make sure the display frame has focus, and then press the
arrow keys to scroll the display.

The web is littered with web pages devoted to Conway's
Life. One such is http://www.math.com/students/wonders/life/life.html.

These source for this simulation can be found at 
repast/src/uchicago/src/sim/life