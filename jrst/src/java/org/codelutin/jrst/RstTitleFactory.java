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
* RstTitleFactory.java
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

public class RstTitleFactory extends RootFactory implements ElementFactory { // RstTitleFactory

    static final Object BEFORE = new Object();
    static final Object AFTER = new Object();

    StringBuffer buffer = new StringBuffer();

    int titleMark = -1;
    int countMark = 0;
    ParseResult parseResult = null;

    public void init(){
        element = null;
        buffer = new StringBuffer();
        titleMark = -1;

        countMark = 0;
        parseResult = PARSE_IN_PROGRESS.create();
        parseResult.STATE = BEFORE;
    }

    /**
    * Retourne true tant que l'objet n'a pas fini de parser son élément.
    * Lorsqu'il retourne false, la factory est capable de savoir si l'élement est convenable ou non, pour cela il faut appeler la méthode {@link getParseResult}.
    */
    public ParseResult accept(int c) {
        if(parseResult.STATE == BEFORE){
            if((char)c == '\n'){
                parseResult.STATE = AFTER;
            }else{
                buffer.append((char)c);
            }
        }else{
            if (titleMark == -1 || titleMark == c){
                titleMark = c;
                countMark++;
            }
            if(countMark > buffer.length() || (countMark < buffer.length() && (char)c == '\n')){
                parseResult = PARSE_FAILED.create();
                parseResult.text = "Title and underline don't have same length";
            }else if((char)c == '\n' && countMark == buffer.length()){
                element = createElement();
                parseResult = PARSE_FINISHED.create();
            }
        }
        return parseResult;
    }

    /**
    * Si le résultat du parsage est ok alors retourne l'element, sinon retourne null
    */
    protected Element createElement(){
        return new RstTitle(buffer.toString(), titleMark);
    }

} // RstTitleFactory

