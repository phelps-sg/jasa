package uchicago.src.sim.parameter.rpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uchicago.src.sim.engine.SimModel;

/**
 * Compiler for RPL format parameter files. The compiler works in two
 * steps, preProcess and compile. The final output is the top-level
 * "main" parameter of which all other parameters are children. This main
 * RPLParameter is used by RPLParameterSetter to set actual model parameters.
 *
 * @version $Revision$ $Date$
 */

public class RPLCompiler {

  private Map globalVars = new HashMap();

  // set of parameter names -- NOT the variable name of the parameter
  // but the actual model parameter name as given by an rpl parameter.
  private Set paramNames = new HashSet();

  // set of constant parameter names -- NOT the variable name of the parameter
  // but the actual model parameter name as given by an rpl parameter.
  private Set constNames = new HashSet();

  // map of rpl defined parameters. key is variable name, value is
  // the RPLParameter object
  private Map params = new HashMap();

  private SimModel model;
  private JavaClass jcModel;
  private String fileName;
  private RPLParameter main;
  private boolean hasMain = false;
  private SimpleNode root;

  /**
   * Creates a RPLCompiler for compiling the specified file.
   *
   * @param file the name of the file to compile
   */
  public RPLCompiler(String file) {
    fileName = file;
  }

  /**
   * Performs an initial first passs compilation on the file specified on
   * the constructor.
   *
   * @throws IOException when encountering a problem with the file specified
   * in the constructor.
   */
  public void preProcess() throws IOException {
    RPLParser parser = new RPLParser(new BufferedReader(new FileReader(fileName)));
    try {
      root = parser.compilationUnit();
      root.preProcess(this);
      if (!hasMain) {
        String message = "File '" + fileName + "' does not defined a 'main' block";
        throw createCompilerException(message, 0);
      }
    } catch (ParseException ex) {
      throw new IOException(ex.getMessage());
    }
  }

  /**
   * Performs the actual compilation turning the rpl format file into RPLParameters.
   * If <code>preProcess</code> has not been called before this is called, then <code>preProcess</code>
   * will be called here. A reference to a model is necessary here as the
   * rpl format permits references to a model's static fields.
   *
   * @param model the model for which this compiler is compiling parameters
   * @throws IOException when encountering a problem with the file specified
   * in the constructor.
   */
  public void compile(SimModel model) throws IOException {
    setModel(model);
    if (root == null) preProcess();
    root.compile(this);
  }

  /**
   * Adds a the specified Object as a global variable .
   *
   * @param varName the name of the variable
   * @param value the value of the variable
   */
  void addGlobalVar(String varName, Object value) {
    globalVars.put(varName, value);
  }

  /**
   * Returns true if the specified name is the name of a global variable.
   * @param varName the name of the global variable
   * @return
   */
  boolean hasGlobalVariable(String varName) {
    return globalVars.containsKey(varName);
  }

  /**
   * Returns the global variable with the specified name or null if the variable
   * is not found.
   *
   * @param varName the name of the global variable.
   * @return
   */
  Object getGlobalVariable(String varName) {
    return globalVars.get(varName);
  }

  /**
   * Adds the specified name to the set of parameter names contained by this
   * RPLCompiler. This is <b>not</b> the variable name of a defined parameter
   * but the actual model parameter name.
   *
   * @param name the name of the parameter
   */
  void addParameterName(String name) {
    paramNames.add(name);
  }

  /**
   * Adds the specified name to the set of contant parameter names contained by this
   * RPLCompiler. This is <b>not</b> the variable name of a defined parameter
   * but the actual model parameter name.
   *
   * @param name the name of the parameter
   */
  void addConstantName(String name) {
    constNames.add(name);
  }

  /**
   * Returns true if the specified name is the name of a parameter.
   *
   * @param name
   * @return
   */
  boolean hasParameterName(String name) {
    return paramNames.contains(name);
  }

  /**
   * Adds the specified RPLParameter with the specified variable name to this
   * RPLCompiler.
   *
   * @param varName the variable name
   * @param parameter the RPLParameter
   */
  void addParameter(String varName, RPLParameter parameter) {
    params.put(varName, parameter);
  }

