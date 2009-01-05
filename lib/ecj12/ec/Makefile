#
# Makefile created by Jeff Bassett, with some
# tweaks by Sean Luke
#
# We assume that you have jikes on your system.  It's a Java compiler
# written in C++ by IBM that's far *far* faster than javac.  You can
# get it at http://oss.software.ibm.com/developerworks/opensource/jikes/`
# To just use javac, simply change the JAVAC flag below as appropriate.
#
# To compile all java but teambots API:	make 
# To compile everything + teambots API:	make teambots
# To delete class files:		make clean
# To build the documentation:		make docs
# To build using jikes for sure:	make jikes
# To build using javac for sure:	make javac
# To auto-indent using Emacs:		make indent
# [also, used here at GMU, you can ignore it...]
# Prepare for distribution:		make dist
#

#JAVAC = javac
JAVAC = jikes
FLAGS = -g

DIRS = \
*.java \
app/ant/*.java \
app/ant/func/*.java \
app/ecsuite/*.java \
app/edge/*.java \
app/edge/func/*.java \
app/lawnmower/*.java \
app/lawnmower/func/*.java \
app/multiplexer/*.java \
app/multiplexer/func/*.java \
app/multiplexerslow/*.java \
app/multiplexerslow/func/*.java \
app/parity/*.java \
app/parity/func/*.java \
app/regression/*.java \
app/regression/func/*.java \
app/sum/*.java \
app/tutorial1/*.java \
app/tutorial2/*.java \
app/tutorial3/*.java \
app/tutorial4/*.java \
app/coevolve1/*.java \
app/coevolve2/*.java \
app/twobox/*.java \
app/twobox/func/*.java \
breed/*.java \
coevolve/*.java \
es/*.java \
exchange/*.java \
gp/*.java \
gp/breed/*.java \
gp/build/*.java \
gp/koza/*.java \
multiobjective/*.java \
select/*.java \
simple/*.java \
steadystate/*.java \
util/*.java \
vector/*.java \
vector/breed/*.java \
parsimony/*.java\
rule/*.java \
rule/breed/*.java \
multiobjective/spea2/*.java \
#teambots/*.java \
#teambots/simple/*.java \



all:
	@ echo For other Makefile options, type:  make help
	${JAVAC} ${FLAGS} ${DIRS}

teambots: all
	${JAVAC} ${FLAGS} teambots/*.java

clean:
	find . -name "*.class" -exec rm -f {} \;
	find . -name "*.stat" -exec rm -f {} \;
	find . -name ".DS_Store" -exec rm -rf {} \;
#	find docs/classdocs \! -name CVS \! -name Entries \! -name Repository \! -name Root \! -name classdocs -exec rm -rf {} \;
	find . -name "*.java*~" -exec rm -rf {} \;
	rm -rf docs/classdocs/ec/

doc:
	javadoc -d docs/classdocs ec ec.gp ec.gp.koza ec.simple ec.select ec.multiobjective ec.steadystate ec.util ec.breed ec.gp.breed ec.gp.build ec.es ec.exchange ec.vector ec.vector.breed ec.parsimony ec.coevolve ec.rule ec.teambots ec.rule.breed ec.multiobjective.spea2

docs: doc

dist: clean all doc
	find . -name "*.stat" -exec rm rf {} \; -print
	echo --------------------------
	echo Expect some errors here...
	echo --------------------------
	find . -name "CVS" -exec rm -rf {} \; -print | cat

javac:
	javac ${FLAGS} ${DIRS}

jikes:
	jikes ${FLAGS} ${DIRS}

indent: 
	touch ${HOME}/.emacs
	find . -name "*.java" -print -exec emacs --batch --load ~/.emacs --eval='(progn (find-file "{}") (mark-whole-buffer) (setq indent-tabs-mode nil) (untabify (point-min) (point-max)) (indent-region (point-min) (point-max) nil) (save-buffer))' \;


# Print a help message
help: 
	@ echo ECJ Makefile options
	@ echo 
	@ echo "make          Builds the ECJ code using the default compiler"
	@ echo "make all	(Same thing)"
	@ echo "make javac    Builds the ECJ code using javac for sure"
	@ echo "make jikes    Builds the ECJ code using jikes"
	@ echo "make docs     Builds the class documentation, found in docs/classsdocs"
	@ echo "make doc	(Same thing)"
	@ echo "make clean    Cleans out all classfiles, checkpoints, and various gunk"
	@ echo "make dist     Does a make clean, make docs, and make, then deletes CVS dirs"
	@ echo "make help     Brings up this message!"
	@ echo "make indent   Uses emacs to re-indent ECJ java files as you'd prefer"
	@ echo
	@ echo By default ECJ uses IBM\'s jikes compiler by default.  You can always
	@ echo use javac as the default: just change the JAVAC variable in the Makefile.
	@ echo If you\'d like to try jikes but don\'t have it, you can download it at
	@ echo "	       http://www.research.ibm.com/jikes/"
	@ echo

