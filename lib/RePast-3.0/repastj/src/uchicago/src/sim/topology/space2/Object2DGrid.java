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
package uchicago.src.sim.topology.space2;


import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import uchicago.src.collection.BaseMatrix;
import uchicago.src.collection.NewMatrix;
import uchicago.src.sim.topology.Context;
import uchicago.src.sim.topology.ModifyableTopology;
import uchicago.src.sim.topology.RelationTopology;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.SimUtilities;


/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Jun 16, 2003
 * Time: 11:21:36 AM
 * To change this template use Options | File Templates.
 */
public class Object2DGrid implements Context, Discrete2DSpace{
//  Object2DGrid grid;
  HashMap locationMap;

  /**
   * Get all of the elements related to the parameter that share the given
   * relationship within the distance.
   * @param element The central element (whose neighbors we want)
   * @param relationType Which relationship we are interested in querying
   * @param distance The range for the query.
   * @return
   */
  public List getRelated(Object element, String relationType, double distance) {
    return null;
  }

  /**
   * Add a Relationship of the given type between the two elements.  Issues of
   * directionality should be dealt with by the RelationTopology.  The
   * relationship should have the given strength.  Both of the elements
   * must be in the Context.
   *
   * @param element1
   * @param element2
   * @param relationType
   * @param distance
   */
  public void addRelation(Object element1, Object element2, String relationType,
                          double distance) {
  }

  /**
   * Determine if the two elements share a relationship in the {@link RelationTopology}
   * within the specified distance.
   *
   * @param element1
   * @param element2
   * @param distance
   * @param relationType
   * @return
   */
  public boolean areRelated(Object element1, Object element2, double distance,
                            String relationType) {
    return false;
  }

  /**
   * Get the topological distance between the two elements in the given {@link
   * RelationTopology}.
   *
   * @param element1
   * @param element2
   * @param relationType)
   * @return
   */
  public double distance(Object element1, Object element2, String relationType) {
    return 0;
  }

  /**
   * Get the actual topology defined by the relationType.
   *
   * @param relationType
   * @return
   */
  public RelationTopology getRelationTopology(String relationType) {
    return null;
  }

  /**
   * Returns <tt>true</tt> if this set contains no elements.
   *
   * @return <tt>true</tt> if this set contains no elements.
   */
  public boolean isEmpty() {
    return false;
  }

  /**
   * Returns <tt>true</tt> if this set contains the specified element.  More
   * formally, returns <tt>true</tt> if and only if this set contains an
   * element <code>e</code> such that <code>(o==null ? e==null :
   * o.equals(e))</code>.
   *
   * @param o element whose presence in this set is to be tested.
   * @return <tt>true</tt> if this set contains the specified element.
   * @throws ClassCastException if the type of the specified element
   * 	       is incompatible with this set (optional).
   * @throws NullPointerException if the specified element is null and this
   *         set does not support null elements (optional).
   */
  public boolean contains(Object o) {
    return false;
  }

  /**
   * Returns an array containing all of the elements in this set.
   * Obeys the general contract of the <tt>Collection.toArray</tt> method.
   *
   * @return an array containing all of the elements in this set.
   */
  public Object[] toArray() {
    return new Object[0];
  }

  /**
   * Returns an array containing all of the elements in this set; the
   * runtime type of the returned array is that of the specified array.
   * Obeys the general contract of the
   * <tt>Collection.toArray(Object[])</tt> method.
   *
   * @param a the array into which the elements of this set are to
   *		be stored, if it is big enough; otherwise, a new array of the
   * 		same runtime type is allocated for this purpose.
   * @return an array containing the elements of this set.
   * @throws    ArrayStoreException the runtime type of a is not a supertype
   *            of the runtime type of every element in this set.
   * @throws NullPointerException if the specified array is <tt>null</tt>.
   */
  public Object[] toArray(Object a[]) {
    return new Object[0];
  }

