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
 * BlockQuoteFactory.java
 *
 * Created: 17 juillet 2004
 *
 */

package org.codelutin.jrst;

public class BlockQuoteFactory extends IndentedAbstractFactory { // BlockQuoteFactory

    /** Constantes **/

    /** Attributs **/

    int countSpace = 0;

    /** Méthodes **/

    // Accesseurs et recopieurs
    protected AbstractFactory factoryNew(){ return new BlockQuoteFactory(); }
    protected Element elementNew(){ return new BlockQuote(); }
    protected BlockQuote getBlockQuote(){ return (BlockQuote)getElement(); }

    // Constructeur
    public BlockQuoteFactory(){ init(); }

    // Initialisation
    public void init(){
        // est ce que l'élément est unique ou bien forme une liste
        unique      = true;
        // est ce que l'élément peut être sur une seule ligne
        //oneLiner    = true;

        // Expression régulière de l'entete
        // qui permet de reconnaitre quel bloc
        headRegExpr = " +.*";

        countSpace  = 0;

        super.init();
    }

    // parse l'entete
    public ParseResult parseHead(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;

        if ((char) c == ' ') {
            countSpace ++;
        }else{
            INDENT_STATE = READING_BODY;
            result = delegate(c);
        }

        return result;
    }


} // BlockQuoteFactory

