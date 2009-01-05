package uchicago.src.sim.parameter.rpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Factory class for creating RPLParameter and RPLValues from intermediary
 * objects.
 *
 * @version $Revision$ $Date$
 */

public class RPLFactory {

  private static Map typeMap = new HashMap();
  private static Map convertorMap = new HashMap();
  private static Set numberClassSet = new HashSet();

  static interface Convertor {
    public Object convert(Object obj);
  }

  static class DoubleConvertor implements Convertor {
    public Object convert(Object obj) {
      return new Double(obj.toString());
    }
  }

  static class LongConvertor  implements Convertor {
    public Object convert(Object obj) {
      return new Long(obj.toString());
    }
  }

  static class FloatConvertor  implements Convertor {
    public Object convert(Object obj) {
      return new Float(obj.toString());
    }
  }

  static class IntConvertor  implements Convertor {
    public Object convert(Object obj) {
      return new Integer(obj.toString());
    }
  }

  static {
    HashSet set = new HashSet();
    set.add(double.class);
    set.add(float.class);
    set.add(long.class);
    typeMap.put(int.class, set);

    set = new HashSet();
    set.add(float.class);
    set.add(double.class);
    typeMap.put(long.class, set);

    set = new HashSet();
    set.add(double.class);
    typeMap.put(float.class, set);

    set = new HashSet();
    typeMap.put(double.class, set);

    convertorMap.put(int.class, new IntConvertor());
    convertorMap.put(double.class, new DoubleConvertor());
    convertorMap.put(float.class, new FloatConvertor());
    convertorMap.put(long.class, new LongConvertor());

    numberClassSet.add(int.class);
    numberClassSet.add(double.class);
    numberClassSet.add(float.class);
    numberClassSet.add(long.class);
    numberClassSet.add(Integer.class);
    numberClassSet.add(Long.class);
    numberClassSet.add(Float.class);
    numberClassSet.add(Double.class);
  }


  /**
   * Creates an RPLList from the specified list. This will convert all the
   * items in the list to the type of the first item, if possible.
   *
   * @param list the list of RPLObjects used to create the RPLList
   * @return the created RPLList
   */
  public static RPLList createRPLList(List list) {
    // check the types on the list
    if (list.size() == 0) throw new NoSuchElementException("list cannot be empty");

    Class type = ((RPLObject) list.get(0)).getType();
    boolean needToConvert = false;

    for (int i = 1, n = list.size(); i < n; i++) {
      RPLObject obj = (RPLObject) list.get(i);
      Class objType = obj.getType();
      if (!type.equals(objType)) {
        if (isConvertable(type, objType)) {
          type = objType;
          needToConvert = true;
        } else if (!isConvertable(objType, type)) {
          throw new ClassCastException("elements of the list must be all of the same type");

        }
      }
    }

    RPLList rplList = new RPLList(type);
    if (needToConvert) {
      Convertor c = (Convertor)convertorMap.get(type);
      for (int i = 0, n = list.size(); i < n; i++) {
        RPLObject obj = (RPLObject) list.get(i);
        rplList.add(c.convert(obj.getValue()));
      }
    } else {
      for (int i = 0, n = list.size(); i < n; i++) {
        RPLObject obj = (RPLObject) list.get(i);
        rplList.add(obj.getValue());
      }
    }
    return rplList;
  }

  private static boolean isConvertable(Class one, Class two) {
    HashSet set = (HashSet) typeMap.get(one);
    if (set == null) return false;
    return set.contains(two);
  }

  /**
   * Creates a constant RPLParameter from the specified name and having the
   * specified value.
   *
   * @param name the name of the parameter
   * @param val an RPLObject representing the value of the constant.
   * @return
   */
  public static RPLParameter createConstant(String name, RPLObject val) {
    if (val.getType().equals(String.class)) {
      return new StringRPLConstant(name, (String)val.getValue());
    } else if (val.getType().equals(boolean.class)) {
      Boolean b = (Boolean)val.getValue();
      return new BooleanRPLConstant(name, b.booleanValue());
    } else {
      // assume that the parser / compiler wouldn't allow us to get this
      // far unless the only other choice was numeric.
      double d = new Double(val.getValue().toString()).doubleValue();
      return new NumericRPLConstant(name, d);
    }
  }

  /**
   * Creates an RPLParameter with specified name whose value is defined
   * by the specified list.
   *
   * @param name the name of the parameter
   * @param list an RPLList whose items are represent the possible values
   * of the parameter
   * @return a new RPLParameter
   */
  public static RPLParameter createParameter(String name, RPLList list) {
    return new ListRPLParameter(name, list);
  }

  /**
   * Creates an RPLParameter with specified name from the specified starting
   * and ending values.
   *
   * @param name the name of the parameter
   * @param start an RPLObject that represents the starting value of the
   * new RPLParameter
   * @param end an RPLObject that represents the ending value of the
   * new RPLParameter
   * @return a new RPLParameter
   */
  public static RPLParameter createParameter(String name, RPLObject start,
                                             RPLObject end)
  {
    if (numberClassSet.contains(start.getType()) &&
            numberClassSet.contains(end.getType())) {
      double dStart = new Double(start.getValue().toString()).doubleValue();
      double dEnd = new Double(end.getValue().toString()).doubleValue();
      if (dStart <= dEnd) throw new IllegalArgumentException("ending parameter value must be greater than starting value");
      return new NumericRPLParameter(name, dStart, dEnd);

    } else {
      throw new ClassCastException("parameter start and end values must be numeric");
    }

  }

  /**
   * Creates an RPLParameter with specified name from the specified starting,
   * ending and increment values.
   *
   * @param name the name of the parameter
   * @param start an RPLObject that represents the starting value of the
   * new RPLParameter
   * @param end an RPLObject that represents the ending value of the
   * new RPLParameter
   * @param incr an RPLObject that represents the increment value of the
   * new RPLParameter
   * @return a new RPLParameter
   */
  public static RPLParameter createParameter(String name,  RPLObject start,
                                      RPLObject end, RPLObject incr)
  {
    if (numberClassSet.contains(start.getType()) &&
            numberClassSet.contains(end.getType()) &&
            numberClassSet.contains(incr.getType())) {
      double dStart = new Double(start.getValue().toString()).doubleValue();
      double dEnd = new Double(end.getValue().toString()).doubleValue();
      double dIncr = new Double(incr.getValue().toString()).doubleValue();

      if (dStart >= dEnd && dIncr > 0) throw new IllegalArgumentException("ending parameter value must be greater than starting value.");
      if (dStart <= dEnd && dIncr < 0) throw new IllegalArgumentException("ending parameter value must be less than starting value.");

      return new NumericRPLParameter(name, dStart, dEnd, dIncr);

    } else {
      throw new ClassCastException("parameter star, end and increment values must be numeric.");
    }

  }

}