  /**
   * Adds the specified element to this set if it is not already present
   * (optional operation).  More formally, adds the specified element,
   * <code>o</code>, to this set if this set contains no element
   * <code>e</code> such that <code>(o==null ? e==null :
   * o.equals(e))</code>.  If this set already contains the specified
   * element, the call leaves this set unchanged and returns <tt>false</tt>.
   * In combination with the restriction on constructors, this ensures that
   * sets never contain duplicate elements.<p>
   *
   * The stipulation above does not imply that sets must accept all
   * elements; sets may refuse to add any particular element, including
   * <tt>null</tt>, and throwing an exception, as described in the
   * specification for <tt>Collection.add</tt>.  Individual set
   * implementations should clearly document any restrictions on the the
   * elements that they may contain.
   *
   * @param o element to be added to this set.
   * @return <tt>true</tt> if this set did not already contain the specified
   *         element.
   *
   * @throws UnsupportedOperationException if the <tt>add</tt> method is not
   * 	       supported by this set.
   * @throws ClassCastException if the class of the specified element
   * 	       prevents it from being added to this set.
   * @throws NullPointerException if the specified element is null and this
   *         set does not support null elements.
   * @throws IllegalArgumentException if some aspect of the specified element
   *         prevents it from being added to this set.
   */
  public boolean add(Object o) {
    return false;
  }

  /**
   * Removes the specified element from this set if it is present (optional
   * operation).  More formally, removes an element <code>e</code> such that
   * <code>(o==null ?  e==null : o.equals(e))</code>, if the set contains
   * such an element.  Returns <tt>true</tt> if the set contained the
   * specified element (or equivalently, if the set changed as a result of
   * the call).  (The set will not contain the specified element once the
   * call returns.)
   *
   * @param o object to be removed from this set, if present.
   * @return true if the set contained the specified element.
   * @throws ClassCastException if the type of the specified element
   * 	       is incompatible with this set (optional).
   * @throws NullPointerException if the specified element is null and this
   *         set does not support null elements (optional).
   * @throws UnsupportedOperationException if the <tt>remove</tt> method is
   *         not supported by this set.
   */
  public boolean remove(Object o) {
    return false;
  }

  /**
   * Returns <tt>true</tt> if this set contains all of the elements of the
   * specified collection.  If the specified collection is also a set, this
   * method returns <tt>true</tt> if it is a <i>subset</i> of this set.
   *
   * @param  c collection to be checked for containment in this set.
   * @return <tt>true</tt> if this set contains all of the elements of the
   * 	       specified collection.
   * @throws ClassCastException if the types of one or more elements
   *         in the specified collection are incompatible with this
   *         set (optional).
   * @throws NullPointerException if the specified collection contains one
   *         or more null elements and this set does not support null
   *         elements (optional).
   * @throws NullPointerException if the specified collection is
   *         <tt>null</tt>.
   * @see    #contains(Object)
   */
  public boolean containsAll(Collection c) {
    return false;
  }

  /**
   * Adds all of the elements in the specified collection to this set if
   * they're not already present (optional operation).  If the specified
   * collection is also a set, the <tt>addAll</tt> operation effectively
   * modifies this set so that its value is the <i>union</i> of the two
   * sets.  The behavior of this operation is unspecified if the specified
   * collection is modified while the operation is in progress.
   *
   * @param c collection whose elements are to be added to this set.
   * @return <tt>true</tt> if this set changed as a result of the call.
   *
   * @throws UnsupportedOperationException if the <tt>addAll</tt> method is
   * 		  not supported by this set.
   * @throws ClassCastException if the class of some element of the
   * 		  specified collection prevents it from being added to this
   * 		  set.
   * @throws NullPointerException if the specified collection contains one
   *           or more null elements and this set does not support null
   *           elements, or if the specified collection is <tt>null</tt>.
   * @throws IllegalArgumentException if some aspect of some element of the
   *		  specified collection prevents it from being added to this
   *		  set.
   * @see #add(Object)
   */
  public boolean addAll(Collection c) {
    return false;
  }

