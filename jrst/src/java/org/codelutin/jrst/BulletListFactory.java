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
 * BulletListFactory.java
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
public class BulletListFactory extends AbstractFactory { // BulletListFactory
    static final String ACCEPTED_CHAR = "-*+";

    /** on recherche le premier symbole de liste */
    final static protected Object FIRST_SYMBOLE_SEARCH = new Object();
    /** on recherche le symbole de liste */
    final static protected Object SYMBOLE_SEARCH = new Object();
    /** on recherche un espace derriere le symbole */
    final static protected Object AFTER_SYMBOLE = new Object();
    /** on est en lecture de l'élément de liste */
    final static protected Object READ = new Object();
    final static protected Object FINISHED = new Object();

    protected AbstractFactory factoryNew(){
        return new BulletListFactory();
    }
    protected Element elementNew(){
        return new BulletList();
    }

    protected BulletList getBulletList(){
        return (BulletList)getElement();
    }

    int lastc = -1;
    int indentLenght = 2;
    int symbole = -1;
    int indentRead = 0;

    public void init(){
        super.init();
        lastc = -1;
        indentLenght = 0;
        indentRead = 0;
        symbole = -1;
        STATE = FIRST_SYMBOLE_SEARCH;
    }

    public ParseResult accept(int c) {
        if(ACCEPTED_CHAR.indexOf((char)c) != -1){
            return ParseResult.ACCEPT;
        }else{
            return ParseResult.FAILED;
        }
    }
    public ParseResult parse(int c) {
        if(STATE == FINISHED)
            throw new IllegalStateException("Parsing is finished");
        ParseResult result = ParseResult.IN_PROGRESS;

        consumedCharCount++;
        if(STATE == FIRST_SYMBOLE_SEARCH){
            if(ACCEPTED_CHAR.indexOf((char)c) != -1){
                getBulletList().setSymbole(c);
                symbole = c;
                indentRead = 0;
                STATE = AFTER_SYMBOLE;
            }else {
                result = ParseResult.FAILED.setError("Illegal symbole for bullet list: '"+(char)c+"'");
            }
        }else if(STATE == AFTER_SYMBOLE){
            if(((char)c != ' ' && indentRead == 0)||((char)c == ' ' && indentRead > 1)){
                result = ParseResult.FAILED.setError("List must have one space after symbole");
            }else if((char)c == ' ' && indentRead == 0){
                indentRead++;
            }else{
                STATE = READ;
            }
        }else if(STATE == SYMBOLE_SEARCH){
            if((char)c == '\n' && (char)lastc == '\n'){
               result = ParseResult.FINISHED.setConsumedCharCount(consumedCharCount-1);
            }
            else if(c == symbole){
                CHILD_STATE = null; // il faut recherche un nouveau fils
                buffer.delete(0, buffer.length()); // on est sur que le fils a tout mangé, puisque c nous qui l'arretons
                STATE = AFTER_SYMBOLE;
            }else if((char)c == ' '){
                indentRead++;
            }else{
                if(indentRead != indentLenght){
                    result = ParseResult.FAILED.setError("bad indentation");
               }else{
                   STATE = READ;
                }
            }
        }

        if(STATE == READ){ // STATE READ
            lastc = c;
            result = delegate(c);
            if((char)c == '\n'){
                indentRead = 0;
                STATE = SYMBOLE_SEARCH;
            }
        }

        return result;
    }

} // BulletListFactory

