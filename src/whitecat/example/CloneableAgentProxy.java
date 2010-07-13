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
package whitecat.example;

import java.util.Random;

import whitecat.core.agents.AgentProxy;
import whitecat.core.agents.IClonableAgentProxy;
import whitecat.core.agents.LocalAgentProxy;
import whitecat.core.agents.WCAgent;

/**
 * An example of a cloneable agent proxy.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class CloneableAgentProxy extends LocalAgentProxy implements
	IClonableAgentProxy {

    // the extended state of the agent proxy
    private String stringVariable  = null;
    private int    integerVariable = 0;
    private int[]  integerArray    = null;
    
    public CloneableAgentProxy( WCAgent agent ){
	super();
	super.setMyAgent(agent);
	
	Random ran = new Random();
	int random = ran.nextInt();
	while( random < 0 )
	    random += 1000;
	while( random > 10000 )
	    random /= 100;
	
	this.stringVariable = "StringVariable " + random;
	this.integerVariable = random;
	
	this.integerArray = new int[ this.integerVariable ];
	for( int i = 0; i < this.integerArray.length; i++ )
	    this.integerArray[i] = ran.nextInt(); 
	
    }
    
    
    /* (non-Javadoc)
     * @see whitecat.core.agents.AgentProxy#update()
     */
    @Override
    public AgentProxy update() {
	// TODO Auto-generated method stub
	return null;
    }

    /* (non-Javadoc)
     * @see whitecat.core.agents.IClonableAgentProxy#cloneAgentProxyState(whitecat.core.agents.IClonableAgentProxy)
     */
    public void cloneAgentProxyState(IClonableAgentProxy sourceAgentProxy) {
	// check if the agent proxy is of my same type!
	if( !(sourceAgentProxy instanceof CloneableAgentProxy) )
	    return;
	
	CloneableAgentProxy sourceProxy = (CloneableAgentProxy) sourceAgentProxy;
	
	// copy each single variable of the extended state
	this.stringVariable = new String( sourceProxy.stringVariable );
	this.integerVariable = sourceProxy.integerVariable;
	this.integerArray = new int[ sourceProxy.integerArray.length ];
	System.arraycopy( sourceProxy.integerArray, 0, this.integerArray, 0, sourceProxy.integerArray.length );
	

    }


    /**
     * Provides the value of the stringVariable field.
     * @return the stringVariable
     */
    public synchronized final String getStringVariable() {
        return this.stringVariable;
    }


    /**
     * Provides the value of the integerVariable field.
     * @return the integerVariable
     */
    public synchronized final int getIntegerVariable() {
        return this.integerVariable;
    }


    /**
     * Provides the value of the integerArray field.
     * @return the integerArray
     */
    public synchronized final int[] getIntegerArray() {
        return this.integerArray;
    }
    
    

}
