/*##%
 * Copyright (C) 2002, 2003 Code Lutin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *##%*/
/*
 * AbstractElement.java
 *
 * Created: 7 oct. 2003
 *
 * @author Benjamin Poussin <poussin@codelutin.com>
 * Copyright Code Lutin
 * @version $Revision$
 *
 * Mise a jour: $Date$
 * par : $Author$
 */
package org.codelutin.jrst;

import java.util.ArrayList;
import java.util.List;
import jregex.Matcher;
import jregex.Pattern;

/** DOCUMENTME Description of the Class */
public class AbstractElement implements Element { // AbstractElement

    // pour récuperer le nom des factories
    /** DOCUMENTME Description of the Field */
    Pattern ElementName  = new Pattern("org\\.codelutin\\.jrst\\.(.*)");
    /** DOCUMENTME Description of the Field */
    ArrayList childs     = new ArrayList();

    /**
     *  le petit nom véritable de la factory
     *
     * @return   DOCUMENTME Description of the Return Value
     */
    public String Name() {
        Matcher myM  = ElementName.matcher(getClass().getName());
        myM.matches();
        return myM.group(1);
    }

    /**
     * Adds a feature to the Child attribute of the AbstractElement object
     *
     * @param e  The feature to be added to the Child attribute
     * @return   DOCUMENTME Description of the Return Value
     */
    public Element addChild(Element e) {
        childs.add(e);
        return this;
    }

    /**
     * Gets the childs attribute of the AbstractElement object
     *
     * @return   The childs value
     */
    public List getChilds() {
        return childs;
    }

} // AbstractElement

