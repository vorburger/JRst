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
 * TemplateIndentedFactory.java
 *
 * template pour la factory d'un élément indenté
 *
 * Created: 17 juillet 2004
 *
 */

package org.codelutin.jrst;

public class TemplateIndentedFactory extends IndentedAbstractFactory { // TemplateIndentedFactory

    /** Constantes **/

    // liste des caractères acceptés
    final static String ACCEPTED_CHAR = "";

    // liste des états
    final static protected Object FIRST_STATE = new Object();
    final static protected Object SECOND_STATE = new Object();
    // ...

    /** Attributs **/

    int type       = 0;
    int countSpace = 0;


    /** Méthodes **/

    // Accesseurs et recopieurs
    protected AbstractFactory factoryNew(){ return new TemplateFactory(); }
    protected Element elementNew(){ return new Template(); }
    protected Template getTemplate(){ return (Template)getElement(); }

    // Constructeur
    public TemplateIndentedFactory(){ init(); }

    // Initialisation
    public void init(){
        // est ce que l'élément est unique ou bien forme une liste
        //unique      = true;
        // est ce que l'élément peut être sur une seule ligne
        //oneLiner    = true;
        // est ce qu'il faut envoyer \n à parseHead pour identifier la tete
        //noEndHead   = true;

        // Expression régulière de l'entete
        // qui permet de reconnaitre quel bloc
        headRegExpr = "";

        // initialisation des attributs de la classe
        type        = 0;
        countSpace  = 0;

        // état initial
        SUB_STATE   = FIRST_STATE;

        super.init();
    }

    // parse l'entete [ exemple sans signification précise ]
    public ParseResult parseHead(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;

        if(SUB_STATE == FIRST_STATE){
            if(ACCEPTED_CHAR.indexOf((char)c) != -1){
                type = c;
                countSpace = 0;
                SUB_STATE = SECOND_STATE;
            }else if ((char) c == ' ') {
                countSpace ++;
            }else
                result = ParseResult.FAILED.setError("The error is... '"+(char)c+"'");

        }else if(SUB_STATE == SECOND_STATE){
            if ((char)c == ' ' && countSpace == 0) {
                result = ParseResult.FAILED.setError("List must have one space after symbole");
            }else{
                INDENT_STATE = READING_BODY;
                SUB_STATE = FIRST_STATE;
                result = delegate(c);
            }
        }

        return result;
    }


} // TemplateIndentedFactory