  /**
   * Retains only the elements in this set that are contained in the
   * specified collection (optional operation).  In other words, removes
   * from this set all of its elements that are not contained in the
   * specified collection.  If the specified collection is also a set, this
   * operation effectively modifies this set so that its value is the
   * <i>intersection</i> of the two sets.
   *
   * @param c collection that defines which elements this set will retain.
   * @return <tt>true</tt> if this collection changed as a result of the
   *         call.
   * @throws UnsupportedOperationException if the <tt>retainAll</tt> method
   * 		  is not supported by this Collection.
   * @throws ClassCastException if the types of one or more elements in this
   *            set are incompatible with the specified collection
   *            (optional).
   * @throws NullPointerException if this set contains a null element and
   *            the specified collection does not support null elements
   *            (optional).
   * @throws NullPointerException if the specified collection is
   *           <tt>null</tt>.
   * @see #remove(Object)
   */
  public boolean retainAll(Collection c) {
    return false;
  }

  /**
   * Removes from this set all of its elements that are contained in the
   * specified collection (optional operation).  If the specified
   * collection is also a set, this operation effectively modifies this
   * set so that its value is the <i>asymmetric set difference</i> of
   * the two sets.
   *
   * @param  c collection that defines which elements will be removed from
   *           this set.
   * @return <tt>true</tt> if this set changed as a result of the call.
   *
   * @throws UnsupportedOperationException if the <tt>removeAll</tt>
   * 		  method is not supported by this Collection.
   * @throws ClassCastException if the types of one or more elements in this
   *            set are incompatible with the specified collection
   *            (optional).
   * @throws NullPointerException if this set contains a null element and
   *            the specified collection does not support null elements
   *            (optional).
   * @throws NullPointerException if the specified collection is
   *           <tt>null</tt>.
   * @see    #remove(Object)
   */
  public boolean removeAll(Collection c) {
    return false;
  }

  /**
   * Removes all of the elements from this set (optional operation).
   * This set will be empty after this call returns (unless it throws an
   * exception).
   *
   * @throws UnsupportedOperationException if the <tt>clear</tt> method
   * 		  is not supported by this set.
   */
  public void clear() {
  }

  public static final int PGM_ASCII = 0;
  public static final int RASTER_ASCII = 1;

  //protected Matrix matrix;
  protected NewMatrix matrix;
  protected int xSize;
  protected int ySize;
  protected AbstractDiscrete2DTopology VNneigh;
  protected AbstractDiscrete2DTopology Mneigh;
  protected ModifyableTopology occupy;
  protected HashMap relations;

  /**
   * Constructs a grid with the specified size.
   * @param xSize the size of the lattice in the x dimension.
   * @param ySize the size of the lattice in the y dimension.
   */
  public Object2DGrid(int xSize, int ySize) {
    this.xSize = xSize;
    this.ySize = ySize;
    if(Random.uniform == null){
      Random.createUniform();
    }
    matrix = new NewMatrix(xSize, ySize);
    VNneigh = new VonNeumannTopology(this);
    Mneigh = new MooreTopology(this);
    occupy = new OccupationTopology(this);
    relations = new LinkedHashMap();
    relations.put(VNneigh.getRelationType(), VNneigh);
    relations.put(Mneigh.getRelationType(), Mneigh);
    relations.put(occupy.getRelationType() , occupy);
    locationMap = new HashMap();
    Location.createLocation(this);
  }

  /**
   * Constructs a grid from an InputStream. Only ASCII PGM format files
   * as the ssource of the InputStream are supported at this
   * time. Code adapted from Nelson Minar's implementation of
   * SugarScape with Swarm.
   */
  public Object2DGrid(InputStream stream, int type) {
    if (type != PGM_ASCII) {
      throw new IllegalArgumentException("File type not supported.");
    }

    BufferedReader in = new BufferedReader(new InputStreamReader(stream));
    init(in);
  }

  /**
   * Constructs a grid from a file. Only ASCII PGM files are supported
   * at this time. Code adapted from Nelson Minar's implementation of
   * SugarScape with Swarm.
   */
  public Object2DGrid(String fileName, int type) {
    if (type != PGM_ASCII) {
      throw new IllegalArgumentException("File type not supported.");
    }

    //StringTokenizer tok;

    try {
      BufferedReader in = new BufferedReader(new FileReader(fileName));

      init(in);
    } catch (IOException ex) {
      SimUtilities.showError("Error Reading image file", ex);
      ex.printStackTrace();
      System.exit(0);
    }
  }

