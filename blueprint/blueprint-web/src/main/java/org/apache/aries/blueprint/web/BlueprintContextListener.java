/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.aries.blueprint.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.aries.blueprint.container.BlueprintContainerImpl;

/**
 * Initialises all the blueprint XML files called <code>META-INF/blueprint.xml</code> on the classpath
 */
public class BlueprintContextListener implements ServletContextListener {

    public static final String CONTAINER_ATTRIBUTE = "org.apache.aries.blueprint.container";

    public static final String LOCATION = "blueprintLocation";

    public static final String PROPERTIES = "blueprintProperties";

    public static final String DEFAULT_LOCATION = "META-INF/blueprint.xml";

    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        String location = servletContext.getInitParameter(LOCATION);
        if (location == null) {
            location = DEFAULT_LOCATION;
        }
        List<URL> resourcePaths = new ArrayList<URL>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration<URL> resources = classLoader.getResources(location);
            while (resources.hasMoreElements()) {
                resourcePaths.add(resources.nextElement());
            }
            servletContext.log("Loading Blueprint contexts " + resourcePaths);

            Map<String, String> properties = new HashMap<String, String>();
            String propLocations = servletContext.getInitParameter(PROPERTIES);
            if (propLocations != null) {
                for (String propLoc : propLocations.split(",")) {
                    Enumeration<URL> propUrl = classLoader.getResources(propLoc);
                    while (propUrl.hasMoreElements()) {
                        URL url = propUrl.nextElement();
                        InputStream is = url.openStream();
                        try {
                            Properties props = new Properties();
                            props.load(is);
                            Enumeration names = props.propertyNames();
                            while (names.hasMoreElements()) {
                                String key = names.nextElement().toString();
                                properties.put(key, props.getProperty(key));
                            }
                        } finally {
                            is.close();
                        }
                    }
                }
            }

            BlueprintContainerImpl container = new BlueprintContainerImpl(classLoader, resourcePaths, properties, true);
            servletContext.setAttribute(CONTAINER_ATTRIBUTE, container);
        } catch (Exception e) {
            servletContext.log("Failed to startup blueprint container. " + e, e);
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        Object container = servletContext.getAttribute(CONTAINER_ATTRIBUTE);
        if (container instanceof BlueprintContainerImpl) {
            BlueprintContainerImpl blueprint = (BlueprintContainerImpl) container;
            blueprint.destroy();
        }
    }
}
