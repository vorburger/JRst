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
import jregex.Pattern;
import jregex.Matcher;

public class AbstractElement implements Element { // AbstractElement
    ArrayList childs = new ArrayList();

    public Element addChild(Element e){
        childs.add(e);
        return this;
    }
    public List getChilds(){
        return childs;
    }


    /**
     *  le petit nom véritable de la factory
     */

    // pour récuperer le nom des factories
    Pattern ElementName = new Pattern("org\\.codelutin\\.jrst\\.(.*)");

    public String Name() {
        Matcher myM = ElementName.matcher(getClass().getName());
        myM.matches();
        return myM.group(1);
    }

} // AbstractElement

