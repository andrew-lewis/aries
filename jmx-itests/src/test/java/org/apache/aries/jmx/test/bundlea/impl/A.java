/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.aries.jmx.test.bundlea.impl;

import java.util.Dictionary;

import org.apache.aries.jmx.test.bundlea.api.InterfaceA;
import org.apache.aries.jmx.test.bundleb.api.InterfaceB;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 *
 * @version $Rev: 898952 $ $Date: 2010-01-13 21:53:07 +0000 (Wed, 13 Jan 2010) $
 */
@SuppressWarnings("unchecked")
public class A implements InterfaceA {

    private ServiceTracker tracker;
    private Dictionary props;
    
    public A(ServiceTracker tracker) {
        this.tracker = tracker;
    }
    
    /* (non-Javadoc)
     * @see org.apache.aries.jmx.test.bundlea.api.InterfaceA#invoke()
     */
    public boolean invoke() {
        
        if (tracker.getService() != null) {
            InterfaceB service = (InterfaceB) tracker.getService();
            return service.invoke();
        } else {
            return false;
        }
        
    }

    public void updated(Dictionary dictionary) throws ConfigurationException {
        this.props = dictionary;
    }
    
    // test cback
    public Dictionary getConfig() {
        return this.props;
    }

}