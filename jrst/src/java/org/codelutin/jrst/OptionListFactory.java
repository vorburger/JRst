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
 * ##%*/

/* *
 * OptionListFactory.java
 *
 * Created: 17 juillet 2004
 *
 */

package org.codelutin.jrst;

import jregex.Matcher;


public class OptionListFactory extends IndentedAbstractFactory { // OptionListFactory

    /** Constantes **/

    // liste des états
    final static protected Object THE_HEAD = new Object();
    final static protected Object THE_BLANK = new Object();

    /** Attributs **/

    int type       = 0;
    int countSpace = 0;


    /** Méthodes **/

    // Accesseurs et recopieurs
    protected AbstractFactory factoryNew(){ return new OptionListFactory(); }
    protected Element elementNew(){ return new OptionList(); }
    protected OptionList getOptionList(){ return (OptionList)getElement(); }

    // Constructeur
    public OptionListFactory(){ init(); }

    // Initialisation
    public void init(){
        // est ce que l'élément est unique ou bien forme une liste
        unique      = true;
        // est ce que l'élément peut être sur une seule ligne
        oneLiner    = true;
        // est ce qu'il faut envoyer \n à parseHead pour identifier la tete
        //noEndHead   = true;

        // Expression régulière de l'entete
        // qui permet de reconnaitre quel bloc
        headRegExpr = "(\\-{1,2}|/)([\\w\\-]+)((=[\\w\\-]+)|( \\w+))?  +";

        // initialisation des attributs de la classe
        type        = 0;
        countSpace  = 0;

        // état initial
        SUB_STATE   = THE_HEAD;

        super.init();
    }

    // parse l'entete
    public ParseResult parseHead(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;

        if (SUB_STATE == THE_HEAD) {
            text.append((char)c);
            if ( headREMatcher.matches(text.toString()) ) {

                OptionList oL = getOptionList();

                Term kind = new Term(headREMatcher.group(1));
                Term name = new Term(headREMatcher.group(2));
                oL.addChild(kind);
                oL.addChild(name);
                if (headREMatcher.group(3) != null) {
                    Term arg  = new Term(headREMatcher.group(3));
                    oL.addChild(arg);
                }

                //if ( headREMatcher.group(0).length() > oL.getLongueur()) {
                //    oL.setLongueur(headREMatcher.group(0).length());
                //}

                text.delete(0,text.length());
                SUB_STATE = THE_BLANK;
            }else if (! headREMatcher.matchesPrefix()) {
                result = ParseResult.FAILED;
            }
        }else if (SUB_STATE == THE_BLANK) {
            if ((char)c != ' ') {
                getOptionList().setLongueur(countSpace);
                INDENT_STATE = READING_BODY;
                SUB_STATE = THE_HEAD;
                result = delegate(c);
            }else{
                countSpace ++;
            }
        }

        return result;
    }




} // TemplateIndentedFactory

