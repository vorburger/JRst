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
 * DocumentFactory.java
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

import java.util.Iterator;

public class DocumentFactory extends AbstractFactory { // DocumentFactory

    protected AbstractFactory factoryNew(){  return new DocumentFactory();  }
    protected Element elementNew(){  return new Document();  }
    public ParseResult accept(int c){  return ParseResult.ACCEPT;  }

    public ParseResult parse(int c){
        ParseResult result = ParseResult.IN_PROGRESS;
        result = delegate(c);
        return result;
    }

    /**
     *   Récursif à travers le modèle pour chercher le titre du document
     *   qui est en réalité le premier titre trouvé !
     *   @param e l'élément dans lequel chercher le titre (lui ou ses fils)
     */

    public Title ParcoursTitle(Element e) {
        if (e instanceof Title) {
            return (Title)e;
        }else{
            for(Iterator i=e.getChilds().iterator(); i.hasNext();){
                return ParcoursTitle((Element)i.next());
            }
        }
        return null;
    }


    public ParseResult parseEnd(int c){
        Document myDoc = (Document)getElement();

        Title t = ParcoursTitle(myDoc);
        if (t != null) {
            myDoc.setTitle(t);
        }else{
            System.out.println("No Title Found!");
        }

        return ParseResult.ACCEPT;
    }
} // DocumentFactory

