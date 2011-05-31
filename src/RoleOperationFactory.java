import whitecat.core.IRoleOperation;
import whitecat.core.WhiteCat;

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
 * Copyright (C) Luca Ferrari 2008-2011 - cat4hire@users.sourceforge.net
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

/**
 * A factory to build a new operation to do with a role booster.
 * Please note that a role booster is always initialized with a role operation,
 * so this factory must be used only to do another operation with the same role booster.
 * @author Luca Ferrari - cat4hire (at) sourceforge.net
 *
 */
public class RoleOperationFactory {

    /**
     * Provides a new role operation.
     * @return a new role operation
     */
    public static IRoleOperation getRoleOperation(){
	return WhiteCat.getNewRoleOperation();
    }
}
