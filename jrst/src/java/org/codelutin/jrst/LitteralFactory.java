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
 * LitteralFactory.java
 *
 * bloc litteral dans le texte rST
 *
 * Created: 23 juillet 2004
 *
 */

package org.codelutin.jrst;

public class LitteralFactory extends IndentedAbstractFactory { // LitteralFactory

    /** Constantes **/

    /** Attributs **/

    int count = 0;

    StringBuffer sb = null;


    /** Méthodes **/

    // Accesseurs et recopieurs
    protected AbstractFactory factoryNew(){ return new LitteralFactory(); }
    protected Element elementNew(){ return new Litteral(); }
    protected Litteral getLitteral(){ return (Litteral)getElement(); }

    // Constructeur
    public LitteralFactory(){ init(); }

    // Initialisation
    public void init(){
        // est ce que l'élément est unique ou bien forme une liste
        unique      = true;
        // est ce que l'élément peut être sur une seule ligne
        //oneLiner    = true;
        // est ce qu'il faut envoyer \n à parseHead pour identifier la tete
        //noEndHead   = true;

        // Expression régulière de l'entete
        // qui permet de reconnaitre quel bloc
        headRegExpr = "\\:\\:";

        // initialisation des attributs de la classe
        count = 0;
        sb = new StringBuffer();

        super.init();
    }

    // parse l'entete [ exemple sans signification précise ]
    public ParseResult parseHead(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;

        System.out.print("\033[00;31m"+(char)c+"\033[00m");

        count++;

        if (count == 2) { // "::"
            INDENT_STATE = READING_BODY;
            result = ParseResult.FINISHED.setConsumedCharCount(consumedCharCount);
        }

        return result;
    }

    // Parse Body
    public ParseResult parseBody(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;
        System.out.print("\033[00;37m"+(char)c+"\033[00m");
        sb.append((char)c);
        return result;
    }

    // Demande à se terminer
    public ParseResult parseEnd(int c){
        getLitteral().setText(sb.toString());

        return super.parseEnd(c);
    }

} // LitteralFactory

