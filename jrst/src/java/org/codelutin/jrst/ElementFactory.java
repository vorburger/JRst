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
 * ElementFactory.java
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

public interface ElementFactory { // ElementFactory

    public final static class ParseResult{
        protected String name;
        /** variable utilisable par les Factories pour conserver des etat internes */
        public Object STATE = null;
        /** variable utilisable par les Factories pour mettre un message */
        public String text = "";
        protected ParseResult(String name){
            this.name = name;
        }
        public ParseResult create(){
            ParseResult result = new ParseResult(name);
            return result;
        }
        public boolean equals(Object o){
            if(o == this)
                return true;
            return o instanceof ParseResult && ((ParseResult)o).name.equals(name);
        }
    }
    public final static ParseResult PARSE_FINISHED = new ParseResult("PARSE_FINISHED");
    public final static ParseResult PARSE_FAILED = new ParseResult("PARSE_FAILED");
    public final static ParseResult PARSE_IN_PROGRESS = new ParseResult("PARSE_IN_PROGRESS");

    public void init();

    /**
     * Tant que l'élément accept les caractères, on continu a lui envoyer.
     *
     *@param c  le caractère à ajouter
     *@return   retourne vrai si l'élément est encore valide avec ce nouveau
     *      caractère.
     */
    public ParseResult accept(int c);

    /**
     * Retourne l'élement représenté créé après la lecture de l'élément.
     *
     *@return null si les caractères lu ne permettent pas de créer un élément.
     */
    public Element getElement();

    public ElementFactory addChild(ElementFactory child);

} // ElementFactory

