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
 * CommentFactory.java
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

public class CommentFactory extends AbstractFactory { // CommentFactory

    /** les différents états d'avancenement de la recherche de bloc litteral **/
    final static Object DOT_DOT_SPACE = new Object(); // pour enlever le ".. " du début
    final static Object SEARCH_INDENT = new Object(); // on compte l'indentation
    final static Object FIND_INDENT = new Object(); // on cherche l'indentation minimum pour rester dans le bloc
    final static Object READING = new Object(); // on lit ce qu'il y a après l'indentation

    //StringBuffer buffer = null;
    int lastc = -1;
    int lastlastc = -1;
    int indentRead = 0;   // indentation en cours de lecture
    int indentLength = 0; // indentation de base trouvée avec INDENT_SEARCH
    StringBuffer fieldText = null;

    protected AbstractFactory factoryNew(){
        return  new CommentFactory();
    }
    protected Element elementNew(){
        return new Comment();
    }
    protected Comment getComment(){
        return (Comment)getElement();
    }

    public void init(){
        super.init();
        fieldText = new StringBuffer();
        STATE = DOT_DOT_SPACE;
        indentRead = 0;
    }

    public ParseResult accept(int c) {
        ParseResult result = parse(c);
        if(result == ParseResult.FINISHED){
            result = ParseResult.ACCEPT;
        }
        return result;
    }

    public ParseResult parseEnd(int c){
        // TODO a faire
        return null;
    }

    /**
    * Retourne true tant que l'objet n'a pas fini de parser son élément.
    * Lorsqu'il retourne false, la factory est capable de savoir si l'élement est convenable ou non, pour cela il faut appeler la méthode {@link getParseResult}.
    */
    public ParseResult parse(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;
        consumedCharCount++;

        if (STATE == DOT_DOT_SPACE) {
            if ((char)c == '.') {
                indentRead++;
                if (indentRead > 2) {
                    result = ParseResult.FAILED.setError("expected only double dot '..'");
                }
            }else if ((char)c == ' ') {
                    indentRead++;
                    if (indentRead == 3) {
                        STATE = SEARCH_INDENT;
                        indentRead = 0;
                        fieldText.delete(0, fieldText.length());
                    }
                  }else{
                      result = ParseResult.FAILED.setError("expected double dots '..'");
                  }
        }else if (STATE == SEARCH_INDENT) {
            if ((char)c == ' ') {
                indentRead ++;
            }else if ((char)c == '\n' ) {
                indentRead = 0;
                if ((char)lastc == '\n' && ((char)lastlastc == '\n')) {
                    result = ParseResult.FINISHED.setConsumedCharCount(consumedCharCount-1);
                }
            }else{
                // on trouve un caractère ki n'est pas un espace
                if (indentRead > 0) {
                    indentLength = indentRead;
                    STATE = READING;
                }else{
                    indentLength = 0;
                    result = ParseResult.FINISHED.setConsumedCharCount(consumedCharCount-1);
                }
            }
        }else if (STATE == READING) {
            if ((char)c == '\n') {
                if ((char)lastc == '\n' && ((char)lastlastc == '\n')) {
                    result = ParseResult.FINISHED.setConsumedCharCount(consumedCharCount-1);
                }
                indentRead = 0;
                STATE = FIND_INDENT;
            }
        }else if (STATE == FIND_INDENT) {
            if ((char) c == ' ') {
                indentRead ++;
            }else if ((char)c == '\n') {
                indentRead = 0;
            }else{
                // on trouve un caractere non espace donc c la fin du bloc
                if (indentRead < indentLength || indentLength == 0) {
                    result = ParseResult.FINISHED.setConsumedCharCount(consumedCharCount-1);
                }else
                    STATE = READING;
            }
        }

        if (result == ParseResult.IN_PROGRESS) {
            if (STATE == READING) {
                result = delegate(c);
            }
        }

        lastlastc = lastc;
        lastc = c;
        return result;
    }

} // CommentFactory

