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
 * RstList.java
 *
 * Created: 17 janv. 2004
 *
 * @author Benjamin Poussin <poussin@codelutin.com>
 * Copyright Code Lutin
 * @version $Revision$
 *
 * Mise a jour: $Date$
 * par : $Author$
 */

package org.codelutin.jrst;

import java.util.Iterator;
import java.util.LinkedList;

public class RstList  extends RootElement implements Element { // RstList

    /** liste de chaine de caratere */
    LinkedList list;
    int indentLenght = 0;
    int symbole = -1;

    public RstList(LinkedList list, int indentLenght, int symbole){
        this.list = list;
        this.indentLenght = indentLenght;
        this.symbole = symbole;
    }

    public int getSymbole(){
        return symbole;
    }

    public int getIndentLenght(){
        return indentLenght;
    }

    public Iterator getList(){
        return list.iterator();
    }
} // RstList

