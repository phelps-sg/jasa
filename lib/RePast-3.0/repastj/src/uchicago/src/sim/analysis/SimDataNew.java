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
package uchicago.src.sim.analysis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import ViolinStrings.Strings;

/**
 * Holds data in tabular format - a vector of vectors. Also provides methods
 * for printing the data to a file.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see DataRecorder
 */

public class SimDataNew implements Serializable{

  // A vector of vectors
  private ArrayList data = new ArrayList();
  private String header = "\"tick\"";
  private String modelHeader;

  private String delimiter = ",";
  private String lineSeparator = "\n";

  /**
   * Constructs a new SimData object from the model header, model and batch flag
   * @param modelHeader
   * the file header.
   */

  public SimDataNew(String modelHeader){
    this.modelHeader = modelHeader;
    lineSeparator = System.getProperty("line.separator");
  }

  public String getHeader() {
    return header;
  }

  public String getModelHeader() {
    return modelHeader;
  }

  /**
   * Sets the column delimiter. Data is written out in tabular format
   * where the columns are separated by the specified delimiter.
   *
   * @param delim the new delimiter
   */
  public void setDelimiter(String delim) {
    header = Strings.change(header, delimiter, delim);
    delimiter = delim;
  }

  /**
   * Adds the specified String to the header associated with this data
   */
  public void addToHeader(String s) {
    if (header.length() == 0) {
      header += "\"" + s + "\"";
    }else if(s.equalsIgnoreCase("run")){
      header = "\"" + s + "\"" + delimiter + header;
    } else {
      header += delimiter + "\"" + s + "\"";
    }
  }

  /**
   * Adds the specified list to the header associated with this data
   */
  public void addToHeader(List l) {
    ListIterator li = l.listIterator();
    while (li.hasNext()) {
      String s = (String) li.next();
      if (header.length() == 0) {
        header += s;
      } else {
        header += delimiter + s;
      }
    }
  }

  /**
   * Add a vector of data to this SimData.
   * @param v the data to add.
   */
  public void addData(List v) {
    data.add(v);
  }


  /**
   * Get the data stored in this SimData object and clear this object.  This is the
   * preferred way of recording data.  Get the data as a String and return it to the
   * recorder object.
   * @return The data currently in the SimData object.
   */
  public String getData(){
    StringBuffer out = new StringBuffer();
    // write all the data and clear the vector

    for (int i = 0; i < data.size(); i++) {
      List v = (List) data.get(i);
      for (int j = 0; j < v.size(); j++) {
        if (j == 0)
          out.append(v.get(j).toString());
        else
          out.append(delimiter + v.get(j).toString());
      }
      out.append(lineSeparator);
    }
    data.clear();
    return out.toString();
  }

  public void clearData(){
    this.data.clear();
  }

  public Iterator iterator(){
    return new SimDataIterator();
  }

  public class SimDataIterator implements Iterator{
    private Iterator i;

    SimDataIterator(){
      i = data.iterator();
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext() {
      return i.hasNext();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */
    public Object next() {
      List v = (List) i.next();
      StringBuffer out = new StringBuffer();
      for (int j = 0; j < v.size(); j++) {
        if (j == 0)
          out.append(v.get(j).toString());
        else
          out.append(delimiter + v.get(j).toString());
      }
      out.append(lineSeparator);
      return out.toString();
    }

    /**
     *
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation).  This method can be called only once per
     * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
     * the underlying collection is modified while the iteration is in
     * progress in any way other than by calling this method.
     *
     * @exception UnsupportedOperationException if the <tt>remove</tt>
     *		  operation is not supported by this Iterator.

     * @exception IllegalStateException if the <tt>next</tt> method has not
     *		  yet been called, or the <tt>remove</tt> method has already
     *		  been called after the last call to the <tt>next</tt>
     *		  method.
     */
    public void remove() {
      i.remove();
    }
  }
}


