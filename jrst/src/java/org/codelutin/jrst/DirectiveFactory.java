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
 * DirectiveFactory.java
 *
 * Created: 23 juin. 2004
 *
 * @author Bucas
 * Copyright Code Lutin
 * @version $Revision$
 *
 * Mise a jour: $Date$
 * par : $Author$
 */

package org.codelutin.jrst;

public class DirectiveFactory extends IndentedAbstractFactory { // DirectiveFactory

    /** Constantes **/

    // les différents états d'avancenement de la recherche de bloc litteral
    final static Object DOT_DOT_SPACE = new Object(); // pour enlever le ".. " du début
    final static Object TERM = new Object(); // on lit le nom de la directive
    final static Object COLONS = new Object(); // on enleve les "::"
    final static Object AFTER_COLONS = new Object(); // après les colons

    /** Attributs **/

    StringBuffer text = null;
    int counter       = 0;

    /** Méthodes **/

    // Accesseurs et recopieurs
    protected AbstractFactory factoryNew(){ return  new DirectiveFactory(); }
    protected Element elementNew(){ return new Directive(); }
    protected Directive getDirective(){ return (Directive)getElement(); }

    // Constructeur
    public DirectiveFactory(){ init(); }

    // Initialisation
    public void init(){
        text        = new StringBuffer();
        counter     = 0;
        SUB_STATE   = DOT_DOT_SPACE;
        unique      = true;
        oneLiner    = true;
        headRegExpr = "\\.\\. \\w+::";
        super.init();
    }

    // parse l'entete de la directive
    public ParseResult parseHead(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;

        if (SUB_STATE == DOT_DOT_SPACE) {
            if ((char)c == '.') {
                counter++;
                if (counter > 2) {
                    result = ParseResult.FAILED.setError("expected only double-dot '..'");
                }
            }else if ((char)c == ' ' && counter == 2) {
                    counter++;
                    if (counter == 3) {
                        SUB_STATE = TERM;
                        counter = 0;
                        text.delete(0, text.length());
                    }
            }else{
                result = ParseResult.FAILED.setError("expected double-dot '..'");
            }
        }else if (SUB_STATE == TERM) {
            if((char)c == ' ') {
                result = ParseResult.FAILED.setError("there should be no space (' ') in the name of the directive");
            }else if((char)c != '\\'){
                if((char)c != ':' || ((char)c == ':' && (char)lastc == '\\')){
                    text.append((char)c);
                }else{
                    //Term t = new Term(text);
                    //getElement().addChild(t);
                    ((Directive)getElement()).setText(text.toString());
                    text.delete(0, text.length());
                    SUB_STATE = COLONS;
                    counter = 1;
                }
            }
        }else if (SUB_STATE == COLONS) {
            if ((char)c == ':') {
                counter ++;
            }else{
                result = ParseResult.FAILED.setError("character illegal");
            }
            if (counter != 1 ) {
                if (counter > 2) {
                    result = ParseResult.FAILED.setError("only double semi-colons allowed '::'");
                }else{
                    counter = 0;
                    SUB_STATE = AFTER_COLONS;
                }
            }
        }else if (SUB_STATE == AFTER_COLONS) {
            if ((char)c == ' ') {
                INDENT_STATE = READING_BODY;
                SUB_STATE = FINISHED;
                result = parseEnd(c);
            }else if ((char)c == '\n') {
                INDENT_STATE = INDENT_COUNT;
                SUB_STATE = FINISHED;
                result = parseEnd(c);
            }else{
                result = ParseResult.FAILED.setError("need a space or carriage return");
            }
        }

        return result;
    }

} // DirectiveFactory

