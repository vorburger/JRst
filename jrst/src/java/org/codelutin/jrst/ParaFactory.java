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

public class ParaFactory extends AbstractFactory { // ParaFactory
    StringBuffer buffer = null;
    int lastc = -1;

    protected AbstractFactory factoryNew(){
        return  new ParaFactory();
    }
    protected Element elementNew(){
        return new Para();
    }

    protected Para getPara(){
        return (Para)getElement();
    }

    public void init(){
        super.init();
        lastc = -1;
        buffer = new StringBuffer();
    }

    public ParseResult accept(int c) {
        if((char)c != ' ' && (char)c != '\n'){
            return ParseResult.ACCEPT;
        }else{
            return ParseResult.FAILED;
        }
    }

    /**
    * Retourne true tant que l'objet n'a pas fini de parser son élément.
    * Lorsqu'il retourne false, la factory est capable de savoir si l'élement est convenable ou non, pour cela il faut appeler la méthode {@link getParseResult}.
    */
    public ParseResult parse(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;
        consumedCharCount++;
        if(lastc == -1 && (char)c == ' '){
            result = ParseResult.FAILED.setError("Para must begin at column 0");
        }else if((char)c == '\n' && (char)lastc =='\n'){
            getPara().setText(buffer.toString());
            result = ParseResult.FINISHED.setConsumedCharCount(consumedCharCount-1);
        }else{
            lastc = c;
            buffer.append((char)c);
            getPara().setText(buffer.toString());
        }
        return result;
    }

} // ParaFactory

