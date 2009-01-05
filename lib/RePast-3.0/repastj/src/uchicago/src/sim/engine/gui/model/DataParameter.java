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

import java.util.ArrayList;


/**
 * @author wes maciorowski
 */
public class DataParameter {
    ArrayList nestedParameters;
    String dataType;
    String name;
    boolean input;
    int runs;

    protected String lineSeparator;
    
    /**
     * @param name
     * @param dataType
     * @param input
     */
    public DataParameter(String name, String dataType, boolean input) {
    	this();
        this.runs = 0;
        this.name = name;
        this.dataType = dataType;
        this.input = input;
        nestedParameters = new ArrayList();
    }

    /**
     * @param runs
     * @param name
     * @param dataType
     * @param input
     */
    public DataParameter(int runs, String name, String dataType, boolean input) {
        this();
        this.runs = runs;
        this.name = name;
        this.dataType = dataType;
        this.input = input;

        nestedParameters = new ArrayList();
    }

    public DataParameter() {
    	this.lineSeparator = System.getProperty("line.separator");
    	if (this.lineSeparator == null || this.lineSeparator.equals(""))
    		this.lineSeparator = "/";
    }
    
    /**
     * @param dataType The dataType to set.
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return Returns the dataType.
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param input The input to set.
     */
    public void setInput(boolean input) {
        this.input = input;
    }

    /**
     * @return Returns the input.
     */
    public boolean isInput() {
        return input;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the nestedParameters.
     */
    public ArrayList getNestedParameters() {
        return nestedParameters;
    }

    /**
     * @param runs The runs to set.
     */
    public void setRuns(int runs) {
        this.runs = runs;
    }

    /**
     * @return Returns the runs.
     */
    public int getRuns() {
        return runs;
    }

    public String toString() {
        return name;
    }
    /**
     * This method writes out this parameter in XML format.  
     * It is really only intended to write output parameters.
 	 * 
 	 * @return
 	 */
     public String toXMLString() {
     	StringBuffer buf = new StringBuffer("<Repast:Param name=");
     	buf.append('"').append(name).append('"');
     	
     	// do input/output
     	buf.append(" io=").append('"');
     	if(input) {
     		buf.append("input");
     	} else {
     		buf.append("output");
     	}
     	buf.append('"');

     	// end tag
     	buf.append(" />").append(lineSeparator);

     	return buf.toString();
     }

}
