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
 * RstGenerator.java
 *
 * Created: 17 janv. 2004
 *
 * @author Benjamin Poussin <poussin@codelutin.com>
 * Copyright Code Lutin
 * @version $Revision$
 *
 * Mise a jour: $Date$
 * par : $Author$
 */

package org.codelutin.jrst;

import java.util.Iterator;

public class RstGenerator extends AbstractGenerator { // RstGenerator
    public void generate(Element e){
        System.out.println("default generate: "+e.getClass().getName());
    }

    public void generate(RstDocument e){
        System.out.println("<Document>");
        System.out.println("document generate: "+e.getClass().getName());
    }

    public void generate(RstTitle e){
        System.out.println("<Title>");
        String title = e.getTitle();
        System.out.println(title);
        for(int i=0; i<title.length(); i++){
            System.out.print((char)e.getTitleMark());
        }
        System.out.println();
        System.out.println();
    }

    public void generate(RstPara e){
        System.out.println("<Para>");
        System.out.println(e.getText());
    }

    public void generate(RstList e){
        System.out.println("<List>");
        for(Iterator i=e.getList(); i.hasNext();){
            for(int l=0; l<e.getIndentLenght(); l++)
                System.out.print(" ");
            System.out.print((char)e.getSymbole()+" ");
            System.out.println(i.next().toString());
        }
        System.out.println();
   }

} // RstGenerator

