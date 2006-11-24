export CP=.:/LOCAL/moyaux/workspace/jasa/bin/
for jar in `ls /LOCAL/moyaux/lib/jar/*.jar`; do export CP=$CP:$jar; done
echo $CP
java -classpath $CP uk.ac.liv.auction.RepastSupplyChainSimulation $1
