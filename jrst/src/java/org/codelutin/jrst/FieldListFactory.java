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
 * FieldListFactory.java
 *
 * Created: 24 janv. 2004
 *
 * @author Benjamin Poussin <poussin@codelutin.com>
 * Copyright Code Lutin
 * @version $Revision$
 *
 * Mise a jour: $Date$
 * par : $Author$
 */

package org.codelutin.jrst;

public class FieldListFactory extends AbstractFactory { // FieldListFactory
    int lastc = -1;
    int indentLength = 0;
    int indentRead = 0;
    StringBuffer fieldText = null;

    static final Object FIELD_TEXT_BEGIN = new Object();
    static final Object AFTER_FIELD_TEXT = new Object();
    static final Object FIELD_TEXT_READ = new Object();
    static final Object INDENT_READ = new Object();
    static final Object CHILD_READ = new Object();
    static final Object FIELD_TEXT_BEGIN_OR_INDENT_READ = new Object();

    protected AbstractFactory factoryNew(){
        return  new FieldListFactory();
    }
    protected Element elementNew(){
        return new FieldList();
    }

    protected FieldList getFieldList(){
        return (FieldList)getElement();
    }

    protected void init(){
        super.init();
        lastc = -1;
        indentLength = 0;
        indentRead = 0;
        fieldText = new StringBuffer();
        STATE = FIELD_TEXT_BEGIN;
    }

    public ParseResult accept(int c) {
        if((char)c == ':'){
            return ParseResult.ACCEPT;
        }else{
            return ParseResult.FAILED;
        }
    }

    public ParseResult parseEnd(){
        // TODO a faire
        return null;
    }

    public ParseResult parse(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;

        consumedCharCount++;

        if(STATE == FIELD_TEXT_BEGIN){
            if((char)c != ':'){
                result = ParseResult.FAILED.setError("FieldList must begin at column 0 with char ':'");
            }else{
                STATE = FIELD_TEXT_READ;
            }
        }else if(STATE == FIELD_TEXT_READ){
            if((char)c != '\\'){
                if((char)c != ':' || ((char)c == ':' && (char)lastc == '\\')){
                    fieldText.append((char)c);
                }else{
                    Term t = new Term();
                    getElement().addChild(t.setText(fieldText.toString()));
                    fieldText.delete(0, fieldText.length());
                    STATE = AFTER_FIELD_TEXT;
                }
            }
        }else if(STATE == AFTER_FIELD_TEXT){
            if((char)c != ' ' && (char)c != '\n'){
                result = ParseResult.FAILED.setError("List must have one space after field text");
            }else if((char)c == '\n'){
                STATE = FIELD_TEXT_BEGIN_OR_INDENT_READ;
            }else{
                STATE = CHILD_READ;
            }
        }else if(STATE == FIELD_TEXT_BEGIN_OR_INDENT_READ){
            if((char)c == ':'){
                CHILD_STATE = null;
                buffer.delete(0, buffer.length());
                STATE = FIELD_TEXT_READ;
            }else if((char)c == ' ' || (char)c == '\n'){
                STATE = INDENT_READ;
            }
        }else if(STATE == CHILD_READ){
            if((char)c == '\n'){
                //result = delegate(c);
                STATE = FIELD_TEXT_BEGIN_OR_INDENT_READ;
            }else{
                result = delegate(c);
            }
        }

        if(STATE == INDENT_READ){
            result = searchChildText(c);
            if(result == ParseResult.FINISHED){
                result = delegate(c);
                STATE = CHILD_READ;
            }else if(result == ParseResult.FAILED){
                if(lastChildIndentRead == 0){
                    // si le niveau d'indentation est 0, alors c qu'en fait la
                    // liste est terminée
                    result = ParseResult.FINISHED.setConsumedCharCount(consumedCharCount - result.getConsumedCharCount()-2);
                }
            }
        }

        lastc = c;
        return result;
    }

} // FieldListFactory

