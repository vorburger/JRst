/* *##% JRst
 * Copyright (C) 2004 - 2008 CodeLutin
 *
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
 * <http://www.gnu.org/licenses/lgpl-3.0.html>. ##%*/

/* *
 * Image.java
 *
 * Created: 4 nov. 06 12:52:02
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */

package org.codelutin.jrst.directive;

import org.codelutin.jrst.JRSTDirective;
import org.codelutin.jrst.JRSTLexer;
import static org.codelutin.jrst.ReStructuredText.IMAGE;
import static org.codelutin.jrst.ReStructuredText.SUBSTITUTION_DEFINITION;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * .. image:: picture.jpeg :height: 100 :width: 200 :scale: 50 :alt: alternate
 * text :align: right
 * 
 * @author poussin
 */
public class ImageDirective implements JRSTDirective {

    /*
     * (non-Javadoc)
     * 
     * @see org.codelutin.jrst.JRSTDirective#parse(org.dom4j.Element)
     */
    public Node parse(Element e) {
        Element result = DocumentHelper.createElement(IMAGE);

        if (e.getParent() != null
                && SUBSTITUTION_DEFINITION.equals(e.getParent().getName())) {
            String ref = e.getParent().attributeValue("name");
            result.addAttribute("alt", ref);
        }
        result.addAttribute("uri", e.attributeValue(JRSTLexer.DIRECTIVE_VALUE));

        Pattern arg = Pattern.compile("\\s+:([^:]+):\\s*(.*)");
        String[] lines = e.getText().split("\n");
        for (String l : lines) {
            Matcher matcher = arg.matcher(l.trim());
            if (matcher.matches()) {
                String name = matcher.group(1);
                String value = matcher.group(2);
                result.addAttribute(name, value);
            }
        }
        return result;
    }

}
