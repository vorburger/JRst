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
 * Litteral.java
 *
 *
 * Created: 23 juillet. 2004
 *
 */

package org.codelutin.jrst;

public class Litteral extends AbstractElement { // Litteral Element

    /** Constantes **/

    /** Attributs **/

    String text = null;

    /** Méthodes **/

    // constructeur
    public Litteral(){}

    // Accesseurs -- SETTERs
    public void setText(String t) { this.text = t; }

    // Accesseurs -- GETTERs
    public String getText() { return text; }


} // Litteral Element