  private void init(BufferedReader in) {
    try {
      StringTokenizer tok;

      String str = in.readLine();

      if (!str.equals("P2")) {
        throw new UnsupportedEncodingException("File is not in PGM ascii format");
      }

      str = in.readLine();
      tok = new StringTokenizer(str);
      xSize = Integer.valueOf(tok.nextToken()).intValue();
      ySize = Integer.valueOf(tok.nextToken()).intValue();

      tok = null;
      in.readLine();

      str = "";
      String line = in.readLine();

      while (line != null) {
        str += line + " ";
        line = in.readLine();
      }
      in.close();

      tok = new StringTokenizer(str);
      matrix = new NewMatrix(xSize, ySize);
      //System.out.println(xSize + " " + ySize);

      for (int i = 0; i < xSize; i++) {
        for (int j = 0; j < ySize; j++) {
          matrix.put(i, j, Integer.valueOf(tok.nextToken()));
        }
      }
    } catch (IOException ex) {
      SimUtilities.showError("Error Reading image file", ex);
      ex.printStackTrace();
      System.exit(0);
    }
    VNneigh = new VonNeumannTopology(this);
    Mneigh = new MooreTopology(this);
  }

  /**
   * Gets the von Neumann neighbors of the object at x, y. Objects are returned
   * in west, east, north, south order. The object at x, y is not returned.
   *
   * @param x the x coordinate of the object
   * @param y the y coordinate of the object
   * @param returnNulls whether nulls (nothing at x,y) should be returned
   * @return a vector of objects (and possibly nulls) in west, east, north,
   * south order
   * @throws java.lang.IndexOutOfBoundsException if the given coordinates are out of
   * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
   */
  public Vector getVonNeumannNeighbors(int x, int y, boolean returnNulls) {
    return new Vector(VNneigh.getRelations(Location.getLocation(x,y), 1));
  }


  /**
   * Gets the extended von Neumann neighbors of the object at x, y. The
   * extension in the x and y direction are specified by xExtent and yExtent.
   * Objects are return in west, east, north, south order.The
   * most distant objects are returned first, that is, all the objects to the
   * west starting with the most distant, then those to the east and so on.
   * The Object at x,y is not returned.
   *
   * @param x the x coordinate of the object
   * @param y the y coordinate of the object
   * @param xExtent the extension of the neighborhood in the x direction
   * @param yExtent the extension of the neighborhood in the y direction
   * @param returnNulls whether nulls should be returned
   * @return a vector of objects (and possibly nulls) in west, east, north,
   * south order with the most distant object first.
   * @throws java.lang.IndexOutOfBoundsException if the given coordinates are out of
   * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
   */
  public Vector getVonNeumannNeighbors(int x, int y, int xExtent, int yExtent,
                                       boolean returnNulls) {
    int[] extents = {xExtent, yExtent};

    return new Vector(VNneigh.getRelations(x, y, extents, returnNulls));
  }

  /**
   * Gets the Moore neighbors of the object at x, y. Objects are returned by
   * row starting with the "NW corner" and ending with the "SE corner."
   * The Object at x, y is not returned.
   *
   * @param x the x coordinate of the object
   * @param y the y coordinate of the object
   * @param returnNulls should the returned Vector contain null objects
   * @return a vector of objects (and possibly nulls) ordered by row starting
   * with the "NW corner" and ending with the "SE corner."
   * @throws java.lang.IndexOutOfBoundsException if the given coordinates are out of
   * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
   */

  public Vector getMooreNeighbors(int x, int y, boolean returnNulls) {
    return new Vector(Mneigh.getRelations(Location.getLocation(x,y),1));
  }

  /**
   * Gets the extended Moore neighbors of the object at x, y. The
   * extension in the x and y direction are specified by xExtent and yExtent.
   * Objects are returned by row starting with the "NW corner" and ending with
   * the "SE corner." The Object at x,y is not returned.
   *
   * @param x the x coordinate of the object
   * @param y the y coordinate of the object
   * @param xExtent the extension of the neighborhood in the x direction
   * @param yExtent the extension of the neighborhood in the y direction
   * @param returnNulls should the returned Vector contain null objects
   * @return a vector of objects (and possibly nulls) ordered by row starting
   * with the "NW corner" and ending with the "SE corner."
   * @throws java.lang.IndexOutOfBoundsException if the given coordinates are out of
   * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
   */

