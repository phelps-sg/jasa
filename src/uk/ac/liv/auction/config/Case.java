/*
 * Created on Nov 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package uk.ac.liv.auction.config;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

/**
 * @author Jinzhong Niu
 * @version $Revision$
 */
public abstract class Case {

	public abstract String toString();
	public abstract void apply(ParameterDatabase pdb, Parameter base);
}
