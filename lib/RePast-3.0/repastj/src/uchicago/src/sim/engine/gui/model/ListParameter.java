/*$$
 * Copyright (c) 2004, Repast Organization for Architecture and Design (ROAD)
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
 * Neither the name of the ROAD nor the names of its
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
package uchicago.src.sim.engine.gui.model;


/**
 * @author wes maciorowski
 */
public class ListParameter extends DataParameter {
    Object[] value;

    /**
     * @param runs
     * @param name
     * @param dataType
     * @param input
     */
    public ListParameter(int runs, String name, String dataType, boolean input,
    		Object[] value) {
        super(runs, name, dataType, input);
        this.value = value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(Object[] value) {
        this.value = value;
    }

    /**
     * @return Returns the value.
     */
    public Object[] getValue() {
        return value;
    }
    

    public String toString() {
    	StringBuffer buf = new StringBuffer(name);
    	buf.append('(').append(dataType).append(',');
    	buf.append(input).append(',').append(runs).append(')');
    	buf.append("[list=");
    	for(int i = 0; i < value.length ; i++) {
    		if(i == value.length -1) {
    	    	buf.append(value[i]);
    		} else {
    	    	buf.append(value[i]).append(",");
    		}
    	}
    	buf.append(']');

    	return buf.toString();
    }

    /**
     * This method writes out this parameter in XML format.  
     * 
    <Repast:ParamBlock runs="1">
      <Repast:Param name="pConst" type="const" value="3"/>
      <Repast:Param name="pStringList" type="string_list" value ="sam bill  joe george  mary jean">
        <Repast:ParamBlock runs="2"> <!-- only need this runs if not same  as parent -->
          <Repast:Param name="pIncr2" type="incr" start=".1" end=".2"
  incr=".1"/>
        </Repast:ParamBlock>
      </Repast:Param>
    </Repast:ParamBlock>
  </Repast:Params>
 	 * 
 	 * @return
 	 */
     public String toXMLString() {
     	StringBuffer buf = new StringBuffer("<Repast:Param name=");
     	buf.append('"').append(name).append('"');
     	
     	// do type boolean_list,string_list,list
     	buf.append(" type=").append('"');

    	if(dataType.equalsIgnoreCase("java.lang.Integer") ||
    		dataType.equalsIgnoreCase("java.lang.Long") ||
    		dataType.equalsIgnoreCase("java.lang.Float") ||
    		dataType.equalsIgnoreCase("java.lang.Byte") ||
    		dataType.equalsIgnoreCase("java.lang.Double")){
         	buf.append("list");
    	} else if(dataType.equalsIgnoreCase("java.lang.Boolean")){
         	buf.append("boolean_list");
    	} else if(dataType.equalsIgnoreCase("java.lang.String")){
         	buf.append("string_list");
    	}

     	buf.append('"');
     	
     	// do value
     	buf.append(" value=").append('"');
    	for(int i = 0; i < value.length ; i++) {
    		if(i == value.length -1) {
    	    	buf.append(value[i]);
    		} else {
    	    	buf.append(value[i]).append(" ");
    		}
    	}
		buf.append('"');

     	// end tag
     	buf.append(" />").append(lineSeparator);

     	return buf.toString();
     }
}