  /**
   * Gets the RPLParameter with the specified variable name.
   *
   * @param varName the variable name
   */
  RPLParameter getParameter(String varName) {
    return (RPLParameter) params.get(varName);
  }

  /**
   * Clears the lists of parameter and constant names.
   */
  void clearNames() {
    paramNames.clear();
    constNames.clear();
  }

  /**
   * Returns true if the specified variable name is the name of a
   * RPLParameter variable.
   *
   * @param varName
   * @return
   */
  boolean isParameter(String varName) {
    return params.containsKey(varName);
  }

  /**
   * Returns true if the specified name is defined as a parameter in
   * the model associated with this RPLCompiler. The model's parameters
   * are those named in the model's <code>getInitParams</code> method.
   *
   * @param name the name of the parameter.
   * @return
   */
   boolean isModelParameter(String name) {
    String[] params = model.getInitParam();
    for (int i = 0; i < params.length; i++) {
      if (params[i].equalsIgnoreCase(name)) return true;
    }

    if ("RngSeed".equalsIgnoreCase(name)) return true;

    return false;
  }

  /**
   * Sets the model associated with this RPLCompiler.
   *
   * @param model the model associated with this RPLCompiler.
   */
  public void setModel(SimModel model) {
    this.model = model;
    jcModel = new JavaClass(model.getClass());
  }

  /**
   * Sets the "main" parameter for this compiler.
   *
   * @param main
   */
  void setMain(RPLParameter main) {
    this.main = main;
    for (Iterator iter = params.values().iterator(); iter.hasNext(); ) {
      RPLParameter p = (RPLParameter)iter.next();
      if (constNames.contains(p.getName())) {
        // p is a defined constant so we add it to main
        main.addChildConstant(p);
      }
    }
  }

  /**
   * Gets the "main" RPLParameter produced by this compiler. This will be
   * null until <code>compile</code> has been called.
   *
   * @return
   */

  public RPLParameter getMain() {
    return main;
  }

  /**
   * Gets the set of parameter names defined during compilation.
   *
   * @return
   */
  public Set getParamNames() {
    return new HashSet(paramNames);
  }

  /**
   * Gets the set of constant parameter names defined during compilation.
   *
   * @return
   */
  public Set getConstNames() {
    return new HashSet(constNames);
  }

  /**
   * Resolves dotted name references. That is, this method resolves statements
   * like "Model.GRAPH_1" into the value 1, assuming that Model has a static
   * field GRAPH_1 whose value is 1.
   *
   * @param dottedName the dotted name to resolve.
   */
  void resolveDottedName(RPLDottedName dottedName) {
    String className = dottedName.getLHS();
    String field = dottedName.getRHS();
    if (className.equals(jcModel.getShortName())) {
      if (jcModel.hasStaticField(field)) {
        dottedName.init(jcModel.getStaticFieldType(field), jcModel.getStaticFieldValue(field));
      } else {
        String message = "field '" + field + "' not found in '" + className + "'";
        throw createCompilerException(message, dottedName.line);
      }
    } else {
      // can't find that the Class with that name
      String message = "name '" + className + "' is not defined";
      throw createCompilerException(message, dottedName.line);
    }
  }

  public CompilerException createCompilerException(String message, int lineNumber) {
    return createCompilerException(message, lineNumber, null);
  }

  public CompilerException createCompilerException(String message, int lineNumber, Exception e) {
    CompilerException ex;
    if (e == null)
      ex = new CompilerException(message);
    else
      ex = new CompilerException(message, e);
    ex.setFileName(fileName);
    ex.setLine(lineNumber);
    try {
      BufferedReader reader = new BufferedReader(new FileReader(fileName));
      for (int i = 0, n = lineNumber - 1; i < n; i++) {
        reader.readLine();
      }

      ex.setCode(reader.readLine());

    } catch (IOException exp) {
    }

    return ex;
  }

  void setHasMain(boolean b) {
    hasMain = true;
  }
}

