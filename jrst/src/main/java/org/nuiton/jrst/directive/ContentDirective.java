/*
 * #%L
 * JRst :: Api
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2004 - 2010 CodeLutin
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.nuiton.jrst.directive;

import org.nuiton.jrst.JRSTDirective;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * ContentDirective
 *
 * @author poussin
 * @version $Revision$
 * 
 * Last update : $Date$
 * By : $Author$
 */
public class ContentDirective implements JRSTDirective {

    /*
     * @see org.nuiton.jrst.JRSTDirective#parse(org.dom4j.Element)
     */
    @Override
    public Node parse(Element e) {
        Element result = DocumentHelper.createElement("topic").addAttribute(
                "value", e.attributeValue("value"));
        result.addAttribute("type", "contents");
        result.setText(e.getText());
        return result;
    }

}