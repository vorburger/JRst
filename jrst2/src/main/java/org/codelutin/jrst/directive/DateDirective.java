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
 * DateDirective.java
 *
 * Created: 4 nov. 06 13:29:25
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */

package org.codelutin.jrst.directive;

import org.codelutin.jrst.JRSTDirective;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * .. date:: .. time::
 * 
 * @author poussin
 * 
 */
public class DateDirective implements JRSTDirective {

    /*
     * (non-Javadoc)
     * 
     * @see org.codelutin.jrst.JRSTDirective#parse(org.dom4j.Element)
     */
    public Node parse(Element e) {

        // String format = e.attributeValue(JRSTLexer.DIRECTIVE_VALUE);

        // TODO used format to format date, this format is not Java standard
        // is python standard "%Y-%m-%d"

        Text result = DocumentHelper.createText(SimpleDateFormat.getInstance()
                .format(new Date()));
        return result;
    }

}
