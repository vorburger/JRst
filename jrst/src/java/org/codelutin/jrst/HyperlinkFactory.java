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
 * HyperlinkFactory.java
 *
 * Created: 24 juin. 2004
 *
 * @author Bucas
 * Copyright Code Lutin
 * @version $Revision$
 *
 * Mise a jour: $Date$
 * par : $Author$
 */

package org.codelutin.jrst;

public class HyperlinkFactory extends IndentedAbstractFactory { // HyperlinkFactory

    /** Constantes **/

    // les différents états d'avancenement de la recherche de bloc litteral
    final static Object DOT_DOT_SPACE = new Object(); // pour enlever le ".. " du début
    final static Object TERM = new Object(); // on lit le nom de la directive
    final static Object COLON = new Object(); // on enleve les "::"
    final static Object AFTER_COLON = new Object();  // on lit le titre

    /** Attributs **/

    StringBuffer text = null;
    int counter       = 0;

    /** Méthodes **/

    // Accesseurs et Recopieurs
    protected AbstractFactory factoryNew(){ return  new HyperlinkFactory(); }
    protected Element elementNew(){ return new Hyperlink(); }
    protected Hyperlink getHyperlink(){ return (Hyperlink)getElement(); }

    // Constructeur
    public HyperlinkFactory(){ init(); }

    // Initialisation
    public void init(){
        text      = new StringBuffer();
        counter   = 0;
        SUB_STATE = DOT_DOT_SPACE;
        unique    = true;
        oneLiner  = true;
        headRegExpr = "\\.\\. [^ ][\\w _]*:";
        super.init();
    }

    // Parse l'entete de l'hyperlink
    public ParseResult parseHead(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;

        // System.out.print((char)c);

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
                      result = ParseResult.FAILED.setError("expected double dot '..'");
                  }
        }else if (SUB_STATE == TERM) {
            if((char)c != '\\'){
                if((char)c != ':' || ((char)c == ':' && (char)lastc == '\\')){
                    text.append((char)c);
                }else{
                    Term t = new Term(text);
                    getElement().addChild(t);
                    text.delete(0, text.length());
                    SUB_STATE = COLON;
                }
            }
        }else if (SUB_STATE == COLON) {
            if ((char)c == ' ') {
                SUB_STATE = AFTER_COLON;
            }else if ((char)c == '\n') {
                result = ParseResult.ACCEPT; // pour 'accept'
            }else if ((char)c == ':') {
                result = ParseResult.FAILED.setError("too much semi colon ':' before link");
            }else
                result = ParseResult.FAILED.setError("need space before link");

        }else if (SUB_STATE == AFTER_COLON) {
            if ((char)c == ' ') {
                result = ParseResult.FAILED.setError("need only one space before link");
            }else if ((char) c == '\n') {
                result = ParseResult.ACCEPT; // pour 'accept'
            }else{
                INDENT_STATE = READING_BODY; // pour 'parse'
                result = delegate(c);
            }
        }

        return result;
    }

} // HyperlinkFactory

