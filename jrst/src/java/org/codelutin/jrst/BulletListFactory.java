/* *##%
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
public class BulletListFactory extends IndentedAbstractFactory { // BulletListFactory

    /** Constantes **/

    final static String ACCEPTED_CHAR = "-*+";

    // on recherche le premier symbole de liste */
    final static protected Object FIRST_SYMBOLE_SEARCH = new Object();
    // on recherche le symbole de liste */
    final static protected Object SYMBOLE_SEARCH = new Object();
    // on recherche un espace derriere le symbole */
    final static protected Object AFTER_SYMBOLE = new Object();

    /** Attributs **/

    int symbole    = -1;
    int countSpace =  0;

    /** Méthodes **/

    // Accesseurs et recopieurs
    protected AbstractFactory factoryNew(){ return new BulletListFactory(); }
    protected Element elementNew(){ return new BulletList(); }
    protected BulletList getBulletList(){ return (BulletList)getElement(); }

    // Constructeur
    public BulletListFactory(){ init(); }

    // Initialisation
    public void init(){
        oneLiner    = true;
        symbole     = -1;
        countSpace  =  0;
        SUB_STATE   = FIRST_SYMBOLE_SEARCH;
        headRegExpr = "(\\-|\\+|\\*) ";
        super.init();
    }

    // parse l'entete de la liste
    public ParseResult parseHead(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;

        if(SUB_STATE == FIRST_SYMBOLE_SEARCH){
            if(ACCEPTED_CHAR.indexOf((char)c) != -1){
                getBulletList().setSymbole(c);
                symbole = c;
                countSpace = 0;
                SUB_STATE = AFTER_SYMBOLE;
            }else
                result = ParseResult.FAILED.setError("Illegal symbole for bullet list: '"+(char)c+"'");
        }else if(SUB_STATE == SYMBOLE_SEARCH){
            if(c == symbole){
                countSpace = 0;
                SUB_STATE = AFTER_SYMBOLE;
            }else{
                result = parseEnd(c);
            }
        }else if(SUB_STATE == AFTER_SYMBOLE){
            if(((char)c != ' ' && countSpace == 0)||
               ((char)c == ' ' && countSpace > 1)){
                   result = ParseResult.FAILED.setError("List must have one space after symbole");
            }else if((char)c == ' ' && countSpace == 0){
                countSpace++;
            }else{
                INDENT_STATE = READING_BODY;
                SUB_STATE = SYMBOLE_SEARCH;
                result = delegate(c);
            }
        }

        return result;
    }

/*    // parse l'entete de la liste
    public ParseResult parseHead(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;

        if(SUB_STATE == FIRST_SYMBOLE_SEARCH){
            if(ACCEPTED_CHAR.indexOf((char)c) != -1){
                getBulletList().setSymbole(c);
                symbole = c;
                countSpace = 0;
                SUB_STATE = AFTER_SYMBOLE;
            }else
                result = ParseResult.FAILED.setError("Illegal symbole for bullet list: '"+(char)c+"'");
        }else if(SUB_STATE == SYMBOLE_SEARCH){
            if(c == symbole){
                countSpace = 0;
                SUB_STATE = AFTER_SYMBOLE;
            }else{
                result = parseEnd(c);
            }
        }else if(SUB_STATE == AFTER_SYMBOLE){
            if(((char)c != ' ' && countSpace == 0)||
               ((char)c == ' ' && countSpace > 1)){
                   result = ParseResult.FAILED.setError("List must have one space after symbole");
            }else if((char)c == ' ' && countSpace == 0){
                countSpace++;
            }else{
                if (STATE == ACCEPTING) {
                    result = ParseResult.ACCEPT;
                }else{
                    STATE = READING_BODY;
                    SUB_STATE = SYMBOLE_SEARCH;
                    result = delegate(c);
                }
            }
        }

        return result;
    }
*/

} // BulletListFactory

