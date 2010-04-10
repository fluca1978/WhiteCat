/* 
 * WhiteCat - A dynamic role injector for agents.
 *
 * This project represents a new implementation of the so called BlackCat,
 * a project I made during my thesis degree. For more information about such project please see:
 * 
 *   G., L. Ferrari, L. Leonardi,
 *   Injecting Roles in Java Agents Through Run-Time Bytecode Manipulation
 *   IBM Systems Journal, Vol. 44, No. 1, pp.185-208, 2005
 *
 * This new approach exploits a completely different implementation, keeping the
 * same idea of BlackCat.
 * 
 *
 * Copyright (C) Luca Ferrari 2008-2010 - cat4hire@users.sourceforge.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package whitecat.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * This class handles the configuration of the running environment.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class Configuration extends Properties {
    
    /**
     * The logger for this class loader.
     */
    private static Logger logger = org.apache.log4j.Logger.getLogger(Configuration.class);
    
    // configure the logger
    static{
	DOMConfigurator.configure("conf/log4j.xml");
    }
    
    
    /**
     * A boolean flag that indicates if the configuration has been succesfully loaded.
     */
    private boolean configurationLoaded = false;


    /**
     * The key for the default proxy handler.
     */
    public static final String DEFAULT_PROXY_HANDLER = "DefaultProxyHandler";
    
    /**
     * The default method forwarder generator.
     */
    public static final String DEFAULT_METHOD_FORWARDER_GENERATOR= "DefaultMethodForwarderGenerator";


    /**
     * The default agent proxy to use.
     */
    public static final String DEFAULT_AGENT_PROXY = "DefaultAgentProxy";


    /**
     * The default role repository to use.
     */
    public static final String DEFAULT_ROLE_REPOSITORY = "DefaultRoleRepository";
    
    
    /**
     * The configuration file that stores the properties.
     */
    private String configurationFile = "./conf/whitecat.xml";

    /**
     * The singleton instance of the configuration object.
     */
    private static Configuration mySelf = null;
    
    
    /**
     * Default configuration.
     */
    private Configuration(){
	super();
	this.reload();
    }
    
    /**
     * Provides the current configuration object, an instance to the configuration
     * object that is used by the WhiteCat engine.
     * @return the configuration instance
     */
    public synchronized static Configuration getInstance(){
	if( mySelf == null )
	    mySelf = new Configuration();
	
	return mySelf;
    }
    
    /**
     * Loads the configuration file (that is supposed to be XML).
     * @return true if the configuration has been loaded
     */
    private boolean reload(){
	try{
	    logger.info("Loading configuration file " + this.configurationFile);
	    this.loadFromXML( new FileInputStream( this.configurationFile ) );
	    this.configurationLoaded = true;
	}catch(IOException e){
	    logger.error("Exception caught while reading the configuration property file", e);
	    this.configurationLoaded = false;
	}
	
	return this.configurationLoaded;
    }


    
    

}
