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
 * Directive.java
 *
 * Created: 23 juin 2004
 *
 * @author Bucas
 * Copyright Code Lutin
 * @version $Revision$
 *
 * Mise a jour: $Date$
 * par : $Author$
 */

package org.codelutin.jrst;

public class Directive extends AbstractElement { // Directive

    // constantes

    final static Object KIND_NOTE = new Object(); // la directive est une note
    final static Object KIND_CONTENTS = new Object(); // la table des matières

    // attributs

    String text = null;
    boolean is_directive;
    Object type = null;

    public Directive(){
        is_directive = false;
    }

    public boolean isDirective() {
        return is_directive;
    }

    public void setDirective(boolean b) {
        is_directive = b;
    }

    public String getText(){
        return text.trim();
    }

    public void setText(String t){
        if ( "note".equals(t.toLowerCase()) ){
            type = KIND_NOTE;
        }else if ( "contents".equals(t.toLowerCase()) ) {
            type = KIND_CONTENTS;
        }

        this.text = t;
    }
    public void setType(Object type) {
        this.type = type;
    }
    public Object getType() {
        return type;
    }
} // Directive

