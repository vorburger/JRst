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

public class FieldListFactory extends IndentedAbstractFactory { // FieldListFactory

    /** Constantes **/

    static final Object FIELD_TEXT_BEGIN = new Object();
    static final Object FIELD_TEXT_READ = new Object();
    static final Object AFTER_FIELD_TEXT = new Object();

    /** Attributs **/

    StringBuffer fieldText = null;
    int nbField = 0;
    int countSpace = 0;

    /** Méthodes **/

    // Accesseurs et Recopieurs
    protected AbstractFactory factoryNew(){ return  new FieldListFactory();    }
    protected Element elementNew(){ return new FieldList(); }
    protected FieldList getFieldList(){ return (FieldList)getElement(); }

    // Constructeur
    public FieldListFactory() { init(); }

    // Initialisation
    protected void init(){
        oneLiner = true;
        nbField = 0;
        countSpace = 0;
        fieldText = new StringBuffer();
        SUB_STATE = FIELD_TEXT_BEGIN;
        headRegExpr = ":\\w+: ";
        super.init();
    }

    // Parse l'entete d'un élement de la liste
    public ParseResult parseHead(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;

        if(SUB_STATE == FIELD_TEXT_BEGIN){
            if((char)c == ':')
                SUB_STATE = FIELD_TEXT_READ;
            else{
                if (nbField == 0)
                    result = ParseResult.FAILED.setError("FieldList must begin at column 0 with char ':'");
                else{
                    result = ParseResult.FINISHED.setConsumedCharCount(consumedCharCount-1);
                    //parseEnd(c);
                }
            }
        }else if(SUB_STATE == FIELD_TEXT_READ){
            if((char)c != ':' ){
                fieldText.append((char)c);
            }else{
                Term t = new Term(fieldText);
                getElement().addChild(t);
                fieldText.delete(0, fieldText.length());
                SUB_STATE = AFTER_FIELD_TEXT;
            }
        }else if(SUB_STATE == AFTER_FIELD_TEXT){
            if((char)c != ' ')
                result = ParseResult.FAILED.setError("List must have one space after symbole");
            else{
                nbField++;
                INDENT_STATE = READING_BODY;
                SUB_STATE = FIELD_TEXT_BEGIN;
            }
        }
        return result;
    }
} // FieldListFactory

