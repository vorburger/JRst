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
 * AndElementFactory.java
 *
 * Created: 18 Juin 2004
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

public class AndElementFactory extends AbstractFactory {

    public String name = null;

    public AndElementFactory() {
        init();
    }

    public AndElementFactory(String nom) {
        init();
        name = nom;
    }

    protected AbstractFactory factoryNew(){
        return new AndElementFactory(name);
    }

    protected Element elementNew(){
        return new AndElement(name);
    }

    public void setName(String n) { name = n; }
    public String getName() { return name; }

    public ParseResult accept(int c){

        // tant qu'un fils n'est pas nécessaire
        // et qu'on a pas d'acceptance
        Iterator it=childs.iterator();
        do {
            ElementFactory factory = (ElementFactory)it.next();
            ParseResult result = factory.accept(c);
            if ( result == ParseResult.ACCEPT ||
                 result == ParseResult.IN_PROGRESS) {
                return result;
            }else{
                if (factory.isNecessaire()) {
                    break;
                }
            }
        }while( it.hasNext() );

        return ParseResult.ACCEPT;
    }

    public ParseResult parseEnd(int c){
        ParseResult result = ParseResult.ACCEPT;
        if (currentChild != null){
//            System.out.println("ANDelemChild:"+currentChild); //((AbstractFactory)currentChild).identify());
            result = currentChild.parseEnd(c);
        }

        CHILD_STATE = null;
        buffer.delete(0,buffer.length());

        return result;

    }


    int lastc = 0;
    int lastlastc = 0;

    public ParseResult parse(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;

        consumedCharCount++;
        //System.out.print((char)c);
        if (lastlastc=='\n' && lastc=='\n' && c=='\n') {
            if (CHILD_STATE != null) {
                result = delegate(c);
            }else{
                result = ParseResult.FINISHED.setConsumedCharCount(consumedCharCount);
            }
        }else{
            result = delegate(c);
        }

        lastlastc = lastc;
        lastc = c;

        return result;
    }

} // StructureModelFactory

