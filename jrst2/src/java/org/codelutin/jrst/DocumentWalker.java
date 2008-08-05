/* *##%
 * Copyright (C) 2006
 *     Code Lutin, Cédric Pineau, Benjamin Poussin
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
 * DocumentWalker.java
 *
 * Created: 30 oct. 06 10:28:10
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */

package org.codelutin.jrst;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;

import java.util.List;

/**
 * @author poussin
 * 
 */

public class DocumentWalker {

    static private Log log = LogFactory.getLog(DocumentWalker.class);

    protected DocumentHandler handler = null;

    /**
     * 
     */
    public DocumentWalker(DocumentHandler handler) {
        this.handler = handler;
    }

    public void walk(Document doc) {
        handler.startDocument(doc);
        Element elem = doc.getRootElement();
        walk(elem);
        handler.endDocument(doc);
    }

    @SuppressWarnings("unchecked")
    public void walk(Element elem) {
        handler.startElement(elem);
        for (Node node : (List<Node>) elem.content()) {
            switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                walk((Element) node);
                break;
            case Node.TEXT_NODE:
                handler.text((Text) node);
                break;
            default:
                log.warn("Not supported element type: "
                        + node.getNodeTypeName());
                break;
            }
        }
        handler.endElement(elem);
    }
}
