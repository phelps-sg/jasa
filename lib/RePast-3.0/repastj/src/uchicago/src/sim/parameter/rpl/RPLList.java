package uchicago.src.sim.parameter.rpl;

import java.util.ArrayList;
//import java.util.Collection;

/**
 * Intermediate representation of a List used by the RPLCompiler.
 *
 * @version $Revision$ $Date$
 */

public class RPLList extends ArrayList {

  private Class type;

  /**
   * Creates an RPLList with the specified initial capacity and that holds
   * objects of the specified type.
   *
   * @param initialCapacity the initial capacity of the list
   * @param type the type object contained by the list
   */
  public RPLList(int initialCapacity, Class type) {
    super(initialCapacity);
    this.type = type;
  }

  /**
   * Creates an RPLList that holdsobjects of the specified type.
   *
   * @param type the type object contained by the list
   */
  public RPLList(Class type) {
    super();
    this.type = type;
  }

  /**
   * Gets the type of objects contained by this list.
   */
  public Class getType() {
    return type;
  }
}
