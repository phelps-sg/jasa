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
package uchicago.src.repastdemos.regression;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.ImageIcon;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Forecaster;
import net.sourceforge.openforecast.ForecastingModel;
import net.sourceforge.openforecast.Observation;
import uchicago.src.sim.engine.AutoStepable;
import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.network.DefaultDrawableNode;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.RepastException;


/**
 * This class represents the agents who predict the output of a boss who uses either 
 * exponential or polynomial function. The agents are not aware of the function used by the boss but
 * know the input to these functions. The agents predict the values using adaptive regression based on history
 * of input and output values. The consultant sets the forecast error of the agent.
 * 
 * @author Prakash Thimmapuram
 * @version $Revision$ $Date$
 */
public class Employee extends DefaultDrawableNode implements AutoStepable {
	private DataSet outputDataset;
	private double error;
	
	private static Image employeePicture;
	
	private static int baseIdNumber;
	
	private ForecastingModel model = null;

	private double forecastValue = 0.0;

	
	public Employee(double x, double y) throws RepastException {
		super(new OvalNetworkItem(x, y));

		// to get semi-unique colors on the graphs
		this.setColor(getNextColor());
		
		loadEmployeePicture();
		this.setHeight(5);
		this.setWidth(2);
		this.setNodeLabel("Milton " + ++baseIdNumber);
	}

	public Employee() throws RepastException {
		this(0, 0);
	}

	
	public static void resetIndices() {
		baseIdNumber = 0;
	}
	
	private static void loadEmployeePicture() {
		if (employeePicture == null) {
			java.net.URL employeePicURL = Employee.class.getResource("person.gif");
			employeePicture = new ImageIcon(employeePicURL).getImage(); 
		}
	}
	
	/**
	 * This method retrieves from the forecatsed values based
	 * on the input from boss.
	 * 
	 * @return the value the forecasted value
	 * 
	 */
	private double retrieve() {
		// query the model
		double y = 0;
		
		if(this.outputDataset != null){
			Iterator it = this.outputDataset.iterator();
		     while ( it.hasNext() ) {
		        DataPoint dp = (DataPoint)it.next();
		        y = dp.getDependentValue() ;
		        break;
		     }
		}
		else
			y = Random.uniform.nextDoubleFromTo(0, 1);
				
	    return y;
	     
	}
	
	public void preStep() {

	}

	public void step() {
		this.forecastValue = retrieve();
	}


	public void postStep() {
	}

	
	public void forecast(double x, DataSet history) {
		if(history != null && history.size() > 1){
			model = Forecaster.getBestForecast(history);
			model.init(history);
			
			this.outputDataset = new DataSet();
			DataPoint dataPoint = new Observation(0.0);
			dataPoint.setIndependentValue("X",x);
			outputDataset.add(dataPoint);
			
			model.forecast(outputDataset);
		}
	}

	public void setError(double error){
		this.error = error;
	}
	
	public double getError() {
		return this.error;
	}

	/**
	 * @return Returns the forecastValue.
	 */
	public double getForecastValue() {
		return forecastValue;
	}

	public void draw(SimGraphics g) {
		// draw the employee's picture
		g.drawImage(employeePicture);
		
		// grab the width of the picture
		int width = employeePicture.getWidth(null);
		
		g.setFont(super.getFont());
		
		// get the size of the node's text
		Rectangle2D bounds = g.getStringBounds(this.getNodeLabel());
		
		// set the graphics to draw the text above the label
		// the x coordinate is relative to the upper left corner of the image
		// so the coordinates are shifted to account for that
		g.setDrawingCoordinates((float) (this.getX() + width / 2.0 - bounds.getWidth() / 2.0),
								(float) (this.getY() - bounds.getHeight() - 2),
								0f);
		
		// draw the label
		g.drawString(getNodeLabel(), Color.BLACK);
	}

	private static Color getNextColor() {
		if (colorIndex == colors.length)
			colorIndex = 0;
		
		return colors[colorIndex++];
	}

	private static int colorIndex = 0;
	private static final Color[] colors = new Color[] {
			Color.PINK,
			Color.BLUE,
			Color.GRAY,
			Color.GREEN,
			Color.MAGENTA,
			Color.YELLOW,
			Color.WHITE,
			Color.CYAN
	};
}