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

import java.beans.IntrospectionException;
import java.net.InetAddress;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.node.NodeException;

import uchicago.src.reflector.Introspector;
import uchicago.src.sim.parameter.DefaultParameterSetter;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.SimUtilities;

/**
 * The Controller that handles a single remote model.
 */


public class RemoteBatchController extends BatchController {
    protected long batchCount;
    // the central controller
    protected HomeController control;

    /**
     * No-arg constructor for making Active
     */
    public RemoteBatchController() {
        super(new DefaultParameterSetter());
        control = null;
    }

    /**
     * Create a RemoteController using a HomeController
     *
     * @param c
     */
    public RemoteBatchController(HomeController c) {
        super(new DefaultParameterSetter());
        control = c;
    }

    /**
     * Sets the model to be controlled by this BatchController.
     *
     * @param model the model to be controlled by this BatchController
     */
    public void setModel(SimModel model) {
        model.setController(this);
        super.setModel(model);
        //make sure the model is aware of this controller
        model.addSimEventListener(this);
        String[] paramArray = model.getInitParam();

        // pArray is used as the String[] to delimit introspection
        // see setModelParameters
        pArray = new String[paramArray.length + 1];

        System.arraycopy(paramArray, 0, pArray, 0, paramArray.length);
        pArray[paramArray.length] = "rngSeed";

        setModelParameters();
    }

    // sets the model to the specified parameters
    protected void setModelParameters() {

        model.generateNewSeed();
        Random.createUniform();
        //create the introspector and set the model, paramsList
        Introspector i = new Introspector();
        try {
            i.introspect(model, pArray);
        } catch (IntrospectionException ex) {
            String msg = "Fatal Error setting model parameters";
            System.out.println(msg);
            SimUtilities.logException(msg, ex);
            ex.printStackTrace();
            System.exit(0);
        }
        try {
            //pass the Active introspector to the home controller
            batchCount = control.nextModel((Introspector) ProActive.turnActive(i));
            if (batchCount == -1) {
                finished = true;
            }
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops an individual run of the simulation, increments the parameters appropriately, and if necessary starts
     * another run.
     */
    public void stopRun() {
        model.setup();
        runThread = null;
        setModelParameters();
        if (finished)
            return;

        runFinished = false;
        start();
    }

    public long getRunCount() {
        return batchCount;
    }

    public void start() {
        listenerList.clear();
        try {
            //Method from the super class designed to start the actual model
            startSim();
        } catch (RuntimeException ie) {
            ie.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Instantiate the model
     *
     * @param modelClass
     */
    public void setModelClass(Class modelClass) {
        try {
            setModel((SimModel) modelClass.newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * Instantiate the model
     *
     * @param modelClass
     */
    public void setModelClass(String modelClass) {
        try {
            setModelClass(Class.forName(modelClass));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Store a persistent object
     *
     * @param key
     * @param val
     */
    public void putPersistentObj(Object key, Object val) {
        //delegate to central controller
        control.putPersistentObj(key, val);
    }

    /**
     * retrieve a persistent object
     *
     * @param key
     * @return
     */
    public Object getPersistentObj(Object key) {
        //delegate to central controller
        Object out = null;
        try {
            out = ProActive.getFutureValue(control.getPersistentObj(key));
        } catch (RuntimeException e) {
            System.out.println("e.getMessage() = " + e.getMessage());
            System.out.println("Null Object returned from Persistent Object Store");
        }
        return out;
    }

    public boolean getPutPersistentObject(Object key, Object value) {
        return control.getPutPersistenceObject(key, value);
    }

    public Object beginRemoteRun() throws Exception {
        String hostname = InetAddress.getLocalHost().getHostName();
        System.out.println("Starting remote " + hostname);
        begin();
        System.out.println("Done remote " + hostname);
        return ProActive.turnActive(new Object());
    }

    public void exitSim() {
        fireSimEvent(new SimEvent(this, SimEvent.END_EVENT));
    }

    public void quit() {
        System.exit(0);
    }

}
