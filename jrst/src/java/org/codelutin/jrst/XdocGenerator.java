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

    public XdocGenerator() { super(); }

    public void generate(Document e){
        doc = e;
        os.println("<?xml version=\"1.0\" encoding=\"iso-8859-15\" ?>");

        os.println("<document>\n<properties>");
        if (e.getTitle() != null)
            os.println("<title>"+encode(e.getTitle().getText())+"</title>");
        os.println("</properties>\n<body>");
        os.println("<section name=\""+getHtmlName(e.getTitle().getText().trim())+"\">");

        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            visit((Element)i.next());
        }
        os.println("</section></body></document>");
    }

    public void generate(Title e){
        if (e == doc.getTitle()) {
        }else if ( e.getUpperline()) {
            os.print("<h1><a class=\"toc-backref\"  name=\""+getHtmlName(e.getText())+"\"> ");//href=\"#id"+e.getId()+"\"
            os.println(inlineMarkup(e.getText())+"</a></h1>");
        }else{
            os.print("<h"+(e.getProfondeur()+1)+"><a class=\"toc-backref\"  name=\""+getHtmlName(e.getText())+"\"> "); //href=\"#id"+e.getId()+"\"
            os.println(inlineMarkup(e.getText())+"</a></h"+(e.getProfondeur()+1)+">");
        }
    }

/*
    public void generate(OrElement e){
        if ( showBalise) os.println("<!-- OrElement:"+e.getName()+" -->");
        if ("Section".equals(e.getName()))
            os.println(getIndent()+"<section name=\""+"NoName"+"\">");

        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            visit((Element)i.next());
        }
        if ("Section".equals(e.getName())) {
            os.println(getIndent()+"</section>");
        }
    }
*/

} // XdocGenerator

