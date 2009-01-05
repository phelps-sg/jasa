package uchicago.src.sim.parameter.rpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Represents a Java class when references in the RPLCompiler.
 *
 * @version $Revision$ $Date$
 */

public class JavaClass {

  private Class clazz;
  private String shortName;
  private Map readProps = new HashMap();
  private Map writeProps = new HashMap();
  private Map staticFields = new HashMap();

  /**
   * Creates a JavaClass for the specified class.
   *
   * @param clazz the Class to create this JavaClass for.
   */
  public JavaClass(Class clazz) {
    this.clazz = clazz;
    shortName = clazz.getName();
    int index = shortName.lastIndexOf(".");
    if (index != -1) {
      shortName = shortName.substring(index + 1, shortName.length());
    }

    index = shortName.lastIndexOf("$");
    // works with inner classes too.
    if (index != -1) {
      shortName = shortName.substring(index + 1, shortName.length());
    }
    fillProperties();
    fillFields();
  }

  private void fillProperties() {
    Method[] methods = clazz.getMethods();
    for (int i = 0, n = methods.length; i < n; i++) {
      Method m = methods[i];
      String name = m.getName();
      if (name.startsWith("set") && m.getParameterTypes().length == 1) {
        String propName = name.substring(3);
        writeProps.put(propName.toLowerCase(), m);
      } else if (name.startsWith("get") && m.getParameterTypes().length == 0) {
        String propName = name.substring(3);
        readProps.put(propName.toLowerCase(), m);
      } else if (name.startsWith("is") && m.getParameterTypes().length == 0 &&
              m.getReturnType().equals(boolean.class)) {
        String propName = name.substring(2);
        readProps.put(propName.toLowerCase(), m);
      }
    }
  }

  private void fillFields() {
    Field[] fields = clazz.getFields();
    for (int i = 0, n = fields.length; i < n; i++) {
      Field f = fields[i];
      if (Modifier.isStatic(f.getModifiers())) {
        staticFields.put(f.getName(), f);
      }
    }
  }

  /**
   * Gets the short unqualified name of the class represented by this
   * JavaClass.
   */
  public String getShortName() {
    return shortName;
  }

  /**
   * Returns true if the specified fieldName is a static field in the class
   * represented by this JavaClass. Otherwise false.
   * @param fieldName the name of the field
   */
  public boolean hasStaticField(String fieldName) {
    return staticFields.containsKey(fieldName);
  }

  /**
   * Returns the type of the named static field. If the class represented
   * by this JavaClass has no such field then a NoSuchElementException
   * is thrown.
   *
   * @param fieldName the name of the field
   * @throws NoSuchElementException if the named field is not found.
   */
  public Class getStaticFieldType(String fieldName) {
    Field f = (Field)staticFields.get(fieldName);
    if (f == null) throw new NoSuchElementException("Class " + clazz.getName() +
                                                    " does not contain field " + fieldName);
    return f.getType();
  }

  /**
   * Returns the value of the named static field. If the class represented
   * by this JavaClass has no such field then a NoSuchElementException
   * is thrown.
   *
   * @param fieldName the name of the field
   * @throws NoSuchElementException if the named field is not found.
   */
  public Object getStaticFieldValue(String fieldName) {
    Field f = (Field)staticFields.get(fieldName);
    if (f == null) throw new NoSuchElementException("Class " + clazz.getName() +
                                                    " does not contain field " + fieldName);
    try {
      return f.get(null);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    } catch (IllegalAccessException e) {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }

    return null;
  }
}
