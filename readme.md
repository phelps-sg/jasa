README for JASA
---------------

Last modified: $Author$\
 $Date$\
 $Revision$\

### About

JASA is a high-performance auction simulator written in JAVA. It is
designed for performing experiments in agent-based computational
economics.

### Development Status

This package is currently at alpha. This code is not stable or fully
tested. Please report any bugs, issues or suggestions to [Steve
Phelps](mailto:sphelps@sphelps.net).

### License

This software is licensed under the [GNU General Public
License](LICENSE.TXT). Although it is not an official term of the
licensing conditions, you are also expected to cite use of this software
if you use it in your research.

### Obtaining the latest release

The latest release of JASA can be downloaded from
[SourceForge](http://sourceforge.net/project/showfiles.php?group_id=47257&package_id=40190).

### Documentation

[API Documentation](doc/api/index.html).

### Prerequisites

-   [Java](http://java.sun.com) JVM 1.6.0 or later
-   JASA is built on top of the [](http://jabm.sourceforge.net)JABM
    framework which must be installed before using JASA.

### Running the examples from the Eclipse IDE

The distribution archive can be imported directly into the [Eclipse
IDE](http://www.eclipse.org/) by using the
[File/Import](http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.platform.doc.user/tasks/tasks-importproject.htm)
menu item. Create a [launch
configuration](http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.jdt.doc.user/tasks/tasks-java-local-configuration.htm)
with the main class `net.sourceforge.jabm.DesktopSimulationManager` and
specify which configuration file you want to use by setting the system
property `jabm.config` using the JVM argument `-D`, for example

`-Djabm.config=examples/chiarellaAndIori/main.xml`

### Documentation

-   [Javadoc and UML](doc/api/index.html)

### Acknowledgements

Additional contributors: Jinzhong Niu and Marek Marcinkiewicz.

This work has been supported by EPSRC grant GR/T10671/01 - "Market Based
Control of Complex Computational Systems." and NSF grant number
IIS-9820657 - "Tools and Techniques for Automated Mechanism Design". It
was originally supported by the EU IST Programme through the SLIE
project.

The system is based on the 4-heap algorithm, described in the paper

Wurman, P. R., Walsh, W. E., & Wellman, M. P. (1998). Flexible double
auctions for electronic commerce: theory and implementation.
International Journal of Decision Support Systems, 24, 17–27.

JASA makes use of the [Mersenne Twister
PRNG](http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/emt.html). Full
details of the Mersenne Twister algorithm can be found in:

Makato Matsumoto and Takuji Nishimura, "Mersenne Twister: A
623-Dimensionally Equidistributed Uniform Pseudo-Random Number
Generator", in *ACM Transactions on modeling and Computer Simulation*,
Vol. 8, No. 1, January 1998, pp 3--30.

JASA includes implementations based on existing agent-based models
described in the literature, including:

-   Iori, G., & Chiarella, C. (2002). A Simulation Analysis of the
    Microstructure of Double Auction Markets. Quantitative Finance, 2,
    346–353.
-   Cliff, D., & Bruten, J. (1997). Minimal-Intelligence Agents for
    Bargaining Behaviors in Market-Based Environments.
-   Nicolaisen, J., Petrov, V., & Tesfatsion, L. (2001). Market power
    and efficiency in a computational electricity market with
    discriminatory double-auction pricing. IEEE Transactions on
    Evolutionary Computation, 5(5), 504–523.

This product includes software developed by the Apache Software
Foundation [(http://www.apache.org)](http://www.apache.org).

* * * * *

\(C) 2014 [Steve Phelps](http://sphelps.net/)
