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


    final static Object THE_HEAD = new Object(); // après les colons

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
        //SUB_STATE   = DOT_DOT_SPACE;
        SUB_STATE   = THE_HEAD;
        unique      = true;
        oneLiner    = true;
        headRegExpr = "\\.\\. (\\w+)::";
        super.init();
    }


    public ParseResult parseHead(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;

        if (SUB_STATE == THE_HEAD) {
            text.append((char)c);
            if ( headREMatcher.matches(text.toString()) ) {

                Directive di = getDirective();

                di.setText(headREMatcher.group(1));

                text.delete(0,text.length());
                INDENT_STATE = READING_BODY;
                if (di.getType() == Directive.KIND_IMAGE){
                    counter = 0;
                    noEndBody   = true;
                }else{
                    //result = delegate(c);
                }
            }else if (! headREMatcher.matchesPrefix()) {
                result = ParseResult.FAILED;
            }
        }

        return result;
    }

    public ParseResult parseBody(int c) {
        if (getDirective().getType() == Directive.KIND_IMAGE) {
            if (c == '\n') {
                if (text.length() > 0) {
                    getDirective().addChild(new Term(text));
                    text.delete(0,text.length());
                    //counter = 2;
                }
            }else if (counter == 0 && c == ' ') {
                counter ++;
            }else if (counter == 1) {
                text.append((char)c);
            }
            return ParseResult.IN_PROGRESS;

        }else{
            return delegate(c);
        }
    }
} // DirectiveFactory

