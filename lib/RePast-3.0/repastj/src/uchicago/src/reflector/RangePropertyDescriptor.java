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
package uchicago.src.reflector;

/**
 * A property descriptor for limiting an integer value to a specified
 * range. A RangePropertyDescriptor is a descriptor for a parameter /
 * property whose value can be expressed as falling within a range of
 * integer values. It is useful when you want to restrict user input
 * to some value within a range of integers. The range is represented
 * in the gui by a slider with minimum and maximum values and a text
 * box into which you can type some specific value. If the value you
 * enter is greater than the maximum allowable value or less than the
 * minimum value the actual value will be set to the maximum or
 * minimum respectively. Similarly, if you enter a non-numerical value
 * into the text box the value will default to the most recent valid
 * value. Selecting an integer from within the range either via the
 * slider or the text box calls the appropriate set method in your
 * model.<p/>
 * 
 * This class's widget is a RangeWidget.
 *
 * @version $Revision$ $Date$
 */

public class RangePropertyDescriptor extends PropertyDescriptor {
  /**
   * Constructs this RangePropertyDescriptor for the named property
   * with the specified range and tick spacing. The tick spacing
   * parameter determines the display of intermediate values in the
   * slider. For example, a range of 10 - 100 and a tick spacing of 10
   * will display ticks on the slider for 20, 30, 40 and so on up to
   * 90. Note that this only determines the display, any integer value
   * between 10 - 100, in this case, can be selected.
   *
   * @param name the of the property
   * @param min the minimum valueq
   * @param max the maximum value
   * @param tickSpacing an int value that determines how often the
   * ticks are labeled in the slider
   */
  public RangePropertyDescriptor(String name, int min, int max,
				 int tickSpacing)
  {
    super(name);
    min = Math.min(min, max);
    max = Math.max(min, max);
    
    super.widget = new RangeWidget(min, max, tickSpacing);
  }

}
