/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package uk.ac.liv.ec.gp;

import uk.ac.liv.ec.gp.func.GPGenericDataPool;
import uk.ac.liv.ec.gp.func.GPGenericData;

import uk.ac.liv.util.Pooled;


/**
 * @author Steve Phelps
 */

public class GPIndividualWithMemory extends GPIndividualCtx  {

  int memorySize;

  GPGenericData[] memory;

  public GPIndividualWithMemory( int memorySize ) {
    this.memorySize = memorySize;
  }

  public void set( long address, GPGenericData newData ) {
    int mapped = mapAddress(address);
    GPGenericData existing = memory[mapped];
    if ( existing != null ) {
      if ( existing.data instanceof Pooled ) {
        ((Pooled) existing.data).release();
      }
      GPGenericDataPool.release(existing);
    }
    memory[mapped] = newData.safeCopy();
  }

  public GPGenericData get( long address ) {
    GPGenericData result = memory[mapAddress(address)];
    if ( result != null ) {
      return result.safeCopy();
    } else {
      return null;
    }
  }

  public int mapAddress( long address ) {
    return Math.abs((int) address % memorySize);
  }

  public void prepareForEvaluating() {
    super.prepareForEvaluating();
    memory = new GPGenericData[memorySize];
  }

  public void doneEvaluating() {
    super.doneEvaluating();
    memory = null;
  }

}