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
 * Document.java
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

import java.util.List;
import java.util.Iterator;

public class Document extends AbstractElement { // Document


    Title title             = null;
    FieldList bibliographic = null;
    List contents           = null;
    List hyperlinks         = null;

    /** SETTER **/
    public void setTitle(Title title) {
        this.title = title;
    }
    public void setBibliographic(FieldList b) {
        this.bibliographic = b;
    }
    public void setContents(List contents) {
        this.contents = contents;
    }
    public void setHyperlinks(List hyperlinks) {
        this.hyperlinks = hyperlinks;
    }

    /** GETTER **/
    public Title getTitle() {
        return title;
    }
    public FieldList getBibliographic() {
        return bibliographic;
    }
    public List getContents() {
        return contents;
    }
    public List getHyperlinks() {
        return hyperlinks;
    }

    /** Méthodes **/

    /**
     * Recherche le lien incriminé
     * @param name : le nom du lien
     **/
    public String findLink(String name) {
        if (hyperlinks != null) {
            for(Iterator i = hyperlinks.iterator(); i.hasNext();) {
                Element link = (Element)i.next();
                if (link instanceof Title) {
                    if (((Title)link).getText().toLowerCase().equals(name.toLowerCase())){
                        return "#"+((Title)link).getText(); // lien interne au document
                    }
                }else if (link instanceof Hyperlink) {
                    if (((Hyperlink)link).getText().toLowerCase().equals("_"+name.toLowerCase())){
                        for(Iterator j= link.getChilds().iterator(); j.hasNext();) {
                            Element p = (Element)j.next();
                            if (p instanceof Para)
                                return ((Para)p).getText();
                        }
                    }
                }
            }
        }
        return "none";
    }


} // Document

