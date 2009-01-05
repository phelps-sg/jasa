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
package uchicago.src.collection;

import cern.colt.map.OpenIntObjectHashMap;

public class SparseObjectMatrix implements BaseMatrix {

  private int sizeX, sizeY;
  private OpenIntObjectHashMap elements;

  public SparseObjectMatrix (int sizeX, int sizeY) {
    this.sizeX = sizeX;
    this.sizeY = sizeY;

    // these values come from the colt implementation of a SparseMatrix
    elements = new OpenIntObjectHashMap();
  }
  
  public Object get(int x, int y) {
    //System.out.println("get index: " + index);
    return elements.get(y * sizeX + x);
  }
  
  public void put(int x, int y, Object obj) {
    //System.out.println("put index: " + index);
    elements.put(y * sizeX + x, obj);
  }
  
  public Object remove(int x, int y) {
    int index = y * sizeX + x;
    Object o = elements.get(index);
    elements.put(index, null);
    return o;
  }

  public int elementSize() {
    return elements.size();
  }
  
  public int size() {
    return sizeY * sizeX;
  }
  
  public int getNumRows() {
    return sizeY;
  }
  
  public int getNumCols() {
    return sizeX;
  }

  public void trimToSize() {
    elements.trimToSize();
  }

  public void trim() {
    trimToSize();
  }
}
