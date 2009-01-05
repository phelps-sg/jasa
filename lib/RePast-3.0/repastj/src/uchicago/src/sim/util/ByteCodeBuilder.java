/*$$
 * Copyright (c) 1999, Trustees of the University of Chicago
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with 
 * or without modification, are permitted provided that the following 
 * conditions are met:
 *
 *	 Redistributions of source code must retain the above copyright notice,
 *	 this list of conditions and the following disclaimer.
 *
 *	 Redistributions in binary form must reproduce the above copyright notice,
 *	 this list of conditions and the following disclaimer in the documentation
 *	 and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Chicago nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE TRUSTEES OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *$$*/
package uchicago.src.sim.util;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import uchicago.src.codegen.GeneratorException;
import uchicago.src.codegen.RSClassLoader;
import uchicago.src.sim.analysis.BinDataSource;
import uchicago.src.sim.analysis.DataSource;
import uchicago.src.sim.analysis.NumericDataSource;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.ObjectPicker;
import uchicago.src.sim.engine.StatCalculator;

import com.go.trove.classfile.AccessFlags;
import com.go.trove.classfile.ClassFile;
import com.go.trove.classfile.CodeBuilder;
import com.go.trove.classfile.Label;
import com.go.trove.classfile.LocalVariable;
import com.go.trove.classfile.MethodInfo;
import com.go.trove.classfile.Opcode;
import com.go.trove.classfile.TypeDescriptor;
import com.go.trove.util.ClassInjector;

