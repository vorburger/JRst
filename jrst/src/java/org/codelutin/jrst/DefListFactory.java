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
 * DefListFactory.java
 *
 * Created: 5 juillet 2004
 *
 * @author Bucas <bucas@codelutin.com>
 * Copyright Code Lutin
 * @version $Revision$
 *
 * Mise a jour: $Date$
 * par : $Author$
 */

package org.codelutin.jrst;

public class DefListFactory extends IndentedAbstractFactory { // DefListFactory

    /** Constantes **/

    static final Object DEF_SEQ_BEGIN = new Object();
    static final Object DEF_SEQ_END = new Object();
    static final Object AFTER_DEF = new Object();

    /** Attributs **/

    StringBuffer text = null;

    /** Méthodes **/

    protected AbstractFactory factoryNew(){ return  new DefListFactory(); }
    protected Element elementNew(){ return new DefList(); }
    protected EnumList getEnumList(){ return (EnumList)getElement(); }

    // Constructeur
    public DefListFactory() { init(); }

    // Initialisation
    protected void init(){
        text = new StringBuffer();
        SUB_STATE = DEF_SEQ_BEGIN;
        headRegExpr = "[\\w ]+( : [\\w ]+)?";
        noEndHead = true;
        super.init();
    }

    // Parse la tete de la partie indentée
    public ParseResult parseHead(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;

        if ((char)c != '\n') {
            //System.out.print("\033[00;36m"+(char)c+"\033[00m");
            text.append((char)c);
        }else{
            Term t = new Term(text.toString());
            getElement().addChild(t);
            text.delete(0,text.length());
        }

        return result;
    }

} // DefListFactory

