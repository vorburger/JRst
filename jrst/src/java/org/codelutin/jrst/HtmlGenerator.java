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
 * HtmlGenerator.java
 *
 * Created: 24 janv. 2004
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

public class HtmlGenerator extends AbstractGenerator { // HtmlGenerator
    public void generate(Element e){
        System.out.println("<!-- default generate: " + e.getClass().getName() + "-->");
    }

    public void generate(Document e){
        System.out.println("<html><body>");
        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            this.visit((Element)i.next());
        }
        System.out.println("</body></html>");
    }

    public void generate(Title e){
        System.out.println("<h1>");
        System.out.println(e.getText());
        System.out.println("</h1>");
    }

    public void generate(Para e){
        System.out.println("<p>");
        System.out.println(e.getText());
        System.out.println("</p>");
    }

    public void generate(BulletList e){
        System.out.println("<ul>");
        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            System.out.print("<li>");
            visit((Element)i.next());
            System.out.print("</li>");
        }
        System.out.println("</ul>");
   }

   public void generate(FieldList e){
       System.out.println("<dl>");
       for(int i=0; i<e.getChilds().size(); i++){
           Object child = e.getChilds().get(i);
           if(child instanceof Term){
               System.out.print("<dt>");
               visit((Element)child);
               System.out.print("<dt>");
           }else{
               System.out.print("<dd>");
               visit((Element)child);
               System.out.print("</dd>");
           }
       }
       System.out.println("</dl>");
   }

   public void generate(Term e){
       for(int i=0; i<e.getChilds().size(); i++){
           Text child = (Text)e.getChilds().get(i);
           visit(child);
       }
   }

} // HtmlGenerator

