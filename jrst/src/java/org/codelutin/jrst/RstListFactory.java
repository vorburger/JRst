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
 * RstListFactory.java
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

import java.util.LinkedList;


/**
* Permet la lecture d'une liste simple. Les retours chariots ne sont pas permit dans un élément de liste. Les espaces devant les symboles doivent tous etre identiques, les symboles doivent tous etre identique. Il doit y avoir un espace apres le symbole.
* <pre>
*
*   - un eleme
*   - un deuxieme
*
*   o une autre liste
*   o deuxieme element de l'autre liste
*
* </pre>
*/
public class RstListFactory extends RootFactory implements ElementFactory { // RstListFactory
    /** on recherche le premier symbole de liste */
    final static protected Object FIRST_SYMBOLE_SEARCH = new Object();
    /** on recherche le symbole de liste */
    final static protected Object SYMBOLE_SEARCH = new Object();
    /** on recherche un espace derriere le symbole */
    final static protected Object AFTER_SYMBOLE = new Object();
    /** on est en lecture de l'élément de liste */
    final static protected Object READ = new Object();

    LinkedList list = null;
    StringBuffer buffer= null;
    ParseResult parseResult = null;

    int lastc = -1;

    int indentLenght = 0;
    int symbole = -1;

    int indentRead = 0;

    public void init(){
        lastc = -1;
        list = new LinkedList();
        buffer = new StringBuffer();
        indentLenght = 0;
        indentRead = 0;
        symbole = -1;
        element = null;
        buffer = new StringBuffer();
        parseResult = PARSE_IN_PROGRESS.create();
        parseResult.STATE = FIRST_SYMBOLE_SEARCH;
    }

    /**
    * Retourne true tant que l'objet n'a pas fini de parser son élément.
    * Lorsqu'il retourne false, la factory est capable de savoir si l'élement est convenable ou non, pour cela il faut appeler la méthode {@link getParseResult}.
    */
    public ParseResult accept(int c) {
        if(!parseResult.equals(PARSE_IN_PROGRESS))
            throw new IllegalStateException("Parsing is finished");

        if(parseResult.STATE == FIRST_SYMBOLE_SEARCH){
            if((char)c == ' '){
                indentLenght++;
            }
            else{
                if(indentLenght <= 0){
                    parseResult = PARSE_FAILED.create();
                    parseResult.text = "List must have one space before symbole";
                }else{
                    symbole = c;
                    parseResult.STATE = AFTER_SYMBOLE;
                }
            }
        }else if(parseResult.STATE == AFTER_SYMBOLE){
            if((char)c != ' '){
                parseResult = PARSE_FAILED.create();
                    parseResult.text = "List must have one space after symbole";
            }else{
                indentRead = 0;
                parseResult.STATE = READ;
            }
        }else if(parseResult.STATE == SYMBOLE_SEARCH){
            if((char)c == '\n' && (char)lastc == '\n'){
                element = createElement();
                parseResult = PARSE_FINISHED;
            }
            else if((char)c == ' '){
                indentRead++;
                if(indentRead > indentLenght){
                    // pas la meme indentation donc une erreur
                    parseResult = PARSE_FAILED.create();
                    parseResult.text = "bad indentation";
                }
            }
            else{
                if(c != symbole){
                    // pas la meme symbole donc une erreur
                    parseResult = PARSE_FAILED.create();
                    parseResult.text = "bad symbole list have:'"+(char)c+"' wait:'"+(char)symbole+"' list was: "+list;
                }else{
                    indentRead = 0;
                    parseResult.STATE = AFTER_SYMBOLE;
                }
            }
        }else{ // STATE READ
            lastc = c;
            if((char)c == '\n'){
                list.add(buffer.toString());
                buffer = new StringBuffer();
                parseResult.STATE = SYMBOLE_SEARCH;
            }else{
                buffer.append((char)c);
            }
        }

        return parseResult;
    }

    /**
    * Si le résultat du parsage est ok alors retourne l'element, sinon retourne null
    */
    protected Element createElement(){
        return new RstList(list, indentLenght, symbole);
    }
} // RstListFactory