  public Vector getMooreNeighbors(int x, int y, int xExtent, int yExtent,
                                  boolean returnNulls) {
    int[] extents = {xExtent, yExtent};

    return new Vector(Mneigh.getRelations(x, y, extents, returnNulls));
  }

  /**
   * Sets the comparator class used by the findMaximum and findMinimum methods.
   *
   * @param c the comparator to use for finding maximum and minimum.
   */
  public void setComparator(Comparator c) {
    VNneigh.setComparator(c);
    Mneigh.setComparator(c);
  }

  /**
   * Finds the maximum grid cell occupant within a specified range from
   * the specified origin coordinate. Maximum is determined by the default
   * or user supplied comparator class. The default comparator compares
   * objects using the >, <, and = operators on the hashcode of the objects.
   *
   * @param x the x origin coordinate
   * @param y the y origin coordinate
   * @param range the range out from the coordinate to search
   * @param includeOrigin include object at origin in search
   * @param neighborhoodType the type of neighborhood to search. Can be one
   * of Discrete2DSpace.VON_NEUMANN or Discrete2DSpace.MOORE.
   * @return the Objects determined to be the maximum.
   * @throws java.lang.IndexOutOfBoundsException if the given coordinates are out of
   * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
   */
  public Vector findMaximum(int x, int y, int range, boolean includeOrigin,
                            int neighborhoodType) {
    Vector v = new Vector();
    //int[] extent = {range, range};

    //TODO: figure out how to handle max and min.
/*      if (neighborhoodType == VON_NEUMANN)
v = VNneigh.findMaximum(x, y, extent, includeOrigin);
if (neighborhoodType == MOORE)
v = Mneigh.findMaximum(x, y, extent, includeOrigin);
*/      return v;
  }

  /**
   * Finds the minimum grid cell occupant within a specified range from
   * the specified origin coordinate. Minimum is determined by the default
   * or user supplied comparator class. The default comparator compares
   * objects using the >, <, and = operators on the hashcode of the objects.
   *
   * @param x the x origin coordinate
   * @param y the y origin coordinate
   * @param range the range out from the coordinate to search
   * @param includeOrigin include object at origin in search
   * @param neighborhoodType the type of neighborhood to search. Can be one
   * of Discrete2DSpace.VON_NEUMANN or Discrete2DSpace.MOORE.
   * @return the Objects determined to be the maximum.
   * @throws java.lang.IndexOutOfBoundsException if the given coordinates are out of
   * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
   */
  public Vector findMinimum(int x, int y, int range, boolean includeOrigin,
                            int neighborhoodType) {
    Vector v = new Vector();
    //int[] extent = {range, range};

    //TODO: figure out how to handle max and min
/*      if (neighborhoodType == VON_NEUMANN)
v = VNneigh.findMinimum(x, y, extent, includeOrigin);
else if (neighborhoodType == MOORE)
v = Mneigh.findMinimum(x, y, extent, includeOrigin);
*/      return v;
  }

  protected void rangeCheck(int x, int y) {
    if (x < 0 || x >= xSize || y < 0 || y >= ySize)
      throw new IndexOutOfBoundsException
            ("x or y coordinate is out of bounds");
  }

  // Discrete2dSpace interface
  /**
   * Gets the size of the x dimension.
   */
  public int getSizeX() {
    return xSize;
  }

  /**
   * Gets the size of the y dimension.
   */
  public int getSizeY() {
    return ySize;
  }

  /**
   * Gets the size as a Dimension.
   */
  public Dimension getSize() {
    return new Dimension(xSize, ySize);
  }

  /**
   * Gets the object at (x,y)
   * @param x the x coordinate
   * @param y the y coordinate
   *
   * @throws java.lang.IndexOutOfBoundsException if the given coordinates are out of
   * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
   */
  public Object getObjectAt(int x, int y) {
    rangeCheck(x, y);
    return matrix.get(x,y);
  }

