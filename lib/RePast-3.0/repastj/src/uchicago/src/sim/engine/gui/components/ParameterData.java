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
package uchicago.src.sim.engine.gui.components;

import uchicago.src.reflector.Introspector;
import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.engine.gui.model.ConstantParameter;
import uchicago.src.sim.engine.gui.model.DataParameter;
import uchicago.src.sim.engine.gui.model.IncrementParameter;
import uchicago.src.sim.engine.gui.model.ListParameter;
import uchicago.src.sim.parameter.DefaultParameterSetter;
import uchicago.src.sim.parameter.Parameter;
import uchicago.src.sim.util.SimUtilities;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;


/**
 * @author wes maciorowski
 *
 */
public class ParameterData {
	private ArrayList inputParameterList;
	private ArrayList outputParameterList;
	private ArrayList parameterList;
	private boolean changed;
	private String outputLocation;
	ArrayList rootNodes = null;
	private ArrayList configuredParameterList;

	public ParameterData() {
		parameterList = new ArrayList();
		inputParameterList = new ArrayList();
		outputParameterList = new ArrayList();

		configuredParameterList = new ArrayList();
		rootNodes = new ArrayList();
	}

	public ParameterData(String filename) {
		this();

		outputLocation = filename;

		DefaultParameterSetter aDefaultParameterSetter = new DefaultParameterSetter();

		try {
			aDefaultParameterSetter.init(filename);

			Iterator en = aDefaultParameterSetter.parameterNames();

			/////
			Hashtable aHashtable = new Hashtable();
			DataParameter aParameter = null;
			String tmpKey = null;
			Parameter p = null;

			DefaultMutableTreeNode aNode;
			DefaultMutableTreeNode parentNode = null;
			ArrayList nodeRelations = new ArrayList();
			Vector childrenNodes = null;
			String[] namePair = null;

			while (en.hasNext()) {
				tmpKey = (String) en.next();
				p = (Parameter) aDefaultParameterSetter.getParameter(tmpKey);

				if (p != null) {
					aParameter = new DataParameter(tmpKey,
												   p.getValue().getClass()
													.toString().replaceFirst("class ",
																			 ""),
												   p.isInput());
					getParameterList().add(aParameter);
				}

				aParameter = createParameterNode(p);
				aNode = new DefaultMutableTreeNode(aParameter);
				aHashtable.put(p.getName(), aNode);

				if (p.getParent() == null) {
					//must be a root node
					rootNodes.add(aNode);
				}

				childrenNodes = p.getChildren();

				if ((childrenNodes != null) && (childrenNodes.size() > 0)) {
					for (int i = 0; i < childrenNodes.size(); i++) {
						namePair = new String[2];
						namePair[0] = p.getName();
						namePair[1] = ((Parameter) childrenNodes.elementAt(i)).getName();
						nodeRelations.add(namePair);
					}
				}
			}

			//set up parent child relations
			for (int j = 0; j < nodeRelations.size(); j++) {
				namePair = (String[]) nodeRelations.get(j);
				parentNode = (DefaultMutableTreeNode) aHashtable.get(namePair[0]);
				aNode = (DefaultMutableTreeNode) aHashtable.get(namePair[1]);
				parentNode.insert(aNode, parentNode.getChildCount());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Initializes this ParameterData object with data from a specified model. 
	 * 
	 * @param model	the model to load the data from
	 */
	public ParameterData(SimModel model) {
		this();
		Hashtable modelPropsHash = null;

		try {
			modelPropsHash = getModelProperties(model);
		} catch (IntrospectionException e) {
			SimUtilities.showError("Unable to read model Parameters", e);
			return;	
		} catch (IllegalAccessException e) {
			SimUtilities.showError("Unable to read model Parameters", e);	
			return;
		} catch (InvocationTargetException e) {
			SimUtilities.showError("Unable to read model Parameters", e);		
			return;
		}

		DataParameter aParameter = null;
		Enumeration en = modelPropsHash.keys();
		String tmpKey = null;

		while (en.hasMoreElements()) {
			tmpKey = (String) en.nextElement();

			Object modelProperty = modelPropsHash.get(tmpKey);

			if (modelProperty != null) {
				aParameter = new DataParameter(tmpKey,
											   modelProperty.getClass()
											   .toString()
											   .replaceFirst("class ", ""),
											   true);
				this.getParameterList().add(aParameter);
			}
		}
	}
	
	
	/**
	 * Returns a Hashtable of all the current property name and value pairs for
	 * the specified model. The properties are those specified by the models
	 * getInitParam() method and the value is the current value of that property
	 * in the model.
	 *
	 * @param model the model whose properties we want to get.
	 * @return
	 * @throws java.beans.IntrospectionException
	 * @throws java.lang.IllegalAccessException
	 * @throws java.lang.reflect.InvocationTargetException
	 */
	private Hashtable getModelProperties(SimModel model)
								  throws IntrospectionException, 
										 IllegalAccessException, 
										 InvocationTargetException {
		Hashtable props = new Hashtable(23);

		model.clearPropertyListeners();

		String[] pNames = model.getInitParam();
		System.out.println(pNames.length);
		if(pNames == null || pNames.length == 0){
			throw new IntrospectionException("No properties available");
		}
		Introspector intro = new Introspector();

		intro.introspect(model, pNames);
		System.out.println(model == null);
		System.out.println(pNames == null);
		props = intro.getPropValues();

		return props;
	}

	/**
	 * @param changed The changed to set.
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	/**
	 * @return Returns the changed.
	 */
	public boolean isChanged() {
		return changed;
	}

	/**
	 * @param inputParameterList The inputParameterList to set.
	 */
	public void setInputParameterList(ArrayList inputParameterList) {
		this.inputParameterList = inputParameterList;
	}

	/**
	 * @return Returns the inputParameterList.
	 */
	public ArrayList getInputParameterList() {
		return inputParameterList;
	}

	/**
	 * @param outputParameterList The outputParameterList to set.
	 */
	public void setOutputParameterList(ArrayList outputParameterList) {
		this.outputParameterList = outputParameterList;
	}

	/**
	 * @return Returns the outputParameterList.
	 */
	public ArrayList getOutputParameterList() {
		return outputParameterList;
	}

	/**
	 * @param parameterList The parameterList to set.
	 */
	public void setParameterList(ArrayList parameterList) {
		this.parameterList = parameterList;
	}

	/**
	 * @return Returns the parameterList.
	 */
	public ArrayList getParameterList() {
		return parameterList;
	}

	public void reloadInputOutput() {
		inputParameterList.clear();
		outputParameterList.clear();

		DataParameter aParameter = null;

		for (int i = 0; i < parameterList.size(); i++) {
			aParameter = (DataParameter) parameterList.get(i);

			if (aParameter.isInput()) {
				inputParameterList.add(aParameter);
			} else {
				outputParameterList.add(aParameter);
			}
		}
	}

	/**
	 * @return Returns the configuredParameterList.
	 */
	public ArrayList getConfiguredParameterList() {
		return configuredParameterList;
	}

	/**
	 * @param configuredParameterList The configuredParameterList to set.
	 */
	public void setConfiguredParameterList(ArrayList configuredParameterList) {
		this.configuredParameterList = configuredParameterList;
	}

	/**
	 * @return Returns the outputLocation.
	 */
	public String getOutputLocation() {
		return outputLocation;
	}

	/**
	 * @param outputLocation The outputLocation to set.
	 */
	public void setOutputLocation(String outputLocation) {
		this.outputLocation = outputLocation;
	}

	/**
	 * @return
	 */
	private DataParameter createParameterNode(Parameter p) {
		String name = null;
		String dataType = null;
		boolean input = true;

		if (p != null) {
			name = p.getName();
			dataType = p.getValue().getClass().toString().replaceFirst("class ",
																	   "");
		}

		int runs = (int) p.getNumRuns();

		if (p.isIncrement()) {
			Object start = p.getStart();
			Object end = p.getEnd();
			Object increment = p.getIncr();

			IncrementParameter aValueParameter = null;
			aValueParameter = new IncrementParameter(runs, name, dataType,
													 input, start, end,
													 increment);

			return aValueParameter;
		}

		if (p.isConstant()) {
			Object value = p.getValue();

			ConstantParameter aConstParameter = null;
			aConstParameter = new ConstantParameter(runs, name, dataType,
													input, value);

			return aConstParameter;
		}

		if (p.isList()) {
			Object[] value = p.getList().toArray();

			ListParameter aListParameter = null;
			aListParameter = new ListParameter(runs, name, dataType, input,
											   value);

			return aListParameter;
		}

		return null;
	}

	/**
	 * @return Returns the rootNodes.
	 */
	public ArrayList getRootNodes() {
		return rootNodes;
	}
}
