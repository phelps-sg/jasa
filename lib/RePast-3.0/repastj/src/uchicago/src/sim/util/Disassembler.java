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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Simple class file dissassembler. This takes a .class file as a File
 * and does enough dissassembly to return the fully qualified name of the
 * Class described by that .class file.
 *
 * @version $Revision$ $Date$
 */

public class Disassembler {

  public static final int CONSTANT_CLASS = 7;
  public static final int FIELD_REF = 9;
  public static final int METHOD_REF = 10;
  public static final int INT_METHOD_REF = 11;
  public static final int STRING = 8;
  public static final int INTEGER = 3;
  public static final int FLOAT = 4;
  public static final int LONG = 5;
  public static final int DOUBLE = 6;
  public static final int NAME_AND_TYPE = 12;
  public static final int UTF8 = 1;

  class ClassDef {
    int magic, minorVer,  majorVer, poolCount, accessFlags, classIndex;
    HashMap classRefs = new HashMap();
  }

  class ClassRef {
    int tag, nameIndex;
  }

  class Utf8 {
    int tag;
    String string;
  }

  private ClassDef def = new ClassDef();

  /**
   * Creates a Dissassebler to disassemble the specified file.
   * @param file the .class file to disassemble
   * @throws IOException if the specified file is not a valid .class file
   */

  public Disassembler(File file) throws IOException {
    DataInputStream in = new DataInputStream(new FileInputStream(file));
    def.magic = in.readInt();
    if (def.magic != 0xCAFEBABE) {
      in.close();
      throw new IOException("Invalid class file");
    }

    def.minorVer = in.readUnsignedShort();
    def.majorVer = in.readUnsignedShort();
    def.poolCount = in.readUnsignedShort();
    readPool(in);
  }

  private void readPool(DataInputStream in) throws IOException {
    for (int i = 1; i < def.poolCount; i++) {
      int tag = in.readUnsignedByte();
      switch (tag) {
        case CONSTANT_CLASS:
          ClassRef ref = new ClassRef();
          ref.tag = CONSTANT_CLASS;
          ref.nameIndex = in.readUnsignedShort();
          def.classRefs.put(new Integer(i), ref);

          break;

        case UTF8:
          Utf8 utf = new Utf8();
          utf.tag = UTF8;
          utf.string = in.readUTF();
          def.classRefs.put(new Integer(i), utf);
          break;

          /*
           * None of the other entries matter to us so we just id
           * and skip.
           */
        case STRING:
          in.skipBytes(2);
          break;

        case FIELD_REF:
        case METHOD_REF:
        case INT_METHOD_REF:
        case INTEGER:
        case NAME_AND_TYPE:
        case FLOAT:
          in.skipBytes(4);
          break;

        case LONG:
        case DOUBLE:
          in.skipBytes(8);
          // longs and doubles take up two entries each in the
          // constant pool so we need to increment the count
          i++;
          break;

        default:
          in.close();
          throw new IOException("Invalid class file");
      }
    }

    def.accessFlags = in.readUnsignedShort();
    def.classIndex = in.readUnsignedShort();
    in.close();
  }

  /**
   * Returns the fully qualified name of the Class whose .class file is
   * being disassembled.
   */ 
  public String getFQClassName() {
    ClassRef ref = (ClassRef)def.classRefs.get(new Integer(def.classIndex));
    Utf8 utf = (Utf8)def.classRefs.get(new Integer(ref.nameIndex));
    return utf.string;
  }

  public static void main(String[] args) {
    try {
      File file = new File("/home/nick/classes2/uchicago/src/sim/heatBugs/HeatBugsModel.class");
      Disassembler d = new Disassembler(file);
      System.out.println("d.getClassName() = " + d.getFQClassName());
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(0);
    }
  }
}
