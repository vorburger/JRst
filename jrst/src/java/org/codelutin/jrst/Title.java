/*##%
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
 *##%**/

/*
 * Title.java
 *
 * Created: 7 oct. 2003
 *
 * @author Benjamin Poussin <poussin@codelutin.com>
 * Copyright Code Lutin
 * @version $Revision$
 *
 * Mise a jour: $Date$
 * par : $Author$
 */

package org.codelutin.jrst;

public class Title extends AbstractElement { // Title

    String text = null;
    int titleMark = -1;
    boolean upperline = false;

    public Title(){
    }

    public int getTitleMark(){
        return titleMark;
    }

    public void setTitleMark(int titleMark){
        this.titleMark = titleMark;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    public boolean getUpperline(){
        return upperline;
    }

    public void setUpperline(boolean upperline){
        this.upperline = upperline;
    }
} // Title

