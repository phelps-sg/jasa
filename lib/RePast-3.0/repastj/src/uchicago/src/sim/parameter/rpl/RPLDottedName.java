package uchicago.src.sim.parameter.rpl;

/**
 * RPLObject representation of a dotted name reference.
 *
 * @version $Revision$ $Date$
 */

public class RPLDottedName implements RPLObject {

  private String name1, name2;
  private Object value;
  private Class type;
  int line, col;

  /**
   * Creates a RPLDottedName from the specified names.
   *
   * @param lhs the name on the left-hand side of the dot
   * @param rhs the name on the right-hand side of the dot
   */
  public RPLDottedName(String lhs, String rhs) {
    this.name1 = lhs;
    this.name2 = rhs;
  }

  /**
   * Gets the left-hand side name.
   * @return
   */
  public String getLHS() {
    return name1;
  }

  /**
   * Gets the right-hand side name.
   *
   * @return
   */
  public String getRHS() {
    return name2;
  }

  /**
   * Returns the value of this RPLDottedName. This is unliked to return
   * anything but null until this RPLDottedName has been compiled.
   */
  public Object getValue() {
    return value;
  }

  /**
   * Returns the type of this RPLDottedName. This is unliked to return
   * anything but null until this RPLDottedName has been compiled.
   */
  public Class getType() {
    return type;
  }

  /**
   * Initializes this RPLDottedName with a value and type. This will be
   * called by the compiler.
   *
   * @param type the type of the value represented by this RPLDottedName
   * @param value the value of the reference represented by this RPLDottedName.
   */
  void init(Class type, Object value) {
    this.type = type;
    this.value = value;
  }

  /**
   * Compile this RPLDottedName, resolving the references if possible.
   *
   * @param compiler the compiler
   */
  public void compile(RPLCompiler compiler) {
    compiler.resolveDottedName(this);
  }

  /**
   * Sets where this RPLDottedName was defined in the source file by line
   * and col.
   *
   * @param line the line number where this was defined
   * @param col the column number where this was defined
   */
  public void setLineCol(int line, int col) {
    this.line = line;
    this.col = col;
  }
}
