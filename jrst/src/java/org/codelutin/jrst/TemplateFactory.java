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
* TemplateFactory.java
*
* Created: 17 juliette. 2004
*
*
*/

package org.codelutin.jrst;

public class TemplateFactory extends AbstractFactory { // TemplateFactory

    /** Constantes **/

    static final String ACCEPTED_CHAR = "";

    // état de l'automate
    static final Object FIRST_STATE = new Object();
    static final Object SECOND_STATE = new Object();
    // ...


    /** Attributs **/

    StringBuffer text = null;

    int type  = 0;
    int count = 0;

    /** Méthodes **/

    protected AbstractFactory factoryNew(){ return new TemplateFactory(); }
    protected Element elementNew(){ return new Template(); }
    protected Template getTemplate(){ return (Template)getElement(); }

    // Initialisation
    public void init(){
        super.init();

        // initialisation des attributs
        type  = 0;
        count = 0;

        // état initial
        STATE = FIRST_STATE;
    }

    // Accepter une séquence de caractères
    public ParseResult accept(int c) {
        ParseResult result = parse(c);
        if(result == ParseResult.FINISHED){
            result = ParseResult.ACCEPT;
        }
        return result;
    }

    // Terminer
    public ParseResult parseEnd(int c){
        return ParseResult.ACCEPT;
    }

    /**
    * Retourne IN_PROGRESS, FAILED ou ACCEPT
    * {@link getParseResult}.
    */
    public ParseResult parse(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;
        consumedCharCount++;

        if(STATE == FIRST_STATE){
            if((char)c == '\n'){
                STATE = SECOND_STATE;
            }else if ((char)c == 'a') {
                result = ParseResult.FAILED.setError("pas de a !!!!");
            }else{
                text.append((char)c);
            }
        }else if (STATE == SECOND_STATE) {
            text.delete(0,text.length());
            result = delegate(c);
        }

        return result;
    }

} // TemplateFactory

