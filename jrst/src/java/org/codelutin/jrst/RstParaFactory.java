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
 * ParaFactory.java
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

public class RstParaFactory extends RootFactory implements ElementFactory { // ParaFactory
    StringBuffer buffer = new StringBuffer();
    ParseResult parseResult = null;
    int lastc = -1;

    public void init(){
        lastc = -1;
        element = null;
        buffer = new StringBuffer();
        parseResult = PARSE_IN_PROGRESS.create();
    }

    /**
    * Retourne true tant que l'objet n'a pas fini de parser son élément.
    * Lorsqu'il retourne false, la factory est capable de savoir si l'élement est convenable ou non, pour cela il faut appeler la méthode {@link getParseResult}.
    */
    public ParseResult accept(int c) {
        if(lastc == -1 && (char)c == ' '){
            parseResult = PARSE_FAILED.create();
            parseResult.text = "Para must begin at column 0";
        }else if((char)c == '\n' && (char)lastc =='\n'){
            element = createElement();
            parseResult = PARSE_FINISHED.create();
        }else{
            lastc = c;
            buffer.append((char)c);
        }
        return parseResult;
    }

    /**
    * Si le résultat du parsage est ok alors retourne l'element, sinon retourne null
    */
    protected Element createElement(){
        return new RstPara(buffer.toString());
    }
} // ParaFactory