  /**
   * Gets the double value at (x,y) if possible
   * @throws java.lang.IllegalArgumentException if object at x,y cannot
   * be converted to a number.
   * @throws java.lang.IndexOutOfBoundsException if the given coordinates are out of
   * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
   */
  public double getValueAt(int x, int y) {
    rangeCheck(x, y);
    Object o = matrix.get(x, y);
    if (o instanceof Number) {
      Number n = (Number) o;

      return n.doubleValue();
    } else {
      throw new IllegalArgumentException("Object cannot be converted to a long");
    }
  }

  /**
   * Puts the specified object at (x,y)
   * @param x the x coordinate
   * @param y the y coordinate
   * @param object the object to put at (x,y)
   * @throws java.lang.IndexOutOfBoundsException if the given coordinates are out of
   * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
   */
  public void putObjectAt(int x, int y, Object object) {
    rangeCheck(x, y);
    matrix.put(x, y, object);
    locationMap.put(object, Location.getLocation(x,y));
  }

  /**
   * Puts the specified double at (x,y)
   * @param x the x coordinate
   * @param y the y coordinate
   * @param value the double to put at (x,y)
   * @throws java.lang.IndexOutOfBoundsException if the given coordinates are out of
   * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
   */
  public void putValueAt(int x, int y, double value) {
    rangeCheck(x, y);
    putObjectAt(x, y, new Double(value));
  }

  /**
   * Returns the matrix collection object associated with this 2d grid
   */
  public BaseMatrix getMatrix() {
    return matrix;
  }

  public Location getLocation(Object o){
    Location loc = (Location) locationMap.get(o);
    return loc;
  }


  public Location getLocation(int x, int y){
    return Location.getLocation(x,y);
  }

  public String[] getRelationTypes() {
    return (String[]) relations.keySet().toArray(
          new String[relations.keySet().size()]);
  }

  public List getRelated(Object element, String relationType) {
      return ((RelationTopology) relations.get(relationType)).getRelations(element,1);
  }


  public void addRelation(Object element1, Object element2, String relationType) {
    if(!relationType.equalsIgnoreCase(OccupationTopology.type)){
      throw new IllegalArgumentException("You cannot create relations of type " + relationType);
    }
    ModifyableTopology top = (ModifyableTopology) relations.get(relationType);
    top.addRelation(element1, element2, 1);
  }

  public void removeRelation(Object element1, Object element2, String relationType) {
    if(!relationType.equalsIgnoreCase(OccupationTopology.type)){
      throw new IllegalArgumentException("You cannot create relations of type " + relationType);
    }
    ModifyableTopology top = (ModifyableTopology) relations.get(relationType);
    top.removeRelation(element1, element2);
  }

  public boolean addElement(Object o) {
    int x = 0;
    int y = 0;
    int i = 0;
    do{
      x = uchicago.src.sim.util.Random.uniform.nextIntFromTo(0,this.getSizeX() - 1);
      y = uchicago.src.sim.util.Random.uniform.nextIntFromTo(0,this.getSizeY() - 1);
      i++;
      if(i < 30){
        return false;
      }
    }while(getObjectAt(x,y) != null);
    putObjectAt(x,y,o);
    return true;
  }

  public void removeElement(Object o) {
    Location l = getLocation(o);
    putObjectAt(l.getX(), l.getY(), null);
  }

  /**
   * Returns the number of elements in this list.  If this list contains
   * more than <tt>Integer.MAX_VALUE</tt> elements, returns
   * <tt>Integer.MAX_VALUE</tt>.
   *
   * @return the number of elements in this list.
   */
  public int size() {
    return 0;
  }

  public Iterator iterator(){
    return locationMap.keySet().iterator();
  }

  public void update() {
    //This method doesn't do anything in this class
  }

  public void addRelationType(RelationTopology top) {
  }

  public boolean areRelated(Object element1, Object element2, String relationType) {
    RelationTopology top = (RelationTopology) relations.get(relationType);
    if(top.getRelations(element1,1).contains(element2)){
      return true;
    }
    return false;
  }
}
