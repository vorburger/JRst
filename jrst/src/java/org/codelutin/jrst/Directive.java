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
    String text = null;
    boolean is_directive;

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
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

} // Directive
