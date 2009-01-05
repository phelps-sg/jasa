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
package uchicago.src.codegen;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import uchicago.src.sim.engine.BasicAction;

import com.go.trove.classfile.AccessFlags;
import com.go.trove.classfile.ClassFile;
import com.go.trove.classfile.CodeBuilder;
import com.go.trove.classfile.MethodInfo;
import com.go.trove.classfile.TypeDescriptor;

public class ClassFactory extends ClassLoader {

  public ClassFactory() {}

  public ClassFactory(ClassLoader loader) {
    super(loader);
  }

  public ClassFile makeClassFile(String path) throws IOException {
    DataInputStream din = new DataInputStream(new FileInputStream(path));
    ClassFile cf = ClassFile.readFrom((DataInput)din);
    return cf;
  }

  public Class loadClass(ClassFile cf) throws IOException,
      ClassNotFoundException
  {
    byte[] bytes = getBytes(cf);
    if (bytes == null) {
      throw new ClassNotFoundException(cf.getClassName());
    }

    Class clazz = defineClass(cf.getClassName(), bytes, 0, bytes.length);
    this.resolveClass(clazz);
    return clazz;
  }

  private byte[] getBytes(ClassFile cf) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    cf.writeTo(out);
    out.close();

    return out.toByteArray();
  }

  private Method getExecMethod() {
    Method execMethod = null;
    try {
      execMethod = BasicAction.class.getMethod("execute", new Class[] {});
    } catch (NoSuchMethodException ex) {
      ex.printStackTrace();
      System.exit(0);
    }

    return execMethod;
  }

  public ClassFile makeInner(String name, ClassFile cf, Class outerClass) {

    //ClassFile cf = outClassFile.addInnerClass(name, BasicAction.class);
    AccessFlags pub = new AccessFlags();
    pub.setPublic(true);
    pub.setStatic(false);

    AccessFlags priv = new AccessFlags();
    priv.setPrivate(true);

    cf.getAccessFlags().setPrivate(false);
    cf.getAccessFlags().setPublic(true);
    cf.getAccessFlags().setStatic(false);

    TypeDescriptor outerType = new TypeDescriptor(outerClass);
    cf.addField(priv, "outer", outerType).markSynthetic();

    TypeDescriptor[] params = {outerType};

    MethodInfo ctor = cf.addConstructor(pub, params);
    ctor.markSynthetic();

    // private InnerTest() {super();}
    CodeBuilder builder = new CodeBuilder(ctor);
    builder.loadThis();
    builder.invokeSuperConstructor(null);

    builder.loadThis();
    builder.loadLocal(builder.getParameters()[0]);
    builder.storeField("outer", outerType);
    builder.returnVoid();

    Method exec = getExecMethod();
    MethodInfo mi = cf.addMethod(exec);
    mi.markSynthetic();

    // this tests inner access to outer class
    // assumes outer class has string field called test
    builder = new CodeBuilder(mi);
    builder.loadThis();
    builder.loadField("outer", outerType);
    builder.loadConstant("goodbye");
    builder.storeField(outerClass.getName(), "test", new TypeDescriptor(String.class));
    builder.returnVoid();

    return cf;
  }

  public Object makeObject(ClassFile cf, Class[] cParam, Object[] iParam)
    throws NoSuchMethodException, ClassNotFoundException, IOException,
    InvocationTargetException, IllegalAccessException, InstantiationException
  {
    Class clazz = loadClass(cf);
    System.out.println("clazz: " + clazz);
    return makeObject(clazz, cParam, iParam);
  }

  public Object makeObject(Class clazz, Class[] cParam, Object[] iParam)
    throws NoSuchMethodException, ClassNotFoundException,
    InvocationTargetException, IllegalAccessException, InstantiationException
  {
    Object ba = null;
    //Class clazz = ci.loadClass(cf.getClassName());
    Constructor[] ctors = clazz.getConstructors();
    for (int i = 0; i < ctors.length; i++) {
      System.out.println(ctors[i]);
    }

    if (cParam != null) {
      Constructor baCtor = clazz.getConstructor(cParam);
      ba = baCtor.newInstance(iParam);
    } else {
      Constructor baCtor = clazz.getConstructor(new Class[]{});
      ba = baCtor.newInstance(new Object[]{});
    }

    return ba;
  }

  public static void main(String[] args) {
    try {
      ClassFactory loader = new ClassFactory();
      ClassFile eClassf = loader.makeClassFile("/home/nick/jbproject/classes/uchicago/src/codegen/jython/EmbedTest.class");
      //Class eClass = loader.loadClass("uchicago.src.codegen.jython.EmbedTest", true);
      Class eClass = loader.loadClass(eClassf);
      ClassFile cf = eClassf.addInnerClass("Inner", BasicAction.class);
      cf = loader.makeInner("Inner", cf, eClass);
      Stepper embed = (Stepper)loader.makeObject(eClass, null, null);
      //Object embed = loader.makeObject(eClass, null, null);
      BasicAction iObj = (BasicAction)loader.makeObject(cf, new Class[]{embed.getClass()},
                                        new Object[]{embed});

      System.out.println("inner class loader: " + iObj.getClass().getClassLoader());
      System.out.println("outer class loader: " + embed.getClass().getClassLoader());

      System.out.println("inner class package: " + iObj.getClass().getPackage());
      System.out.println("outer class package: " + embed.getClass().getPackage());

      embed.setBasicAction(iObj);
      System.out.println("before: " + embed.getTest());
      embed.step();
      System.out.println("after: " + embed.getTest());
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(0);
    }
  }
}


