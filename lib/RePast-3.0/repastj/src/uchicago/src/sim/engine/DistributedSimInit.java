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
package uchicago.src.sim.engine;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.group.ProActiveGroup;
//import fabio.IcingModel;

/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Jan 6, 2003
 * Time: 5:08:52 PM
 * To change this template use Options | File Templates.
 */
public class DistributedSimInit {

	private VirtualNode createNodes(String descriptor) {
		ProActiveDescriptor pad = null;
		//activate the proper node mappings
		try {
			pad = ProActive.getProactiveDescriptor(descriptor);
			pad.activateMapping("remote");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pad.getVirtualNode("remote");
	}

	private RemoteBatchController createRemoteControllers(
		VirtualNode repast,
		HomeController controller) {
		RemoteBatchController controllers = null;
		try {
			//create a group for the remote batch controllers
			controllers =
				(RemoteBatchController) ProActiveGroup.newGroup(
					RemoteBatchController.class.getName());

			Group controlGroup = ProActiveGroup.getGroup(controllers);

			String[] repastNodes = repast.getNodesURL();
			RemoteBatchController tmp;
			//these are the parameters for building a remote controller, just the home controller
			Object[] params = { controller };
			for (int i = 0; i < repastNodes.length; i++) {
				//build the remote controllers
				tmp =
					(RemoteBatchController) ProActive.newActive(
						RemoteBatchController.class.getName(),
						params,
						repastNodes[i]);
				controlGroup.add(tmp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return controllers;
	}

	public void open(
		String descriptor,
		String modelClass,
		String paramFileName) {
		VirtualNode remote = createNodes(descriptor);
		try {
			//create the central controller
			HomeController controller =
				new HomeController(paramFileName, modelClass);
			HomeController activeController =
				(HomeController) ProActive.turnActive(controller);
			//create the remote controllers using the home controller
			RemoteBatchController controllers =
				createRemoteControllers(remote, activeController);
			//set the remote controllers in the home controller
			controller.setControllers(controllers);
			controller.begin();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	/*  public static void main(String[] args){
	    DistributedSimInit init = new DistributedSimInit();
	    init.open("repastCluster.xml", IcingModel.class.getName(), "./halfparams.txt");
	  }*/
}
