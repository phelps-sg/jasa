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
package uchicago.src.sim.parameter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

import uchicago.src.sim.engine.gui.model.ConstantParameter;
import uchicago.src.sim.engine.gui.model.DataParameter;
import uchicago.src.sim.engine.gui.model.IncrementParameter;
import uchicago.src.sim.engine.gui.model.ListParameter;

/**
 * Creates an XML based parameter file.
 * 
 * @author wes maciorowski
 */
public class XMLParameterFileWriter {
    private static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";

    private PrintStream out;

	/**
	 * Creates a parameter file using outputLocation and writes to it all input parameters
	 * stored in treeTop and all output parameters stored in .
	 * 
	 * @param outputLocation
	 * @param treeTop
	 */
	public void write(String outputLocation, DefaultMutableTreeNode treeTop, ArrayList outputParmList) {
	    FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(new File(outputLocation));
            out = new PrintStream(outStream, false, DEFAULT_CHARACTER_ENCODING);
            // XML standard header
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<Repast:Params xmlns:Repast=\"http://www.src.uchicago.edu\" name=\"myparams\">");
            writeParameterBlock(treeTop, out, 4);
            writeOutputParameters(outputParmList,out);
            out.println("</Repast:Params>");
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}

	/**
	 * @param outputParmList
	 * @param out2
	 */
	private void writeOutputParameters(ArrayList outputParmList, PrintStream out2) {
		DataParameter aParameter = null;
		for(int i = 0 ; i < outputParmList.size() ; i++) {
			aParameter = (DataParameter) outputParmList.get( i ) ; 
	        out.println(aParameter.toXMLString());
		}
	}

	/**
	 * Writes out all children of someNode to printStream out2.
	 * 
	 * @param someNode
	 * @param out2
	 * @param indent - the size of indent lines will be adjusted by
	 */	
	private void writeParameterBlock(DefaultMutableTreeNode someNode, PrintStream out2, int indent) {
		Enumeration enumer = someNode.children();
		DefaultMutableTreeNode currNode;
		IncrementParameter anIncrementParameter = null;
		ListParameter aListParameter= null;
		ConstantParameter aConstantParameter= null;
		Object tmpObject = null;
		int lastRuns = 0;
		int currRuns = 0;
		boolean isFirstHeader = true;
		boolean wroteHeader = false;
		String indentString = createIndentString(indent);
		
		while(enumer.hasMoreElements()) {
			currNode = (DefaultMutableTreeNode) enumer.nextElement();
			tmpObject = currNode.getUserObject();
			if(tmpObject != null && (tmpObject instanceof IncrementParameter || 
					tmpObject instanceof ListParameter ||
					tmpObject instanceof ConstantParameter)) {
				
				if(tmpObject instanceof IncrementParameter) {
					anIncrementParameter = (IncrementParameter) tmpObject;
					aListParameter= null;
					aConstantParameter= null;
					currRuns= anIncrementParameter.getRuns();
				}
				
				if(tmpObject instanceof ListParameter) {
					anIncrementParameter = null;
					aListParameter= (ListParameter)tmpObject;
					aConstantParameter= null;
					currRuns= aListParameter.getRuns();
				}
				
				if(tmpObject instanceof ConstantParameter) {
					anIncrementParameter = null;
					aListParameter= null;
					aConstantParameter= (ConstantParameter)tmpObject;
					currRuns= aConstantParameter.getRuns();
				}
				
				if(currRuns != lastRuns) {
					if(isFirstHeader) {
						isFirstHeader = false;
					} else {
						out.print(indentString);
				        out.println("</Repast:ParamBlock>");
					}
					out.print(indentString);
			        out.println("<Repast:ParamBlock runs=\"" +Integer.toString(currRuns)+ "\">");
			        if(!wroteHeader) wroteHeader = true;
			        lastRuns = currRuns;
				}
			
				if(anIncrementParameter != null) {
					out.print(indentString);
			        out.println(anIncrementParameter.toXMLString());
				} else if(aListParameter != null) {
					out.print(indentString);
			        out.println(aListParameter.toXMLString());
				} else if(aConstantParameter != null) {
					out.print(indentString);
			        out.println(aConstantParameter.toXMLString());
				}
				writeParameterBlock(currNode, out, indent+4);
				out.print(indentString);
			}
		}
        
		if(wroteHeader) {
			out.print(indentString);
	        out.println("</Repast:ParamBlock>");
		}
	}

	/**
	 * Create a blank string of specified length.
	 * @param indent
	 * @return
	 */
	private String createIndentString(int indent) {
		StringBuffer buf = new StringBuffer();
		// TODO: jrv, the indenting is currently broken, so it is being disabled here
//		for(int i= 0 ; i < indent ; i++) {
//			buf.append(' ');
//		}
		return buf.toString();
	}
}
