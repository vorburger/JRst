/* *##% Plugin maven JRst
 * Copyright (C) 2006 - 2008 CodeLutin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>. ##%*/

/* *
 * GenerationJRstException.java
 *
 * Created: 14 avril 2006
 *
 * @author ruchaud
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */

package org.codelutin.jrst.plugin;

/**
 * GenerationJRstException
 *
 * @author ruchaud
 * @version $Revision$
 * 
 * Last update : $Date$
 * By : $Author$
 */
public class GenerationJRstException extends RuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = 7121169280356405413L;
    
    static private String filesErrors = "";

    public GenerationJRstException(String fileIn) {
        filesErrors += fileIn + "\n";
    }

    public static String getFilesErrors() {
        return filesErrors;
    }
}
