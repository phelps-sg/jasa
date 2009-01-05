package uchicago.src.sim.parameter.rpl;

/**
 * Intermediate representation of RPLParameters, lists and so forth. The
 * compiler creates AST nodes, then various RPLObjects from those nodes and
 * then actual RPLParameters.
 *
 * @version $Revision$ $Date$
 */

public interface RPLObject {

  /**
   * Gets the value of this RPLObject.
   * @return
   */
  public Object getValue();

  /**
   * Gets the type of this RPLObject.
   *
   * @return
   */
  public Class getType();

  /**
   * Compiles this RPLObject.
   *
   * @param compile the compiler
   */
  public void compile(RPLCompiler compile);

}
