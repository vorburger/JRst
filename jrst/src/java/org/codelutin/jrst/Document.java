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

public class Document extends AbstractElement { // Document


    Title title     = null;
    Para author    = null;
    Para contact   = null;
    Para revision  = null;
    Para date      = null;
    Para copyright = null;


    public void setTitle(Title title) {
        this.title = title;
    }
    public void setCopyright(Para copyright) {
        this.copyright = copyright;
    }
    public void setAuthor(Para author) {
        this.author = author;
    }
    public void setContact(Para contact) {
        this.contact = contact;
    }
    public void setRevision(Para revision) {
        this.revision = revision;
    }
    public void setDate(Para date) {
        this.date = date;
    }


    public Title getTitle() {
        return title;
    }
    public Para getCopyright() {
        return copyright;
    }
    public Para getAuthor() {
        return author;
    }
    public Para getContact() {
        return contact;
    }
    public Para getRevision() {
        return revision;
    }
    public Para getDate() {
        return date;
    }

} // Document

