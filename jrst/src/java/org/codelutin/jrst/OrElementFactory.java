/*##%
 * Copyright (C) 2002, 2004 Code Lutin
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
 * OrElementFactory.java
 *
 * Created: 2004/06/15
 *
 * @author Bucas Jean-François <bucas@codelutin.com>
 * Copyright Code Lutin
 * @version $Revision$
 *
 * Mise a jour: $Date$
 * par : $Author$
 */

package org.codelutin.jrst;

import java.util.Iterator;

public class OrElementFactory extends AbstractFactory { // DocumentFactory

    /** on est en train de parser autre chose qu'un paragraphe **/
//    final static protected Object PARSING = new Object();
    /** on est en train de parser un paragraphe **/
//    final static protected Object PARSING_PARA = new Object();

    // le nom de l'élément (son role)
    String name = "";

    public OrElementFactory(String nom) {
        init();
        name = nom;
    }

    protected void init() {

       super.init();
       factoryAND_OR = FUNCTION_OR;
//       STATE = PARSING;

    }

    protected AbstractFactory factoryNew(){
        return new OrElementFactory(name);
    }

    protected Element elementNew(){
        return new OrElement(name);
    }

    public ParseResult accept(int c){
        for(Iterator it=childs.iterator(); it.hasNext();){
            if (((ElementFactory)it.next()).accept(c) == ParseResult.ACCEPT) {
                return ParseResult.ACCEPT;
            }
        }
        return ParseResult.FAILED;
    }

    int lastc = 0;
    int lastlastc = 0;
    int lastlastlastc = 0;

    public ParseResult parse(int c){
        ParseResult result = ParseResult.IN_PROGRESS;
        consumedCharCount++;

        // le Child Courant tout en bas de l'arborescence
        AbstractFactory lastChild = (AbstractFactory)currentChild;
        while (lastChild != null && lastChild.currentChild != null) {
            lastChild = (AbstractFactory)lastChild.currentChild;
        }

        if (c=='\n' && lastc==':' && lastlastc==':') {
            if (currentChild instanceof ParaFactory) {
                if (isMultiple()) {
                    CHILD_STATE = SEARCH_CHILD;
                    buffer.delete(0, buffer.length()-2);
                }
            }
        }

        if (lastc=='\n' && c=='\n') {
            // double ligne blanche
            if (lastlastc=='\n') {
                if (!(lastChild instanceof LitteralFactory)) {
                   //     System.out.println(" Double Ligne blanche " + this + "-" +
                   //     currentChild + " == " + lastChild);
                        //result = delegate(c);
                        result = delegate(c);
                        if (result != ParseResult.FINISHED)
                            result = ParseResult.FINISHED.setConsumedCharCount(consumedCharCount);
                }
            }else if (currentChild instanceof ParaFactory) {
                if (isMultiple()) {
                    CHILD_STATE = SEARCH_CHILD;
                    buffer.delete(0, buffer.length());
 //                   System.out.println("Ligne Blanche ");
                }else{
                    result = delegate(c);
                }
            }else{
                result = delegate(c);
            }
        }else{
            result = delegate(c);
        }

        lastlastlastc = lastlastc;
        lastlastc = lastc;
        lastc = c;

        return result;
    }
    public ParseResult parseEnd(){
    // TODO a faire
    return null;
    }
} // OrElementFactory

