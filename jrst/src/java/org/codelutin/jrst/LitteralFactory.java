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
 * LitteralFactory.java
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

public class LitteralFactory extends AbstractFactory { // LitteralFactory

    /** les différents états d'avancenement de la recherche de bloc litteral **/
    final static Object COLONS_REMOVE = new Object(); // pour enlever les "::" du début
    final static Object INDENT_SEARCH = new Object(); // on cherche la première fois l'indentation
    final static Object READING = new Object();  // on lit le texte
    final static Object FIND_INDENT = new Object(); // on cherche l'indentation minimum pour rester dans le bloc litteral

    //StringBuffer buffer = null;
    int lastc = -1;
    int lastlastc = -1;
    int indentRead = 0;   // indentation en cours de lecture
    int indentLength = 0; // indentation de base trouvée avec INDENT_SEARCH

    protected AbstractFactory factoryNew(){
        return  new LitteralFactory();
    }
    protected Element elementNew(){
        return new Litteral();
    }

    protected Litteral getLitteral(){
        return (Litteral)getElement();
    }

    public void init(){
        super.init();
        lastc = -1;
        //buffer = new StringBuffer();
        STATE = COLONS_REMOVE;
        indentRead = 0;
    }

    public ParseResult accept(int c) {
        ParseResult result = parse(c);
        if(result == ParseResult.FINISHED){
            result = ParseResult.ACCEPT;
        }
        return result;
    }

    public ParseResult parseEnd(){
        // TODO a faire
        System.out.println("Litteral FINITED");
        return null;
    }

    /**
    * Retourne true tant que l'objet n'a pas fini de parser son élément.
    * Lorsqu'il retourne false, la factory est capable de savoir si l'élement est convenable ou non, pour cela il faut appeler la méthode {@link getParseResult}.
    */
    public ParseResult parse(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;
        consumedCharCount++;

        Object lastState = STATE;

        if (STATE == COLONS_REMOVE) {
            if ((char)c == ':') {
                indentRead++;
                if (indentRead > 2) {
                    result = ParseResult.FAILED.setError("expected only double semi-colon '::'");
                }
            }else if ((char)c == '\n') {
                    indentRead++;
                    if (indentRead == 3) {
                        STATE = INDENT_SEARCH;
                        indentRead = 0;
                    }
                  }else{
                      result = ParseResult.FAILED.setError("expected double semi-colon '::'");
                  }
        }else if (STATE == INDENT_SEARCH) {
            if ((char)c == ' ') {
                indentRead ++;
            }else if ((char)c == '\n' ) {
                indentRead = 0;
            }else{
                // on trouve un caractère ki n'est pas un espace
                indentLength = indentRead;
                STATE = READING;
            }
        }else if (STATE == READING) {
            if ((char)c == '\n') {
                System.out.print(getLitteral().getText());
                indentRead = 0;
                STATE = FIND_INDENT;
            }
        }else if (STATE == FIND_INDENT) {
            if ((char) c == ' ') {
                indentRead ++;
            }else if ((char)c == '\n') {
                indentRead = 0;
            }else{
                // on trouve un caractere non espace
                if (indentRead < indentLength){
                    System.out.println("Youpi le finichead");
                    result = ParseResult.FINISHED.setConsumedCharCount(consumedCharCount-1);
                }else{
                    STATE = READING;
                }
            }
        }

        if (result == ParseResult.IN_PROGRESS) {
            if (lastState != COLONS_REMOVE) {
                buffer.append((char)c);
                getLitteral().setText(buffer.toString());
            }
        }

        if (result == ParseResult.FAILED) {
            if (result.getError() != "expected double semi-colon '::'")
            System.out.println(" :: "+result.getError());
        }

        return result;
    }

} // LitteralFactory

