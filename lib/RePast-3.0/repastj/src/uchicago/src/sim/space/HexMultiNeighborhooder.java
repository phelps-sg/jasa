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
package uchicago.src.sim.space;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class HexMultiNeighborhooder extends AbstractNeighborhooder {

    private IMulti2DGrid grid;
    private int sizeX, sizeY;
    private LocAdder lAdder = new LocAdder();
    private ObjectAdder oAdder = new ObjectAdder();
    private Adder adder;
    private boolean returnNull;
	
    public HexMultiNeighborhooder(IMulti2DGrid space) {
        super(space);
        this.grid = space;
        torus = space instanceof Torus;
        sizeX = space.getSizeX();
        sizeY = space.getSizeY();
    }

    /**
     * Normalize the x value to the toroidal coordinates
     *
     * @param x the value to normalize
     * @return the normalized value
     */
    public int xnorm(int x) {
        if (x > sizeX - 1 || x < 0) {
            while (x < 0) x += sizeX;
            return x % sizeX;
        }
 
        return x;
    }
 
    /**
     * Normalize the y value to the toroidal coordinates
     *
     * @param y the value to normalize
     * @return the normalized value
     */
    public int ynorm(int y) {
        if (y > sizeY - 1 || y < 0) {
            while (y < 0) y += sizeY;
            return y % sizeY;
        }
 
        return y;
    }         

    public Vector getNeighbors(int x, int y, int[] extents, boolean returnNull) {
        return new Vector(getNeighborsList(x, y, extents, returnNull));
    }

    public ArrayList getNeighborsList(int x, int y, int[] extents,
        boolean returnNull) {
        adder = oAdder;
        return getNeighs(x, y, extents, returnNull);
    }

    public ArrayList getNeighborsLoc(int x, int y, int[] extents,
        boolean returnNull) {
        adder = lAdder;
        return getNeighs(x, y, extents, returnNull);
    }

    protected ArrayList getNeighs(int  x, int y, int[] extents,
        boolean returnNull) {
        this.returnNull = returnNull;
        if (extents.length != 1)
            throw new IllegalArgumentException("Hexagonal Neighborhoods take one argument");
        int radius = extents[0];

        if (radius < 1) return new ArrayList();
    
        if (radius == 1) return singleExtent(x, y);
        if (radius == 2) return doubleExtent(x, y);
        else return gtTwoExtent(x, y, radius);
    }

    private ArrayList gtTwoExtent(int x, int y, int extent) {
        ArrayList v = new ArrayList(3 * extent * (extent + 1));
        //int destIndex = 0;

        if (x % 2 == 0) {
            for (int radius = extent; radius > 2; radius--) {
                ArrayList src = getEvenRing(x, y, radius);

                v.addAll(src);
            }
        } else {
            for (int radius = extent; radius > 2; radius--) {
                ArrayList src = getOddRing(x, y, radius);

                v.addAll(src);
            }
        }

        ArrayList src = doubleExtent(x, y);

        v.addAll(src);
    
        return v;
    }

    private ArrayList getEvenRing(int x, int y, int radius) {
        ArrayList v = new ArrayList(radius * 6);
        int yVal = y - radius;
    
        addXY(v, x, yVal++);
    
        //int aIndex = 1;
        int limit = x + radius;
        int xVal = x + 1;

        while (xVal <= limit) {
            addXY(v, xVal++, yVal);
            if (xVal > limit) {
                yVal++;
                break;
            }
            addXY(v, xVal++, yVal);
            yVal++;
        }

        xVal = x + radius;
        for (int i = 0; i < radius; i++)
            addXY(v, xVal, yVal++);

        if (xVal % 2 != 0) {
            xVal--;
            yVal--;
            addXY(v, xVal, yVal++);
        }

        xVal--;

        while (xVal > x) {
            addXY(v, xVal--, yVal);
            if (xVal == x) break;
            addXY(v, xVal--, yVal);
            yVal++;
        }

        yVal = y + radius;
        addXY(v, x, yVal);
        addXY(v, x - 1, yVal);

        yVal--;
        xVal = x - 2;
        limit = x - radius;
        while (xVal >= limit) {
            addXY(v, xVal--, yVal);
            if (xVal < limit) {
                yVal--;
                break;
            }
            addXY(v, xVal--, yVal);
            yVal--;
        }

        xVal = x - radius;
        for (int i = 0; i < radius; i++) 
            addXY(v, xVal, yVal--);

        xVal++;
        if (xVal % 2 != 0) {
            yVal++;
            addXY(v, xVal++, yVal--);
        }
    
        while (xVal < x) {
            addXY(v, xVal++, yVal);
            if (xVal == x) break;
            addXY(v, xVal++, yVal);
            yVal--;
        }

        //printArray(rarray);
        return v;
    }

    private ArrayList getOddRing(int x, int y, int radius) {
    
        ArrayList v = new ArrayList(radius * 6);

        addXY(v, x, y - radius);
        addXY(v, x + 1, y - radius);
    
        //int aIndex = 2;
        int xVal = x + 2;
        int yVal = (y - radius) + 1;
        int limit = x + radius;
      
        while (xVal <= limit) {
            addXY(v, xVal++, yVal);
            if (xVal > limit) {
                yVal++;
                break;
            }
            addXY(v, xVal++, yVal);
            yVal++;
        }
    
        xVal = x + radius;
        for (int i = 0; i < radius; i++)
            addXY(v, xVal, yVal++);

        if (xVal % 2 != 0) {
            yVal--;
            xVal--;
            addXY(v, xVal, yVal++);
        }
      
        xVal--;
        while (xVal > x) {
            addXY(v, xVal--, yVal);
            addXY(v, xVal--, yVal);
            yVal++;
        }

        addXY(v, x, y + radius);
        yVal = y + radius - 1;
        xVal = x - 1;

        limit = x - radius;
        while (xVal >= limit) {
            addXY(v, xVal--, yVal);
            if (xVal < limit) {
                yVal--;
                break;
            }
            addXY(v, xVal--, yVal);
            yVal--;
        }
    
        xVal = x - radius;
        for (int i = 0; i < radius; i++)
            addXY(v, xVal, yVal--);

        xVal++;
        if (xVal % 2 != 0) {
            yVal++;
            addXY(v, xVal++, yVal);
            yVal--;
        }
    
        while (xVal < x) {
            addXY(v, xVal++, yVal);
            if (xVal == x) break;
            addXY(v, xVal++, yVal);
            yVal--;
        }

        return v;
    }
  
    private ArrayList doubleExtent(int x, int y) {
        ArrayList v = new ArrayList(18);
    
        if (x % 2 == 0) {
            addXY(v, x, y - 2);
            addXY(v, x + 1, y - 1);
            addXY(v, x + 2, y - 1);
            addXY(v, x + 2, y);
            addXY(v, x + 2, y + 1);
            addXY(v, x + 1, y + 2);
            addXY(v, x, y + 2);
            addXY(v, x - 1, y + 2);
            addXY(v, x - 2, y + 1);
            addXY(v, x - 2, y);
            addXY(v, x - 2, y - 1);
            addXY(v, x - 1, y - 1);
      
        } else {
            addXY(v, x, y - 2);
            addXY(v, x + 1, y - 2);
            addXY(v, x + 2, y - 1);
            addXY(v, x + 2, y);
            addXY(v, x + 2, y + 1);
            addXY(v, x + 1, y + 1);
            addXY(v, x, y + 2);
            addXY(v, x - 1, y + 1);
            addXY(v, x - 2, y + 1);
            addXY(v, x - 2, y);
            addXY(v, x - 2, y - 1);
            addXY(v, x - 1, y - 2);
        }
        v.addAll(singleExtent(x, y));
        return v;
    }

    private ArrayList singleExtent(int x, int y) {
        ArrayList v = new ArrayList(6);
    
        if (x % 2 == 0) {
            addXY(v, x, y - 1);
            addXY(v, x + 1, y);
            addXY(v, x + 1, y + 1);
            addXY(v, x, y + 1);
            addXY(v, x - 1, y + 1);
            addXY(v, x - 1, y);
        } else {
            int top = y - 1;

            addXY(v, x, top);
            addXY(v, x + 1, top);
            addXY(v, x + 1, y);
            addXY(v, x, y + 1);
            addXY(v, x - 1, y);
            addXY(v, x - 1, top);
        }

        return v;
    }

    protected void addXY(ArrayList v, int x, int y) {
        adder.add(v, x, y);
    }

    interface Adder {
        public void add(ArrayList v, int x, int y);
    }


    public class ObjectAdder implements Adder {
    
        public void add(ArrayList v, int x, int y) {
            List l = null;

            if (!torus) {
                if (x >= 0 & x < sizeX & y >= 0 & y < sizeY)
                    l = grid.getObjectsAt(x, y);
                else return;
            } else l = grid.getObjectsAt(x, y);

            int lsize = l.size();

            if (lsize == 0 && returnNull) v.add(null);
            else if (lsize > 0) v.addAll(l);
        }
    }


    public class LocAdder implements Adder {
    
        public void add(ArrayList v, int x, int y) {
            List l = null;

            if (!torus) {
                if (x >= 0 & x < sizeX & y >= 0 & y < sizeY) {
                    l = grid.getObjectsAt(x, y);
                    int lsize = l.size();

                    if (lsize == 0 && returnNull) v.add(new ObjectLocation(null, x, y));
                    else if (lsize > 0)
                        v.addAll(ObjectLocation.makeObjectLocations(l, x, y));
                } else return;
            } else {
                l = grid.getObjectsAt(x, y);
        
                int lsize = l.size();

                if (lsize == 0 && returnNull)
                    v.add(new ObjectLocation(null, xnorm(x), xnorm(y)));
                else if (lsize > 0)
                    v.addAll(ObjectLocation.makeObjectLocations(l, xnorm(x),
                            ynorm(y)));
            }
        }
    }      
}
