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
 * EnumList.java
 *
 * Created: 25 juin 2004
 *
 * @author Bucas <bucas@codelutin.com>
 * Copyright Code Lutin
 * @version $Revision$
 *
 * Mise a jour: $Date$
 * par : $Author$
 */

package org.codelutin.jrst;

import java.util.ArrayList;

public class EnumList extends AbstractElement { // EnumList

    /** Constantes **/

    // type du compteur de l'énumération
    static final Object KIND_ALPHA = new Object();
    static final Object KIND_ALPHA_MAJ = new Object();
    static final Object KIND_NUM = new Object();
    static final Object KIND_ROMAN = new Object();

    // type de formateur
    static final Object KIND_DOT = new Object();  //  1.
    static final Object KIND_PARA = new Object(); //  1)
    static final Object KIND_PARA_PARA = new Object(); //  (1)

    /** Attributs **/

    Object type = null;
    Object formateur = null;
    int debut = -1;

    /** Méthodes **/

    // constructeur
    public EnumList() { }

    // Accesseurs -- SETTERs
    public void setDebut(int debut) { this.debut = debut; }
    public void setFormateur(Object formateur) { this.formateur = formateur;    }
    public void setType(Object type) { this.type = type; }

    // Accesseurs -- GETTERs
    public int getDebut() { return debut;  }
    public Object getFormateur() { return formateur; }
    public Object getType() { return type; }

} // EnumList

