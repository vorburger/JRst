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

    // le nom de l'élément (son role)
    String name = null;

    protected AbstractFactory factoryNew(){ return new OrElementFactory(name); }
    protected Element elementNew(){ return new OrElement(name); }

    public OrElementFactory() {
        init();
    }

    public OrElementFactory(String nom) {
        init();
        name = nom;
    }

    public void setName(String n) { name = n; }
    public String getName() { return name; }


    protected void init() {
       super.init();
       factoryAND_OR = FUNCTION_OR;
    }

    public ParseResult accept(int c){
        for(Iterator it=childs.iterator(); it.hasNext();){
            ParseResult result = ((ElementFactory)it.next()).accept(c);
            if (result == ParseResult.ACCEPT ||
                result == ParseResult.IN_PROGRESS) {
                return result;
            }
        }
        return ParseResult.FAILED;
    }

    public ParseResult parseEnd(int c){
        ParseResult result = ParseResult.ACCEPT;

        // on termine l'élément en cours
        if (currentChild != null){
        // System.out.println("ORelemChild:"+currentChild); //((AbstractFactory)currentChild).identify());
            result = currentChild.parseEnd(c);
        }else{
            // si aucun élément n'a été séléctionné, on prend
            // le premier qui accepte
            ParseResult theresult = ParseResult.IN_PROGRESS;
            while ( currentChildIndex < childs.size() &&
                    currentChild == null &&
                    theresult != ParseResult.ACCEPT) {

                theresult = searchChild();

                currentChildIndex++;
            }

            //if ( currentChildIndex <=  childs.size() ){
            if ( currentChild != null ){
                for(int i=0; i<buffer.length(); i++) {
                    result = currentChild.parse((int)buffer.charAt(i));
                }

                result = currentChild.parseEnd(c);

            }

                /*
                for(int i=currentChildIndex; i<buffer.length(); i++) {
                    ElementFactory element = ((ElementFactory)childs.get(i));
                    ParseResult result = element.accept(c);
                    if (result == ParseResult.ACCEPT)
                        element.parse(c);

                }
            }
                */
        }
        // System.out.println("current Child : " + currentChild);

        CHILD_STATE = null;
        buffer.delete(0,buffer.length());

        return result;
    }

    int lastc = 0;
    int lastlastc = 0;
    int lastlastlastc = 0;

    public ParseResult parse(int c){
        ParseResult result = ParseResult.IN_PROGRESS;
        boolean dodelegate = true;
        consumedCharCount++;

       // System.out.print("\033[00;32m"+(char)c+"\033[00m");

/*
        // le Child Courant tout en bas de l'arborescence
        AbstractFactory lastChild = (AbstractFactory)currentChild;
        while (lastChild != null && lastChild.currentChild != null) {
            lastChild = (AbstractFactory)lastChild.currentChild;
        }

        // pour les formes litterales
        if (lastlastc==':' && lastc==':' && c=='\n') { // ::
            if (currentChild instanceof ParaFactory) {
                if (isMultiple()) {
                    CHILD_STATE = SEARCH_CHILD;
                    buffer.delete(0, buffer.length()-2);
                }
            }
        }
*/
        if (result != ParseResult.FINISHED){
           if (lastc=='\n' && c=='\n') {
               if (lastlastc=='\n') { // double ligne blanche
                   //dodelegate = false;
                   result = parseEnd(c);
               }
                  /* if (!(lastChild instanceof LitteralFactory)) {
                       // System.out.println(" Double Ligne blanche " + this + "-" +
                       // currentChild + " == " + lastChild);
                       result = delegate(c);
                       if (result != ParseResult.FINISHED)
                           result = ParseResult.FINISHED.setConsumedCharCount(consumedCharCount);
                       dodelegate = false;
                   }else{
                       // ne passe pas là quand il fodrai !!! BUG BUG BUG :(
                       // TODO
                       System.out.println("Coucou !!! ");
                       dodelegate = false;
                   }

               }else if (currentChild instanceof ParaFactory) {
                   if (isMultiple()) {
                       CHILD_STATE = SEARCH_CHILD;
                       buffer.delete(0, buffer.length());
                       dodelegate = false;
                       System.out.println("Ligne Blanche \n\n");
                   }
               }
               */
           }
        }

        if (dodelegate) result = delegate(c);

        lastlastlastc = lastlastc;
        lastlastc = lastc;
        lastc = c;

        return result;
    }
} // OrElementFactory

