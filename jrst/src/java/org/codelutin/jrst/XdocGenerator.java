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
 * ##%*/

/* *
 * XdocGenerator.java
 *
 * Created: 21 juillette 2004
 *
 */

package org.codelutin.jrst;

import java.util.Iterator;

public class XdocGenerator extends HtmlGenerator { // XdocGenerator

    public XdocGenerator() {}

    public void generate(Document e){
        doc = e;
        System.out.println("<?xml version=\"1.0\" encoding=\"iso-8859-15\" ?>");

        System.out.println("<document>\n<properties>");
        if (e.getTitle() != null)
            System.out.println("<title>"+e.getTitle().getText()+"</title>");
        System.out.println("</properties>\n<body>");

        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            visit((Element)i.next());
        }
        System.out.println("</body></document>");
    }

    public void generate(OrElement e){
        if ( showBalise) System.out.println("<!-- OrElement:"+e.getName()+" -->");
        if ("Section".equals(e.getName()))
            System.out.println(getIndent()+"<section name=\""+"NoName"+"\">");

        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            visit((Element)i.next());
        }
        if ("Section".equals(e.getName())) {
            System.out.println(getIndent()+"</section>");
        }
    }

} // XdocGenerator