/**
 * A factory class for dynamically creating instances of a few object
 * types.  This factory generates the bytecode for the classes, and
 * then instantiates instances of those classes. Reflection is not
 * used, and the created objects should be just as fast as manually
 * coded counterparts.<p>
 *
 * This class is primarily used by the scheduling mechanism and DataRecorder
 * to dynamically create BasicActions, DataSources, and NumericDataSources.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class ByteCodeBuilder {

  private static int id = 0;

  public static RSClassLoader loader;

  private static synchronized String getUnqName() {
    return "BAB_SYNTH_" + (++id);
  }

  private static Method getMethod(Class target, String methodName)
          throws GeneratorException {
    return getMethod(target, methodName, null);
  }


  private static Method getMethod(Class target, String methodName, Class retType)
          throws GeneratorException {
    Method targetMethod;
    try {
      targetMethod = target.getMethod(methodName, new Class[]{});
      if (retType != null) {
        Class methRetType = targetMethod.getReturnType();

        if (!retType.isAssignableFrom(methRetType)) {
          throw new GeneratorException("Method " + methodName +
                                       "() must return a " + retType.getName(), null);
        }
      }
    } catch (NoSuchMethodException ex) {
      throw new GeneratorException(ex.getMessage(), ex);
    }

    return targetMethod;
  }

  // Returns a Method object for the named method of the target class.
  // Checks if this Method returns a double or something that can be cast into
  // a double. If not then throw exception.
  private static Method getMethodRetDouble(Class target, String methodName,
                                           Class[] params)
          throws GeneratorException {
    HashSet set = new HashSet();
    set.add(int.class);
    set.add(long.class);
    set.add(double.class);
    set.add(float.class);

    Method targetMethod;
    try {
      targetMethod = target.getMethod(methodName, params);
      Class methRetType = targetMethod.getReturnType();


      if (!set.contains(methRetType)) {
        throw new GeneratorException("Method " + methodName +
                                     "() must return a number", null);
      }
    } catch (NoSuchMethodException ex) {

      throw new GeneratorException(ex.getMessage(), ex);
    }

    return targetMethod;

  }

  // returns a Method object for the name method of the target class.
  // Checks if this ethod returns a double or something that can be cast into
  // a double. If not then throw exception. Also checks if this method
  // takes a single Object parameter. If not, throws and exception.
  private static Method getMethodObjDouble(Class target,
                                           String methodName)
          throws GeneratorException {
    Method m = getMethodRetDouble(target, methodName, new Class[]{Object.class});
    Class[] params = m.getParameterTypes();
    if (params.length != 1) {
      throw new GeneratorException("Method " + methodName + " must have a " +
                                   "single parameter of type Object", null);
    }

    if (!Object.class.isAssignableFrom(params[0])) {
      throw new GeneratorException("Method " + methodName + " must have a " +
                                   "single parameter of type Object", null);
    }

    return m;
  }

  private static Method getBasicActionExecMethod() throws GeneratorException {
    Method execMethod;
    try {
      execMethod = BasicAction.class.getMethod("execute", new Class[]{});
    } catch (NoSuchMethodException ex) {
      throw new GeneratorException(ex.getMessage(), ex);
    }

    return execMethod;
  }

  private static Method getStatCalculatorExecMethod() throws GeneratorException {
    Method execMethod;
    try {
      execMethod = StatCalculator.class.getMethod("calc", new Class[]{});
    } catch (NoSuchMethodException ex) {
      throw new GeneratorException(ex.getMessage(), ex);
    }

    return execMethod;
  }

  private static Method getObjectPickerExecMethod() throws GeneratorException {
      Method execMethod;
      try {
        execMethod = ObjectPicker.class.getMethod("pickObjects", new Class[]{});
      } catch (NoSuchMethodException ex) {
        throw new GeneratorException(ex.getMessage(), ex);
      }

      return execMethod;
    }


  private static Method getDataSourceExecMethod() throws GeneratorException {
    Method execMethod;
    try {
      execMethod = DataSource.class.getMethod("execute", new Class[]{});
    } catch (NoSuchMethodException ex) {
      throw new GeneratorException(ex.getMessage(), ex);
    }

    return execMethod;
  }

  private static Method getNumericDataSourceExecMethod() throws GeneratorException {
    Method execMethod;
    try {
      execMethod = NumericDataSource.class.getMethod("execute", new Class[]{});
    } catch (NoSuchMethodException ex) {
      throw new GeneratorException(ex.getMessage(), ex);
    }

    return execMethod;
  }

  private static Method getSequenceSValueMethod() throws GeneratorException {
    Method svMethod;
    try {
      svMethod = Sequence.class.getMethod("getSValue", new Class[]{});

    } catch (NoSuchMethodException ex) {
      throw new GeneratorException(ex.getMessage(), ex);
    }

    return svMethod;
  }

  private static Method getBDSBinValueMethod() throws GeneratorException {
    Method bvMethod;
    try {
      bvMethod = BinDataSource.class.getMethod("getBinValue",
                                               new Class[]{Object.class});

    } catch (NoSuchMethodException ex) {
      throw new GeneratorException(ex.getMessage(), ex);
    }

    return bvMethod;
  }

  public static StatCalculator generateMinCalculator(List list, String methodName)
    throws GeneratorException
  {
    Class clazz = list.get(0).getClass();
    return generateMinCalculator(clazz, list, methodName);
  }

  public static StatCalculator generateMinCalculator(Class clazz,
                                                     List list, String methodName)
    throws GeneratorException
  {
    Method targetMethod = getMethodRetDouble(clazz, methodName, new Class[]{});

    ClassFile cf = new ClassFile(getUnqName(), StatCalculator.class);
    cf.markSynthetic();

    AccessFlags priv = new AccessFlags();
    priv.setPrivate(true);
    AccessFlags pub = new AccessFlags();
    pub.setPublic(true);


    TypeDescriptor listType = new TypeDescriptor(List.class);
    TypeDescriptor[] params = {listType};
    MethodInfo ctor = cf.addConstructor(pub, params);
    ctor.markSynthetic();

    // code for constructor
    CodeBuilder builder = new CodeBuilder(ctor);
    builder.loadThis();
    builder.loadLocal(builder.getParameters()[0]);
    builder.invokeSuperConstructor(new TypeDescriptor[]{listType});
    builder.returnVoid();


    Method execMethod = getStatCalculatorExecMethod();
    MethodInfo mi = cf.addMethod(execMethod);
    mi.markSynthetic();

    TypeDescriptor intType = new TypeDescriptor(int.class);
    TypeDescriptor doubleType = new TypeDescriptor(double.class);
    TypeDescriptor elementType = new TypeDescriptor(clazz);
    TypeDescriptor objectType = new TypeDescriptor(Object.class);

    // implement MinPicker.calc.
    builder = new CodeBuilder(mi);

    // int size = items.size();
    builder.loadThis();
    builder.loadField("items", listType);
    builder.invokeInterface("java.util.List", "size", intType, null);
    LocalVariable size = builder.createLocalVariable("listSize", intType);
    builder.storeLocal(size);
    // create the min variable for tracking the min value
    LocalVariable min = builder.createLocalVariable("min", doubleType);
    builder.loadStaticField("java.lang.Double", "MAX_VALUE", doubleType);
    builder.storeLocal(min);

    //LocalVariable l = builder.createLocalVariable("l", doubleType);

    // int i = 0 of "for (int i = 0; ...)"
    LocalVariable i = builder.createLocalVariable("i", intType);
    builder.loadConstant(0);
    builder.storeLocal(i);
    Label loopTest = builder.createLabel();
    builder.branch(loopTest);
    Label loop = builder.createLabel().setLocation();

    // list.get(i)
    builder.loadThis();
    builder.loadField("items", listType);
    builder.loadLocal(i);
    builder.invokeInterface("java.util.List", "get", objectType,
                            new TypeDescriptor[]{intType});

    // top_stack = (ElementType)top_stack
    builder.checkCast(elementType);
    // element.targetMethod();
    builder.invoke(targetMethod);
    // result should be on the stack
    if (!targetMethod.getReturnType().equals(double.class)) {
      builder.convert(targetMethod.getReturnType(), double.class);
    }
    builder.loadLocal(min);
    builder.invokeStatic("java.lang.Math", "min", doubleType,
                         new TypeDescriptor[]{doubleType, doubleType});
    builder.storeLocal(min);


    // i++ part of for (int i = 0; i < size; i++)
    builder.integerIncrement(i, 1);

    loopTest.setLocation();
    // i < size part of for (int i = 0; i < size; i++)
    builder.loadLocal(i);
    builder.loadLocal(size);
    builder.ifComparisonBranch(loop, "<");

    builder.loadLocal(min);
    builder.returnValue(double.class);

    return createStatCalculator(cf, list);
  }

  public static StatCalculator generateMaxCalculator(List list, String methodName)
    throws GeneratorException
  {
    Class clazz = list.get(0).getClass();
    return generateMaxCalculator(clazz, list, methodName);
  }

  public static StatCalculator generateMaxCalculator(Class clazz,
                                                     List list, String methodName)
    throws GeneratorException
  {
    Method targetMethod = getMethodRetDouble(clazz, methodName, new Class[]{});

    ClassFile cf = new ClassFile(getUnqName(), StatCalculator.class);
    cf.markSynthetic();

    AccessFlags priv = new AccessFlags();
    priv.setPrivate(true);
    AccessFlags pub = new AccessFlags();
    pub.setPublic(true);


    TypeDescriptor listType = new TypeDescriptor(List.class);
    TypeDescriptor[] params = {listType};
    MethodInfo ctor = cf.addConstructor(pub, params);
    ctor.markSynthetic();

    // code for constructor
    CodeBuilder builder = new CodeBuilder(ctor);
    builder.loadThis();
    builder.loadLocal(builder.getParameters()[0]);
    builder.invokeSuperConstructor(new TypeDescriptor[]{listType});
    builder.returnVoid();


    Method execMethod = getStatCalculatorExecMethod();
    MethodInfo mi = cf.addMethod(execMethod);
    mi.markSynthetic();

    TypeDescriptor intType = new TypeDescriptor(int.class);
    TypeDescriptor doubleType = new TypeDescriptor(double.class);
    TypeDescriptor elementType = new TypeDescriptor(clazz);
    TypeDescriptor objectType = new TypeDescriptor(Object.class);

    // implement StatCalculator.calc.
    builder = new CodeBuilder(mi);

    // int size = items.size();
    builder.loadThis();
    builder.loadField("items", listType);
    builder.invokeInterface("java.util.List", "size", intType, null);
    LocalVariable size = builder.createLocalVariable("listSize", intType);
    builder.storeLocal(size);
    // create the max variable for tracking the max value
    LocalVariable max = builder.createLocalVariable("max", doubleType);
    builder.loadStaticField("java.lang.Double", "MIN_VALUE", doubleType);
    builder.storeLocal(max);

    // int i = 0 of "for (int i = 0; ...)"
    LocalVariable i = builder.createLocalVariable("i", intType);
    builder.loadConstant(0);
    builder.storeLocal(i);
    Label loopTest = builder.createLabel();
    builder.branch(loopTest);
    Label loop = builder.createLabel().setLocation();

    // list.get(i)
    builder.loadThis();
    builder.loadField("items", listType);
    builder.loadLocal(i);
    builder.invokeInterface("java.util.List", "get", objectType,
                            new TypeDescriptor[]{intType});

    // top_stack = (ElementType)top_stack
    builder.checkCast(elementType);
    // element.targetMethod();
    builder.invoke(targetMethod);
    // result should be on the stack
    if (!targetMethod.getReturnType().equals(double.class)) {
      builder.convert(targetMethod.getReturnType(), double.class);
    }
    builder.loadLocal(max);
    builder.invokeStatic("java.lang.Math", "max", doubleType,
                         new TypeDescriptor[]{doubleType, doubleType});
    builder.storeLocal(max);


    // i++ part of for (int i = 0; i < size; i++)
    builder.integerIncrement(i, 1);

    loopTest.setLocation();
    // i < size part of for (int i = 0; i < size; i++)
    builder.loadLocal(i);
    builder.loadLocal(size);
    builder.ifComparisonBranch(loop, "<");

    builder.loadLocal(max);
    builder.returnValue(double.class);

    return createStatCalculator(cf, list);
  }

  public static StatCalculator generateAvgCalculator(List list, String methodName)
    throws GeneratorException
  {
    Class clazz = list.get(0).getClass();
    return generateAvgCalculator(clazz, list, methodName);
  }

  public static StatCalculator generateAvgCalculator(Class clazz, List list, String methodName)
    throws GeneratorException
  {
    Method targetMethod = getMethodRetDouble(clazz, methodName, new Class[]{});

    ClassFile cf = new ClassFile(getUnqName(), StatCalculator.class);
    cf.markSynthetic();

    AccessFlags priv = new AccessFlags();
    priv.setPrivate(true);
    AccessFlags pub = new AccessFlags();
    pub.setPublic(true);

    TypeDescriptor listType = new TypeDescriptor(List.class);
    TypeDescriptor[] params = {listType};
    MethodInfo ctor = cf.addConstructor(pub, params);
    ctor.markSynthetic();

    // code for constructor
    CodeBuilder builder = new CodeBuilder(ctor);
    builder.loadThis();
    builder.loadLocal(builder.getParameters()[0]);
    builder.invokeSuperConstructor(new TypeDescriptor[]{listType});
    builder.returnVoid();

    Method execMethod = getStatCalculatorExecMethod();
    MethodInfo mi = cf.addMethod(execMethod);
    mi.markSynthetic();

    TypeDescriptor intType = new TypeDescriptor(int.class);
    TypeDescriptor doubleType = new TypeDescriptor(double.class);
    TypeDescriptor elementType = new TypeDescriptor(clazz);
    TypeDescriptor objectType = new TypeDescriptor(Object.class);

    // implement StatCalculator.calc.
    builder = new CodeBuilder(mi);

    // int size = items.size();
    builder.loadThis();
    builder.loadField("items", listType);
    builder.invokeInterface("java.util.List", "size", intType, null);
    LocalVariable size = builder.createLocalVariable("listSize", intType);
    builder.storeLocal(size);
    // double sum = 0;
    LocalVariable sum = builder.createLocalVariable("sum", doubleType);
    builder.loadConstant(0d);
    builder.storeLocal(sum);

    // int i = 0 of "for (int i = 0; ...)"
    LocalVariable i = builder.createLocalVariable("i", intType);
    builder.loadConstant(0);
    builder.storeLocal(i);
    Label loopTest = builder.createLabel();
    builder.branch(loopTest);
    Label loop = builder.createLabel().setLocation();

    // list.get(i)
    builder.loadThis();
    builder.loadField("items", listType);
    builder.loadLocal(i);
    builder.invokeInterface("java.util.List", "get", objectType,
                            new TypeDescriptor[]{intType});

    // top_stack = (ElementType)top_stack
    builder.checkCast(elementType);
    // element.targetMethod();
    builder.invoke(targetMethod);
    // result should be on the stack
    if (!targetMethod.getReturnType().equals(double.class)) {
      builder.convert(targetMethod.getReturnType(), double.class);
    }

    builder.loadLocal(sum);
    builder.math(Opcode.DADD);
    builder.storeLocal(sum);


    // i++ part of for (int i = 0; i < size; i++)
    builder.integerIncrement(i, 1);

    loopTest.setLocation();
    // i < size part of for (int i = 0; i < size; i++)
    builder.loadLocal(i);
    builder.loadLocal(size);
    builder.ifComparisonBranch(loop, "<");

    builder.loadLocal(sum);
    builder.loadLocal(size);
    builder.convert(int.class, double.class);
    builder.math(Opcode.DDIV);
    builder.returnValue(double.class);

    return createStatCalculator(cf, list);
  }

  public static ObjectPicker generateMinObjectPicker(List list, String methodName)
    throws GeneratorException
  {
    Class clazz = list.get(0).getClass();
    return generateMinObjectPicker(clazz, list, methodName);
  }

  public static ObjectPicker generateMinObjectPicker(Class clazz, List list, String methodName)
    throws GeneratorException
  {
    Method targetMethod = getMethodRetDouble(clazz, methodName, new Class[]{});

    ClassFile cf = new ClassFile(getUnqName(), ObjectPicker.class);
    cf.markSynthetic();

    AccessFlags priv = new AccessFlags();
    priv.setPrivate(true);
    AccessFlags pub = new AccessFlags();
    pub.setPublic(true);


    TypeDescriptor listType = new TypeDescriptor(List.class);
    TypeDescriptor[] params = {listType};
    MethodInfo ctor = cf.addConstructor(pub, params);
    ctor.markSynthetic();

    // code for constructor
    CodeBuilder builder = new CodeBuilder(ctor);
    builder.loadThis();
    builder.loadLocal(builder.getParameters()[0]);
    builder.invokeSuperConstructor(new TypeDescriptor[]{listType});
    builder.returnVoid();


    Method execMethod = getObjectPickerExecMethod();
    MethodInfo mi = cf.addMethod(execMethod);
    mi.markSynthetic();

    TypeDescriptor intType = new TypeDescriptor(int.class);
    TypeDescriptor doubleType = new TypeDescriptor(double.class);
    TypeDescriptor elementType = new TypeDescriptor(clazz);
    TypeDescriptor objectType = new TypeDescriptor(Object.class);
    TypeDescriptor arrayListType = new TypeDescriptor(ArrayList.class);
    TypeDescriptor clazzType = new TypeDescriptor(clazz);
    TypeDescriptor booleanType = new TypeDescriptor(boolean.class);

    // implement ObjectPicker.pickObjects
    builder = new CodeBuilder(mi);

    // List retVal = new ArrayList();
    LocalVariable retVal = builder.createLocalVariable("retVal", listType);
    builder.newObject(arrayListType);
    builder.dup();
    builder.invokeConstructor("java.util.ArrayList", null);
    builder.storeLocal(retVal);


    // int size = items.size();
    builder.loadThis();
    builder.loadField("items", listType);
    builder.invokeInterface("java.util.List", "size", intType, null);
    LocalVariable size = builder.createLocalVariable("listSize", intType);
    builder.storeLocal(size);
    // create the min variable for tracking the min value
    LocalVariable min = builder.createLocalVariable("min", doubleType);
    builder.loadStaticField("java.lang.Double", "MAX_VALUE", doubleType);
    builder.storeLocal(min);

    LocalVariable item = builder.createLocalVariable("item", clazzType);
    LocalVariable val = builder.createLocalVariable("val", doubleType);

    Label notLtBranch = builder.createLabel();
    Label endBranch = builder.createLabel();

    // int i = 0 of "for (int i = 0; ...)"
    LocalVariable i = builder.createLocalVariable("i", intType);
    builder.loadConstant(0);
    builder.storeLocal(i);
    Label loopTest = builder.createLabel();
    builder.branch(loopTest);
    Label loop = builder.createLabel().setLocation();

    // list.get(i)
    builder.loadThis();
    builder.loadField("items", listType);
    builder.loadLocal(i);
    builder.invokeInterface("java.util.List", "get", objectType,
                            new TypeDescriptor[]{intType});

    // ElementType item = (ElementType)element
    builder.checkCast(elementType);
    builder.storeLocal(item);

    // d = item.targetMethod();
    builder.loadLocal(item);
    builder.invoke(targetMethod);
    // result should be on the stack
    // cast if necessary
    if (!targetMethod.getReturnType().equals(double.class)) {
      builder.convert(targetMethod.getReturnType(), double.class);
    }
    builder.dup2();
    builder.storeLocal(val);
    builder.loadLocal(min);


    // at this point we should have the method result and the
    // current min value on the stack. So we need to branch
    // depending on which one is min.
    // do < comparison between method retVal and min
    builder.math(Opcode.DCMPL);
    // stack: -1: retVal < min, 0: retVal = min, 1: retVal > min



    builder.ifZeroComparisonBranch(notLtBranch, ">=");
    // min = val;
    builder.loadLocal(val);
    builder.storeLocal(min);
    // retVal.clear();
    builder.loadLocal(retVal);
    builder.invokeInterface("java.util.List", "clear", null, null);
    // retVal.add(item);
    builder.loadLocal(retVal);
    builder.loadLocal(item);
    builder.invokeInterface("java.util.List", "add", booleanType,
                            new TypeDescriptor[]{objectType});
    // pop the returned boolean off the stack
    builder.pop();
    builder.branch(endBranch);

    notLtBranch.setLocation();

    builder.loadLocal(val);
    builder.loadLocal(min);
    builder.math(Opcode.DCMPL);
    builder.ifZeroComparisonBranch(endBranch, "!=");
    // drop through
    builder.loadLocal(retVal);
    builder.loadLocal(item);
    builder.invokeInterface("java.util.List", "add", booleanType,
                            new TypeDescriptor[]{objectType});
    // pop the returned boolean off the stack
    builder.pop();
    endBranch.setLocation();


    // i++ part of for (int i = 0; i < size; i++)
    builder.integerIncrement(i, 1);

    loopTest.setLocation();
    // i < size part of for (int i = 0; i < size; i++)
    builder.loadLocal(i);
    builder.loadLocal(size);
    builder.ifComparisonBranch(loop, "<");


    builder.loadLocal(retVal);
    builder.returnValue(java.util.List.class);

    return createObjectPicker(cf, list);
  }

  public static ObjectPicker generateMaxObjectPicker(List list, String methodName)
    throws GeneratorException
  {
    Class clazz = list.get(0).getClass();
    return generateMaxObjectPicker(clazz, list, methodName);
  }

  public static ObjectPicker generateMaxObjectPicker(Class clazz, List list, String methodName)
    throws GeneratorException
  {
    Method targetMethod = getMethodRetDouble(clazz, methodName, new Class[]{});

    ClassFile cf = new ClassFile(getUnqName(), ObjectPicker.class);
    cf.markSynthetic();

    AccessFlags priv = new AccessFlags();
    priv.setPrivate(true);
    AccessFlags pub = new AccessFlags();
    pub.setPublic(true);


    TypeDescriptor listType = new TypeDescriptor(List.class);
    TypeDescriptor[] params = {listType};
    MethodInfo ctor = cf.addConstructor(pub, params);
    ctor.markSynthetic();

    // code for constructor
    CodeBuilder builder = new CodeBuilder(ctor);
    builder.loadThis();
    builder.loadLocal(builder.getParameters()[0]);
    builder.invokeSuperConstructor(new TypeDescriptor[]{listType});
    builder.returnVoid();


    Method execMethod = getObjectPickerExecMethod();
    MethodInfo mi = cf.addMethod(execMethod);
    mi.markSynthetic();

    TypeDescriptor intType = new TypeDescriptor(int.class);
    TypeDescriptor doubleType = new TypeDescriptor(double.class);
    TypeDescriptor elementType = new TypeDescriptor(clazz);
    TypeDescriptor objectType = new TypeDescriptor(Object.class);
    TypeDescriptor arrayListType = new TypeDescriptor(ArrayList.class);
    TypeDescriptor clazzType = new TypeDescriptor(clazz);
    TypeDescriptor booleanType = new TypeDescriptor(boolean.class);

    // implement ObjectPicker.pickObjects
    builder = new CodeBuilder(mi);

    // List retVal = new ArrayList();
    LocalVariable retVal = builder.createLocalVariable("retVal", listType);
    builder.newObject(arrayListType);
    builder.dup();
    builder.invokeConstructor("java.util.ArrayList", null);
    builder.storeLocal(retVal);


    // int size = items.size();
    builder.loadThis();
    builder.loadField("items", listType);
    builder.invokeInterface("java.util.List", "size", intType, null);
    LocalVariable size = builder.createLocalVariable("listSize", intType);
    builder.storeLocal(size);
    // create the max variable for tracking the max value
    LocalVariable max = builder.createLocalVariable("max", doubleType);
    builder.loadStaticField("java.lang.Double", "MIN_VALUE", doubleType);
    builder.storeLocal(max);

    LocalVariable item = builder.createLocalVariable("item", clazzType);
    LocalVariable val = builder.createLocalVariable("val", doubleType);

    Label ltBranch = builder.createLabel();
    Label endBranch = builder.createLabel();

    // int i = 0 of "for (int i = 0; ...)"
    LocalVariable i = builder.createLocalVariable("i", intType);
    builder.loadConstant(0);
    builder.storeLocal(i);
    Label loopTest = builder.createLabel();
    builder.branch(loopTest);
    Label loop = builder.createLabel().setLocation();

    // list.get(i)
    builder.loadThis();
    builder.loadField("items", listType);
    builder.loadLocal(i);
    builder.invokeInterface("java.util.List", "get", objectType,
                            new TypeDescriptor[]{intType});

    // ElementType item = (ElementType)element
    builder.checkCast(elementType);
    builder.storeLocal(item);

    // d = item.targetMethod();
    builder.loadLocal(item);
    builder.invoke(targetMethod);
    // result should be on the stack
    // cast if necessary
    if (!targetMethod.getReturnType().equals(double.class)) {
      builder.convert(targetMethod.getReturnType(), double.class);
    }
    builder.dup2();
    builder.storeLocal(val);
    builder.loadLocal(max);


    // at this point we should have the method result and the
    // current max value on the stack. So we need to branch
    // depending on which one is max.
    // do < comparison between method retVal and max
    builder.math(Opcode.DCMPL);
    // stack: -1: retVal < max, 0: retVal = max, 1: retVal > max

    builder.ifZeroComparisonBranch(ltBranch, "<=");
    // max = val;
    builder.loadLocal(val);
    builder.storeLocal(max);
    // retVal.clear();
    builder.loadLocal(retVal);
    builder.invokeInterface("java.util.List", "clear", null, null);
    // retVal.add(item);
    builder.loadLocal(retVal);
    builder.loadLocal(item);
    builder.invokeInterface("java.util.List", "add", booleanType,
                            new TypeDescriptor[]{objectType});
    // pop the returned boolean off the stack
    builder.pop();
    builder.branch(endBranch);

    ltBranch.setLocation();

    builder.loadLocal(val);
    builder.loadLocal(max);
    builder.math(Opcode.DCMPL);
    builder.ifZeroComparisonBranch(endBranch, "!=");
    // drop through
    builder.loadLocal(retVal);
    builder.loadLocal(item);
    builder.invokeInterface("java.util.List", "add", booleanType,
                            new TypeDescriptor[]{objectType});
    // pop the returned boolean off the stack
    builder.pop();
    endBranch.setLocation();


    // i++ part of for (int i = 0; i < size; i++)
    builder.integerIncrement(i, 1);

    loopTest.setLocation();
    // i < size part of for (int i = 0; i < size; i++)
    builder.loadLocal(i);
    builder.loadLocal(size);
    builder.ifComparisonBranch(loop, "<");


    builder.loadLocal(retVal);
    builder.returnValue(java.util.List.class);

    return createObjectPicker(cf, list);
  }

  /**
   * Dynamically creates a BasicAction object whose execute method will
   * iterate through the
   * specifed list and call the specified method on each object in that list.
   * The BasicAction is generated by creating bytecode for a class that extends
   * BasicAction, dynamically loading this class, and then instantiating an
   * object of this class. The class looks like the following:
   *
   * <code><pre>
   * public class a_synthetic_name extends BasicAction {
   *
   *  private List target;
   *
   *  public a_synthetic_name(List target) {
   *    this.target = target;
   *  }
   *
   *  public void execute() {
   *    int size = target.size();
   *    for (int i = 0; i < size; i++) {
   *      ObjectType x = (ObjectType)target.get(i);
   *      x.SomeMethod();
   *    }
   *  }
   * }
   * </pre></code>
   *
   * where ObjectType is the class of the object returned by <code>list.get(0).
   * </code>, and <code>SomeMethod</code> is the method whose name is specified
   * in the parameters below.<p>
   *
   * <b>Note</b> that the size of the list is computed once. If the specified
   * method call alters the size of the list, the effect of this BasicAction
   * is undefined, but not good.<p>
   *
   * @param list the list containing the objects on which to call the specified
   * method
   * @param methodName the name of the method to call on the objects in the
   * specified list
   *
   * @return a synthesized BasicAction instance.
   */
  public static BasicAction generateBasicActionForList(List list,
                                                       String methodName)
          throws GeneratorException {
    return generateBasicActionForList(list, methodName, list.get(0).
                                                        getClass(), false);
  }

  /**
   * Dynamically creates a BasicAction object whose execute method will
   * shuffle the specified list with uchicago.src.sim.util.SimUtilites.shuffle
   * and then iterate through the
   * specifed list, calling the specified method on each object in that list.
   * The BasicAction is generated by creating bytecode for a class that extends
   * BasicAction, dynamically loading this class, and then instantiating an
   * object of this class. The class looks like the following:
   *
   * <code><pre>
   * public class a_synthetic_name extends BasicAction {
   *
   *  private List target;
   *
   *  public a_synthetic_name(List target) {
   *    this.target = target;
   *  }
   *
   *  public void execute() {
   *    int size = target.size();
   *    SimUtilities.shuffle(target);
   *    for (int i = 0; i < size; i++) {
   *      ObjectType x = (ObjectType)target.get(i);
   *      x.SomeMethod();
   *    }
   *  }
   * }
   * </pre></code>
   *
   * where ObjectType is the class of the object returned by <code>list.get(0).
   * </code>, and <code>SomeMethod</code> is the method whose name is specified
   * in the parameters below.<p>
   *
   * <b>Note</b> that the size of the list is computed once. If the specified
   * method call alters the size of the list, the effect of this BasicAction
   * is undefined, but not good.<p>
   *
   * @param list the list containing the objects on which to call the specified
   * method
   * @param methodName the name of the method to call on the objects in the
   * specified list
   *
   * @return a synthesized BasicAction instance.
   */
  public static BasicAction generateBasicActionForListRnd(List list,
                                                          String methodName)
          throws GeneratorException {
    return generateBasicActionForList(list, methodName, list.get(0).
                                                        getClass(), true);
  }

  /**
   * Dynamically creates a BasicAction object whose execute method will iterate
   * through the specifed list and call the specified method on each object
   * in that list.
   * The BasicAction is generated by creating bytecode for a class that extends
   * BasicAction, dynamically loading this class, and then instantiating an
   * object of this class. The class looks like the following:
   *
   * <code><pre>
   * public class a_synthetic_name extends BasicAction {
   *
   *  private List target;
   *
   *  public a_synthetic_name(List target) {
   *    this.target = target;
   *  }
   *
   *  public void execute() {
   *    int size = target.size();
   *    for (int i = 0; i < size; i++) {
   *      ObjectType x = (ObjectType)target.get(i);
   *      x.SomeMethod();
   *    }
   *  }
   * }
   * </pre></code>
   *
   * where ObjectType is the specified class, and <code>SomeMethod</code> is
   * the method whose name is specified in the parameters below. Specifying
   * the class allows for the generation of bytecode that invokes a super-class
   * or interface method on the objects in the specified list.<p>
   *
   * <b>Note</b> that the size of the list is computed once. If the specified
   * method call alters the size of the list, the effect of this BasicAction
   * is undefined, but not good.<p>
   *
   * @param list the list containing the objects on which to call the specified
   * method
   * @param methodName the name of the method to call on the objects in the
   * specified list
   * @param clazz the common type of the objects in the specified list
   * @param randomize whether the list should be randomized (shuffled) before
   * iterating through it. Shuffling is done via SimUtilities.shuffle(List)
   *
   * @return a synthesized BasicAction instance.
   */

  public static BasicAction generateBasicActionForList(List list,
                                                       String methodName, Class clazz, boolean randomize)
          throws GeneratorException {

    Method targetMethod = getMethod(clazz, methodName);
    ClassFile cf = new ClassFile(getUnqName(), BasicAction.class);
    cf.markSynthetic();

    AccessFlags priv = new AccessFlags();
    priv.setPrivate(true);
    AccessFlags pub = new AccessFlags();
    pub.setPublic(true);

    TypeDescriptor targetType = new TypeDescriptor(list.getClass());
    cf.addField(priv, "target", targetType).markSynthetic();

    TypeDescriptor[] params = {targetType};
    MethodInfo ctor = cf.addConstructor(pub, params);
    ctor.markSynthetic();

    CodeBuilder builder = new CodeBuilder(ctor);
    builder.loadThis();
    builder.invokeSuperConstructor(null);
    builder.loadThis();
    builder.loadLocal(builder.getParameters()[0]);
    builder.storeField("target", targetType);
    builder.returnVoid();

    Method execMethod = getBasicActionExecMethod();
    MethodInfo mi = cf.addMethod(execMethod);
    mi.markSynthetic();

    TypeDescriptor intType = new TypeDescriptor(int.class);
    TypeDescriptor elementType = new TypeDescriptor(clazz);
    TypeDescriptor objectType = new TypeDescriptor(Object.class);

    builder = new CodeBuilder(mi);
    // get the size of the list
    builder.loadThis();
    builder.loadField("target", targetType);

    // int size = list.size();
    builder.invokeInterface("java.util.List", "size", intType, null);
    LocalVariable size = builder.createLocalVariable("listSize", intType);
    builder.storeLocal(size);


    TypeDescriptor listTD = new TypeDescriptor(List.class);
    if (randomize) {
      // SimUtilities.shuffle(List);
      builder.loadThis();
      builder.loadField("target", targetType);
      builder.invokeStatic("uchicago.src.sim.util.SimUtilities", "shuffle",
                           null, new TypeDescriptor[]{listTD});
    }


    // int i = 0 of "for (int i = 0; ...)"
    LocalVariable i = builder.createLocalVariable("i", intType);
    builder.loadConstant(0);
    builder.storeLocal(i);
    Label loopTest = builder.createLabel();
    builder.branch(loopTest);
    Label loop = builder.createLabel().setLocation();

    // list.get(i)
    builder.loadThis();
    builder.loadField("target", targetType);
    builder.loadLocal(i);

    builder.invokeInterface("java.util.List", "get", objectType,
                            new TypeDescriptor[]{intType});

    // top_stack = (ElementType)top_stack
    builder.checkCast(elementType);
    // element.targetMethod();
    builder.invoke(targetMethod);

    // i++ part of for (int i = 0; i < size; i++)
    builder.integerIncrement(i, 1);

    loopTest.setLocation();
    // i < size part of for (int i = 0; i < size; i++)
    builder.loadLocal(i);
    builder.loadLocal(size);
    builder.ifComparisonBranch(loop, "<");

    builder.returnVoid();

    return createBasicAction(cf, list);

  }

  /**
   * Dynamically creates a BasicAction object whose execute method calls the
   * specified method on the specified object. The BasicAction is generated by
   * creating bytecode for a Class that extends BasicAction,
   * dynamically loading this class, and then instantiating an object of
   * this class. The class looks like the following:
   *
   * <code><pre>
   * public class a_synthetic_name extends BasicAction {
   *
   *  private ObjectType target
   *
   *  public a_synthetic_name(ObjectType target) {
   *    this.target = target;
   *  }
   *
   *  public void execute() {
   *    target.someMethod();
   *  }
   * }
   * </pre></code>
   *
   * where ObjectType is the class of the object specified in the parameters
   * below, and <code>SomeMethod</code> is
   * the method whose name is specified in the parameters below.<p>
   *
   * @param target the object on which to call the method
   * @param methodName the name of the method to call on the specified
   * object
   *
   * @return a synthesized BasicAction instance.
   */

  public static BasicAction generateBasicAction(Object target,
                                                String methodName) throws GeneratorException {
    // make sure target has the specified method
    Method targetMethod = getMethod(target.getClass(), methodName);

    String name = getUnqName();
    ClassFile cf = new ClassFile(name, BasicAction.class);
    cf.markSynthetic();

    AccessFlags priv = new AccessFlags();
    priv.setPrivate(true);
    AccessFlags pub = new AccessFlags();
    pub.setPublic(true);

    TypeDescriptor targetType = new TypeDescriptor(target.getClass());
    cf.addField(priv, "target", targetType).markSynthetic();

    TypeDescriptor[] params = {targetType};
    MethodInfo ctor = cf.addConstructor(pub, params);
    ctor.markSynthetic();

    CodeBuilder builder = new CodeBuilder(ctor);
    builder.loadThis();
    builder.invokeSuperConstructor(null);

    builder.loadThis();
    builder.loadLocal(builder.getParameters()[0]);
    builder.storeField("target", targetType);
    builder.returnVoid();

    Method execMethod = getBasicActionExecMethod();

    MethodInfo mi = cf.addMethod(execMethod);
    mi.markSynthetic();

    builder = new CodeBuilder(mi);
    builder.loadThis();
    builder.loadField("target", targetType);
    builder.invoke(targetMethod);
    builder.returnVoid();

    return createBasicAction(cf, target);
  }

  /**
   * Dynamically creates a DataSource object whose execute method calls
   * the specified method on the specified object. The result of this
   * method call is then returned. The DataSource is generated by
   * creating bytecode for a Class that implements DataSource,
   * dynamically loading this class, and then instantiating an object of
   * this class. The class looks like the following:
   *
   * <code><pre>
   * public class a_synthetic_name implements DataSource {
   *
   *  private ObjectType target
   *
   *  public a_synthetic_name(ObjectType target) {
   *    this.target = target;
   *  }
   *
   *  public Object execute() {
   *    return target.someMethod();
   *  }
   * }
   * </pre></code>
   *
   * where ObjectType is the class of the object specified in the parameters
   * below, and <code>SomeMethod</code> is
   * the method whose name is specified in the parameters below.<p>
   *
   * @param target the object on which to call the method
   * @param methodName the name of the method to call on the specified
   * object. <b>The method must return an object</b>
   *
   * @return a synthesized DataSource instance.
   */

  public static DataSource generateDataSource(Object target,
                                              String methodName) throws GeneratorException {

    // make sure target has the specified method
    Method targetMethod = getMethod(target.getClass(), methodName, Object.class);

    String name = getUnqName();
    ClassFile cf = new ClassFile(name);
    cf.addInterface(DataSource.class);
    cf.markSynthetic();

    AccessFlags priv = new AccessFlags();
    priv.setPrivate(true);
    AccessFlags pub = new AccessFlags();
    pub.setPublic(true);

    TypeDescriptor targetType = new TypeDescriptor(target.getClass());
    cf.addField(priv, "target", targetType).markSynthetic();

    TypeDescriptor[] params = {targetType};
    MethodInfo ctor = cf.addConstructor(pub, params);
    ctor.markSynthetic();

    CodeBuilder builder = new CodeBuilder(ctor);
    builder.loadThis();
    builder.invokeSuperConstructor(null);

    builder.loadThis();
    builder.loadLocal(builder.getParameters()[0]);
    builder.storeField("target", targetType);
    builder.returnVoid();

    Method execMethod = getDataSourceExecMethod();

    MethodInfo mi = cf.addMethod(execMethod);
    mi.markSynthetic();

    builder = new CodeBuilder(mi);
    builder.loadThis();
    builder.loadField("target", targetType);
    builder.invoke(targetMethod);
    builder.returnValue(Object.class);

    return createDataSource(cf, target);
  }


  /**
   * Dynamically creates a NumericDataSource object whose execute method calls
   * the specified method on the specified object. The result of this
   * method call is then returned. The DataSource is generated by
   * creating bytecode for a Class that implements NumericDataSource,
   * dynamically loading this class, and then instantiating an object of
   * this class. The class looks like the following:
   *
   * <code><pre>
   * public class a_synthetic_name implements NumericDataSource {
   *
   *  private ObjectType target
   *
   *  public a_synthetic_name(ObjectType target) {
   *    this.target = target;
   *  }
   *
   *  public double execute() {
   *    return target.someMethod();
   *  }
   * }
   * </pre></code>
   *
   * where ObjectType is the class of the object specified in the parameters
   * below, and <code>SomeMethod</code> is
   * the method whose name is specified in the parameters below.<p>
   *
   * @param target the object on which to call the method
   * @param methodName the name of the method to call on the specified
   * object. <b>The method must return a double</b>
   *
   * @return a synthesized DataSource instance.
   */

  public static NumericDataSource generateNumericDataSource(Object target,
                                                            String methodName) throws GeneratorException {

    // make sure target has the specified method
    Method targetMethod = getMethodRetDouble(target.getClass(), methodName, null);

    String name = getUnqName();
    ClassFile cf = new ClassFile(name);
    cf.addInterface(NumericDataSource.class);
    cf.markSynthetic();

    AccessFlags priv = new AccessFlags();
    priv.setPrivate(true);
    AccessFlags pub = new AccessFlags();
    pub.setPublic(true);

    TypeDescriptor targetType = new TypeDescriptor(target.getClass());
    cf.addField(priv, "target", targetType).markSynthetic();

    TypeDescriptor[] params = {targetType};
    MethodInfo ctor = cf.addConstructor(pub, params);
    ctor.markSynthetic();

    CodeBuilder builder = new CodeBuilder(ctor);
    builder.loadThis();
    builder.invokeSuperConstructor(null);

    builder.loadThis();
    builder.loadLocal(builder.getParameters()[0]);
    builder.storeField("target", targetType);
    builder.returnVoid();

    Method execMethod = getNumericDataSourceExecMethod();

    MethodInfo mi = cf.addMethod(execMethod);
    mi.markSynthetic();

    builder = new CodeBuilder(mi);
    builder.loadThis();
    builder.loadField("target", targetType);
    builder.invoke(targetMethod);
    if (!targetMethod.getReturnType().equals(double.class)) {
      builder.convert(targetMethod.getReturnType(), double.class);
    }
    builder.returnValue(double.class);

    return createNumericDataSource(cf, target);
  }

  /**
   * Dynamically creates a BinDataSource object whose getBinValue method is
   * passed an Object of whatever type param is. This object is cast to
   * the appropriate type and the specified method is called.
   * method call is then returned. The BinDataSource is generated by
   * creating bytecode for a Class that implements BinDataSource,
   * dynamically loading this class, and then instantiating an object of
   * this class. The class looks like the following:
   *
   * <code><pre>
   * public class a_synthetic_name implements BinDataSource {
   *
   *
   *  public a_synthetic_name() {}
   *
   *  public double getBinValue(Object obj) {
   *    return ((param_type)o).someMethod();
   *  }
   * }
   * </pre></code>
   *
   * where <code>SomeMethod</code> is
   * the method whose name is specified in the parameters below. This may
   * also add a cast to the result of target.someMethod if that is a
   * primitive non-double numeric value
   * <p>
   *
   * @param param a prototypical on which to call the method
   * @param methodName the name of the method to call on the specified
   * object. <b>The method must return a numeric value</b>
   *
   * @return a synthesized BinDataSource instance.
   */

  public static BinDataSource generateNoTargetBinDataSource(Object param,
                                                            String methodName)
          throws GeneratorException {

    // make sure target has the specified method
    Method targetMethod = getMethodRetDouble(param.getClass(), methodName,
                                             null);

    String name = getUnqName();
    ClassFile cf = new ClassFile(name);
    cf.addInterface(BinDataSource.class);
    cf.markSynthetic();

    AccessFlags priv = new AccessFlags();
    priv.setPrivate(true);
    AccessFlags pub = new AccessFlags();
    pub.setPublic(true);

    TypeDescriptor[] params = {};
    MethodInfo ctor = cf.addConstructor(pub, params);
    ctor.markSynthetic();

    // create the constructor
    CodeBuilder builder = new CodeBuilder(ctor);
    builder.loadThis();
    builder.invokeSuperConstructor(null);

    builder.returnVoid();

    Method bvMethod = getBDSBinValueMethod();

    MethodInfo mi = cf.addMethod(bvMethod);
    mi.markSynthetic();

    builder = new CodeBuilder(mi);
    builder.loadLocal(builder.getParameters()[0]);
    builder.checkCast(new TypeDescriptor(param.getClass()));
    builder.invoke(targetMethod);
    if (!targetMethod.getReturnType().equals(double.class)) {
      builder.convert(targetMethod.getReturnType(), double.class);
    }
    builder.returnValue(double.class);

    return createNoTargetBinDataSource(cf, param);
  }

  private static BinDataSource createNoTargetBinDataSource(ClassFile cf,
                                                           Object param)
          throws GeneratorException {
    ClassInjector ci = new ClassInjector(param.getClass().getClassLoader());
    BinDataSource bds = null;

    try {
      OutputStream out = ci.getStream(cf.getClassName());
      cf.writeTo(out);
      out.close();

      Class clazz = ci.loadClass(cf.getClassName());
      Constructor baCtor = clazz.getConstructor(new Class[]{});
      bds = (BinDataSource) baCtor.newInstance(new Object[]{});
    } catch (Exception ex) {
      throw new GeneratorException(ex.getMessage(), ex);
    }

    return bds;
  }

  /**
   * Dynamically creates a BinDataSource object whose getBinValue method calls
   * the specified method on the specified object. The result of this
   * method call is then returned. The BinDataSource is generated by
   * creating bytecode for a Class that implements BinDataSource,
   * dynamically loading this class, and then instantiating an object of
   * this class. The class looks like the following:
   *
   * <code><pre>
   * public class a_synthetic_name implements BinDataSource {
   *
   *  private ObjectType target
   *
   *  public a_synthetic_name(ObjectType target) {
   *    this.target = target;
   *  }
   *
   *  public double getBinValue(Object obj) {
   *    return target.someMethod(obj);
   *  }
   * }
   * </pre></code>
   *
   * where ObjectType is the class of the target specified in the parameters
   * below, and <code>SomeMethod</code> is
   * the method whose name is specified in the parameters below. This may
   * also add a cast to the result of target.someMethod if that is a
   * primitive non-double numeric value
   * <p>
   *
   * @param target the object on which to call the method
   * @param methodName the name of the method to call on the specified
   * object. <b>The method must return a numeric value</b>
   *
   * @return a synthesized BinDataSource instance.
   */

  public static BinDataSource generateBinDataSource(Object target,
                                                    String methodName)
          throws GeneratorException {

    // make sure target has the specified method
    Method targetMethod = getMethodObjDouble(target.getClass(), methodName);

    String name = getUnqName();
    ClassFile cf = new ClassFile(name);
    cf.addInterface(BinDataSource.class);
    cf.markSynthetic();

    AccessFlags priv = new AccessFlags();
    priv.setPrivate(true);
    AccessFlags pub = new AccessFlags();
    pub.setPublic(true);

    TypeDescriptor targetType = new TypeDescriptor(target.getClass());
    cf.addField(priv, "target", targetType).markSynthetic();

    TypeDescriptor[] params = {targetType};
    MethodInfo ctor = cf.addConstructor(pub, params);
    ctor.markSynthetic();

    // create the constructor
    CodeBuilder builder = new CodeBuilder(ctor);
    builder.loadThis();
    builder.invokeSuperConstructor(null);

    builder.loadThis();
    builder.loadLocal(builder.getParameters()[0]);
    builder.storeField("target", targetType);
    builder.returnVoid();

    Method bvMethod = getBDSBinValueMethod();

    MethodInfo mi = cf.addMethod(bvMethod);
    mi.markSynthetic();

    builder = new CodeBuilder(mi);
    builder.loadThis();
    builder.loadField("target", targetType);
    builder.loadLocal(builder.getParameters()[0]);
    builder.invoke(targetMethod);
    if (!targetMethod.getReturnType().equals(double.class)) {
      builder.convert(targetMethod.getReturnType(), double.class);
    }
    builder.returnValue(double.class);

    return createBinDataSource(cf, target);
  }

  private static BinDataSource createBinDataSource(ClassFile cf, Object target)
          throws GeneratorException {
    ClassInjector ci = new ClassInjector(target.getClass().getClassLoader());
    BinDataSource bds = null;

    try {
      OutputStream out = ci.getStream(cf.getClassName());
      cf.writeTo(out);
      out.close();

      Class clazz = ci.loadClass(cf.getClassName());
      Constructor baCtor = clazz.getConstructor(new Class[]{target.getClass()});
      bds = (BinDataSource) baCtor.newInstance(new Object[]{target});
    } catch (Exception ex) {
      throw new GeneratorException(ex.getMessage(), ex);
    }

    return bds;
  }

  /**
   * Dynamically creates a Sequence object whose execute method calls
   * the specified method on the specified object. The result of this
   * method call is then returned. The Sequence is generated by
   * creating bytecode for a Class that implements Sequence,
   * dynamically loading this class, and then instantiating an object of
   * this class. The class looks like the following:
   *
   * <code><pre>
   * public class a_synthetic_name implements Sequence {
   *
   *  private ObjectType target
   *
   *  public a_synthetic_name(ObjectType target) {
   *    this.target = target;
   *  }
   *
   *  public double getSValue() {
   *    return target.someMethod();
   *  }
   * }
   * </pre></code>
   *
   * where ObjectType is the class of the object specified in the parameters
   * below, and <code>SomeMethod</code> is
   * the method whose name is specified in the parameters below. This may
   * also add a cast to the result of target.someMethod if that is a primitive non-double
   * numeric value
   * <p>
   *
   * @param target the object on which to call the method
   * @param methodName the name of the method to call on the specified
   * object. <b>The method must return a numeric value</b>
   *
   * @return a synthesized Sequence instance.
   */

  public static Sequence generateSequence(Object target,
                                          String methodName) throws GeneratorException {

    // make sure target has the specified method
    Method targetMethod = getMethodRetDouble(target.getClass(), methodName,
                                             new Class[]{});

    String name = getUnqName();
    ClassFile cf = new ClassFile(name);
    cf.addInterface(Sequence.class);
    cf.markSynthetic();

    AccessFlags priv = new AccessFlags();
    priv.setPrivate(true);
    AccessFlags pub = new AccessFlags();
    pub.setPublic(true);

    TypeDescriptor targetType = new TypeDescriptor(target.getClass());
    cf.addField(priv, "target", targetType).markSynthetic();

    TypeDescriptor[] params = {targetType};
    MethodInfo ctor = cf.addConstructor(pub, params);
    ctor.markSynthetic();

    // create the constructor
    CodeBuilder builder = new CodeBuilder(ctor);
    builder.loadThis();
    builder.invokeSuperConstructor(null);

    builder.loadThis();
    builder.loadLocal(builder.getParameters()[0]);
    builder.storeField("target", targetType);
    builder.returnVoid();

    Method svalueMethod = getSequenceSValueMethod();

    MethodInfo mi = cf.addMethod(svalueMethod);
    mi.markSynthetic();

    builder = new CodeBuilder(mi);
    builder.loadThis();
    builder.loadField("target", targetType);
    builder.invoke(targetMethod);
    if (!targetMethod.getReturnType().equals(double.class)) {
      builder.convert(targetMethod.getReturnType(), double.class);
    }
    builder.returnValue(double.class);

    return createSequence(cf, target);
  }

  private static Sequence createSequence(ClassFile cf, Object target)
          throws GeneratorException {
    ClassInjector ci = new ClassInjector(target.getClass().getClassLoader());
    Sequence seq = null;

    try {
      OutputStream out = ci.getStream(cf.getClassName());
      cf.writeTo(out);
      out.close();

      Class clazz = ci.loadClass(cf.getClassName());
      Constructor baCtor = clazz.getConstructor(new Class[]{target.getClass()});
      seq = (Sequence) baCtor.newInstance(new Object[]{target});
    } catch (Exception ex) {
      throw new GeneratorException(ex.getMessage(), ex);
    }

    return seq;
  }

  private static NumericDataSource createNumericDataSource(ClassFile cf, Object target)
          throws GeneratorException {
    ClassInjector ci = new ClassInjector(target.getClass().getClassLoader());
    NumericDataSource nds = null;

    try {
      OutputStream out = ci.getStream(cf.getClassName());
      cf.writeTo(out);
      out.close();

      Class clazz = ci.loadClass(cf.getClassName());
      Constructor baCtor = clazz.getConstructor(new Class[]{target.getClass()});
      nds = (NumericDataSource) baCtor.newInstance(new Object[]{target});
    } catch (Exception ex) {
      throw new GeneratorException(ex.getMessage(), ex);
    }

    return nds;
  }

  private static DataSource createDataSource(ClassFile cf, Object target)
          throws GeneratorException {
    ClassInjector ci = new ClassInjector(target.getClass().getClassLoader());
    DataSource ds = null;

    try {
      //FileOutputStream o = new FileOutputStream(cf.getClassName() + ".class");
      //cf.writeTo(o);
      //o.close();

      OutputStream out = ci.getStream(cf.getClassName());
      cf.writeTo(out);
      out.close();

      Class clazz = ci.loadClass(cf.getClassName());
      Constructor baCtor = clazz.getConstructor(new Class[]{target.getClass()});
      ds = (DataSource) baCtor.newInstance(new Object[]{target});
    } catch (Exception ex) {
      throw new GeneratorException(ex.getMessage(), ex);
    }

    return ds;
  }

  private static StatCalculator createStatCalculator(ClassFile cf, List list)
          throws GeneratorException {

    StatCalculator mp = null;

    if (loader == null) {
      //ClassInjector ci = new ClassInjector(ClassFile.class.getClassLoader());
      ClassInjector ci = new ClassInjector(list.getClass().getClassLoader());
      try {
        //FileOutputStream o = new FileOutputStream(cf.getClassName() + ".class");
        //cf.writeTo(o);
        //o.close();

        OutputStream out = ci.getStream(cf.getClassName());
        cf.writeTo(out);
        out.close();

        Class clazz = ci.loadClass(cf.getClassName());
        Constructor baCtor = clazz.getConstructor(new Class[]{List.class});
        mp = (StatCalculator) baCtor.newInstance(new Object[]{list});
      } catch (Exception ex) {
        throw new GeneratorException(ex.getMessage(), ex);
      }
    } else {

      try {
        loader.addClass(cf.getClassName(), cf);
        Class clazz = loader.loadClass(cf.getClassName());
        Constructor baCtor = clazz.getConstructor(new Class[]{List.class});
        mp = (StatCalculator) baCtor.newInstance(new Object[]{list});
      } catch (Exception ex) {
        throw new GeneratorException(ex.getMessage(), ex);
      }
    }
    return mp;
  }

  private static ObjectPicker createObjectPicker(ClassFile cf, List list)
          throws GeneratorException {

    ObjectPicker op = null;

    if (loader == null) {
      //ClassInjector ci = new ClassInjector(ClassFile.class.getClassLoader());
      ClassInjector ci = new ClassInjector(list.getClass().getClassLoader());
      try {
        //FileOutputStream o = new FileOutputStream(cf.getClassName() + ".class");
        //cf.writeTo(o);
        //o.close();

        OutputStream out = ci.getStream(cf.getClassName());
        cf.writeTo(out);
        out.close();

        Class clazz = ci.loadClass(cf.getClassName());
        Constructor baCtor = clazz.getConstructor(new Class[]{List.class});
        op = (ObjectPicker) baCtor.newInstance(new Object[]{list});
      } catch (Exception ex) {
        throw new GeneratorException(ex.getMessage(), ex);
      }
    } else {

      try {
        loader.addClass(cf.getClassName(), cf);
        Class clazz = loader.loadClass(cf.getClassName());
        Constructor baCtor = clazz.getConstructor(new Class[]{List.class});
        op = (ObjectPicker) baCtor.newInstance(new Object[]{list});
      } catch (Exception ex) {
        throw new GeneratorException(ex.getMessage(), ex);
      }
    }
    return op;
  }


  private static BasicAction createBasicAction(ClassFile cf, Object target)
          throws GeneratorException {
    BasicAction ba = null;

    if (loader == null) {
      //ClassInjector ci = new ClassInjector(ClassFile.class.getClassLoader());
      ClassInjector ci = new ClassInjector(target.getClass().getClassLoader());
      try {
        //FileOutputStream o = new FileOutputStream(cf.getClassName() + ".class");
        //cf.writeTo(o);
        //o.close();

        OutputStream out = ci.getStream(cf.getClassName());
        cf.writeTo(out);
        out.close();

        Class clazz = ci.loadClass(cf.getClassName());
        Constructor baCtor = clazz.getConstructor(new Class[]{target.getClass()});
        ba = (BasicAction) baCtor.newInstance(new Object[]{target});
      } catch (Exception ex) {
        throw new GeneratorException(ex.getMessage(), ex);
      }
    } else {

      try {
        loader.addClass(cf.getClassName(), cf);
        Class clazz = loader.loadClass(cf.getClassName());
        Constructor baCtor = clazz.getConstructor(new Class[]{target.getClass()});
        ba = (BasicAction) baCtor.newInstance(new Object[]{target});
      } catch (Exception ex) {
        throw new GeneratorException(ex.getMessage(), ex);
      }
    }
    return ba;
  }
}







