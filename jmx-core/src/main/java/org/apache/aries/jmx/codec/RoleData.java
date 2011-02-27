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
package org.apache.aries.jmx.codec;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;

import org.osgi.jmx.JmxConstants;
import org.osgi.jmx.service.useradmin.UserAdminMBean;
import org.osgi.service.useradmin.Role;


/**
 * <p>
 * <tt>RoleData</tt> represents Role Type @see {@link UserAdminMBean#ROLE_TYPE}.It is a codec
 * for the <code>CompositeData</code> representing a Role.
 * </p>
 *
 * @version $Rev: 920467 $ $Date: 2010-03-08 19:22:22 +0000 (Mon, 08 Mar 2010) $
 */
public class RoleData {
    
    /**
     * role name.
     */
    protected String name;
    /**
     * role type.
     */
    protected int type;
    
    /**
     * Constructs new RoleData from Role object.
     * @param role {@link Role} instance.
     */
    public RoleData(Role role){
        this(role.getName(),role.getType());
    }
    
    /**
     * Constructs new RoleData.
     * @param name role name.
     * @param type role type.
     */
    public RoleData(String name, int type){
        this.name = name;
        this.type = type;
    }
    
    /**
     * Translates RoleData to CompositeData represented by
     * compositeType {@link UserAdminMBean#ROLE_TYPE}.
     * 
     * @return translated RoleData to compositeData.
     */
    public CompositeData toCompositeData() {
        try {
            Map<String, Object> items = new HashMap<String, Object>();
            items.put(UserAdminMBean.NAME, name);
            items.put(UserAdminMBean.TYPE, type);
            return new CompositeDataSupport(UserAdminMBean.ROLE_TYPE, items);
        } catch (OpenDataException e) {
            throw new IllegalStateException("Can't create CompositeData" + e);
        }
    }

    /**
     * Static factory method to create RoleData from CompositeData object.
     * 
     * @param data {@link CompositeData} instance.
     * @return RoleData instance.
     */
    public static RoleData from(CompositeData data) {
        if(data == null){
            return null;
        }
        String name = (String) data.get(UserAdminMBean.NAME);
        int type = (Integer) data.get(UserAdminMBean.TYPE);
        return new RoleData(name, type);
    }

    /**
     * Creates TabularData from Dictionary.
     * 
     * @param props Dictionary instance.
     * @return TabularData instance.
     */
    protected static TabularData toTabularData(Dictionary<String, Object> props){
        if(props == null){
            return null;
        }
        TabularData data = new TabularDataSupport(JmxConstants.PROPERTIES_TYPE);
        for (Enumeration<String> keys = props.keys(); keys.hasMoreElements();) {
            String key = keys.nextElement();
            data.put(PropertyData.newInstance(key, props.get(key)).toCompositeData());
        }
        return data;
    }
    
    /**
     * Creates properties from TabularData object.
     * 
     * @param data {@link TabularData} instance.
     * @return translated tabular data to properties {@link Dictionary}.
     */
    protected static Dictionary<String, Object> propertiesFrom(TabularData data){
        if(data == null){
            return null;
        }
        
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        for(CompositeData compositeData : (Collection<CompositeData>)data.values()){
            PropertyData  property = PropertyData.from(compositeData);
            props.put(property.getKey(), property.getValue());
        }
        
        return props;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

}