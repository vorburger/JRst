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
 * GridTable.java
 *
 * Grid Table Element
 *
 * Created: 20 juillet. 2004
 *
 */

package org.codelutin.jrst;

public class GridTable extends AbstractElement { // GridTable Element

    /** Constantes **/

    /** Attributs **/

    Object type = null;
    int largeur = 0;
    int longueur = 0;
    Element[][] table = null;
    boolean head = false;


    /** Méthodes **/

    // constructeur
    public GridTable(){}

    public void initTable(int largeur, int longueur) {
        table = new Element[largeur][longueur];
        this.largeur = largeur;
        this.longueur = longueur;
    }

    // Accesseurs -- SETTERs
    public void setType(Object type) { this.type = type; }
    public void setLargeur(int larg) {  largeur = larg;  }
    public void setLongueur(int larg) {  longueur = larg;  }
    public void setHead(boolean h) {  head = h;  }

    /**
     *   Set Table
     *
     * @param x @param y : la coordonnée de l'élément à entrer
     * @param e : l'élément à placer dans la case
     *
     */
    public void setTable(int x, int y, Element e) {
        table[x][y] = e;
    }


    // Accesseurs -- GETTERs
    public Object getType() { return type; }
    public int getLargeur() { return largeur; }
    public int getLongueur() { return longueur; }
    public boolean getHead() { return head; }

    /**
     *   Get Table
     *
     * @param x @param y : la coordonnée de l'élément à entrer
     *
     */
    public Element getTable(int x, int y) {
        return table[x][y];
    }



} // GridTable Element

