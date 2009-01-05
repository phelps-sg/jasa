Mouse Trap is a port of the mousetrap simulation from the 
Swarm simulation toolkit. It is a good example of dynamic 
scheduling and how to do discrete-event simulations using 
Schedule objects. 

The simulation is as follows. A torus is populated with "mousetraps."
These traps each contain some n number of balls. A ball is thrown from
the "outside" onto the center mousetrap. This trap triggers and throws
its n number of balls into the air. These balls then trigger other traps
and so on and so on. In terms of implementation, the first trap has its
trigger method scheduled at 0 with Schedule.scheduleActionAt. When
a mousetrap is triggered it schedules a trigger method on n of its
surrounding mousetraps, where n is the number of balls each mousetrap holds.
The actual scheduling is done through the use of a TriggerAction class that
extends BasicAction. This TriggerAction is passed the MouseTrap to schedule
in its constructor, and its execute method calls trigger on this MouseTrap. 

The simulation can be run on Windows by running the jiggle.bat batch file from
within the hyper directory, or by double clicking on the trap.jar file 
If using Unix, use the mouse.sh script.

The source for this simulation can be found in 
repast/src/uchicago/src/sim/mousetrap.
