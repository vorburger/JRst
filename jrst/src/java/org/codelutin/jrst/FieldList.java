/* *##%
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

/* *
 * FieldList.java
 *
 * Created: 24 janv. 2004
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

/**
* Represente une liste de champs
* <pre>
* :field1: text
* :field2: text
* ...
* </pre>
* la liste childs contient alors des Strings qui represente le nom du champs
* puis ensuite les elements constituants le texte pour le champs jusqu'au
* prochain objet de type String ou la fin de la liste.
*/
public class FieldList extends AbstractElement { // FieldList

    public FieldList(){
    }

    public void addFieldText(String fieldText){
        this.childs.add(fieldText);
    }

} // FieldList

